package org.npci.bhim.BHIMSMSserviceAPI.repos;

import org.npci.bhim.BHIMSMSserviceAPI.entities.WAResponses;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WAResponseRepository extends JpaRepository<WAResponses,String> {
}
