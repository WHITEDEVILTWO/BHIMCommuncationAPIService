package org.npci.bhim.BHIMSMSserviceAPI.model;

import lombok.Data;

import java.util.List;

@Data
public class Consent{
    private List<String> msisdnList;
}

