import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Scanner;

// To run on a single core, compile and then run as:
// taskset -c 0 java GroupN
// To avoid file reading/writing connections to the server, run in /tmp 
// of your lab machine.

public class Group0 {

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

		sort(toSort); // sort again

		long end = System.currentTimeMillis();

		System.out.println(end - start);

		writeOutResult(toSort, outFileName); // write out the results

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

	// YOUR SORTING METHOD GOES HERE.
	// You may call other methods and use other classes.
	// Note: you may change the return type of the method.
	// You would need to provide your own function that prints your sorted array to
	// a file in the exact same format that my program outputs
	private static void sort(String [] toSort) {
		Arrays.sort(toSort, new SortingCompetitionComparator());
	}

	private static class SortingCompetitionComparator implements Comparator<String> {
		private static int toVal(String seq){
			int result = 0;
			int n = seq.length();
			int j = 1;
			for(int i = n-1; i>=0; i--){
				if(seq.charAt(i)=='1'){
					result += 2*j-1;
				}
				j++;				
			}

			return result;
		}
		private static int countOnes(String seq){
			int count = 0;
			int n = seq.length();
			for(int i = n-1; i>=0; i--){
				if(seq.charAt(i)=='1'){
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

			if(val1 != val2){
				return val1 - val2; // Values are different
			} 
			
			if(count1 != count2){
				return count1 - count2; // # of ones are different
			} 

			if(seq1.length() != seq2.length()){
				return seq1.length() - seq2.length(); // Strings are different lengths
			}

			return seq1.compareTo(seq2);
		}

		public static void runComparatorTests() {
			String str24a = "101101";
			String str24b = "110011";
			String str24c = "10010000"; 
			String str24d = "1100000"; 
			String str25  = "11111";
			System.out.println(toVal(str24a)); // Should get 1 + 5 + 7 + 11   = 24
			System.out.println(toVal(str24b)); // Should get 1 + 3 + 9 + 11   = 24			 		
			System.out.println(toVal(str24c)); // Should get 9 + 15           = 24			 		
			System.out.println(toVal(str24d)); // Should get 13 + 11          = 24			 		
			System.out.println(toVal(str25)); // Should get 1 + 3 + 5 + 7 + 9 = 25

			System.out.println("Comparing arr1 and arr2");
			System.out.println((new SortingCompetitionComparator()).compare(str24a, str25)); //Neg	
			System.out.println((new SortingCompetitionComparator()).compare(str24a, str24c)); //Pos
			System.out.println((new SortingCompetitionComparator()).compare(str24a, str24b)); //Neg
			System.out.println((new SortingCompetitionComparator()).compare(str24c, str24d)); //Pos
		}		
	}
	
	private static void writeOutResult(String [] sorted, String outputFilename) throws FileNotFoundException {
		PrintWriter out = new PrintWriter(outputFilename);
		for (String s : sorted) {			
			out.println(s);
		}
		out.close();
	}		
}
