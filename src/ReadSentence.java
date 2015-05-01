import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by Bruno on 4/30/2015.
 */
public class ReadSentence {

    ArrayList<String> SetOfSentences = new ArrayList<String>();

    public void read(String file) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        String line = bufferedReader.readLine();
        while(line != null){
            SetOfSentences.add(line);
            line = bufferedReader.readLine();
        }
    }

    public ArrayList<String> getSetOfSentences() {
        return SetOfSentences;
    }

    public static void main(String [] args) throws IOException {
        ReadSentence readSentence = new ReadSentence();
        readSentence.read("HW6.unlabeled.txt");
    }
}
