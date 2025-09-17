package org.npci.bhim.BHIMSMSserviceAPI.entities;


//import jakarta.persistence.Entity;
//import jakarta.persistence.Id;
//import jakarta.persistence.Table;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.npci.bhim.BHIMSMSserviceAPI.responseDTO.MediaUploadResponse;


@Entity
@Table(name="media_responses")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MediaResponseEntity {
    @Id
    private String acknowledgementId;
    private String mediaUrl;

    public MediaResponseEntity(MediaUploadResponse response,String mediaUrl) {
        this.acknowledgementId=response.acknowledgementId();
        this.mediaUrl=mediaUrl;
    }
}
