package xyz.facta.jtools.genmutator.util;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class VarNameGeneratorA {

    public static List<String> tokenize(String text) {
        List<String> result = new ArrayList<>();
        try {
            Analyzer analyzer = new StandardAnalyzer();
            TokenStream tokenStream = analyzer.tokenStream(null, new StringReader(text));
            CharTermAttribute charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);
            tokenStream.reset();
            while (tokenStream.incrementToken()) {
                result.add(charTermAttribute.toString());
            }
            tokenStream.end();
            tokenStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String generateVariableName(List<String> classNames, List<String> functionNames, List<String> variableNames) {
        // Collect contextual information
        List<String> context = new ArrayList<>();
        context.addAll(classNames);
        context.addAll(functionNames);
        context.addAll(variableNames);

        // Tokenize and Normalize
        List<String> tokens = new ArrayList<>();
        for (String name : context) {
            tokens.addAll(tokenize(name));
        }

        // TODO: temp placeholder code here, need to choose a name according to similarity to the context
        Random rand = new Random();
        String prefix = tokens.get(rand.nextInt(tokens.size()));

        // TODO: temp placeholder code here, construct new name
        return prefix + rand.nextInt(100);
    }
}
