package com.sporty.f1bet.infrastructure.mapper;

import com.sporty.f1bet.application.entity.Driver;
import com.sporty.f1bet.infrastructure.provider.dto.DriverDTO;

public class DriverMapper {
    public static Driver toEntity(DriverDTO dto) {
        return new Driver(dto.fullName(), dto.driverNumber(), null);
    }
}
