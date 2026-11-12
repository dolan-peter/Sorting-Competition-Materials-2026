import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Scanner;

// Created by Hayden and Katelyn

// To run on a single core, compile and then run as:
// taskset -c 0 java Group2 input.txt output.txt
// To avoid file reading/writing connections to the server, run in /tmp 
// of your lab machine.

public class Group2 {

    public static void main(String[] args) throws InterruptedException, FileNotFoundException {

        if (args.length < 2) {
            System.out.println(
                    "Running tests since input and output file names not specified");
            runSortTests();
            System.exit(0);
        }

        String inputFileName = args[0];
        String outFileName = args[1];

        // Uncomment to test comparator methods

        String[] data = readData(inputFileName); // read data as strings

        String[] toSort = data.clone(); // clone the data

        sort(toSort); // call the sorting method once for JVM warmup

        toSort = data.clone(); // clone again

        Thread.sleep(10); // to let other things finish before timing; adds stability of runs

        long start = System.currentTimeMillis();

        sort(toSort); // sort again

        long end = System.currentTimeMillis();

        System.out.println(end - start);

        writeOutResult(toSort, outFileName); // write out the results

    }

    private static String[] readData(String inputFileName) throws FileNotFoundException {
        ArrayList<String> input = new ArrayList<>();
        Scanner in = new Scanner(new File(inputFileName));

        while (in.hasNext()) {
            input.add(in.next());
        }

        in.close();

        // the string array is passed just so that the correct type can be created
        return input.toArray(new String[0]);
    }

    // YOUR SORTING METHOD GOES HERE.
    // You may call other methods and use other classes.
    // Note: you may change the return type of the method.
    // You would need to provide your own function that prints your sorted array to
    // a file in the exact same format that my program outputs
    private static class Data {
        String binary;
        long value;
        int ones;

        Data(String binary) {
            this.binary = binary;
            this.value = 0;
            this.ones = 0;

            int n = binary.length();

            for (int i = 0; i < n; i++) {
                int bit = binary.charAt(i) - '0';
                ones += bit;
                value += bit * (2L * (n - 1 - i) + 1);
            }
        }
    }

    private static void sort(String[] toSort) {
        int n = toSort.length;

        Data[] data = new Data[n];

        // Convert each string into a Data object
        for (int i = 0; i < n; i++) {
            data[i] = new Data(toSort[i]);
        }

        // Temporary array used during Merge Sort
        Data[] temp = new Data[n];

        // Run Merge Sort
        mergeSort(data, temp, 0, n - 1);

        // Copy sorted strings back into original array
        for (int i = 0; i < n; i++) {
            toSort[i] = data[i].binary;
        }
    }

    private static void mergeSort(Data[] data, Data[] temp, int left, int right) {
        // Stop when there is only one element
        if (left >= right) {
            return;
        }

        // Find the middle
        int mid = left + (right - left) / 2;

        // Sort the left half
        mergeSort(data, temp, left, mid);

        // Sort the right half
        mergeSort(data, temp, mid + 1, right);

        // Merge the two sorted halves
        merge(data, temp, left, mid, right);
    }

    private static void merge(Data[] data, Data[] temp, int left, int mid, int right) {

        int i = left;
        int j = mid + 1;
        int k = left;

        // Compare elements from both halves
        while (i <= mid && j <= right) {
            if (compare(data[i], data[j]) <= 0) {
                temp[k] = data[i];
                i++;
            } else {
                temp[k] = data[j];
                j++;
            }
            k++;
        }

        // Copy remaining elements from left half
        while (i <= mid) {
            temp[k] = data[i];
            i++;
            k++;
        }

        // Copy remaining elements from right half
        while (j <= right) {
            temp[k] = data[j];
            j++;
            k++;
        }

        // Copy merged elements back
        for (int x = left; x <= right; x++) {
            data[x] = temp[x];
        }
    }

    private static int compare(Data a, Data b) {

        if (a.value != b.value) {
            return Long.compare(a.value, b.value);
        }

        if (a.ones != b.ones) {
            return Integer.compare(a.ones, b.ones);
        }

        if (a.binary.length() != b.binary.length()) {
            return Integer.compare(a.binary.length(), b.binary.length());
        }

        return a.binary.compareTo(b.binary);
    }

    private static void runSortTests() {
        String[] examples = { "101101", "110011", "10010000", "1100000", "11111" };
        for (int i = 0; i < examples.length; i++) {
            Data item = new Data(examples[i]);
            int[] expectedOnes = { 4, 4, 2, 2, 5 };
            if (item.value != (i == 4 ? 25 : 24) || item.ones != expectedOnes[i]) {
                throw new AssertionError("Incorrect Data fields for " + examples[i]);
            }
        }
        checkSort(examples, new String[] { "1100000", "10010000", "101101", "110011", "11111" });
        checkSort(new String[] {}, new String[] {});
        checkSort(new String[] { "1" }, new String[] { "1" });
        checkSort(new String[] { "001", "1", "01", "0", "00", "1" },
                new String[] { "0", "00", "1", "1", "01", "001" });
        System.out.println("All Data and sorting tests passed");
    }

    private static void checkSort(String[] input, String[] expected) {
        sort(input);
        if (!Arrays.equals(input, expected)) {
            throw new AssertionError("Unexpected sort result: " + Arrays.toString(input));
        }
    }

    private static class SortingCompetitionComparator implements Comparator<String> {
        private static int toVal(String seq) {
            int result = 0;
            int n = seq.length();
            int j = 1;
            for (int i = n - 1; i >= 0; i--) {
                if (seq.charAt(i) == '1') {
                    result += 2 * j - 1;
                }
                j++;
            }

            return result;
        }

        private static int countOnes(String seq) {
            int count = 0;
            int n = seq.length();
            for (int i = n - 1; i >= 0; i--) {
                if (seq.charAt(i) == '1') {
                    count++;
                }
            }

            return count;
        }

        @Override
        public int compare(String seq1, String seq2) {
            int val1 = toVal(seq1);
            int val2 = toVal(seq2);
            int count1 = countOnes(seq1);
            int count2 = countOnes(seq2);

            if (val1 != val2) {
                return val1 - val2; // Values are different
            }

            if (count1 != count2) {
                return count1 - count2; // # of ones are different
            }

            if (seq1.length() != seq2.length()) {
                return seq1.length() - seq2.length(); // Strings are different lengths
            }

            return seq1.compareTo(seq2);
        }

        public static void runComparatorTests() {
            String str24a = "101101";
            String str24b = "110011";
            String str24c = "10010000";
            String str24d = "1100000";
            String str25 = "11111";
            System.out.println(toVal(str24a)); // Should get 1 + 5 + 7 + 11 = 24
            System.out.println(toVal(str24b)); // Should get 1 + 3 + 9 + 11 = 24
            System.out.println(toVal(str24c)); // Should get 9 + 15 = 24
            System.out.println(toVal(str24d)); // Should get 13 + 11 = 24
            System.out.println(toVal(str25)); // Should get 1 + 3 + 5 + 7 + 9 = 25

            System.out.println("Comparing arr1 and arr2");
            System.out.println((new SortingCompetitionComparator()).compare(str24a, str25)); // Neg
            System.out.println((new SortingCompetitionComparator()).compare(str24a, str24c)); // Pos
            System.out.println((new SortingCompetitionComparator()).compare(str24a, str24b)); // Neg
            System.out.println((new SortingCompetitionComparator()).compare(str24c, str24d)); // Pos
        }
    }

    private static void writeOutResult(String[] sorted, String outputFilename) throws FileNotFoundException {
        PrintWriter out = new PrintWriter(outputFilename);
        for (String s : sorted) {
            out.println(s);
        }
        out.close();
    }
}