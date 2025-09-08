package org.npci.bhim.BHIMSMSserviceAPI.modelRCS;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
public class Template_channel {
    @NonNull
    @JsonProperty(value = "RCS")
    private Template_Channel_Type_RCS RCS;
}
