package org.npci.bhim.BHIMSMSserviceAPI.entities;


import jakarta.annotation.Nullable;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.npci.bhim.BHIMSMSserviceAPI.responseDTO.WADeliveryReport;

@Entity
@Table(name = "WA_Delivery_Report")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WADLRReportEntity {
    @Id
    private String messageId;
    private String rqst_ack_id;
    private String del_time;
    private String moble_no;
    private String del_status;
    @Nullable
    private String del_desc;


    public WADLRReportEntity(WADeliveryReport report) {
        this.messageId=report.messageId();
        this.rqst_ack_id= report.rqst_ack_id();
        this.del_time= report.del_time();
        this.moble_no= report.moble_no();
        this.del_status= report.del_status();
        this.del_desc= report.del_desc();
    }

}
