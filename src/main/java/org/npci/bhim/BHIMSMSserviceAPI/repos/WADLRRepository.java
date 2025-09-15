package org.npci.bhim.BHIMSMSserviceAPI.repos;

import org.npci.bhim.BHIMSMSserviceAPI.entities.WADLRReportEntity;
import org.npci.bhim.BHIMSMSserviceAPI.responseDTO.WADeliveryReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WADLRRepository extends JpaRepository<WADLRReportEntity,String> {
}
