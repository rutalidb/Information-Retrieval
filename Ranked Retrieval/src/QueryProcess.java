package rdb170002_Homework3;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.regex.Pattern;

class QueryProcess {

	static File[] files;
	static HashSet<String> stopWordlist;

	ArrayList<Query> query_dictionaries = new ArrayList<Query>();
	TreeMap<String,Double> W1;
	TreeMap<String,Double> W2;
	TreeMap<Integer, TreeMap<String, Double>> DocumentTerm_W1 = new TreeMap<Integer, TreeMap<String, Double>> ();;
	TreeMap<Integer, TreeMap<String, Double>> DocumentTerm_W2 = new TreeMap<Integer, TreeMap<String, Double>> ();;
	TreeMap<Integer, TreeMap<Integer, Double>> Cosine_W1 = new TreeMap<Integer, TreeMap<Integer, Double>>();;
	TreeMap<Integer, TreeMap<Integer, Double>> Cosine_W2 = new TreeMap<Integer, TreeMap<Integer, Double>>();;
	static TreeMap<Integer, documentIdentifier> fileHeadline = new TreeMap<Integer, documentIdentifier> ();

	static double avgdoclen = 0;
	
	public static void main(String args[]) throws Exception {
		long startTimer;
		long endTimer;
		startTimer = System.currentTimeMillis();
		String directoryPath;
		
		if(args.length == 0) 
			  directoryPath = "/people/cs/s/sanda/cs6322/Cranfield/";
		  else 
			  directoryPath = (args[0]).toString();
		 
		File stopWords = new File(args[1].toString());
		
		String QueryPath = args[2].toString();
		
		Indexing i = new Indexing();
		QueryProcess qp = new QueryProcess();
		Tokenization t = new Tokenization();
		qp.createStopWordsSet(stopWords);
		File file = new File(directoryPath);
		files = file.listFiles();
		for (File f : files) {
			t.convertFileToString(f);
		}
		
		qp.readyQuery(QueryPath);
		i.buildIndex(directoryPath);
		avgdoclen = i.totalDoclen / 1400;
				
		qp.computeDocumentWeights(directoryPath);
		int c = 0;
		for(Query q : qp.query_dictionaries) {
			qp.W1 = new TreeMap<String, Double>();
			qp.W2 = new TreeMap<String, Double>();
			c = c + 1;
			System.out.println("******************* Query " + c + " *******************");
			System.out.println();
			System.out.println("Vector Representation for " + "Query " + c + " - W1"); 
			System.out.println();
			qp.computeQueryWeights(q);
			for(String j : q.query_dictionary.keySet())
				System.out.println(String.format("%-15s%-20s",j,qp.W1.get(j)));
				System.out.println();
			
			System.out.println();
			System.out.println("Vector Representation for " + "Query " + c + " - W2"); 
			System.out.println();		
			for(String j : q.query_dictionary.keySet())
				System.out.println(String.format("%-15s%-20s",j,qp.W2.get(j)));
				System.out.println();
		
		qp.computeCosineSimilarity_W1(q);
		System.out.println();
		System.out.println("Calculated ranks for " + "Query " + c + " - W1");
		System.out.println();
		for(Integer j : qp.Cosine_W1.keySet()) {
			int counter = 0;
			Map<Integer, Double> sorted_doc = qp.sortByValues(qp.Cosine_W1.get(j));
			System.out.println(String.format("%-6s%-20s%-30s%-100s","Rank","Score","External Document Identifier","Headline"));
			for (Map.Entry<Integer, Double> entry : sorted_doc.entrySet()) {
				counter += 1;
				System.out.println(String.format("%-6s%-20s%-30s%-100s",counter,entry.getValue(),fileHeadline.get(entry.getKey()).filename,fileHeadline.get(entry.getKey()).headline));
				if (counter == 5)
					break;
			}
			System.out.println();
			System.out.println("Vector representation of top 5 documents: ");
			System.out.println();
			counter = 0;
			for (Map.Entry<Integer, Double> entry : sorted_doc.entrySet()) {
				System.out.println("Document: " + fileHeadline.get(entry.getKey()).filename);
				System.out.println(qp.DocumentTerm_W1.get(entry.getKey()));
				counter++;
				if (counter == 5)
					break;
			}
			
		}
		
		qp.computeCosineSimilarity_W2(q);
		System.out.println();
		System.out.println("Calculated ranks for " + "Query " + c + " - W2"); 
		System.out.println();
		for(Integer j : qp.Cosine_W2.keySet()) {
			int counter = 0;
			Map<Integer, Double> sorted_doc = qp.sortByValues(qp.Cosine_W2.get(j));
			System.out.println(String.format("%-6s%-20s%-30s%-100s","Rank","Score","External Document Identifier","Headline"));
			for (Map.Entry<Integer, Double> entry : sorted_doc.entrySet()) {
				counter += 1;
				System.out.println(String.format("%-6s%-20s%-30s%-100s",counter,entry.getValue(),fileHeadline.get(entry.getKey()).filename,fileHeadline.get(entry.getKey()).headline));
				if (counter == 5)
					break;
			}
			
			System.out.println();
			System.out.println("Vector representation of top 5 documents: ");
			System.out.println();
			counter = 0;
			for (Map.Entry<Integer, Double> entry : sorted_doc.entrySet()) {
				System.out.println("Document: " + fileHeadline.get(entry.getKey()).filename);
				System.out.println(qp.DocumentTerm_W2.get(entry.getKey()));
				counter++;
				if (counter == 5)
					break;
			}
		}
		System.out.println();
	}
		endTimer = System.currentTimeMillis();
		System.out.println("Total execution time: : " + (endTimer - startTimer) + " milliseconds");
		
}
	

	public void createStopWordsSet(File stopWords) throws IOException {
		stopWordlist = new HashSet<String>();
		Scanner scanner = new Scanner(stopWords);
		while (scanner.hasNext()) {
			stopWordlist.add(scanner.next());
		}
		scanner.close();
	}

	public void readyQuery(String QueryPath) throws IOException {
		File file = new File(QueryPath);
		List<String> query_dictionary = new ArrayList<String>();
		BufferedReader br = new BufferedReader(new FileReader(file));
		StringBuilder sb = new StringBuilder();
		String s;
		while ((s = br.readLine()) != null) {
			sb.append(s);
			sb.append("\n");
		}
		String[] queries = Pattern.compile("[Q0-9:]+").split(sb);
		for (String q : queries) {
			q = q.replaceAll("\\n", " ").trim();
			if (q.length() > 0)
				query_dictionary.add(q);
		}

		for (String q : query_dictionary) {
			Tokenization t = new Tokenization();
			t.convertStringtoArray(q);
						
			int max_tf = 0;
			for(String d : t.dictionary.keySet()) {
				if(t.dictionary.get(d) > max_tf) {
					max_tf = t.dictionary.get(d);
				}
			}
			Query q1 = new Query();
			for (String key : t.dictionary.keySet()) {
				QueryDictionary qd1 = new QueryDictionary(key, t.dictionary.get(key), t.doclen, max_tf);
				q1.query_dictionary.put(key, qd1);
			}
			query_dictionaries.add(q1);
		}
		br.close();
	}
		
	public void computeDocumentWeights(String directoryPath) throws Exception {
		File file = new File(directoryPath);
		files = file.listFiles();
		int docID = 0;
		for (File f : files) {			
			Tokenization t = new Tokenization();
			//get lemmatized dictionary
			docID += 1;
			t.convertFileToString(f);
			
			int max_tf = 0;
			for(String d : t.dictionary.keySet()) {
				if(t.dictionary.get(d) > max_tf) {
					max_tf = t.dictionary.get(d);
				}
			}
			
			for(String term : t.dictionary.keySet()) {
				Dictionary d1 = Indexing.invertedDictionary.get(term);
				if(d1 == null) {
					continue;
				}
				
				int docFrequency = d1.docFrequency;
				
				int doclen = t.substringArrayLength;
				int termFrequency = t.dictionary.get(term);
				
				updateWeights_document(term, docFrequency, max_tf, termFrequency, doclen, docID);
			
		}
		}
		
	}
	
	public void updateWeights_document(String term, int docFrequency, int max_tf, int termFrequency, int doclen, int docID) {
		//compute W1
		double weight = 0.0;
		double w1 = computeW1(termFrequency, max_tf, docFrequency);
		if (DocumentTerm_W1.get(docID) == null) {
			DocumentTerm_W1.put(docID, new TreeMap<String, Double>());
		}

		weight = DocumentTerm_W1.get(docID).containsKey(term) ? DocumentTerm_W1.get(docID).get(term) : 0.0;
		DocumentTerm_W1.get(docID).put(term, weight + w1);
		
		//compute W2
		double w2 = computeW2(termFrequency, doclen, avgdoclen, docFrequency);
		if (DocumentTerm_W2.get(docID) == null) {
			DocumentTerm_W2.put(docID, new TreeMap<String, Double>());
		}

		weight = DocumentTerm_W2.get(docID).containsKey(term) ? DocumentTerm_W2.get(docID).get(term) : 0.0;
		DocumentTerm_W2.get(docID).put(term, weight + w2);
	}
	
		
	
	public void computeQueryWeights(Query q) {
		for(String queryTerm : q.query_dictionary.keySet()) {
			Dictionary d1 = Indexing.invertedDictionary.get(queryTerm);
			if(d1 == null) {
				W1.put(queryTerm, 0.0);
				W2.put(queryTerm, 0.0);
				continue;
			}
			
			int docFrequency = d1.docFrequency;
			LinkedList<PostingList> posting_entry = d1.postings;
			
			for(PostingList term : posting_entry) {
				int max_tf = q.query_dictionary.get(queryTerm).max_tf;
				int termFrequency = q.query_dictionary.get(queryTerm).count;
				int doclen = q.query_dictionary.get(queryTerm).querylen;

				updateWeights_query(queryTerm, docFrequency, term, max_tf, termFrequency, doclen);
			}
		}
	}
	
	public void updateWeights_query(String queryTerm, int docFrequency,PostingList term, int max_tf, int termFrequency, int doclen) {
		double w1 = computeW1(termFrequency, max_tf, docFrequency);
		W1.put(queryTerm, w1);
		
		double w2 = computeW2(termFrequency, doclen, avgdoclen , docFrequency);
		W2.put(queryTerm, w2);
	}
	
	public void computeCosineSimilarity_W1(Query q) {
		TreeMap<Integer, Double> cs = new TreeMap<Integer, Double>();
		int i = 0;
			for(Integer docID : DocumentTerm_W1.keySet()) {
				double cosine_similarity = 0.0;
				double query_length = 0.0;
				double doc_length = 0.0;
				double cosineSimilarity = 0.0;
				for(String query_term : q.query_dictionary.keySet()) {
					TreeMap<String, Double> doc_W1 = DocumentTerm_W1.get(docID);

						if(doc_W1.containsKey(query_term)) {
							if(!doc_W1.get(query_term).equals(null) && !W1.get(query_term).equals(null)) {
								cosine_similarity += doc_W1.get(query_term) * W1.get(query_term);
								doc_length += doc_W1.get(query_term) * doc_W1.get(query_term);
								query_length += W1.get(query_term)* W1.get(query_term);
							}
						}
					double dl = Math.sqrt(doc_length);
					double ql = Math.sqrt(query_length);
					cosineSimilarity = cosine_similarity / (dl + ql);
					if(cosineSimilarity > 0.0)
						cs.put(docID, cosineSimilarity);
					else
						cs.put(docID, 0.0);
				}
			}Cosine_W1.put(++i, cs);
		}
	
	public void computeCosineSimilarity_W2(Query q) {
		TreeMap<Integer, Double> cs = new TreeMap<Integer, Double>();
		int i = 0;
			for(Integer docID : DocumentTerm_W2.keySet()) {
				double cosine_similarity = 0.0;
				double query_length = 0.0;
				double doc_length = 0.0;
				double cosineSimilarity = 0.0;
				for(String query_term : q.query_dictionary.keySet()) {
					TreeMap<String, Double> doc_W2 = DocumentTerm_W2.get(docID);

						if(doc_W2.containsKey(query_term)) {
							if(!doc_W2.get(query_term).equals(null) && !W2.get(query_term).equals(null)) {
								cosine_similarity += doc_W2.get(query_term) * W2.get(query_term);
								doc_length += doc_W2.get(query_term) * doc_W2.get(query_term);
								query_length += W2.get(query_term)* W2.get(query_term);
							}
						}
					double dl = Math.sqrt(doc_length);
					double ql = Math.sqrt(query_length);
					cosineSimilarity = cosine_similarity / (dl + ql);
					if(cosineSimilarity > 0.0)
						cs.put(docID, cosineSimilarity);
					else
						cs.put(docID, 0.0);
				}
			}Cosine_W2.put(++i, cs);
		}
	
	
	public double computeW1(int tf, int max_tf, int df) {
		double w1 = 0.0;
		//catch if divide by 0 occurs
		try {
		w1 = (0.4 + 0.6 * Math.log10(tf + 0.5) / Math.log10(max_tf + 1.0)) * (Math.log10(1400 / df) / Math.log10(1400));
		}catch(Exception e)
		{
			w1 = 0.0;
		}
		return w1;
	}
	
	public double computeW2(int tf, int doclen, double avgdoclen, int df) {
		double w2 = 0.0;
		//catch if divide by 0 occurs
		try {
		w2 = 0.4 + 0.6 * (tf / (tf + 0.5 + 1.5 * (doclen / avgdoclen))) * Math.log(1400 / df) / Math.log(1400);
		}catch(Exception e)
		{
			w2 = 0.0;
		}
		return w2;
	}
	
	public <K, V extends Comparable<? super V>> Map<K, V> sortByValues(Map<K, V> dictionary) {
			List<Map.Entry<K, V>> list = new ArrayList<>(dictionary.entrySet());
			list.sort(Map.Entry.comparingByValue((s2, s1) -> s1.compareTo(s2)));
			Map<K, V> sorted_dictionary = new LinkedHashMap<>();
			for (Map.Entry<K, V> entry : list) 
				sorted_dictionary.put(entry.getKey(), entry.getValue());
			return sorted_dictionary;
		}
	}
