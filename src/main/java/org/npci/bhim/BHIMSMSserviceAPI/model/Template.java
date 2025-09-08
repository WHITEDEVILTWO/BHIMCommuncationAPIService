package org.npci.bhim.BHIMSMSserviceAPI.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.npci.bhim.BHIMSMSserviceAPI.convAPIConstants.ConvAPIConstants;

import java.util.List;
import java.util.Objects;

@Data
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class Template {
    private String name= ConvAPIConstants.WATempltes.bbps_pre_06082025;
    private Language language;
    private List<Components> components;
}
