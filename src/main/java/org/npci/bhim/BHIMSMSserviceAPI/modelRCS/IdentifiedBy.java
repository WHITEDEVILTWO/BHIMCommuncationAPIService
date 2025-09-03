package org.npci.bhim.BHIMSMSserviceAPI.modelRCS;

import lombok.Data;
import lombok.NonNull;

@Data
public class IdentifiedBy {
    @NonNull
    private ChannelIdentities identified_by;
}
