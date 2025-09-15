package org.npci.bhim.BHIMSMSserviceAPI.entities;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class RCSDLRReportEntity {
    @Id
    private String app_id;
    private String accepted_time;
    private String event_time;
    private String project_id;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "message_id",referencedColumnName = "message_id")
    private MessageDLRReportEntity message_delivery_report;
    private String message_metadata;


}