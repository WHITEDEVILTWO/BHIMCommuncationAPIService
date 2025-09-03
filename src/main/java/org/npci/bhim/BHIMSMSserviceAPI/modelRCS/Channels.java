package org.npci.bhim.BHIMSMSserviceAPI.modelRCS;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NonNull
@NoArgsConstructor@AllArgsConstructor
public class Channels {

    @NonNull
    private String channel;
    @NonNull
    private String identity;
}
