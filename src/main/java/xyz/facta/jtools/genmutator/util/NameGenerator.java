package xyz.facta.jtools.genmutator.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

public class NameGenerator {
    private static final Logger logger = LogManager.getLogger(NameGenerator.class);
    private static List<String> adjectives;
    private static List<String> verbs;
    private static List<String> nouns;

    private static final Random rand = new Random();
    private static final Map<NameCategory, Set<String>> generatedNames = new HashMap<>();
    private static final int MAX_TRIES = 4;

    private static final Set<String> java_lang_keywords = new HashSet<>(Arrays.asList(
        "abstract", "continue", "for", "new", "switch",
        "assert", "default", "goto", "package", "synchronized",
        "boolean", "do", "if", "private", "this",
        "break", "double", "implements", "protected", "throw",
        "byte", "else", "import", "public", "throws",
        "case", "enum", "instanceof", "return", "transient",
        "catch", "extends", "int", "short", "try",
        "char", "final", "interface", "static", "void",
        "class", "finally", "long", "strictfp", "volatile",
        "const", "float", "native", "super", "while"
    ));

    static {
        for (NameCategory category : NameCategory.values()) {
            generatedNames.put(category, new HashSet<>());
        }
        loadWords();
        //try {
        //    ObjectMapper mapper = new ObjectMapper();

        //    InputStream adjsStream = NameGenerator.class.getClassLoader().getResourceAsStream("dict/adjs.json");
        //    AdjectiveList adjList = mapper.readValue(adjsStream, AdjectiveList.class);
        //    logger.info("Load {} adjectives", adjList.getAdjectives().size());

        //    InputStream verbsStream = NameGenerator.class.getClassLoader().getResourceAsStream("dict/verbs.json");
        //    VerbList verbList = mapper.readValue(verbsStream, VerbList.class);
        //    logger.info("Load {} verbs", verbList.getVerbs().size());

        //    InputStream nounsStream = NameGenerator.class.getClassLoader().getResourceAsStream("dict/nouns.json");
        //    NounList nounList = mapper.readValue(nounsStream, NounList.class);
        //    logger.info("Load {} nouns", nounList.getNouns().size());

        //    adjectives = adjList.getAdjectives();
        //    // This will hold only the present form
        //    verbs = verbList.getVerbs().stream().map(VerbList.Verb::getPresent).collect(Collectors.toList());
        //    nouns = nounList.getNouns();

        //} catch (Exception e) {
        //    e.printStackTrace();
        //}
    }

    private static void loadWords() {
        adjectives = loadWordList("dict/adjs.txt");
        verbs = loadWordList("dict/verbs.txt");
        nouns = loadWordList("dict/nouns.txt");

        logger.info("Load {} adjectives", adjectives.size());
        logger.info("Load {} verbs", verbs.size());
        logger.info("Load {} nouns", nouns.size());
    }

    private static List<String> loadWordList(String fileName) {
        List<String> wordList = new ArrayList<>();
        try {
            InputStream inputStream = NameGenerator.class.getClassLoader().getResourceAsStream(fileName);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                wordList.add(line.trim());
            }
        } catch (Exception e) {
            logger.error("Error loading word list from file: {}", fileName, e);
        }
        return wordList;
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
                name = nameBuilder.toString();
                name = sanitizeWord(name);
                if (cat != NameCategory.TypeName) {
                    name = decapitalize(name);
                }
                tries++;
            } else {
                name = generateRandomLetterName();
            }
        } while (generatedNames.get(cat).contains(name) || java_lang_keywords.contains(name));

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
