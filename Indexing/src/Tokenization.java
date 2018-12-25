package rdb170002_Homework2;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

class Tokenization {
	// To count the run time
	static long startTimer = System.currentTimeMillis();
	int doclen = 0;
	TreeMap<String, Integer> dictionary;
	TreeMap<String, Integer> stem_dictionary = new TreeMap<String, Integer>();
	static int substringArrayLength = 0;
	static Stemmer s = new Stemmer();
	static File[] files;
	static StanfordLemmatizer objlemma= new StanfordLemmatizer();
	
	public static void main(String args[]) throws Exception {
		String directoryPath;
		if(args.length == 0)
			directoryPath = "/people/cs/s/sanda/cs6322/Cranfield";
		else
			directoryPath = (args[0]).toString();
		// pass the path to the file as a parameter
		File file = new File(directoryPath);
		files = file.listFiles();
		for (File f : files) {
			// Convert file content to String
			convertFileToString(f);
		}
		// To display the final result
		finalResult();

	}

	public void finalResult() {
		// Tokenization result
		//printDictionary(dictionary);
		System.out.println("Program 1. Tokenization");
		System.out.println("1. The number of tokens in the Cranfield text collection : " + substringArrayLength);
		System.out.println("2. The number of unique words in the Cranfield text collection : " + dictionary.size());
		System.out.println("3. The number of words that occur only once in the Cranfield text collection : " + appearedOnlyOnce(dictionary));
		System.out.println("4. The 30 most frequent words in the Cranfield text collection : ");
				
		frequentlyUsed(dictionary);
		
		System.out.println("5. The average number of word stems per document : " + substringArrayLength/files.length);
		
		System.out.println();
		
		// Stemming result
		//printDictionary(stem_dictionary);
		System.out.println("Program 2. Stemming");
		System.out.println("1. The number of distinct stems in the Cranfield text collection : " + stem_dictionary.size());
		System.out.println("2. The number of stems that occur only once in the Cranfield text collection : " + appearedOnlyOnce(stem_dictionary));
		System.out.println("3. The 30 most frequent stems in the Cranfield text collection : ");
		frequentlyUsed(stem_dictionary);
		
		System.out.println("4. The average number of word stems per document : " + substringArrayLength/files.length);
		
		System.out.println();
		long endTimer = System.currentTimeMillis();

		System.out.println("Execution time : " + (endTimer - startTimer) + " milliseconds");
	}

	// This function will convert file content to String
	public void convertFileToString(File f) throws Exception {
		dictionary = new TreeMap<String, Integer>();
		BufferedReader br = new BufferedReader(new FileReader(f));
		StringBuilder sb = new StringBuilder();
		String s;
		while ((s = br.readLine()) != null) {
			sb.append(s);
			sb.append("\n");
		}

		convertStringtoArray(sb.toString());
		br.close();
	}

	// This function will convert String into array using split function
	public void convertStringtoArray(String sb) {
		sb = sb.toLowerCase().replaceAll("\\<.*?\\>", "");
		String[] substringArray = sb.split("[()=,!%\\s\\/-[+*]]");
		for (String i : substringArray) {
			tokenization(i);
		}
	}

	// For each word in String, tokens will be generated in this function
	public void tokenization(String s) {
		// if word ends with 's
		if (s.endsWith("'s"))
			s = s.replace("'s", "");
				
		//replace everything other than a-z to null
		s = s.replaceAll("[^a-z]", "");
		
		// for any other words
		if (!s.equals("")) {
			// Passing final token for stemming
			callStemmerClass(s);
			//to count total number of words in all documents
			substringArrayLength++;
			addToDictionary(s,dictionary);
		}
	}

	// This function will add tokens/stems into dictionary
	public void addToDictionary(String token, TreeMap<String, Integer> dictionary) {
		
		doclen++;
			token = objlemma.lemmatize(token);
			if (dictionary.containsKey(token))
				dictionary.put(token, dictionary.get(token) + 1);
			else {
				dictionary.put(token, 1);
			}
		}


	// This function will sort dictionaries in descending order of values.
	public static <K, V extends Comparable<? super V>> Map<K, V> sortDictionaryByValues(Map<K, V> dictionary) {
		List<Map.Entry<K, V>> list = new ArrayList<>(dictionary.entrySet());
		list.sort(Map.Entry.comparingByValue((s1, s2) -> s2.compareTo(s1)));
		Map<K, V> sorted_dictionary = new LinkedHashMap<>();
		for (Map.Entry<K, V> entry : list) 
			sorted_dictionary.put(entry.getKey(), entry.getValue());
		return sorted_dictionary;
	}
	
	//This function will help to get 30 most frequently used token/stems.
	public static void frequentlyUsed(TreeMap<String, Integer> dictionary) {
		int counter = 0;
		Map<String, Integer> sortedMap = sortDictionaryByValues(dictionary);
		for (Map.Entry<String, Integer> entry : sortedMap.entrySet()) {
			System.out.println(entry.getKey() + " " + entry.getValue());
			counter++;
			if (counter == 30)
				break;
		}
	}
	
	//This function will get the tokens/stems appeared only once in the dictionary
	public static int appearedOnlyOnce(TreeMap<String, Integer> dictionary) {
		int onlyOnce = 0;
		for (String values : dictionary.keySet()) {
			if (dictionary.get(values) == 1)
				onlyOnce += 1;	 
		}	
		return onlyOnce;
	}
	
	//This function will call the Stemmer.java and will get the stem of the token
	public void callStemmerClass(String token) {
		s.add(token.toCharArray(), token.toCharArray().length);
		s.stem();
		addToDictionary(s.toString(),stem_dictionary);
	}
	
	//This function will print the dictionary
	public static void printDictionary(TreeMap<String, Integer> dictionary) {
		for (String values : dictionary.keySet()) {
			String key = values.toString(); 
			String value = dictionary.get(values).toString(); 
			System.out.println(key + " : " +value);
		}
	}
}