package rdb170002_Homework3;

import java.util.TreeMap;

public class Query {
	String queryTerm;
	QueryDictionary queryDict;
	
	TreeMap<String, QueryDictionary> query_dictionary = new TreeMap<String, QueryDictionary>();
	
	public Query()
	{}
	
	public Query(String queryTerm, QueryDictionary q) {
		this.queryTerm = queryTerm;
		this.queryDict = q;
	}
	
}
