package com.sporty.f1bet.repository;

import com.sporty.f1bet.entity.IdempotencyKey;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IdempotencyKeyRepository extends JpaRepository<IdempotencyKey, UUID> {}
