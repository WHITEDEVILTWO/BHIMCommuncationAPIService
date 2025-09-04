package org.npci.bhim.BHIMSMSserviceAPI.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.annotation.Nullable;
import lombok.Data;
import lombok.NonNull;

@Data
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class TextMsgRequest {
    @NonNull
    private String recipient_type;
    @NonNull
    private String to;
    @NonNull
    private String type;
    @Nullable
    private Template template;
    @Nullable
    private Text text;

    private Metadata metadata;

}
