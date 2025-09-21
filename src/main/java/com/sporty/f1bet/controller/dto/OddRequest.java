package com.sporty.f1bet.controller.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record OddRequest(Long userId, BigDecimal amount, UUID eventId, Integer driverNumber) {}
