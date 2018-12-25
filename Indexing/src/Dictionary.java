package rdb170002_Homework2;

import java.util.LinkedList;

class Dictionary{
	String term;
	Integer termFrequency;
	Integer docFrequency;
	LinkedList<PostingList> postings;
	
	public Dictionary(String term, Integer termFrequency, Integer docFrequency, LinkedList<PostingList> postings) {
		this.term = term;
		this.docFrequency = docFrequency;
		this.termFrequency = termFrequency;
		this.postings = postings;
	}
}
