package org.npci.bhim.BHIMSMSserviceAPI.repoServices;

import jakarta.transaction.Transactional;
import org.npci.bhim.BHIMSMSserviceAPI.repos.RCSResponseRepository;
import org.npci.bhim.BHIMSMSserviceAPI.responseDTO.RcsResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RCSResponseService {
    @Autowired
    private RCSResponseRepository rcsResponseRepository;

    @Transactional
    public void saveTODb(RcsResponses response){

        rcsResponseRepository.save(response);
    }

}
