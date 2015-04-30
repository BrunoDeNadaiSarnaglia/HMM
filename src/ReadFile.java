import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by Bruno on 4/30/2015.
 */
public class ReadFile {

    HashSet<String> setOfTags = new HashSet<String>();
    HashMap<String, Integer> setOfWordsToInteger = new HashMap<String, Integer>();

    Integer numWords;
    Integer numTags;

    public void readTagsAndWords(String file) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        String line = bufferedReader.readLine();
        Integer idx = 0;
        while(line != null){
            String[] strings = line.split(" ");
            for (int i = 1; i < strings.length; i++) {
                setOfTags.add(strings[i]);
            }
            setOfWordsToInteger.put(strings[0], idx);
            line = bufferedReader.readLine();
        }
        numTags = setOfTags.size();
        numWords = setOfWordsToInteger.size();
    }

    public HashSet<String> getSetOfTags() {
        return setOfTags;
    }

    public HashMap<String, Integer> getSetOfWordsToInteger() {
        return setOfWordsToInteger;
    }

    public Integer getNumWords() {
        return numWords;
    }

    public Integer getNumTags() {
        return numTags;
    }

    public static void main(String[] args) throws IOException {
        ReadFile readFile = new ReadFile();
        readFile.readTagsAndWords("HW6.lexicon.txt");
    }
}
