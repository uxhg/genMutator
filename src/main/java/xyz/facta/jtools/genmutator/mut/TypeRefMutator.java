package xyz.facta.jtools.genmutator.mut;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import spoon.processing.AbstractProcessor;
import spoon.reflect.reference.CtTypeReference;
import xyz.facta.jtools.genmutator.util.NameGenerator;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class TypeRefMutator extends GMAbstractMutator<CtTypeReference<?>> {
    private static final Logger logger = LogManager.getLogger(TypeRefMutator.class);
    private static double MUTATION_PROBABILITY;

    private final Random random = new Random();

    // Keep track of the names that have been changed
    private static final HashMap<String, String> changedNames = new HashMap<>();

    // Define the types within the java.lang and collection subsets as static sets
    private static final Set<String> javaLangTypes = new HashSet<>(Set.of(
        "java.lang.String",
        "java.lang.Character",
        "java.lang.Number",
        "java.lang.Integer",
        "java.lang.Long",
        "java.lang.Double",
        "java.lang.Float"
    ));

    private static final Set<String> collectionTypes = new HashSet<>(Set.of(
        "List",
        "Set",
        "Queue",
        "Deque",
        "Iterator",
        "SortedSet"
    ));

    public TypeRefMutator(double prob) {
        MUTATION_PROBABILITY = prob;
    }

    @Override
    public void process(CtTypeReference<?> typeRef) {
        if (!typeRef.isPrimitive() && shouldMutateType(typeRef)) {
            // Mutate the non-primitive type reference
            mutateTypeReference(typeRef);
        }
    }

    private void mutateTypeReference(CtTypeReference<?> typeRef) {
        String newType;
        String qualName = typeRef.getQualifiedName();
        if (changedNames.containsKey(qualName)) {
            newType = changedNames.get(qualName);
        } else {
            if (qualName.endsWith("[]")) {
                // If the type is an array, mutate the component type
                String componentTypeName = qualName.substring(0, qualName.length() - 2);
                newType = genNewTypeName(componentTypeName);
            } else {
                newType = genNewTypeName(qualName);
            }
            changedNames.put(qualName, newType);
        }
        logger.debug("[mutating type reference] {} -> {}", qualName, newType);
        typeRef.setSimpleName(newType.substring(newType.lastIndexOf('.') + 1));
    }

    private String genNewTypeName(String qualName) {
        String newType;
        String newRandName = NameGenerator.capitalize(NameGenerator.generateName(-1, -1, NameGenerator.NameCategory.TypeName));
        // Based on the type's subset, choose a random mutation
        if (javaLangTypes.contains(qualName)) {
            // Mutate java.lang types randomly
            newType = javaLangTypes.toArray(new String[0])[random.nextInt(javaLangTypes.size())];
        } else if (collectionTypes.contains(qualName)) {
            // Mutate collection types randomly
            newType = collectionTypes.toArray(new String[0])[random.nextInt(collectionTypes.size())];
        } else if (qualName.endsWith("Exception")) {
            // Mutate the type by appending "Mutated" to the end
            newType = newRandName + "Exception";
        } else if (qualName.endsWith("Error")) {
            newType = newRandName + "Error";
        } else if (qualName.equals("Optional")) {
            newType = "Optional"; // do not mutate Optional type
        } else {
            newType = newRandName;
        }
        return newType;
    }


    private boolean shouldMutateType(CtTypeReference<?> typeRef) {
        //String typeName = typeRef.getQualifiedName();
        //// If the type is in javaLangTypes or collectionTypes, mutate it randomly
        //if (javaLangTypes.contains(typeName) || collectionTypes.contains(typeName)) {
        //    return true;
        //}
        //return random.nextDouble() < MUTATION_PROBABILITY;
        return true;
    }

    @Override
    public void reset() {
        changedNames.clear();
    }
}
















