import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class NumSum {
    public static void main(String[] args) throws IOException {
        BufferedReader reader =
                new BufferedReader(new InputStreamReader(System.in));

        int numTests = Integer.parseInt(reader.readLine());
        String[] outcomes = new String[numTests];
        for(int i=0; i<numTests; i++){
            String line1 = reader.readLine();
            String line2 = reader.readLine();
            int beansReqd = Integer.parseInt(line1.split(" ")[0]);
            Integer[] packets = getPacketTypes(line2);
            int outcome = computeOutcome(beansReqd, packets);
            outcomes[i] = ""+outcome;
        }

        for(int i=0; i<numTests; i++){
            System.out.println(outcomes[i]);
        }
    }

    private static Integer[] getPacketTypes(String line) {
        String[] splits = line.split(" ");
        Integer[] packets = new Integer[splits.length];
        for(int i=0; i<splits.length; i++){
            packets[i] = Integer.parseInt(splits[i]);
        }
        return packets;
    }

    private static int computeOutcome(int beansReqd, Integer[] packets) {
        packets = sort(packets);
        int sum=0;
        int packetCount=0;
        for(int i=0; i<packets.length; i++){
            while (beansReqd > sum){
                if( (sum + packets[i]) < beansReqd){
                    sum += packets[i];
                    packetCount++;
                }else {
                    break;
                }
            }
        }
        return packetCount;
    }

    private static Integer[] sort(Integer[] packets) {
        for(int i=0;i<packets.length-1;i++){
            for(int j=i+1; j<packets.length; j++){
                if(packets[j] > packets[i]){
                    int temp = packets[j];
                    packets[j] = packets[i];
                    packets[i] = temp;
                }
            }
        }
        return packets;
    }
}
