package com.tinqinacademy.authentication.persistence.repository;

import com.tinqinacademy.authentication.persistence.entity.RecoverPasswordEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RecoverPasswordRepository extends JpaRepository<RecoverPasswordEntity,UUID> {

    boolean existsByRecoveryCode(String generatePasswordRecoveryCode);

    Optional<RecoverPasswordEntity> findByRecoveryCode(String code);
}

