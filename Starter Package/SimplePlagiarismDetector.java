import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class SimplePlagiarismDetector {
	private ArrayList<String> intersect(ArrayList<String> S1,
			ArrayList<String> S2) {
		ArrayList<String> result = new ArrayList<>();

		for (String t1 : S1) {
			for (String t2 : S2) {
				if (t1.equals(t2))
					if(!result.contains(t1))
					result.add(t1);
			}
		}

		return result;
	}

	private ArrayList<String> union(ArrayList<String> S1, ArrayList<String> S2) {

		HashMap<String, Integer> map = new HashMap<>();

		for(String t1 : S1)
			map.put(t1, 1);

		for(String t2 : S2) {
			if(!map.containsKey(t2))
				map.put(t2, 1);
		}

		Set<String> keys = map.keySet();
		ArrayList<String> result = new ArrayList<>();

		for(String k : keys)
			result.add(k);

		return result;
	}

	private Map<String, Integer> occurences(ArrayList<String> S) {
		Map<String, Integer> table = new HashMap<>();

		for(String term : S) {
			if(!table.containsKey(term))
				table.put(term, 1);
			else
				table.put(term, table.get(term) + 1);
		}

		return table;
	}

	// computes the unweighted similarity between the two lists of terms
	private float getUnweightedNGramSimilarity(ArrayList<String> x, ArrayList<String> y) {
			return (float)intersect(x, y).size() / union(x, y).size();
    }

	// computes the weighted similarity between the two lists of terms
	private float getWeightedBGramSimilarity(ArrayList<String> x, ArrayList<String> y) {
		Map<String, Integer> x_occurences = occurences(x),
													y_occurences = occurences(y);

		int dom = 0, nom = 0;
		ArrayList<String> xUy = union(x, y);

		for(String t : xUy) {
			Integer xt_temp = x_occurences.get(t),
			 	yt_temp = y_occurences.get(t);

			int xt = xt_temp != null? xt_temp : 0, yt = yt_temp != null? yt_temp : 0;

			dom += Math.min(xt, yt);
			nom += Math.max(xt, yt);
		}

		return (float)dom/nom;
	}

	//returns list of n-grams given unigrams
	private ArrayList<String> getNGrams(ArrayList<String> a, int n) {

		ArrayList<String> nGrams = new ArrayList<>();

		for(int i = 0; i < a.size() - n + 1; i++) {
			StringBuilder term = new StringBuilder(a.get(i));
			int len = 1; //the first one is already added

			while(len < n) {
				term.append(' ');
				term.append(a.get(i + len));
				len++;
			}

			nGrams.add(term.toString());
		}

		return nGrams;
	}

	// returns a list of unigrams by loading a file given its path
	private ArrayList<String> loadUnigramsFromFile(String fname) {
		Scanner reader;
		ArrayList<String> ret = new ArrayList<>();
		try {
			reader = new Scanner(new File(fname));

		} catch (FileNotFoundException e) {
			return null;
		}

		while(reader.hasNext()){
			ret.add(reader.next().toLowerCase());//to make the detector case insensitive
		}
		reader.close();

		return ret;
	}

	// returns a list of lines by loading a file given its path
	private ArrayList<String> loadLinesFromFile(String fname) {
		Scanner reader;
		ArrayList<String> ret = new ArrayList<>();
		try {
			reader = new Scanner(new File(fname));

		} catch (FileNotFoundException e) {
			return null;
		}

		while (reader.hasNextLine()) {
			ret.add(reader.nextLine().toLowerCase());//to make the detector case insensitive
		}
		reader.close();

		return ret;
	}

	//computes the n-gram similarity between two files, given n and the weighting option of similarity function for both words and lines options
	//xFileName: path to the first file
	//yFileName: path to the second file
	//isLine: False means word n-grams. True means line n-grams.
	//n: n-gram terms
	//weighted: true: use weighted similarity, false: use unweighted similarity
	public float getSimilarity(String xFileName, String yFileName, boolean isLine, int n, boolean weighted){
		//>>>> DO NOT CHANGE <<<<<
		ArrayList<String> terms = null;
		if(!isLine)
			terms = loadUnigramsFromFile(xFileName);
		else
			terms= loadLinesFromFile(xFileName);
		ArrayList<String> x = getNGrams(terms, n);
		System.out.println("List of x = "+x);
		if(!isLine)
			terms = loadUnigramsFromFile(yFileName);
		else
			terms= loadLinesFromFile(yFileName);
		ArrayList<String> y = getNGrams(terms, n);
		System.out.println("The list of y = "+y);
		if(!weighted)
			return getUnweightedNGramSimilarity(x, y);
		return getWeightedBGramSimilarity(x, y);
	}

	//main function showing examples of using the class
	//Change as you test your code
	public static void main(String[] args) {
		String xFileName = "x.txt";
		String yFileName = "y.txt";

		SimplePlagiarismDetector pd = new SimplePlagiarismDetector();

		//using word n-grams, n = 2, weighted similarity
		System.out.println(pd.getSimilarity(xFileName, yFileName, false, 2, true));

		//using word n-grams, n = 3, unweighted similarity
		System.out.println(pd.getSimilarity(xFileName, yFileName, false, 3, false));

		//using line n-grams, n = 1, unweighted similarity
		System.out.println(pd.getSimilarity(xFileName, yFileName, true, 1, false));

		//Q1
		Formatter writer=null;
		try {
			writer=new Formatter("OutputQ1.csv");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("Q 1:");
		boolean isLine=false, weighted=false;
		for(int i=0; i<4; i++){
			switch(i){
			case 0:
				isLine=false;
				weighted=false;
				break;
			case 1:
				isLine=false;
				weighted=true;
				break;
			case 2:
				isLine=true;
				weighted=false;
				break;
			case 3:
				isLine=true;
				weighted=true;
			}

			System.out.printf("Is Line: %s, Is weighted : %s \n",isLine,weighted);
			writer.format("Is Line: %s Is weighted : %s \n",isLine,weighted);
			for(int j=1; j<=4;j++){
				System.out.print("n =" + j);
				float n;
				System.out.println(n=pd.getSimilarity(xFileName, yFileName, isLine, j, weighted));
				writer.format("%d,%.2f\n", j,n);
			}
		}
		writer.close();

	}
}
