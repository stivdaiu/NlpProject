import java.util.HashMap;

public class SimilarityCalculator {
    //finds the sum of the squares of the values in the hashMap
    private static double sumOfSquares(HashMap<String, Integer> gramsHashMap){
        return gramsHashMap.values().stream()
                .map(n -> n*n)
                .reduce(0, (a, b) -> a + b);
    }

    //finds the dot product of the values in two hash maps (numerator of cosine similarity measure)
    private static double dotProduct(HashMap<String, Integer> gramsHashMap1,HashMap<String, Integer> gramsHashMap2){
        return gramsHashMap1.keySet().stream()
                .filter(n -> gramsHashMap2.containsKey(n))
                .map(n -> gramsHashMap1.get(n)*gramsHashMap2.get(n))
                .reduce(0, (a, b) -> a + b);
    }

    //evaluates the cosine similary between two hash maps
    public static double cosineSimilarity(HashMap<String, Integer> gramsHashmap1,HashMap<String, Integer> gramsHashmap2){
        return dotProduct(gramsHashmap1,gramsHashmap2)/Math.sqrt(sumOfSquares(gramsHashmap1)*sumOfSquares(gramsHashmap2));
    }
}