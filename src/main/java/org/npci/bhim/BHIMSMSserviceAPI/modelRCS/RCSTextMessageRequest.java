package org.npci.bhim.BHIMSMSserviceAPI.modelRCS;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.npci.bhim.BHIMSMSserviceAPI.convAPIConstants.ConvAPIConstants;

@Data
@NoArgsConstructor
public class RCSTextMessageRequest {
    @NonNull
    private String app_id= ConvAPIConstants.Conv_app_id;
    @NonNull
    private IdentifiedBy recipient;
    @NonNull
    private Message message;
}
