import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Scanner;

// To run on a single core, compile and then run as:
// taskset -c 0 java GroupN
// To avoid file reading/writing connections to the server, run in /tmp 
// of your lab machine.

public class Group1 {

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

		writeOutResult(toSort, outFileName);
		System.out.println(end - start);

		
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
		SortEntry[] entries = new SortEntry[toSort.length];
		for (int i = 0; i < toSort.length; i++) {
			entries[i] = new SortEntry(toSort[i]);
		}

		mergeSort(entries, new SortEntry[entries.length], 0, entries.length,
				new SortingCompetitionComparator());
		for (int i = 0; i < toSort.length; i++) {
			toSort[i] = entries[i].sequence;
		}
	}

	private static void mergeSort(SortEntry[] values, SortEntry[] buffer, int start, int end,
			Comparator<SortEntry> comparator) {
		if (end - start < 2) {
			return;
		}

		int middle = start + (end - start) / 2;
		mergeSort(values, buffer, start, middle, comparator);
		mergeSort(values, buffer, middle, end, comparator);

		int left = start;
		int right = middle;
		int destination = start;
		while (left < middle && right < end) {
			if (comparator.compare(values[left], values[right]) <= 0) {
				buffer[destination++] = values[left++];
			} else {
				buffer[destination++] = values[right++];
			}
		}
		while (left < middle) {
			buffer[destination++] = values[left++];
		}
		while (right < end) {
			buffer[destination++] = values[right++];
		}
		System.arraycopy(buffer, start, values, start, end - start);
	}

	private static class SortEntry {
		private final String sequence;
		private final long value;
		private final int ones;
		private final int length;

		private SortEntry(String sequence) {
			this.sequence = sequence;
			this.length = sequence.length();

			long computedValue = 0;
			int computedOnes = 0;
			int position = 1;
			for (int i = length - 1; i >= 0; i--, position++) {
				if (sequence.charAt(i) == '1') {
					computedValue += 2L * position - 1;
					computedOnes++;
				}
			}
			this.value = computedValue;
			this.ones = computedOnes;
		}
	}

	private static class SortingCompetitionComparator implements Comparator<SortEntry> {
		@Override
		public int compare(SortEntry first, SortEntry second) {
			int result = Long.compare(first.value, second.value);
			if (result != 0) {
				return result;
			}

			result = Integer.compare(first.ones, second.ones);
			if (result != 0) {
				return result;
			}

			result = Integer.compare(first.length, second.length);
			if (result != 0) {
				return result;
			}

			return first.sequence.compareTo(second.sequence);
		}

		public static void runComparatorTests() {
		}
	}

	private static void writeOutResult(String[] sorted, String outputFilename)
			throws FileNotFoundException {
		try (PrintWriter out = new PrintWriter(outputFilename)) {
			for (String sequence : sorted) {
				out.println(sequence);
			}
		}
	}
}
