package org.npci.bhim.BHIMSMSserviceAPI.responseDTO.rcs.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.annotation.Nullable;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class MessageDLRReport {

    private String message_id;
    private String conversation_id;
    private String status;
    private ChannelIdentity channel_identity;
    private String contact_id;
    @Nullable
    private Reason reason;
    private String metadata;
    private String processing_mode;
}
