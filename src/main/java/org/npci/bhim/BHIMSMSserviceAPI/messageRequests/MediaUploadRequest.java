package org.npci.bhim.BHIMSMSserviceAPI.messageRequests;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.annotation.Nullable;
import lombok.Data;
import lombok.NonNull;

@Data
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class MediaUploadRequest {
    private String mediaUrl; ///https:.jpg
    private String mediaFormat;
    private boolean always_upload;
}
