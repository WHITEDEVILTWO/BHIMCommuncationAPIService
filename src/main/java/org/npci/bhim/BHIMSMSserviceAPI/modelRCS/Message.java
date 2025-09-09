package org.npci.bhim.BHIMSMSserviceAPI.modelRCS;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.annotation.Nullable;
import lombok.Data;
import lombok.NonNull;

@Data
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class Message {

    @Nullable
    private TextMessage text_message;
    @Nullable
    private Template_Message template_message;
//    @Nullable
//    private Buttons choice_message;

}
