package com.sporty.f1bet.helper;

import com.sporty.f1bet.entity.Driver;
import com.sporty.f1bet.entity.Session;
import java.security.SecureRandom;
import java.util.Collections;

public abstract class BuilderHelper {

    public static Session buildSessionWithKey(Integer sessionKey) {
        return new Session(
                sessionKey,
                "Spa-Francorchamps",
                2023,
                "BEL",
                "BEL",
                "Belgium GP",
                Session.SessionType.PRACTICE,
                "Spa-Francorchamps",
                Collections.emptyList());
    }

    public static Session buildSession() {
        return buildSessionWithKey(new SecureRandom().nextInt(100) + 2);
    }

    public static Driver buildDriverWithName(final Session session, String name) {
        return new Driver(name, 44, (session == null) ? buildSession() : session);
    }

    public static Driver buildDriver(final Session session) {
        return buildDriverWithName(session, null);
    }
}
