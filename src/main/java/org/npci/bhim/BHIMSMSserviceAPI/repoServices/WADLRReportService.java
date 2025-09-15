package org.npci.bhim.BHIMSMSserviceAPI.repoServices;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.npci.bhim.BHIMSMSserviceAPI.entities.WADLRReportEntity;
import org.npci.bhim.BHIMSMSserviceAPI.repos.WADLRRepository;
import org.npci.bhim.BHIMSMSserviceAPI.responseDTO.WADeliveryReport;
import org.springframework.stereotype.Service;

@Service
public class WADLRReportService {

    private final WADLRRepository wadlrRepository;
    private final ObjectMapper mapper;

    public WADLRReportService(WADLRRepository wadlrRepository, ObjectMapper mapper) {
        this.wadlrRepository = wadlrRepository;
        this.mapper = mapper;
    }
    @Transactional
    public void saveReportToDb(WADeliveryReport report){
        // WADeliveryReport report=mapper.readValue(json,WADeliveryReport.class);
        WADLRReportEntity entity=new WADLRReportEntity(report);
        wadlrRepository.save(entity);

    }
}
