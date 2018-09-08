import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class DigitalRoot {
    public static void main(String[] args) throws IOException {
        BufferedReader reader =
                new BufferedReader(new InputStreamReader(System.in));

        int numTests = Integer.parseInt(reader.readLine());
        int[] inputs = new int[numTests];
        for(int i=0; i<numTests; i++){
            inputs[i] = Integer.parseInt(reader.readLine());
        }

        for(int i=0; i<numTests; i++){
            System.out.println(findDigitalRoot(inputs[i]));
        }
    }

    private static int findDigitalRoot(int input) {
        int sum=0;
        do{
            int digit = input % 10;
            sum += digit;
            input = input/10;
        }while (input != 0);

        if(sum > 9){
            sum = findDigitalRoot(sum);
        }
        return sum;
    }
}
