package com.juliusbaer.cmt.pat.entityDB.repositories;

import com.juliusbaer.cmt.pat.entityDB.entity.PreClearanceRequestHistoricEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PreClearanceRequestHistoricRepository extends JpaRepository<PreClearanceRequestHistoricEntity, String> {
}
