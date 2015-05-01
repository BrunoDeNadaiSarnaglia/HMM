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
    Integer numSentences;


    double[] PI;
    double[][] A;
    double[][] B;
    double[][] emissionCounts;
    double[][] transitionCounts;
    double[] initialCounts;
    double[] tagCounts;


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
        numSentences = setOfSentences.size();
        PI = new double[numTags];
        for (int i = 0; i < numTags; i++) {
            PI[i] = Math.log(1.0/numTags);
        }
        A = new double[numTags][numTags];
        for (int i = 0; i < numTags; i++) {
            for (int j = 0; j < numTags; j++) {
                A[i][j] = Math.log(1.0/numTags);
            }
        }
        B = new double[numWords][numTags];
        for (int i = 0; i < numWords; i++) {
            for (int j = 0; j < numTags; j++) {
                B[i][j] = Math.log(1.0/numWords);
            }
        }
        emissionCounts = new double[numWords][numTags];
        for (int i = 0; i < numWords; i++) {
            for (int j = 0; j < numTags; j++) {
                emissionCounts[i][j] = Double.NEGATIVE_INFINITY;
            }
        }
        transitionCounts = new double[numTags][numTags];
        for (int i = 0; i < numTags; i++) {
            for (int j = 0; j < numTags; j++) {
                transitionCounts[i][j] = Double.NEGATIVE_INFINITY;
            }
        }
        initialCounts = new double[numTags];
        for (int i = 0; i < numTags; i++) {
            initialCounts[i] = Double.NEGATIVE_INFINITY;
        }
        tagCounts = new double[numTags];
        for (int i = 0; i < numTags; i++) {
            tagCounts[i] = Double.NEGATIVE_INFINITY;
        }
    }

    public void train(Integer max){
        for (int i = 0; i < max; i++) {
            trainWithOneSentence(setOfSentences.get(i));
        }
        eachTagWordLoop();
        maximization();
    }

    public void trainWithOneSentence(String sentence){
        listOfWordsInSentence = sentence.split(" ");
        backward = new double[numTags][listOfWordsInSentence.length];
        forward = new double[numTags][listOfWordsInSentence.length];
        calcBackward();
        calcForward();
        wordTagLoop(listOfWordsInSentence);
        tagTagloop(listOfWordsInSentence);
        tagLoop(listOfWordsInSentence[0]);

    }

    public void maximization(){
        for (int i = 0; i < numTags; i++) {
            PI[i] = initialCounts[i] - Math.log(numSentences);
        }
        for (int i = 0; i < numTags; i++) {
            for (int j = 0; j < numTags; j++) {
                A[i][j] = transitionCounts[i][j] - tagCounts[i];
            }
        }
        for (int i = 0; i < numTags; i++) {
            for (int j = 0; j < numWords; j++) {
                B[j][i] = emissionCounts[j][i] - tagCounts[i];
            }
        }
    }

    public void eachTagWordLoop(){
        for (int i = 0; i < numTags; i++) {
            for (int j = 0; j < numWords; j++) {
                tagCounts[i] = MathHelper.logAdd(tagCounts[i], emissionCounts[j][i]);
            }
        }
    }

    public void tagLoop(String firstWord){
        for (int i = 0; i < numTags; i++) {
            initialCounts[i] = MathHelper.logAdd(initialCounts[i], PI[i]+B[setOfWordsToInteger.get(firstWord)][i] + backward[i][0] - sumOfLastForwards);
        }
    }

    public void tagTagloop(String[] listWords){
        for (int i = 0; i < numTags; i++) {
            for (int j = 0; j < numTags; j++) {
                for (int k = 0; k < listWords.length - 1; k++) {
                    transitionCounts[i][j] = MathHelper.logAdd(transitionCounts[i][j], forward[i][k] + A[i][j] + B[setOfWordsToInteger.get(listWords[k+1])][j] + backward[j][k+1] - sumOfLastForwards );
                }
            }
        }
    }

    public void wordTagLoop(String[] listWords){
        for (int i = 0; i < listWords.length; i++) {
            for (int j = 0; j < numTags; j++) {
                emissionCounts[setOfWordsToInteger.get(listWords[i])][j] = MathHelper.logAdd(emissionCounts[setOfWordsToInteger.get(listWords[i])][j], forward[j][i] + backward[j][i] - sumOfLastForwards);
            }
        }
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


    public ArrayList<Integer> evaluateSentence(String[] listWords){
        ArrayList<Integer> arrayListInteger = new ArrayList<Integer>();
        ArrayList<Double> previousValues = new ArrayList<Double>();
        for (int i = 0; i < numTags; i++) {
            previousValues.add(PI[i] + B[setOfWordsToInteger.get(listWords[0])][i]);
        }
        arrayListInteger.add(idxMaxValue(previousValues));
        for (int i = 1; i < listWords.length; i++) {
            ArrayList<Double> aux = new ArrayList<Double>();
            for (int j = 0; j < numTags; j++) {
                ArrayList<Double> aux2 = new ArrayList<Double>();
                for (int k = 0; k < numTags; k++) {
                    aux2.add(previousValues.get(k) + A[k][j]);
                }
                aux.add(maxValue(aux2) + B[setOfWordsToInteger.get(listWords[i])][j]);
            }
            previousValues = aux;
            arrayListInteger.add(idxMaxValue(previousValues));
        }
        return arrayListInteger;
    }

    private double maxValue(ArrayList<Double> doubles){
        double value = doubles.get(0);
        for (int i = 1; i < doubles.size(); i++) {
            if(value < doubles.get(i)){
                value = doubles.get(i);
            }
        }
        return value;
    }

    private int idxMaxValue(ArrayList<Double> doubles){
        int idx = 0;
        double value = doubles.get(0);
        for (int i = 1; i < doubles.size(); i++) {
            if(value < doubles.get(i)){
                value = doubles.get(i);
                idx = i;
            }
        }
        return idx;
    }

    public static void main(String[] args) throws IOException {
        HMM hmm = new HMM();
        hmm.train(2500);
        for (int i = 0; i < hmm.numTags; i++) {
            for (int j = 0; j < hmm.numTags; j++) {
                System.out.print(hmm.A[i][j]);
            }
            System.out.println();
        }
        System.out.println(hmm.evaluateSentence(hmm.setOfSentences.get(2).split(" ")));
    }
}
