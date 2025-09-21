package com.sporty.f1bet.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "idempotency_keys")
public class IdempotencyKey extends Auditable {

    @Id
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "result_id", nullable = false)
    private UUID resultId;

    public IdempotencyKey() {}

    public IdempotencyKey(UUID id, Long userId, UUID resultId) {
        this.id = id;
        this.userId = userId;
        this.resultId = resultId;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public UUID getResultId() {
        return resultId;
    }

    public void setResultId(UUID resultId) {
        this.resultId = resultId;
    }
}
