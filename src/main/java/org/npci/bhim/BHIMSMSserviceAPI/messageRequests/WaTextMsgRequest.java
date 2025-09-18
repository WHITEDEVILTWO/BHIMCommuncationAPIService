package org.npci.bhim.BHIMSMSserviceAPI.messageRequests;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.npci.bhim.BHIMSMSserviceAPI.model.Metadata;
import org.npci.bhim.BHIMSMSserviceAPI.model.Template;
import org.npci.bhim.BHIMSMSserviceAPI.model.Text;
import org.npci.bhim.BHIMSMSserviceAPI.piiDataManagement.EncryptablePayload;

@Data
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@AllArgsConstructor
@NoArgsConstructor
@EncryptablePayload
public class WaTextMsgRequest {
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
