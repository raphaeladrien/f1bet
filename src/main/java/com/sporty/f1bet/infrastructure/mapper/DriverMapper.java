package com.sporty.f1bet.infrastructure.mapper;

import com.sporty.f1bet.infrastructure.persistence.entity.Driver;
import com.sporty.f1bet.infrastructure.provider.dto.DriverDTO;

public class DriverMapper {
    public static Driver toEntity(DriverDTO dto) {
        return new Driver(null, dto.fullName(), dto.driverNumber());
    }
}
