package org.npci.bhim.BHIMSMSserviceAPI.model;

import lombok.Data;

import java.util.List;

@Data
public class Components {

    private String type;

    private List<Parameters> parameters;

}
