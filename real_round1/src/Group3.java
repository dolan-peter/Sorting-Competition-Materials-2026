import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Scanner;

import javax.xml.crypto.Data;

// To run on a single core, compile and then run as:
// taskset -c 0 java GroupN
// To avoid file reading/writing connections to the server, run in /tmp 
// of your lab machine.

public class Group3 {

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
	private static double cdf(double x, double shortLen, double longLen) {
		double a = shortLen;
		double b = longLen;
		if (x <= 4.0) return 0.0;
		if (x < a) return 0.25 * Math.pow((x - 4.0) / (a - 4.0), 3.0);
		if (x < b) return 0.25 + 0.50 * (x - a) / (b - a);
		return 0.75 + 0.25 * (1.0 - Math.exp(-(x - b) / (b - a)));
	}

	private static void sort(String [] toSort) {
		Data[] data = new Data[toSort.length];
		//caculate each string once
		for(int i = 0; i < toSort.length; i++){
			data[i] = new Data(toSort[i]);
		}
		
		int n = data.length; //number of strings
		int bSize = 4; //desired average number of strings in each bucket
		int B = Math.max(1, n / bSize); //number of buckets

		ArrayList<Data>[] buckets = new ArrayList[B];
		for (int i = 0; i < B; i++) buckets[i] = new ArrayList<>();

		//filtering into appropriate buckets based on estimated lengths
		for (Data d : data) {
			double apxLen = Math.sqrt(2.0 * d.value); //approximate length using the value of the string
			int b = (int) (cdf(apxLen, 500.0, 1000.0) * B); //map the approximate length to a bucket
			if (b >= B) b = B - 1; //handle the edge case where b is on the bound
			buckets[b].add(d); //put string s into bucket b
		}

		//sort each bucket and then overwrite original array with new values from each bucket, in order
		int i = 0;
		for (ArrayList<Data> bucket : buckets) { //iterate through each bucket
			insertionSort(bucket); //sorts small bucket, now with ArrayList's timSort with our comparator rules
			for (Data d : bucket) {
				toSort[i] = d.text; //overwrite index i in the original array with the new appropriate value
				i = i + 1;
			}
		}
	}

	private static void insertionSort(ArrayList<Data> d){
		for(int i = 1; i < d.size(); i++){
			Data key = d.get(i);
			int j = i - 1;
			while(j >= 0 && compareData(d.get(j), key) > 0){
				d.set(j + 1, d.get(j));
				j--;
			}
			d.set(j + 1, key);
		}
	}

	private static int compareData(Data d1, Data d2) {
		if (d1.value != d2.value) {
			return (int) (d1.value - d2.value);
		}
		if (d1.ones != d2.ones) {
			return d1.ones - d2.ones;
		}
		if (d1.text.length() != d2.text.length()) {
			return d1.text.length() - d2.text.length();
		}
		return d1.text.compareTo(d2.text);
	}

	private static class Data {
    	String text;
        long value;
        int ones;

        Data(String text) {
        	this.text = text;
    		// right to left,start from 0
        	for (int i = 0; i < text.length(); i++) {
            	if (text.charAt(text.length() - 1 - i) == '1') {
                	value += 2L * i + 1;
                	ones++;
            	}
        	}
    	}
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
