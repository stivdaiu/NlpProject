import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {

    //process all the subdirectories of the given directory, creating a new thread for each subdirectory
    public static List<DirectoryReader> listDirectories(String mainDirectory) throws IOException {
        List<DirectoryReader> directoryReaders = null;
        try (Stream<Path> stream = Files.list(Paths.get(mainDirectory))) {
            directoryReaders = stream
                    .filter(file -> Files.isDirectory(file))
                    .map(p -> new DirectoryReader(p))
                    .peek(p -> p.start())
                    .collect(Collectors.toList());
        }
        return directoryReaders;
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        String mainDirectory = args[0];
        int N;
        if(args.length==2){
            N = Integer.parseInt(args[1]);
        }else{
            N = 2; //default value of N is 2
        }
        DirectoryReader.setN(N);

        //Call the listDirectory method to process all the sub-directories
        List<DirectoryReader> dReaders = listDirectories(mainDirectory);

        //process the mystery.txt file
        DirectoryReader mysteryFileReader = new DirectoryReader(Paths.get(mainDirectory));
        mysteryFileReader.start();

        //make sure that all directory reader threads are done, before continuing further
        dReaders.stream().peek(dr -> {
            try {
                dr.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).collect(Collectors.toList());

        //make sure that mystery file reader is done too, before continuing further
        mysteryFileReader.join();

        Stream<DirectoryReader> directoryReaderStream = dReaders.stream();
        DirectoryReader closestLanguage = directoryReaderStream
                        .max(Comparator.comparing(dReader -> SimilarityCalculator.cosineSimilarity(dReader.getGramsHashMap(), mysteryFileReader.getGramsHashMap())))
                        .orElseThrow(NoSuchElementException::new);
        System.out.println("Closest language is: " + closestLanguage.getPath().getFileName());
    }
}