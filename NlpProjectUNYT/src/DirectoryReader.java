import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DirectoryReader extends Thread{
    private HashMap<String, Integer> gramsHashMap;
    private Path path;
    public static int N;

    public DirectoryReader(Path path){
        this.path = path;
        gramsHashMap = new HashMap<>();
    }

    /* processing each file inside the directory, passing it to the readFile method */
    public void run(){
            try (Stream<Path> filesStream = Files.list(path)) {
                List<String> filesList = filesStream
                        .filter(file -> !Files.isDirectory(file) && isTextFile(file))
                        .peek(file -> readFile(file))
                        .map(file -> file.getFileName().toString())
                        .collect(Collectors.toList());
            }catch(IOException e){
                e.printStackTrace();
            }
    }

    //processes the current file, generating the n-grams and storing them in the hash map
    private void readFile(Path path) {
        Stream<String> linesStream = null;
        try {
            linesStream = Files.lines(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        linesStream
                .flatMap(n -> Stream.of(n.split("\\W+")))
                .map(n -> n.toLowerCase())
                .flatMap(x -> generateNGrams(x, N))
                .peek(x -> addToGramsHashMap(x))
                .count();
    }

    //generates all the N-grams of a given word
    private Stream<String> generateNGrams(String word, int n) {
        if (word.length() >= n) {
            return Stream.iterate(0, x -> x + 1)
                    .map(x -> word.substring(x, x + n))
                    .limit(word.length() - n + 1);

        }else{
            return null;
        }
    }

    //adds an N-gram to the hash map (storing the frequencies of N-grams)
    private void addToGramsHashMap(String gram){
        if( gramsHashMap.containsKey(gram)){
            gramsHashMap.put(gram, gramsHashMap.get(gram)+1);
        }else{
            gramsHashMap.put(gram, 1);
        }
    }

    private boolean isTextFile(Path path){
        return path.getFileName().toString().endsWith(".txt");
    }

    //sets the static variable N
    public static void setN(int n){
        N = n;
    }

    public Path getPath(){
        return path;
    }

    public HashMap<String,Integer> getGramsHashMap(){
        return gramsHashMap;
    }
}