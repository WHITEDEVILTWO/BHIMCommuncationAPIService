package org.npci.bhim.BHIMSMSserviceAPI.repos;

import org.npci.bhim.BHIMSMSserviceAPI.entities.MediaResponseEntity;
//import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MediaResponseRepository extends CrudRepository<MediaResponseEntity,String> {

}
