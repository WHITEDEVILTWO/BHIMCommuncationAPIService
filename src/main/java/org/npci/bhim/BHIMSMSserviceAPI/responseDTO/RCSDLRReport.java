/**
 * This section describes the callbacks that are sent from the Sinch India Conversation API. These callbacks can contain delivery reports (about outbound messages).
 * Delivery report callback A delivery report contains the status and state of each message sent through the Sinch India RBM API.
 *
 *
 * -------------------------------------------------------------------------------------
 *
 * RCS Failure error messagaes
 * -------------------------------
 * Error Type	                                                Reason
 * ------------------------------------------------------------------------------------------------------------------
 * Invalid Template	                User is not in a conversation, and the provided message template is not approved.
 * Opted Out	                                            opted_out
 * Curfew Hours	                                            curfew_hrs
 * Message Limit	                                Monthly message limit exceeded.
 * RCS Disabled	                                        Number is RCS disabled.
 * Internal Server Error	                        Specific internal server error.
 * 4xx MAAP Errors	                                       MAAP-specific error.
 * */
package org.npci.bhim.BHIMSMSserviceAPI.responseDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.npci.bhim.BHIMSMSserviceAPI.responseDTO.rcs.models.MessageDLRReport;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class RCSDLRReport {

    private String app_id;
    private String accepted_time;
    private String event_time;
    private String project_id;
    private MessageDLRReport message_delivery_report;
    private String message_metadata;


}
