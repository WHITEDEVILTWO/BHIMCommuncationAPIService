package org.npci.bhim.BHIMSMSserviceAPI.repoServices;

import jakarta.transaction.Transactional;
import org.npci.bhim.BHIMSMSserviceAPI.responseDTO.WAResponses;
import org.npci.bhim.BHIMSMSserviceAPI.repos.WAResponseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WAResponseService {
    @Autowired
    private WAResponseRepository waResponseRepository;
    @Transactional
    public void saveToDb(WAResponses responses){
        waResponseRepository.save(responses);
    }
}
