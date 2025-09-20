package com.sporty.f1bet.mapper;

import com.sporty.f1bet.entity.Driver;
import com.sporty.f1bet.provider.dto.DriverDTO;

public class DriverMapper {
    public static Driver toEntity(DriverDTO dto) {
        return new Driver(dto.fullName(), dto.driverNumber(), null);
    }
}
