package rdb170002_Homework3;

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.BitSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.TreeMap;

public class IndexCompression {
	static String gammaString = "";
	static TreeMap<String, CompressedDictionary> compressedInvertedDictionary = new TreeMap<String, CompressedDictionary>();
	static TreeMap<String, CompressedDictionary> compressedStemInvertedDictionary = new TreeMap<String, CompressedDictionary>();
	
	public static void createCompressedDictionary(TreeMap<String, Dictionary> compressDictionary) throws IOException {
		CompressedDictionary cd = null;
		for(String term : compressDictionary.keySet()) {
			cd = new CompressedDictionary(term, IndexCompression.gammaEncoding(compressDictionary.get(term).postings.size()), IndexCompression.createPostingList(term, compressDictionary.get(term).postings, "Gamma"));
			compressedInvertedDictionary.put(term, cd);
		}

		System.out.println("");
	}
	
	public static void createCompressedStemDictionary(TreeMap<String, Dictionary> compressDictionary) throws IOException {
		CompressedDictionary cd = null;
		for(String term : compressDictionary.keySet()) {
			cd = new CompressedDictionary(term, IndexCompression.deltaEncoding(compressDictionary.get(term).postings.size()), IndexCompression.createPostingList(term, compressDictionary.get(term).postings, "Delta"));
			compressedStemInvertedDictionary.put(term, cd);
		}

		System.out.println("");
	}
	
	public static void blockedCompression(TreeMap<String, CompressedDictionary> compressDictionary) throws IOException {
		DataOutputStream dOS = new DataOutputStream(new FileOutputStream("CompressedIndex_version1.compressed"));
		int k=0;
		String Blockterms[] = new String[8];
		String firstTerm = "";
		for(String term : compressDictionary.keySet()) {
			StringBuffer sb = new StringBuffer();
			if(k==0) { 
				Blockterms = new String[8];
				firstTerm = term;
			}
			if(k<8) {
				if(compressDictionary.get(term)==null)
					break;
				Blockterms[k] = term.length() + term;
				compressedInvertedDictionary.get(term).getcompressedPostingList();
				k++;
			}
			if(k==8) {
				int i = 0;
				while(i < 8) {
				sb.append(Blockterms[i]);
				i++;
				}
				//System.out.println(firstTerm);
				dOS.writeUTF(sb.toString() + " - " + compressedInvertedDictionary.get(firstTerm).compressedPostingList.hashCode() + System.lineSeparator());
				k = 0;
			}
		}
		dOS.flush();
		dOS.close();
		//System.out.println("Compressed dictionary creation");
		Iterator<Entry<String, CompressedDictionary>> itr = compressedInvertedDictionary.entrySet().iterator();
		IndexCompression.postingPrint("CompressedPostings_version1.compressed", itr);
	}
	
		public static void postingPrint(String f, Iterator<Entry<String, CompressedDictionary>> itr) throws IOException {
		DataOutputStream dOS_posting = new DataOutputStream(new FileOutputStream(f));
		Entry<String, CompressedDictionary> entry = null;
		while (itr.hasNext()) {
			entry = itr.next();
			
			for (CompressedPostingList term : entry.getValue().getcompressedPostingList()) {
				dOS_posting.write(term.docID);
				dOS_posting.write(term.termFrequency);
				dOS_posting.writeBytes(System.lineSeparator());
			
		}
		}
		dOS_posting.flush();
		dOS_posting.close();
		//System.out.println("Compressed postings creation");
	}
	

	public static LinkedList<CompressedPostingList> createPostingList(String term, LinkedList<PostingList> postingList, String encode) throws IOException{
		LinkedList<CompressedPostingList> createCompressedPostingList = new LinkedList<CompressedPostingList>();
		int previousDocID = 0;
		for(PostingList postings: postingList) {
			int gap = postings.docID - previousDocID;
			byte[] gapByte;
			byte[] termFrequency;
			byte[] maxdocLengthByte;
			byte[] maxtfByte;
			if(encode == "Gamma") {
				gapByte = IndexCompression.gammaEncoding(gap);
				termFrequency =	IndexCompression.gammaEncoding(postings.termFrequency);
				maxdocLengthByte = IndexCompression.gammaEncoding(postings.doclen);
				maxtfByte = IndexCompression.gammaEncoding(postings.max_tf);
			}
			else
			{
				gapByte = IndexCompression.deltaEncoding(gap);
				termFrequency =	IndexCompression.deltaEncoding(postings.termFrequency);
				maxdocLengthByte = IndexCompression.deltaEncoding(postings.doclen);
				maxtfByte = IndexCompression.deltaEncoding(postings.max_tf);
			}
			
			CompressedPostingList CPL = new CompressedPostingList(gapByte, termFrequency, maxdocLengthByte, maxtfByte);		
			createCompressedPostingList.add(CPL);
		}
		
		return createCompressedPostingList;
	}
	
	public static void frontCoding(TreeMap<String, CompressedDictionary> invertedStemDictionary) throws IOException {
		DataOutputStream dOS = new DataOutputStream(new FileOutputStream("CompressedIndex_version2.compressed"));
		int k=0;
		//String[] termArray = new String[Indexing.invertedDictionary.size()];
		String Blockterms[] = new String[8];
		String firstTerm = "";
		for(String term : invertedStemDictionary.keySet()) {
			StringBuffer sb = new StringBuffer();
			if(k==0) { 
				Blockterms = new String[8];
				firstTerm = term;
			}
			if(k<8) {
				if(invertedStemDictionary.get(term)==null)
					break;
				Blockterms[k] = term;
				k++;
			}
			if(k==8) {
				int m = 0;
				boolean flag = true;
				int block_length = 0;
				for(int j = 0; j < 8; j++) {
					if(firstTerm.charAt(0)==Blockterms[j].charAt(0))
						block_length += 1;
				}
				for(int i = 0; i < firstTerm.length(); i++) {
					for(int j = 0; j < block_length; j++) {
						if(firstTerm.charAt(i)!= Blockterms[j].charAt(i) || Blockterms[j].length() < i) {
							flag = false;
							break;
						}
					}
					if(flag == true) 
						m++;
					else break;						
				}
				
				sb.append(firstTerm.length());
				sb.append(firstTerm.substring(0, m));
				sb.append("*");
				sb.append(firstTerm.substring(m));
			
				int i = 0;
				while(i < block_length) {
				sb.append(Blockterms[i].substring(m).length());
				sb.append("~");
				sb.append(Blockterms[i].substring(m));
				i++;
				}
				int j = block_length;
				if(block_length < 8) {
					while(j < 8) {
						sb.append(Blockterms[j].length());
						sb.append(Blockterms[j]);
						j++;
					}
				}
				dOS.writeUTF(sb.toString() + " - " + compressedStemInvertedDictionary.get(firstTerm).compressedPostingList.hashCode());
				dOS.writeUTF(System.lineSeparator());
				k = 0;
			}
		}
		dOS.flush();
		dOS.close();
		
		Iterator<Entry<String, CompressedDictionary>> itr = compressedStemInvertedDictionary.entrySet().iterator();
		IndexCompression.postingPrint("CompressedPostings_version2.compressed", itr);
		
		
	}
	
	//To convert String to Byte
	private static byte[] convertStringToByteArray(String s) {
		BitSet bitset = new BitSet(s.length());
		for (int i = 0; i < s.length(); i++) {
			Boolean value = s.charAt(i) == '1' ? true : false;
			bitset.set(i, value);
		}
		return bitset.toByteArray();
	}
	
	public static byte[] gammaEncoding(int num) throws IOException {
		String gamma = "";
		    String l = Integer.toBinaryString(num);
		    int length_of_l = l.length();
		    String length, offset;
		    if(length_of_l == 1) {
		    	length = "0";
		    	offset = "";
		    }
		    else {
		    	length = "";
		    	while(length_of_l > 1){
		    		length += "1";
		    		length_of_l--;
		    	}
		    	length += "0";
		    	offset = l.substring(1);
		    }
		    gamma += length + offset;
		    IndexCompression.gammaString = gamma;
		    byte[] gammaInByte = convertStringToByteArray(gamma);
		    
		    return gammaInByte;
		    
	}
	
	public static byte[] deltaEncoding(int num) throws IOException {
			String delta = "";
		    String binary = Integer.toBinaryString(num);
		    int length_of_l = binary.length();
		    String length;
		    String offset;
		    if(length_of_l == 1) {
		    	length = "0";
		    	offset = "";
		    }
		    else {
		    	byte [] gamma_byte = IndexCompression.gammaEncoding(length_of_l);
		    	String gamma_string = IndexCompression.gammaString;
		    	length = gamma_string;
		    	offset = binary.substring(1);
		    }
		    delta += length + offset;
		    byte[] deltaInByte = convertStringToByteArray(delta); 
		    return deltaInByte;
	}
	
}

class CompressedDictionary{
	String term;
	byte[] docFrequency;
	LinkedList<CompressedPostingList> compressedPostingList;
	
	public CompressedDictionary(String term, byte[] docFrequency, LinkedList<CompressedPostingList> compressedPostingList) {
		this.term = term;
		this.docFrequency = docFrequency;
		this.compressedPostingList = compressedPostingList;
	}
			
	public LinkedList<CompressedPostingList> getcompressedPostingList(){
		return  compressedPostingList;
	}
}

class CompressedPostingList{
	byte[] docID;
	byte[] termFrequency;
	byte[] max_docLength;
	byte[] max_tf;
	
	public CompressedPostingList(byte[] docID, byte[] termFrequency, byte[] max_docLength, byte[] max_tf) {
		this.docID = docID;
		this.termFrequency = termFrequency;
		this.max_docLength = max_docLength;
		this.max_tf = max_tf;
	}
}

class CompressedStemDictionary{
	String term;
	byte[] docFrequency;
	LinkedList<CompressedPostingList> compressedStemPostingList;
	
	public CompressedStemDictionary(String term, byte[] docFrequency, LinkedList<CompressedPostingList> compressedStemPostingList) {
		this.term = term;
		this.docFrequency = docFrequency;
		this.compressedStemPostingList = compressedStemPostingList;
	}
	
	public LinkedList<CompressedPostingList> getcompressedStemPostingList(){
		return  compressedStemPostingList;
	}
}

class CompressedStemPostingList{
	byte[] docID;
	byte[] termFrequency;
	byte[] max_docLength;
	byte[] max_tf;
	
	public CompressedStemPostingList(byte[] docID, byte[] termFrequency, byte[] max_docLength, byte[] max_tf) {
		this.docID = docID;
		this.termFrequency = termFrequency;
		this.max_docLength = max_docLength;
		this.max_tf = max_tf;
	}
}