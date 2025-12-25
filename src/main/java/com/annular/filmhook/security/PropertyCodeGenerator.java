package com.annular.filmhook.security;

import java.security.SecureRandom;

public final class PropertyCodeGenerator {

    private static final SecureRandom RANDOM = new SecureRandom();

    private PropertyCodeGenerator() {}

    public static String generate() {
        return "PROP" + (100000 + RANDOM.nextInt(900000));
    }
}