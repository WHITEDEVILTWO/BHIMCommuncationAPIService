package org.npci.bhim.BHIMSMSserviceAPI.modelRCS;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChannelIdentities {
    @NonNull
    private List<Channels> channel_identities;
}
