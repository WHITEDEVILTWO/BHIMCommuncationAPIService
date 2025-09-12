package org.npci.bhim.BHIMSMSserviceAPI.entities;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name="whatsapp_responses")
@Data
public class WAResponses {
    @Id
    private String responseId;
    private String requestBody;
}
