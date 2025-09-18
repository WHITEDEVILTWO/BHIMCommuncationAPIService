package org.npci.bhim.BHIMSMSserviceAPI.rcsMessageRequests;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.npci.bhim.BHIMSMSserviceAPI.convAPIConstants.ConvAPIConstants;
import org.npci.bhim.BHIMSMSserviceAPI.modelRCS.IdentifiedBy;
import org.npci.bhim.BHIMSMSserviceAPI.modelRCS.Message;
import org.npci.bhim.BHIMSMSserviceAPI.piiDataManagement.EncryptablePayload;

@Data
@NoArgsConstructor
@EncryptablePayload
public class RCSTextMessageRequest {
    @NonNull
    private String app_id= ConvAPIConstants.Conv_app_id;
    @NonNull
    private IdentifiedBy recipient;
    @NonNull
    private Message message;
}
