package org.npci.bhim.BHIMSMSserviceAPI.model;

import lombok.Data;
import lombok.NonNull;

import java.util.UUID;

@Data
public class Metadata {
    @NonNull
    private String messageId= UUID.randomUUID().toString();
    private String transactionId=UUID.randomUUID().toString();
    private String callbackDlrUrl;
}
