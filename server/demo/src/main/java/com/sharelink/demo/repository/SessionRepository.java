package com.sharelink.demo.repository;

import com.sharelink.demo.entity.SessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SessionRepository extends JpaRepository<SessionEntity, Long>, JpaSpecificationExecutor<SessionEntity> {
    boolean existsBySessionIdAndObjectId (String sessionId, long objectId);
    long countBySessionId (String sessionId);
    void deleteAllBySessionId(String sessionId);
    void deleteByObjectId (long id);
}
