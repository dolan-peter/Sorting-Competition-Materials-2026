import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Scanner;

// Group 6
// Partners: Gabe and Tyler
// This file also needs BinaryNum.java, which is submitted along with it. Compile both together.

public class Group6 {

        public static void main(String[] args) throws InterruptedException, FileNotFoundException {

                if (args.length < 2) {
                        System.out.println(
                                        "Running tests since input and output file names not specified");
                        SortingCompetitionComparator.runComparatorTests();
                        System.exit(0);
                }

                String inputFileName = args[0];
                String outFileName   = args[1];

                // Uncomment to test comparator methods

                String [] data = readData(inputFileName); // read data as strings

                String [] toSort = data.clone(); // clone the data

                sort(toSort); // call the sorting method once for JVM warmup

                toSort = data.clone(); // clone again

                Thread.sleep(10); // to let other things finish before timing; adds stability of runs

                long start = System.currentTimeMillis();

                BinaryNum[] sorted = sort(toSort); // sort again

                long end = System.currentTimeMillis();

                System.out.println(end - start);

                writeOutResult(sorted, outFileName); // write out the results

        }


        private static String [] readData(String inputFileName) throws FileNotFoundException {
                ArrayList<String> input = new ArrayList<>();
                Scanner in = new Scanner(new File(inputFileName));

                while (in.hasNext()) {
                        input.add(in.next());
                }

                in.close();

                // the string array is passed just so that the correct type can be created
                return input.toArray(new String [0]);
        }

        // calculates the value, number of 1's and length of every string once, and stores them
        // in one BinaryNum object per string; the objects are returned in a new array
        private static BinaryNum[] calculateValues(String[] toSort) {
                BinaryNum[] binaryNums = new BinaryNum[toSort.length];

                for (int i = 0; i < toSort.length; i++) {
                        String seq = toSort[i];
                        int result = 0;
                        int numberOfOnes = 0;
                        int n = seq.length();
                        int j = 1;

                        for(int k = n-1; k>=0; k--){
                                if(seq.charAt(k)=='1'){
                                        result += 2*j-1;
                                        numberOfOnes++;
                                }
                                j++;
                        }

                        // create the new object to hold the three values and store it in the array
                        BinaryNum objVar = new BinaryNum(seq, result, numberOfOnes, n);
                        binaryNums[i] = objVar;
                }
                return binaryNums;
        }

        // YOUR SORTING METHOD GOES HERE.
        // You may call other methods and use other classes.
        // Note: you may change the return type of the method.
        // You would need to provide your own function that prints your sorted array to
        // a file in the exact same format that my program outputs
        private static BinaryNum[] sort(String [] toSort) {
                BinaryNum[] nums = calculateValues(toSort); // Calculate values for each binary string and store them in BinaryNum objects
                Arrays.sort(nums, new SortingCompetitionComparator());
                return nums;
        }

        private static class SortingCompetitionComparator implements Comparator<BinaryNum> {

                // The values were already calculated once in calculateValues, so we only compare
                // the stored numbers here 
                @Override
                public int compare(BinaryNum obj1, BinaryNum obj2) {
                        if(obj1.value != obj2.value){
                                return obj1.value - obj2.value; // Values are different
                        }

                        if(obj1.numberOfOnes != obj2.numberOfOnes){
                                return obj1.numberOfOnes - obj2.numberOfOnes; // # of ones are different
                        }

                        if(obj1.length != obj2.length){
                                return obj1.length - obj2.length; // Strings are different lengths
                        }

                        return obj1.binaryString.compareTo(obj2.binaryString);
                }

                public static void runComparatorTests() {
                        String str24a = "101101";
                        String str24b = "110011";
                        String str24c = "10010000";
                        String str24d = "1100000";
                        String str25  = "11111";

                        BinaryNum[] nums = calculateValues(new String[] {str24a, str24b, str24c, str24d, str25});

                        System.out.println(nums[0].value); // Should get 1 + 5 + 7 + 11   = 24
                        System.out.println(nums[1].value); // Should get 1 + 3 + 9 + 11   = 24
                        System.out.println(nums[2].value); // Should get 9 + 15           = 24
                        System.out.println(nums[3].value); // Should get 13 + 11          = 24
                        System.out.println(nums[4].value); // Should get 1 + 3 + 5 + 7 + 9 = 25

                        System.out.println("Comparing arr1 and arr2");
                        System.out.println((new SortingCompetitionComparator()).compare(nums[0], nums[4])); //Neg
                        System.out.println((new SortingCompetitionComparator()).compare(nums[0], nums[2])); //Pos
                        System.out.println((new SortingCompetitionComparator()).compare(nums[0], nums[1])); //Neg
                        System.out.println((new SortingCompetitionComparator()).compare(nums[2], nums[3])); //Pos
                }
        }

        private static void writeOutResult(BinaryNum [] sorted, String outputFilename) throws FileNotFoundException {
                PrintWriter out = new PrintWriter(outputFilename);
                for (BinaryNum s : sorted) {
                        out.println(s.binaryString);
                }
                out.close();
        }
}
