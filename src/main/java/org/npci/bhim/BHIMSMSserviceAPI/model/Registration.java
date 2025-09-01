package org.npci.bhim.BHIMSMSserviceAPI.model;

import lombok.Data;

@Data
public class Registration {
    private String grant_type;
    private String client_id;
    private String username;
    private String password;
}
