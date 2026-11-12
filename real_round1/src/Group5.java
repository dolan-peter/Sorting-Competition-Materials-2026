import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.util.Arrays;

public class Group5 { // Replace Group1 with your actual assigned group number (e.g., Group2, Group3)

    static class BinaryData implements Comparable<BinaryData> {
        String original;
        long value;
        int onesCount;
        int length;

        public BinaryData(String s) {
            this.original = s;
            this.length = s.length();
            this.onesCount = 0;
            long val = 0;

            for (int i = 0; i < length; i++) {
                char c = s.charAt(length - 1 - i);
                if (c == '1') {
                    onesCount++;
                    val += 2L * i + 1; // Fixed: using addition for the sum of odd integers
                }
            }
            this.value = val;
        }
    
        @Override
        public int compareTo(BinaryData other) {
            if (this.value != other.value) {
                return Long.compare(this.value, other.value);
            }
            if (this.onesCount != other.onesCount) {
                return Integer.compare(this.onesCount, other.onesCount);
            }
            if (this.length != other.length) {
                return Integer.compare(this.length, other.length);
            }
            return this.original.compareTo(other.original);
        }
    }

    public static String[] sort(String[] data) {
        BinaryData[] wrapped = new BinaryData[data.length];
        for (int i = 0; i < data.length; i++) {
            wrapped[i] = new BinaryData(data[i]);
        }
        Arrays.sort(wrapped);

        String[] result = new String[data.length];
        for (int i = 0; i < data.length; i++) {
            result[i] = wrapped[i].original;
        }
        return result;
    }

    // --- Standard template boilerplate below to handle I/O, warmup, and timing ---
    public static void main(String[] args) {
        String inputFilename = "SmallData.txt"; // Change to prelim1.txt or your test file
        if (args.length > 0) {
            inputFilename = args[0];
        }

        // Read data
        String[] data = readData(inputFilename);
        String[] dataWarmup = Arrays.copyOf(data, data.length);

        // JVM Warmup
        sort(dataWarmup);

        // Pause for GC / I/O settling
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Timed run
        long startTime = System.currentTimeMillis();
        String[] sortedData = sort(data);
        long endTime = System.currentTimeMillis();

        // Print ONLY the time in milliseconds (competition requirement)
        System.out.println(endTime - startTime);

        // Optional: write output to verify correctness during testing
        writeOutput("output.txt", sortedData);
    }

    public static String[] readData(String filename) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            return reader.lines().toArray(String[]::new);
        } catch (IOException e) {
            e.printStackTrace();
            return new String[0];
        }
    }

    public static void writeOutput(String filename, String[] data) {
        try {
            PrintWriter writer = new PrintWriter(new FileWriter(filename));
            for (String s : data) {
                writer.println(s);
            }
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}