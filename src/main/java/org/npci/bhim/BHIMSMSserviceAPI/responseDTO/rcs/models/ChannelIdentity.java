package org.npci.bhim.BHIMSMSserviceAPI.responseDTO.rcs.models;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChannelIdentity {
    private String channel;
    private String identity;
    private String app_id;
}
