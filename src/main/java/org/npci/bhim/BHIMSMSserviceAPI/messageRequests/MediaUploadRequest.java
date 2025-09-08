package org.npci.bhim.BHIMSMSserviceAPI.messageRequests;

import jakarta.annotation.Nullable;
import lombok.Data;
import lombok.NonNull;

@Data
public class MediaUploadRequest {
    private String mediaUrl;
    private String mediaFormat;
    private boolean always_upload;
}
