package org.npci.bhim.BHIMSMSserviceAPI.responseDTO;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "rcs_responses")
@AllArgsConstructor
@NoArgsConstructor
public class RcsResponses {
    @Id
    private String messageId;
    @Column(length = 10000)
    private String requestBody;
}
