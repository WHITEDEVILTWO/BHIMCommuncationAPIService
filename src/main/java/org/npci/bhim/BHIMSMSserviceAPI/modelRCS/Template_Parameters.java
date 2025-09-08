package org.npci.bhim.BHIMSMSserviceAPI.modelRCS;

import jakarta.annotation.Nullable;
import lombok.Data;
import lombok.NonNull;

@Data
@Nullable
public class Template_Parameters {
    private String cust_name;
    private String amount;
    private String discount;
}
