package com.sporty.f1bet.application.provider;

import com.sporty.f1bet.infrastructure.persistence.entity.Driver;
import com.sporty.f1bet.infrastructure.persistence.entity.Session;

import java.util.List;

public interface Provider {
    List<Session> getSessions();
    List<Driver> getDrivers(Integer sessionKey);
}
