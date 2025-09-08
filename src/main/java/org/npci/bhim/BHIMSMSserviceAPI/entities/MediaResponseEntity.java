package org.npci.bhim.BHIMSMSserviceAPI.entities;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table (name="media_responses")
@Data
public class MediaResponseEntity {
    @Id
    private String acknowledgementId;
    private String mediaUrl;
}
