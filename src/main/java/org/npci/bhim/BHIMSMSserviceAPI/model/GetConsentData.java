package org.npci.bhim.BHIMSMSserviceAPI.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class GetConsentData {

    private LocalDate fromDate;
    private LocalDate toDate;
}
