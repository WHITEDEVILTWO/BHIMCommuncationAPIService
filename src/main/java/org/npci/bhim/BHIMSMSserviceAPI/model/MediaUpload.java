package org.npci.bhim.BHIMSMSserviceAPI.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MediaUpload {
    @NonNull
    private String mediaUrl;
}
