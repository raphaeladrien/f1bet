package com.sporty.f1bet.application.provider;

import com.sporty.f1bet.application.entity.Driver;
import com.sporty.f1bet.application.entity.Session;
import java.util.List;

public interface Provider {
    List<Session> getSessions(String sessionType, Integer year, String country);

    List<Driver> getDrivers(Integer sessionKey);
}
