package xyz.facta.jtools.genmutator.util;

import java.util.Random;

public class VarNameGenerator {

    private static final String[] WORD_POOL = {
        "data", "info", "details", "config", "temp", "user",
        "account", "settings", "file", "path", "buffer", "string",
        "number", "count", "index", "table", "list", "map", "set",
        "queue", "stack", "heap", "input", "output", "result", "object",
        "value", "element", "node", "id", "name", "content", "view"
    };

    private static final String[] ADJECTIVES = {
        "current", "previous", "next", "first", "last", "new",
        "old", "temp", "main", "default", "primary", "secondary",
        "final", "mutable", "immutable", "local", "global", "persistent",
        "dynamic", "static", "internal", "external", "nested", "concrete",
        "derived", "shared", "transient"
    };

    private static final String[] PREFIXES = {
        "get", "set", "is", "has", "can", "update", "delete", "add", "validate",
        "check", "compute", "fetch", "store", "begin", "start", "resume", "pause", "exec",
        "process", "trigger", "display", "handle", "resolve", "toggle"
    };

    private static final Random rand = new Random();

    public static String generateVariableName() {
        StringBuilder nameBuilder = new StringBuilder();

        // 50% chance to add a prefix
        if (rand.nextInt(100) < 50) {
            nameBuilder.append(PREFIXES[rand.nextInt(PREFIXES.length)]);
        }

        // 50% chance to add an adjective
        if (rand.nextInt(100) < 50) {
            nameBuilder.append(capitalize(ADJECTIVES[rand.nextInt(ADJECTIVES.length)]));
        }

        // Always add a main word
        nameBuilder.append(capitalize(WORD_POOL[rand.nextInt(WORD_POOL.length)]));

        return decapitalize(nameBuilder.toString());
    }

    private static String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    private static String decapitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toLowerCase() + str.substring(1);
    }
}
