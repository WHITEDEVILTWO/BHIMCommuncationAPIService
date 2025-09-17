package org.npci.bhim.BHIMSMSserviceAPI.repoServices;

import jakarta.transaction.Transactional;
import org.npci.bhim.BHIMSMSserviceAPI.entities.MediaResponseEntity;
import org.npci.bhim.BHIMSMSserviceAPI.repos.MediaResponseRepository;
import org.npci.bhim.BHIMSMSserviceAPI.responseDTO.MediaUploadResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MediaResponseService {
    @Autowired
    private MediaResponseRepository mediaResponseRepository;

    @Transactional
    public void saveToDb(MediaResponseEntity response){

        mediaResponseRepository.save(response);
    }

}
