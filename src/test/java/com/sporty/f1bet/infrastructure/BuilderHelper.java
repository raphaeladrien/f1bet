package com.sporty.f1bet.infrastructure;

import com.sporty.f1bet.application.entity.Driver;
import com.sporty.f1bet.application.entity.Session;
import java.util.Collections;

public abstract class BuilderHelper {

    public static Session buildSession() {
        return new Session(
                7,
                "Spa-Francorchamps",
                2023,
                "BEL",
                "Belgium",
                "Belgium GP",
                Session.SessionType.PRACTICE,
                Collections.emptyList());
    }

    public static Driver buildDriver() {
        return new Driver("Lewis Hamilton", 44, buildSession());
    }
}
