import java.util.*;
import java.lang.*;
import java.io.*;

class GFG {
    public static void main (String[] args) {
        Scanner scanner = new Scanner(System.in);
        int numTests = scanner.nextInt();
        while(numTests-- > 0){
            System.out.println(findPell(scanner.nextInt()));
        }
    }

    static int findPell(int n){
        if(n == 0){
            return 0;
        }

        if(n == 1){
            return 1;
        }

        return 2*(findPell(n-1)) + findPell(n-2);
    }
}