package com.juliusbaer.cmt.pat.entityDB.service;

import com.juliusbaer.cmt.pat.entityDB.dto.PreClearanceRequestDto;
import com.juliusbaer.cmt.pat.entityDB.mapper.PreClearanceRequestMapper;
import com.juliusbaer.cmt.pat.entityDB.entity.PreClearanceRequestEntity;
import com.juliusbaer.cmt.pat.entityDB.repositories.PreClearanceRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.*;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PreClearanceRequestService {

    private final PreClearanceRequestRepository repo;
    private final PreClearanceRequestMapper mapper;

    @Transactional(value = "entityTx", propagation = Propagation.REQUIRES_NEW)
    public PreClearanceRequestDto create(PreClearanceRequestDto dto) {
        PreClearanceRequestEntity saved = repo.save(mapper.toEntity(dto));
        return mapper.toDto(saved);
    }

    @Transactional(readOnly = true, transactionManager = "entityTx")
    public List<PreClearanceRequestDto> findAll() {
        return mapper.toDtoList(repo.findAll());
    }
}
