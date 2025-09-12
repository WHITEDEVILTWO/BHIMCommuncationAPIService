package org.npci.bhim.BHIMSMSserviceAPI.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "rcs_responses")
public class RcsResponses {
    @Id
    private String messageId;
    private String requestBody;
}
