package org.npci.bhim.BHIMSMSserviceAPI.piiDataManagement;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.npci.bhim.BHIMSMSserviceAPI.messageRequests.WaTextMsgRequest;
import org.npci.bhim.BHIMSMSserviceAPI.rcsMessageRequests.RCSTemplateMessageRequest;
import org.npci.bhim.BHIMSMSserviceAPI.rcsMessageRequests.RCSTextMessageRequest;
import org.npci.bhim.BHIMSMSserviceAPI.service.MessageService;
import org.npci.bhim.BHIMSMSserviceAPI.service.MessageServiceRCS;
import org.npci.bhim.BHIMSMSserviceAPI.utils.EncryptionProperties;
import org.npci.bhim.BHIMSMSserviceAPI.utils.EncryptionUtilities;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DataEncLayer {

    private final MessageService messageService;
    private final MessageServiceRCS messageServiceRCS;
    private final EncryptionUtilities encryptionUtilities;
    private final EncryptionProperties encryptionProperties; // Injected config
    private final SecretKey secretKey;


    // --- WhatsApp ---
    public Mono<Map<String, Object>> routeToService(WaTextMsgRequest textMsgRequest) throws Exception {
        WaTextMsgRequest encryptedRequest = encryptRequestFields(textMsgRequest);
        return messageService.sendMessage(encryptedRequest);
    }

    // --- RCS ---
    public Mono<Map<String, Object>> routeToService(RCSTextMessageRequest textMsgRequest) throws Exception {
        RCSTextMessageRequest encryptedRequest = encryptRequestFields(textMsgRequest);
        return messageServiceRCS.sendMessage(encryptedRequest);
    }
    public Mono<Map<String, Object>> routeToService(RCSTemplateMessageRequest textMsgRequest) throws Exception {
        RCSTemplateMessageRequest encryptedRequest = encryptRequestFields(textMsgRequest);
       return  messageServiceRCS.sendMessage(encryptedRequest);
    }


    // 🔐 Generic field encryption
    private <T> T encryptRequestFields(T request) throws Exception {
        List<String> fieldsToEncrypt = encryptionProperties.getFields();

        for (String fieldName : fieldsToEncrypt) {
            try {
                Field field = request.getClass().getDeclaredField(fieldName);
                field.setAccessible(true);

                Object value = field.get(request);
                if (value != null && value instanceof String) {
                    String encryptedValue = encryptionUtilities.encryptField(secretKey, (String) value);
                    field.set(request, encryptedValue);
                }

            } catch (NoSuchFieldException ignored) {
                // If the request object doesn’t have this field, skip it
            }
        }
        return request;
    }
}

