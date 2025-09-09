package org.npci.bhim.BHIMSMSserviceAPI.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Registration {
    private String grant_type="password";
    private String client_id="ipmessaging-client";
    private String username;
    private String password;
}
