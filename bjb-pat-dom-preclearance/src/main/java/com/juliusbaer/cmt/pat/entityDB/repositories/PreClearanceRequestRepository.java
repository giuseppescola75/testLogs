package com.juliusbaer.cmt.pat.entityDB.repositories;

import com.juliusbaer.cmt.pat.entityDB.entity.PreClearanceRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PreClearanceRequestRepository extends JpaRepository<PreClearanceRequestEntity, String> {
}
