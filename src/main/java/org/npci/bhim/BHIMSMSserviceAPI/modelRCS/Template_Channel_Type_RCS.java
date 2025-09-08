package org.npci.bhim.BHIMSMSserviceAPI.modelRCS;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.annotation.Nullable;
import lombok.Data;
import lombok.NonNull;
import org.npci.bhim.BHIMSMSserviceAPI.convAPIConstants.ConvAPIConstants;

@Data
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class Template_Channel_Type_RCS {
    private String template_id= ConvAPIConstants.RCSTemplates.carousal_pre_bbps_05092025; //Template identificatoin
    @NonNull
    private String language_code;//The BCP-47 language  code en-US, or sr-Latn, English is default
    @Nullable
    private Template_Parameters parameters;

}
