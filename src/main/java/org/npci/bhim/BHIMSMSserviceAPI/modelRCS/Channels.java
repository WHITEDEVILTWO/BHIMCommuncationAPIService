package org.npci.bhim.BHIMSMSserviceAPI.modelRCS;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.springframework.validation.beanvalidation.MethodValidationAdapter;

import java.util.Objects;

@Data
@NonNull
@NoArgsConstructor@AllArgsConstructor
public class Channels {

    @NonNull
    private String channel;
    @NonNull
    private String identity;

    public void setIdentity(String identity) {
        this.identity = validateMobileNumber(identity);
    }

    public String validateMobileNumber(String identity){
        Objects.requireNonNull(identity,"Mobile number Should not be empty");
        if(!containsOnlyDigits(identity)||(identity.length() != 12)||!identity.startsWith("91")) {
                throw new IllegalArgumentException("Mobile number Should contain only digits ,should starts with 91 and should contain 12 digits(including 91)");
        }
//        else{
//            throw new IllegalArgumentException("Should contain only digits and should contain 12 digits(including 91)");
//        }
        return identity;
    }

    public static boolean containsOnlyDigits(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        for (char c : str.toCharArray()) {
            if (!Character.isDigit(c)) {
                return false;
            }
        }
        return true;
    }
}
