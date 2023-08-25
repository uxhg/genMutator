package xyz.facta.jtools.genmutator.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import xyz.facta.jtools.genmutator.data.AdjectiveList;
import xyz.facta.jtools.genmutator.data.NounList;
import xyz.facta.jtools.genmutator.data.VerbList;

import java.io.File;
import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

public class VarNameGenerator {
    private static List<String> adjectives;
    private static List<String> verbs;  // This will hold only the present form
    private static List<String> nouns;

    private static final Random rand = new Random();
    private static final Set<String> generatedNames = new HashSet<>();
    private static final int MAX_TRIES = 2;
    static{
        try {
            ObjectMapper mapper = new ObjectMapper();

            InputStream adjsStream = VarNameGenerator.class.getClassLoader().getResourceAsStream("dict/adjs.json");
            AdjectiveList adjList = mapper.readValue(adjsStream, AdjectiveList.class);

            InputStream verbsStream = VarNameGenerator.class.getClassLoader().getResourceAsStream("dict/verbs.json");
            VerbList verbList = mapper.readValue(verbsStream, VerbList.class);

            InputStream nounsStream = VarNameGenerator.class.getClassLoader().getResourceAsStream("dict/nouns.json");
            NounList nounList = mapper.readValue(nounsStream, NounList.class);

            adjectives = adjList.getAdjectives();
            verbs = verbList.getVerbs().stream().map(VerbList.Verb::getPresent).collect(Collectors.toList());
            nouns = nounList.getNouns();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static String generateVariableName() {
        int tries = 1;
        String name;
        do {
            if (tries <= MAX_TRIES) {
                StringBuilder nameBuilder = new StringBuilder();

                // 50% chance to add a prefix
                if (rand.nextInt(100) < 50) {
                    nameBuilder.append(verbs.get(rand.nextInt(verbs.size())));
                }

                // 50% chance to add an adjective
                if (rand.nextInt(100) < 50) {
                    nameBuilder.append(capitalize(adjectives.get(rand.nextInt(adjectives.size()))));
                }

                // Always add a main word
                nameBuilder.append(capitalize(nouns.get(rand.nextInt(nouns.size()))));
                name = sanitizeWord(decapitalize(nameBuilder.toString()));
                tries++;
            } else {
                name = generateRandomLetterName();
            }
        } while (generatedNames.contains(name));

        generatedNames.add(name);
        return name;
    }

    private static String generateRandomLetterName() {
        int length = 5 + rand.nextInt(5);  // generate names between 5 and 9 characters
        StringBuilder name = new StringBuilder();

        for (int i = 0; i < length; i++) {
            char c = (char) ('a' + rand.nextInt(26));  // generate random lowercase letters
            name.append(i == 0 ? Character.toUpperCase(c) : c);  // capitalize the first letter
        }

        return name.toString();
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


    public static String sanitizeWord(String word) {
        if (word == null) {
            return null;
        }
        return word.replaceAll("[^a-zA-Z0-9_]", "");
    }
}
