package org.npci.bhim.BHIMSMSserviceAPI.responseDTO;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="whatsapp_responses")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WAResponses {
    @Id
    private String responseId;
    @Column(length = 10000)
    private String requestBody;
}
