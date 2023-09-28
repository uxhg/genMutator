package xyz.facta.jtools.genmutator.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xyz.facta.jtools.genmutator.data.AdjectiveList;
import xyz.facta.jtools.genmutator.data.NounList;
import xyz.facta.jtools.genmutator.data.VerbList;
import xyz.facta.jtools.genmutator.mut.FnNameMutator;

import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

public class NameGenerator {
    private static final Logger logger = LogManager.getLogger(NameGenerator.class);
    private static List<String> adjectives;
    private static List<String> verbs;
    private static List<String> nouns;

    private static final Random rand = new Random();
    private static final Map<NameCategory, Set<String>> generatedNames = new HashMap<>();
    private static final int MAX_TRIES = 4;
    static{
        for (NameCategory category : NameCategory.values()) {
            generatedNames.put(category, new HashSet<>());
        }
        try {
            ObjectMapper mapper = new ObjectMapper();

            InputStream adjsStream = NameGenerator.class.getClassLoader().getResourceAsStream("dict/adjs.json");
            AdjectiveList adjList = mapper.readValue(adjsStream, AdjectiveList.class);
            logger.info("Load {} adjectives", adjList.getAdjectives().size());

            InputStream verbsStream = NameGenerator.class.getClassLoader().getResourceAsStream("dict/verbs.json");
            VerbList verbList = mapper.readValue(verbsStream, VerbList.class);
            logger.info("Load {} verbs", verbList.getVerbs().size());

            InputStream nounsStream = NameGenerator.class.getClassLoader().getResourceAsStream("dict/nouns.json");
            NounList nounList = mapper.readValue(nounsStream, NounList.class);
            logger.info("Load {} nouns", nounList.getNouns().size());

            adjectives = adjList.getAdjectives();
            // This will hold only the present form
            verbs = verbList.getVerbs().stream().map(VerbList.Verb::getPresent).collect(Collectors.toList());
            nouns = nounList.getNouns();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static String generateName(double prefixProb, double adjectiveProb, NameCategory cat) {
        int tries = 1;
        String name;
        do {
            if (tries <= MAX_TRIES) {
                StringBuilder nameBuilder = new StringBuilder();

                // prefixProb chance to add a prefix
                if (rand.nextDouble() < prefixProb) {
                    nameBuilder.append(verbs.get(rand.nextInt(verbs.size())));
                }

                // adjectiveProb chance to add an adjective
                if (rand.nextDouble() < adjectiveProb) {
                    nameBuilder.append(capitalize(adjectives.get(rand.nextInt(adjectives.size()))));
                }

                // Always add a main word
                nameBuilder.append(capitalize(nouns.get(rand.nextInt(nouns.size()))));
                name = sanitizeWord(decapitalize(nameBuilder.toString()));
                tries++;
            } else {
                name = generateRandomLetterName();
            }
        } while (generatedNames.get(cat).contains(name));

        generatedNames.get(cat).add(name);
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

    public static String capitalize(String str) {
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

    public enum NameCategory {
        FuncName,
        VarName,
        TypeName
    }
}
