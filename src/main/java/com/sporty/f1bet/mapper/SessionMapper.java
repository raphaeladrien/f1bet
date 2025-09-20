package com.sporty.f1bet.mapper;

import com.sporty.f1bet.entity.Session;
import com.sporty.f1bet.provider.dto.SessionDTO;
import java.util.Collections;

public class SessionMapper {

    public static Session toEntity(SessionDTO dto) {
        return new Session(
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
