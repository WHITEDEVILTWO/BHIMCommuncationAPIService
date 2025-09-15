package org.npci.bhim.BHIMSMSserviceAPI.repoServices;

import jakarta.transaction.Transactional;
import org.npci.bhim.BHIMSMSserviceAPI.entities.MessageDLRReportEntity;
import org.npci.bhim.BHIMSMSserviceAPI.entities.RCSDLRReportEntity;
import org.npci.bhim.BHIMSMSserviceAPI.repos.MessageDLRReportRepository;
import org.npci.bhim.BHIMSMSserviceAPI.repos.RCSDLRReportRepository;
import org.npci.bhim.BHIMSMSserviceAPI.responseDTO.RCSDLRReport;
import org.npci.bhim.BHIMSMSserviceAPI.responseDTO.rcs.models.ChannelIdentity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RCSDLRReportService {
    @Autowired
    private RCSDLRReportRepository rcsRepo;
    @Autowired
    private MessageDLRReportRepository messageDLRReportRepository;

    @Transactional
    public void saveReport(RCSDLRReport dto) {
        // Convert DTO to Entity (manual mapping or with MapStruct/ModelMapper)
        RCSDLRReportEntity entity = convertDtoToEntity(dto);
        rcsRepo.save(entity);
    }

    public String fetchStatusByMessageIs(String messageId){
        return messageDLRReportRepository.findById(messageId)
                .map(MessageDLRReportEntity::getStatus).orElse("NOT FOUND");
    }

    private RCSDLRReportEntity convertDtoToEntity(RCSDLRReport dto) {
        // Map fields manually as needed
        MessageDLRReportEntity msgEntity = new MessageDLRReportEntity();
        msgEntity.setMessage_id(dto.getMessage_delivery_report().getMessage_id());
        msgEntity.setStatus(dto.getMessage_delivery_report().getStatus());
        msgEntity.setContact_id(dto.getMessage_delivery_report().getContact_id());
        msgEntity.setMetadata(dto.getMessage_delivery_report().getMetadata());
        msgEntity.setProcessing_mode(dto.getMessage_delivery_report().getProcessing_mode());
        ChannelIdentity ci = new ChannelIdentity();
        ci.setChannel(dto.getMessage_delivery_report().getChannel_identity().getChannel());
        ci.setChannel(dto.getMessage_delivery_report().getChannel_identity().getIdentity());
        ci.setChannel(dto.getMessage_delivery_report().getChannel_identity().getApp_id());
        msgEntity.setChannel_identity(ci);

        RCSDLRReportEntity entity = new RCSDLRReportEntity(
            dto.getApp_id(),
            dto.getAccepted_time(),
            dto.getEvent_time(),
            dto.getProject_id(),
            msgEntity,
            dto.getMessage_metadata()
        );
        return entity;
    }
}