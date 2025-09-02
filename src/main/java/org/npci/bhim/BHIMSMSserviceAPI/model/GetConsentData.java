package org.npci.bhim.BHIMSMSserviceAPI.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetConsentData {

    private LocalDate fromDate;
    private LocalDate toDate;
}
