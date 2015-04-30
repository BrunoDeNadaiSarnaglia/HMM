import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by Bruno on 4/30/2015.
 */
public class HMM {

    HashSet<String> setOfTags = new HashSet<String>();
    HashMap<String, Integer> setOfWordsToInteger = new HashMap<String, Integer>();

    Integer numWords;
    Integer numTags;


    public HMM() throws IOException {
        ReadFile readFile = new ReadFile();
        readFile.readTagsAndWords("HW6.lexicon.txt");
        ReadSentence readSentence = new ReadSentence();
        readSentence.read("HW6.unlabeled.txt");

    }
}
