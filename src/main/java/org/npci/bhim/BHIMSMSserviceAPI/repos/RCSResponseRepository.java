package org.npci.bhim.BHIMSMSserviceAPI.repos;

import org.npci.bhim.BHIMSMSserviceAPI.entities.RcsResponses;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RCSResponseRepository extends JpaRepository<RcsResponses,String> {
}
