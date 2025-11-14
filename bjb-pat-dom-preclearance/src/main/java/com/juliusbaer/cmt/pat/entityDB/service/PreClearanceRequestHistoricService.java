package com.juliusbaer.cmt.pat.entityDB.service;

import com.juliusbaer.cmt.pat.entityDB.dto.PreClearanceRequestHistoricDto;
import com.juliusbaer.cmt.pat.entityDB.mapper.PreClearanceRequestHistoricMapper;
import com.juliusbaer.cmt.pat.entityDB.entity.PreClearanceRequestEntity;
import com.juliusbaer.cmt.pat.entityDB.entity.PreClearanceRequestHistoricEntity;
import com.juliusbaer.cmt.pat.entityDB.repositories.PreClearanceRequestHistoricRepository;
import com.juliusbaer.cmt.pat.entityDB.repositories.PreClearanceRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PreClearanceRequestHistoricService {

    private final PreClearanceRequestRepository preClearanceRequestRepository;
    private final PreClearanceRequestHistoricRepository preClearanceRequestHistoricRepository;
    private final PreClearanceRequestHistoricMapper mapper;

    @Transactional(value = "entityTx", propagation = Propagation.REQUIRES_NEW)
    public PreClearanceRequestHistoricDto append(PreClearanceRequestHistoricDto dto) {

        PreClearanceRequestEntity entity = preClearanceRequestRepository.findById(dto.getCaseId())
                .orElseThrow(() -> new RuntimeException("Linked Case not found in Entity Database: " + dto.getCaseId()));


        PreClearanceRequestHistoricEntity historicEntity = mapper.toEntity(dto, entity);

        PreClearanceRequestHistoricEntity saved = preClearanceRequestHistoricRepository.save(historicEntity);
        return mapper.toDto(saved);
    }

}
