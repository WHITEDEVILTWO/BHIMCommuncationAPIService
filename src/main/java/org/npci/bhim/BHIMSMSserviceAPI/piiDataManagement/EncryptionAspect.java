package org.npci.bhim.BHIMSMSserviceAPI.piiDataManagement;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.npci.bhim.BHIMSMSserviceAPI.utils.EncryptionProperties;
import org.npci.bhim.BHIMSMSserviceAPI.utils.EncryptionUtilities;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.node.ArrayNode;

import javax.crypto.SecretKey;
import java.util.Arrays;
//
//@Aspect
//@Component
//@Slf4j
//@RequiredArgsConstructor
//public class EncryptionAspect {
//
//    private final EncryptionProperties encryptionProperties;
//    private final EncryptionUtilities encryptionUtilities;
//    private final ObjectMapper objectMapper = new ObjectMapper();
//    private final SecretKey secretKey;
//    private final DataEncLayer dataEncLayer;
//
//    @Around("@annotation(org.npci.bhim.BHIMSMSserviceAPI.piiDataManagement.EncryptablePayload)")
//    public Object encryptFields(ProceedingJoinPoint pjp) throws Throwable {
//        Object[] args = pjp.getArgs();
//        for (int i = 0; i < args.length; i++) {
//            args[i] = encryptObjectIfNeeded(args[i]);
//        }
//        return pjp.proceed(args);
//    }
//
//    private Object encryptObjectIfNeeded(Object arg) {
//        if (arg == null) return null;
//        try {
//            String json = objectMapper.writeValueAsString(arg);
//            JsonNode root = objectMapper.readTree(json);
//
//            for (String fieldPath : encryptionProperties.getFields()) {
//                encryptField(root, fieldPath.split("\\."));
//            }
//
//            return objectMapper.treeToValue(root, arg.getClass());
//        } catch (Exception e) {
//            log.error("Encryption failed for {}", arg, e);
//            return arg;
//        }
//    }
//
//    private void encryptField(JsonNode node, String[] path) {
//        if (node == null) return;
//        if (path.length == 0) return;
//
//        if (node.isArray()) {
//            for (JsonNode item : node) {
//                encryptField(item, Arrays.copyOfRange(path,1,path.length));
//            }
//        } else if (node.isObject()) {
//            ObjectNode objNode = (ObjectNode) node;
//            String field = path[0];
//            JsonNode child = objNode.get(field);
//
//            if (child == null) return;
//
//            if (path.length == 1 && child.isTextual()) {
//                try {
//                    String encrypted = encryptionUtilities.encryptField(secretKey, child.asText());
//                    objNode.put(field, encrypted);
//                } catch (Exception e) {
//                    log.error("❌ Error encrypting field {}: {}", field, e.getMessage());
//                }
//            } else {
//                encryptField(child, java.util.Arrays.copyOfRange(path, 1, path.length));
//            }
//        }
//    }
//
//}

//------------------------------------------------------------------------------------------------------------


@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class EncryptionAspect {

    private final EncryptionProperties encryptionProperties;
    private final EncryptionUtilities encryptionUtilities;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final SecretKey secretKey;
    private final DataEncLayer dataEncLayer;

    @Around("@annotation(org.npci.bhim.BHIMSMSserviceAPI.piiDataManagement.EncryptablePayload)")
    public Object encryptFields(ProceedingJoinPoint pjp) throws Throwable {
        Object[] args = pjp.getArgs();
        for (int i = 0; i < args.length; i++) {
            args[i] = encryptObjectIfNeeded(args[i]);
        }
        return pjp.proceed(args);
    }

    private Object encryptObjectIfNeeded(Object arg) {
        if (arg == null) return null;
        try {
            String json = objectMapper.writeValueAsString(arg);
            JsonNode root = objectMapper.readTree(json);

            for (String fieldPath : encryptionProperties.getFields()) {
                String[] pathSegments = fieldPath.split("\\.");
                encryptFieldRecursive(root, pathSegments, 0);
            }

            return objectMapper.treeToValue(root, arg.getClass());
        } catch (Exception e) {
            log.error("Encryption failed for {}", arg, e);
            return arg;
        }
    }

    /**
     * Recursively encrypts JSON fields.
     *
     * @param node         Current JSON node
     * @param pathSegments Array of path segments to traverse
     * @param index        Current index in the path
     */
    private void encryptFieldRecursive(JsonNode node, String[] pathSegments, int index) {
        if (node == null || index >= pathSegments.length) return;

        String currentField = pathSegments[index];

        if (node.isObject()) {
            ObjectNode objNode = (ObjectNode) node;
            JsonNode child = objNode.get(currentField);
            if (child == null) return;

            if (index == pathSegments.length - 1) {
                // Last segment: encrypt textual values
                if (child.isTextual()) {
                    try {
                        String encrypted = encryptionUtilities.encryptField(secretKey, child.asText());
                        objNode.put(currentField, encrypted);
                        log.debug("Encrypted field: {}", String.join(".", pathSegments));
                    } catch (Exception e) {
                        log.error("Error encrypting field {}: {}", currentField, e.getMessage());
                    }
                } else if (child.isArray()) {
                    encryptArrayElements((ArrayNode) child);
                }
            } else {
                // Recurse deeper
                encryptFieldRecursive(child, pathSegments, index + 1);
            }

        } else if (node.isArray()) {
            // Apply same path recursively to each array element
            for (JsonNode item : node) {
                encryptFieldRecursive(item, pathSegments, index);
            }
        }
    }

    /**
     * Encrypts all textual elements in an array node.
     */
    private void encryptArrayElements(ArrayNode arrayNode) {
        for (int i = 0; i < arrayNode.size(); i++) {
            JsonNode element = arrayNode.get(i);
            if (element.isTextual()) {
                try {
                    String encrypted = encryptionUtilities.encryptField(secretKey, element.asText());
                    arrayNode.set(i, objectMapper.convertValue(encrypted, JsonNode.class));
                } catch (Exception e) {
                    log.error("Error encrypting array element {}: {}", element.asText(), e.getMessage());
                }
            } else if (element.isObject() || element.isArray()) {
                // Recursively encrypt nested objects/arrays inside the array
                encryptFieldRecursive(element, new String[]{""}, 0); // dummy path for recursion
            }
        }
    }
}
