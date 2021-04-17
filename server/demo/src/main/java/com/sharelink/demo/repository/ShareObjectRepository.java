package com.sharelink.demo.repository;

import com.sharelink.demo.entity.ShareObjectEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface ShareObjectRepository extends JpaRepository<ShareObjectEntity, Long>, JpaSpecificationExecutor<ShareObjectEntity> {
    @Override
    Page<ShareObjectEntity> findAll(Pageable pageable);

    Optional<ShareObjectEntity> findTopByOrderByIdDesc();

    Optional<ShareObjectEntity> findAllByDisplayCode(int displayCode);
}
