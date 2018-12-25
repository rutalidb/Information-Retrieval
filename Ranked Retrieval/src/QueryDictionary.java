package rdb170002_Homework3;


public class QueryDictionary {
	String term;
	Integer count;
	Integer querylen;
	Integer max_tf;
	
	public QueryDictionary()
	{}
		
	public QueryDictionary(String term, Integer count, Integer querylen, Integer max_tf) {
		this.term = term;
		this.count = count;
		this.querylen = querylen;
		this.max_tf = max_tf;
	}
	

}
