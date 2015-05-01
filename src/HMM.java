import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by Bruno on 4/30/2015.
 */
public class HMM {

    HashSet<String> setOfTags = new HashSet<String>();
    HashMap<String, Integer> setOfWordsToInteger = new HashMap<String, Integer>();
    ArrayList<String> setOfSentences = new ArrayList<String>();

    double[][] backward;
    double[][] forward;
    String[] listOfWordsInSentence;
    double sumOfLastForwards;

    Integer numWords;
    Integer numTags;


    double[] PI;
    double[][] A;
    double[][] B;

    public HMM() throws IOException {
        ReadFile readFile = new ReadFile();
        readFile.readTagsAndWords("HW6.lexicon.txt");
        ReadSentence readSentence = new ReadSentence();
        readSentence.read("HW6.unlabeled.txt");
        setOfTags = readFile.getSetOfTags();
        setOfWordsToInteger = readFile.getSetOfWordsToInteger();
        numWords = readFile.getNumWords();
        numTags = readFile.getNumTags();
        setOfSentences = readSentence.getSetOfSentences();
        PI = new double[numTags];
        for (int i = 0; i < numTags; i++) {
            PI[i] = Math.log(1/numTags);
        }
        A = new double[numTags][numTags];
        for (int i = 0; i < numTags; i++) {
            for (int j = 0; j < numTags; j++) {
                A[i][j] = Math.log(1/numTags);
            }
        }
        B = new double[numWords][numTags];
        for (int i = 0; i < numWords; i++) {
            for (int j = 0; j < numTags; j++) {
                B[i][j] = Math.log(1/numWords);
            }
        }
        trainWithOneSentence(setOfSentences.get(9018));
    }

    public void trainWithOneSentence(String sentence){
        listOfWordsInSentence = sentence.split(" ");
        backward = new double[numTags][listOfWordsInSentence.length];
        forward = new double[numTags][listOfWordsInSentence.length];
        calcBackward();
        calcForward();

    }

    public void calcBackward(){
        Integer numWordsInSentence = listOfWordsInSentence.length;
        for(int j=0;j<numTags;j++) {
            backward[j][numWordsInSentence-1] = 0.0;
        }

        for(int t=numWordsInSentence-2;t>=0;t--) {
            for(int i=0;i<numTags;i++) {
                double sum = 0.0;
                for(int j= 0;j<numTags;j++) {
                    sum = MathHelper.logAdd(backward[j][t+1] + A[j][i] + B[j][setOfWordsToInteger.get(listOfWordsInSentence[t+1])], sum);
                }
                backward[i][t] = sum;
            }
        }
    }

    public void calcForward(){
        Integer numWordsInSentence = listOfWordsInSentence.length;
        for (int i = 0; i < numTags; i++)
            forward[i][0] = PI[i] + B[i][setOfWordsToInteger.get(listOfWordsInSentence[0])];

    /* induction */
        for (int t = 0; t <= numWordsInSentence -2; t++) {
            for (int j = 0; j < numTags; j++) {
                forward[j][t+1] = 0;
                for (int i = 0; i < numTags; i++)
                    forward[j][t+1] = MathHelper.logAdd(forward[i][t] + A[j][i], forward[j][t+1]);
                forward[j][t+1] += B[j][setOfWordsToInteger.get(listOfWordsInSentence[t+1])];
            }
        }
        sumOfLastForwards = forward[0][numWordsInSentence-1];
        for(int i = 1; i < numTags; i++){
            sumOfLastForwards = MathHelper.logAdd(sumOfLastForwards, forward[i][numWordsInSentence-1]);
        }
    }

    public static void main(String[] args) throws IOException {
        HMM hmm = new HMM();

    }
}
