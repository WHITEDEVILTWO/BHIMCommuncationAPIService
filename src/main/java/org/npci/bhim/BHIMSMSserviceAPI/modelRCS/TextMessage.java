package org.npci.bhim.BHIMSMSserviceAPI.modelRCS;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
public class TextMessage {


    @NonNull
    private String text;


    public  String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = validateBody(text);
    }
    public static String validateBody(String text){
        Objects.requireNonNull(text,"Text should not be empty");
        if(text.length() >2500){
            throw new IllegalArgumentException("Message should not exceed 2500 characters");
        }
        return text;
    }
}
