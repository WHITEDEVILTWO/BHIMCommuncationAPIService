package org.npci.bhim.BHIMSMSserviceAPI.model;

import lombok.Data;
import lombok.NonNull;

@Data
public class Text {
    @NonNull
    private String body;
}
