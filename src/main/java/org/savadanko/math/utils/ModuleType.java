package org.savadanko.math.utils;

import java.util.Arrays;

public enum ModuleType {
    SIN("sin"),
    COS("cos"),
    LN("ln"),
    TAN("tan"),
    COT("cot", "ctg"),
    SEC("sec"),
    LOG2("log2", "log_2"),
    LOG3("log3", "log_3"),
    LOG5("log5", "log_5"),
    LOG10("log10", "log_10"),
    SYSTEM("system");

    private final String[] aliases;

    ModuleType(String... aliases) {
        this.aliases = aliases;
    }

    public static ModuleType fromString(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Module name is empty");
        }

        String normalized = value.trim().toLowerCase();

        return Arrays.stream(values())
                .filter(module -> Arrays.stream(module.aliases).anyMatch(alias -> alias.equals(normalized)))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown module: " + value));
    }
}
