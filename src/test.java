import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.Buffer;
import java.util.HashMap;

/**
 * Created by Bruno on 4/30/2015.
 */
public class test {

    HashMap<String, Integer> integerHashMap = new HashMap<String, Integer>();

    public test() throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader("HW6.gold.txt"));
        for(String line = bufferedReader.readLine(); line != null; line = bufferedReader.readLine()){
            String[] listWords = line.split(" ");
            for (String str : listWords){
                String[] strings = str.split("_");
                if(!integerHashMap.containsKey(strings[1])){
                    integerHashMap.put(strings[1], 1);
                } else {
                    integerHashMap.put(strings[1], integerHashMap.get(strings[1]) + 1);
                }
            }
        }
    }

    public static void main(String[] args) throws IOException {
        test test = new test();
        System.out.print(test.integerHashMap);
        int sum = 0;
        for(String str : test.integerHashMap.keySet()){
            sum += test.integerHashMap.get(str);
        }
        System.out.println((double) test.integerHashMap.get("N") / (double) sum);
    }
}
