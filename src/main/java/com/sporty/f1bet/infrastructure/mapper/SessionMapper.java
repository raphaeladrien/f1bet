package com.sporty.f1bet.infrastructure.mapper;

import com.sporty.f1bet.infrastructure.persistence.entity.Session;
import com.sporty.f1bet.infrastructure.provider.dto.SessionDTO;
import java.util.Collections;

public class SessionMapper {

    public static Session toEntity(SessionDTO dto) {
        return new Session(
                null,
                dto.sessionKey(),
                dto.sessionName(),
                dto.year(),
                dto.countryCode(),
                dto.countryName(),
                dto.sessionName(),
                Session.SessionType.fromString(dto.sessionType()),
                Collections.emptyList());
    }
}
