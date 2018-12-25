package rdb170002_Homework2;

class PostingList{
	Integer docID;
	Integer termFrequency;
	Integer max_tf;
	Integer doclen;
	
	public PostingList(Integer docID, Integer termFrequency, Integer max_tf, Integer doclen) {
		this.docID = docID;
		this.termFrequency = termFrequency;
		this.max_tf = max_tf;
		this.doclen = doclen;
		
	}
}
