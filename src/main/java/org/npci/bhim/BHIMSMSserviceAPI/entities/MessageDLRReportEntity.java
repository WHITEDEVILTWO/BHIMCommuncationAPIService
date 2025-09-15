package org.npci.bhim.BHIMSMSserviceAPI.entities;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.npci.bhim.BHIMSMSserviceAPI.responseDTO.rcs.models.ChannelIdentity;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageDLRReportEntity {
    @Id
    private String message_id;
    private String conversation_id;
    private String status;
    @Embedded
    private ChannelIdentity channel_identity;
    private String contact_id;
    private String metadata;
    private String processing_mode;
}
