
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
//Anton and Nursultan
public class Group4 {

    public static void main(String[] args)
            throws InterruptedException, FileNotFoundException {

        if (args.length < 2) {
            System.out.println(
                    "Running tests since input and output file names not specified");
            SortingCompetitionComparator.runComparatorTests();
            System.exit(0);
        }

        String inputFileName = args[0];
        String outFileName = args[1];

        String[] data = readData(inputFileName);
        String[] toSort = data.clone();

        sort(toSort);

        toSort = data.clone();

        Thread.sleep(10);

        long start = System.currentTimeMillis();
        sort(toSort);
        long end = System.currentTimeMillis();

        System.out.println(end - start);

        writeOutResult(toSort, outFileName);
    }

    private static String[] readData(String inputFileName)
            throws FileNotFoundException {

        ArrayList<String> input = new ArrayList<>();
        Scanner in = new Scanner(new File(inputFileName));

        while (in.hasNext()) {
            input.add(in.next());
        }

        in.close();

        return input.toArray(new String[0]);
    }

    private static void sort(String[] data) {

        long[] sums = new long[data.length];
        int[] ones = new int[data.length];
        int[] lengths = new int[data.length];

        for (int i = 0; i < data.length; i++) {

            String s = data[i];
            lengths[i] = s.length();

            long sum = 0;
            int count = 0;
            int position = 1;

            for (int j = s.length() - 1; j >= 0; j--) {

                if (s.charAt(j) == '1') {
                    sum += 2L * position - 1;
                    count++;
                }

                position++;
            }

            sums[i] = sum;
            ones[i] = count;
        }

        Integer[] indexes = new Integer[data.length];

        for (int i = 0; i < indexes.length; i++) {
            indexes[i] = i;
        }

        Arrays.sort(indexes, (a, b) -> {

            int result = Long.compare(sums[a], sums[b]);

            if (result != 0) {
                return result;
            }

            result = Integer.compare(ones[a], ones[b]);

            if (result != 0) {
                return result;
            }

            result = Integer.compare(lengths[a], lengths[b]);

            if (result != 0) {
                return result;
            }

            return data[a].compareTo(data[b]);
        });

        String[] sorted = new String[data.length];

        for (int i = 0; i < indexes.length; i++) {
            sorted[i] = data[indexes[i]];
        }

        System.arraycopy(sorted, 0, data, 0, data.length);
    }

    private static void writeOutResult(
            String[] sorted, String outputFilename)
            throws FileNotFoundException {

        PrintWriter out = new PrintWriter(outputFilename);

        for (String s : sorted) {
            out.println(s);
        }

        out.close();
    }

    private static class SortingCompetitionComparator
            implements java.util.Comparator<String> {

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

            for (int i = seq.length() - 1; i >= 0; i--) {
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

            if (val1 != val2) {
                return Integer.compare(val1, val2);
            }

            int count1 = countOnes(seq1);
            int count2 = countOnes(seq2);

            if (count1 != count2) {
                return Integer.compare(count1, count2);
            }

            if (seq1.length() != seq2.length()) {
                return Integer.compare(seq1.length(), seq2.length());
            }

            return seq1.compareTo(seq2);
        }

        public static void runComparatorTests() {

            String str24a = "101101";
            String str24b = "110011";
            String str24c = "10010000";
            String str24d = "1100000";
            String str25 = "11111";

            System.out.println(toVal(str24a));
            System.out.println(toVal(str24b));
            System.out.println(toVal(str24c));
            System.out.println(toVal(str24d));
            System.out.println(toVal(str25));

            System.out.println("Comparing arr1 and arr2");
            System.out.println(
                    (new SortingCompetitionComparator()).compare(str24a, str25));
            System.out.println(
                    (new SortingCompetitionComparator()).compare(str24a, str24c));
            System.out.println(
                    (new SortingCompetitionComparator()).compare(str24a, str24b));
            System.out.println(
                    (new SortingCompetitionComparator()).compare(str24c, str24d));
        }
    }
}

