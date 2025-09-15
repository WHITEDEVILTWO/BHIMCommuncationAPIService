package org.npci.bhim.BHIMSMSserviceAPI.repos;

import org.npci.bhim.BHIMSMSserviceAPI.entities.MessageDLRReportEntity;
import org.npci.bhim.BHIMSMSserviceAPI.entities.RCSDLRReportEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RCSDLRReportRepository extends JpaRepository<RCSDLRReportEntity,String> {
}
