package org.npci.bhim.BHIMSMSserviceAPI.model;

import lombok.Data;
import lombok.NonNull;

@Data
public class TextMsgRequest {
    @NonNull
    private String recipient_type;
    @NonNull
    private String to;
    @NonNull
    private String type;
    @NonNull
    private Text text;
    private Metadata metadata;

}
