import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Random;

/*
 * Generates a specified number of permutations (1 per line) 
 * of a random number of integers
 *
 * The length is between 3 and 9.
 */

public class DataGenerator {
	private static int seed = 4242; // change the seed to get different data
	private static Random r = new Random(seed);
	private static double  shortLen = 5;
	private static double  longLen  = 8;

        public static String arrayToString(List<Integer> arr) { //Borrowed from https://www.geeksforgeeks.org/convert-array-of-integers-to-comma-separated-string/#
            // Variable to store the answer
    	    StringBuilder answer = new StringBuilder();
            int N = arr.size();
	    // Iterate over all the elements in arr[]
            for (int i = 0; i < N; i++) {
            	answer.append(arr.get(i));
	        if (i < N - 1) {
        	    answer.append(",");
                } 
            }
            return answer.toString();
        } 
	public static double generateCustomRandom() {
		double a = shortLen;
		double b = longLen;

        if (a <= 4.0) {
            throw new IllegalArgumentException("Parameter 'a' must be greater than 4.");
        }
        if (b <= a) {
            throw new IllegalArgumentException("Parameter 'b' must be greater than 'a'.");
        }

        double u = r.nextDouble();

        if (u < 0.25) {
            // 1. Lower Tail (25% chance): values between 4.0 and 'a'
            // Rescale u to [0, 1)
            double uLower = u / 0.25; 
            
            // Power curve (u^0.33) concentrates density near 'a' 
            // and drops sharply toward 4.0
            return 4.0 + (a - 4.0) * Math.pow(uLower, 1.0 / 3.0);

        } else if (u < 0.75) {
            // 2. Middle Interval (50% chance): values in [a, b]
            // Rescale u to [0, 1)
            double uMid = (u - 0.25) / 0.50; 
            
            return a + uMid * (b - a);

        } else {
            // 3. Upper Long Tail (25% chance): values > b
            // Rescale u to [0, 1)
            double uUpper = (u - 0.75) / 0.25; 
            
            // Exponential scale factor controls tail stretch (scaled relative to range)
            double scale = b - a; 
            
            // Inverse CDF of Exponential distribution: b - scale * ln(1 - u)
            return b - scale * Math.log(1.0 - uUpper);
        }
    }

	public static void main(String[] args) throws FileNotFoundException {

		if (args.length < 2) {
			System.out.println(
					"Please run with three command line arguments: output file name and the number of items");
			System.exit(0);
		}
		String outFileName = args[0];
		int n = Integer.parseInt(args[1]);		
		
		PrintWriter out = new PrintWriter(outFileName);
		
		for(int i = 0; i < n; ++i) {
			int m = (int) Math.ceil(generateCustomRandom());
			// generate string
			char[] chars = new char[m];
			chars[0] = '1';
			for(int j=1; j< m; j++){				
				chars[j] = r.nextBoolean()? '1': '0';
			}			
			
			out.println(chars);
		}
		out.close();
	}
	


}
