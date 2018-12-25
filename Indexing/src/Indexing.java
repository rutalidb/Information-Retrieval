package rdb170002_Homework2;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.Map.Entry;


class Indexing{

	static File[] files;
	static HashSet<String> stopWordlist;
	static Map<String, Integer> termIndex = new TreeMap<String,Integer>();
	static TreeMap<String, Dictionary> invertedDictionary = new TreeMap<String, Dictionary>();
	static TreeMap<String, Dictionary> invertedStemDictionary = new TreeMap<String, Dictionary>();
	static StanfordLemmatizer objlemma =  new StanfordLemmatizer();
	
	public static void main(String args[]) throws Exception
	{
		String directoryPath;
		if(args.length == 0)
			directoryPath = "/people/cs/s/sanda/cs6322/Cranfield/";
		else
			directoryPath = (args[0]).toString();
		File stopWords = new File(args[1].toString());
		createStopWordsSet(stopWords);
		
		buildIndex(directoryPath);
		
		File uncompressedIndex = new File("UncompressedIndex");
		File uncompressedPostings = new File("UncompressedPostings");
		File uncompressedStemIndex = new File("UncompressedStemIndex");
		File uncompressedStemPostings = new File("UncompressedStemPostings");
		
		//-------------------------Required Output------------------------------
		long startTimer;
		long endTimer;
		
		startTimer = System.currentTimeMillis();
		Indexing.uncompressedDictionary(uncompressedIndex, uncompressedPostings,invertedDictionary);
		endTimer = System.currentTimeMillis();
		System.out.println("The elapsed time required to build uncompressed Lemma Index: " + (endTimer - startTimer) + " milliseconds");
		
		startTimer = System.currentTimeMillis();
		Indexing.uncompressedDictionary(uncompressedStemIndex, uncompressedStemPostings,invertedStemDictionary);
		endTimer = System.currentTimeMillis();
		System.out.println("The elapsed time required to build uncompressed Stem Index: " + (endTimer - startTimer) + " milliseconds");
		
		
		IndexCompression.createCompressedDictionary(invertedDictionary);
		
		startTimer = System.currentTimeMillis();
		IndexCompression.blockedCompression(IndexCompression.compressedInvertedDictionary);
		endTimer = System.currentTimeMillis();
		System.out.println("The elapsed time required to build compressed Stem Index using blocked compression: " + (endTimer - startTimer) + " milliseconds");
		
		IndexCompression.createCompressedStemDictionary(invertedStemDictionary);
		
		startTimer = System.currentTimeMillis();
		IndexCompression.frontCoding(IndexCompression.compressedStemInvertedDictionary);
		endTimer = System.currentTimeMillis();
		System.out.println("The elapsed time required to build compressed Stem Index using front coding compression: " + (endTimer - startTimer) + " milliseconds");
		
		
		//file size in byte
		File f1 = new File("CompressedIndex_version1.compressed");
		File f2 = new File("CompressedIndex_version2.compressed");
		File f3 = new File("CompressedPostings_version1.compressed");
		File f4 = new File("CompressedPostings_version2.compressed");
		System.out.println("The size of the index Version 1 uncompressed: " + (uncompressedIndex.length() + uncompressedPostings.length()) + " bytes");
		System.out.println("The size of the index Version 1 uncompressed: " + (uncompressedStemIndex.length() + uncompressedStemPostings.length()) + " bytes");
		
		System.out.println("The size of the index Version 1 compressed created using blocked compression: " + (f1.length() + f3.length()) + " bytes");
		System.out.println("The size of the index Version 2 compressed created using front coding compression: " + (f2.length() + f4.length()) + " bytes");
		
		
		System.out.println("The number of inverted lists in version 1 uncompressed of the index: " + invertedDictionary.size());
		System.out.println("The number of inverted lists in version 1 compressed of the index: " + IndexCompression.compressedInvertedDictionary.size());
		System.out.println("The number of inverted lists in version 2 uncompressed of the index: " + invertedStemDictionary.size());
		System.out.println("The number of inverted lists in version 2 compressed of the index: " + IndexCompression.compressedStemInvertedDictionary.size());
		
		System.out.println();
		System.out.println("word" + "\t" + "df" + "\t" + "tf" + "\t" + "inverted list length" + "\t");
		String word_list[] = {"Reynolds", "NASA", "Prandtl", "flow", "pressure", "boundary", "shock" };
		for(String word : word_list) {
			System.out.print(word + "   ");
			getDfTfLength(word);
		}
		System.out.println("-------------------------------------");
		System.out.println("For NASA");
		System.out.println("Document Frequency: " + invertedDictionary.get("nasa").docFrequency);
		System.out.println("TermFrequency: " + invertedDictionary.get("nasa").termFrequency);
		
		computeNASATerms("NASA");
		System.out.println("-------------------------------------");
		System.out.println("Index 1:");
		largestLowestdf(invertedDictionary);
		System.out.println("-------------------------------------");
		System.out.println("Index 2:");
		largestLowestdf(invertedStemDictionary);
		System.out.println("-------------------------------------");
		documentMaxLenDocLen();
		System.out.println("-------------------------------------");
		
	}
	
	public static void getDfTfLength(String term) {
			term = objlemma.lemmatize(term.toLowerCase());
			System.out.print(invertedDictionary.get(term).docFrequency + "\t");
			System.out.print(invertedDictionary.get(term).termFrequency + "\t");
			System.out.print(getBytesOfCompressedPostingList(IndexCompression.compressedInvertedDictionary.get(term).compressedPostingList));
			System.out.println();
	}

	
	public static void computeNASATerms(String term) {
		term = objlemma.lemmatize(term.toLowerCase());
		int i = 0;
		List<PostingList> list = invertedDictionary.get(term).postings;
		Collections.sort(list, new Comparators());
		
		while(i<3) {
			PostingList pl = list.get(i);
			
			System.out.println("Document ID: " 	+ pl.docID);
			System.out.println("tf: " + pl.termFrequency);
			System.out.println("doc len: " + pl.doclen);
			System.out.println("max_tf: " + pl.max_tf);
			System.out.println();
			i++;
		}
	}
	
	public static long getBytesOfCompressedPostingList(LinkedList<CompressedPostingList> compressedPostingList) {
		long lenght = 0;
		for(CompressedPostingList pl : compressedPostingList){
			lenght += pl.docID.length + pl.termFrequency.length + pl.max_docLength.length + pl.max_tf.length;
		}
		return lenght;
	}
	
	public static void largestLowestdf(TreeMap<String, Dictionary> DesiredDictionary) {
		int minDf = Integer.MAX_VALUE;
		int maxDf = Integer.MIN_VALUE;
		int size;
		Iterator<Entry<String, Dictionary>> itr = DesiredDictionary.entrySet().iterator();
		Entry<String, Dictionary> entry;
		while(itr.hasNext()) {
			entry = itr.next();
			size = entry.getValue().postings.size();
			if(minDf > size){
				minDf = size;
			}
			if(maxDf < size){
				maxDf = size;
			}
		}
		
		//print documents with maxdf and mindf
		List<String> minTerms = new ArrayList<String>();
		List<String> maxTerms = new ArrayList<String>();
		itr = DesiredDictionary.entrySet().iterator();
		entry = null;
		String term;
		while(itr.hasNext()){
			entry = itr.next();
			term = entry.getKey();
			size = entry.getValue().postings.size();
			if(size==minDf){
				minTerms.add(term);
			}
			if(size==maxDf){
				maxTerms.add(term);
			}
		}
		
		System.out.println("Terms having minimum document frequency: ");
		for(String t : minTerms)
			System.out.print(t + ",");
		
		System.out.println();
		System.out.println("The minimum in df in Index: " + minDf);
		System.out.println();
		
		System.out.println();
		System.out.println("Terms having maximum document frequency: ");
		for(String t : maxTerms)
			System.out.print(t);
		
		System.out.println();
		System.out.println("The maximum in df in Index: " + maxDf);
		System.out.println();
	}
	
	public static void documentMaxLenDocLen() {
		int max_tf = -1;
        int max_tf_docid = -1;
        int doclen_max = -1;
        int doclen_docid = -1;

        for (Entry<String, Dictionary> entry : invertedStemDictionary.entrySet()) {
               LinkedList<PostingList> pl = entry.getValue().postings;
               for (PostingList p : pl) {
                     if (p.max_tf > max_tf) {
                    	 max_tf_docid = p.docID;
                    	 max_tf = p.max_tf;
                     }

                     if (p.doclen > doclen_max) {
                    	 doclen_max = p.doclen;
                    	 doclen_docid = p.docID;
                     }
               }
        }
      		
      		System.out.println("Document having maximum document length: ");
      			System.out.println("Cranfield0" + doclen_docid);

      		System.out.println("Document having maximum term frequency: ");
      			System.out.println("Cranfield0" + max_tf_docid);
      		System.out.println();
      		
	}
	//this function creates stop words set
	public static void createStopWordsSet(File stopWords) throws IOException {
		stopWordlist = new HashSet<String>();
		Scanner scanner = new Scanner(stopWords);
		while(scanner.hasNext()){
			stopWordlist.add(scanner.next());
		}
		scanner.close();
	}
	
	public static void buildIndex(String directoryPath) throws Exception{
		File file = new File(directoryPath);
		files = file.listFiles();
		for (File f : files) {			
			Tokenization t = new Tokenization();
			//get lemmatized dictionary
			t.convertFileToString(f);
			String document_name = f.getName();
			document_name = document_name.substring(9);
			addToIndex(Integer.parseInt(document_name),t.dictionary,invertedDictionary, t.doclen);
			addToIndex(Integer.parseInt(document_name),t.stem_dictionary,invertedStemDictionary, t.doclen);
			t.doclen=0;
		}
	}
	
	public static void addToIndex(int docID, Map<String, Integer> termIndex, TreeMap<String, Dictionary> DesiredDictionary, int doclen) {
		
		int max_tf = 0;
		for (String term : termIndex.keySet()) {				
		//if not a stop word, then add to inverted file
		
			if(!stopWordlist.contains(term)) {
			
				if(termIndex.get(term) > max_tf){
					max_tf = termIndex.get(term);
			}
				
			addToDictionaryPosting(docID, term, termIndex.get(term),DesiredDictionary, (doclen/2), max_tf);
		}
		}
	}
	
	public static void addToDictionaryPosting(int docID, String dictionary_term, int termFrequency, TreeMap<String, Dictionary> DesiredDictionary,int totalWordOccurances, int max_tf) {
		Dictionary term = DesiredDictionary.get(dictionary_term);
		
		if(term == null) {
			term = new Dictionary(dictionary_term, 0, 0, new LinkedList<PostingList>());
			DesiredDictionary.put(dictionary_term, term);
		}
			term.postings.add(new PostingList(docID, termFrequency, max_tf, totalWordOccurances));
			term.termFrequency += termFrequency;
			term.docFrequency += 1;
			
	}
	
	public static void uncompressedDictionary(File uncompressedIndex, File uncompressedPostings, TreeMap<String, Dictionary> desiredDictionary) throws FileNotFoundException {
		PrintWriter uncompressedDictionary = new PrintWriter(uncompressedIndex);
		
		Iterator<Entry<String, Dictionary>> itr = desiredDictionary.entrySet().iterator();
		Entry<String, Dictionary> entry = null;
		uncompressedDictionary.write("Term - Term Frequency - Posting size - Document Frequency - TermFrequency - Doc Length - Max Term Frequency");
		while (itr.hasNext()) {
			String s = "";
			entry = itr.next();
			for (PostingList term : entry.getValue().postings) {
				s += "(" + term.docID + "," + term.termFrequency + "," + term.doclen + "," + term.max_tf + ")";
		}
			uncompressedDictionary.write(entry.getKey() + "\t" + entry.getValue().termFrequency + "\t" + entry.getValue().postings.size() + "\t" + s + "\n");
		}
		uncompressedDictionary.flush();
		uncompressedDictionary.close();
	}	
}


