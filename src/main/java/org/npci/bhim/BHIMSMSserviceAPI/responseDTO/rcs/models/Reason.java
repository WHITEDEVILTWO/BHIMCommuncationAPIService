package org.npci.bhim.BHIMSMSserviceAPI.responseDTO.rcs.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Reason {
    private String code;
    private String description;
    private String sub_code;
}
