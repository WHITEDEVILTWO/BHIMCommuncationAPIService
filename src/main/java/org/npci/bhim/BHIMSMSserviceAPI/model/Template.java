package org.npci.bhim.BHIMSMSserviceAPI.model;

import lombok.Data;
import org.npci.bhim.BHIMSMSserviceAPI.convAPIConstants.ConvAPIConstants;

import java.util.List;
import java.util.Objects;

@Data
public class Template {
    private String name= ConvAPIConstants.WATempltes.p2p_cohort_19082025;
    private Language language;
    private List<Components> components;
}
