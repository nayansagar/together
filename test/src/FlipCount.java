import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class FlipCount {
    public static void main (String[] args) throws IOException {
        System.out.println("abc".substring(0,1));
        BufferedReader reader =
                new BufferedReader(new InputStreamReader(System.in));

        int numTests = Integer.parseInt(reader.readLine());

        while(numTests-- > 0){
            String input = reader.readLine();
            System.out.println(flipCount(input));
        }
    }

    private static int flipCount(String input){
        int result1 = 0, result2 = 0;
        for(int i=0; i<input.length(); i++){
            if( (i % 2) == 0){
                if (((input.charAt(i) - '0') ^ 0) == 0){ result1++;}
                if (((input.charAt(i) - '0') ^ 1) == 0){ result2++;}
            }else{
                if (((input.charAt(i) - '0') ^ 1) == 0){ result1++;}
                if (((input.charAt(i) - '0') ^ 0) == 0){ result2++;}
            }
        }
        return result1 > result2 ? result2 : result1;
    }
}