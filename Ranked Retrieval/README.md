
******************************************************************************* 	
Course Number : CS 6322.001 Information Retrieval - F18
Homework 3
Date: 11/14/2018
	
I've used already implemented Porter stemmer available in open-source by Tartarus.
	
Porter stemmer in Java. The original paper is in

Porter, 1980, An algorithm for suffix stripping, Program, Vol. 14, no. 3, pp 130-137,

See also http://www.tartarus.org/~martin/PorterStemmer
*******************************************************************************

How to run a program?

	I've attached rdb170002.tar
	Copy the file on your local repository.
	
	Tar file contains ReadMe.txt, Output.txt and rdb170002_Homework3(This folder contains .java files).

Run Commands: 

tar -xvf rdb170002.tar

source /usr/local/corenlp350/classpath.sh

cd rdb170002_Homework3
javac -classpath $CLASSPATH StanfordLemmatizer.java
javac PostingList.java
javac Stemmer.java
cd ..
javac rdb170002_Homework3/Dictionary.java
javac rdb170002_Homework3/IndexCompression.java
javac rdb170002_Homework3/Comparators.java
javac rdb170002_Homework3/Tokenization.java
javac rdb170002_Homework3/Indexing.java
javac rdb170002_Homework3/QueryDictionary.java
javac rdb170002_Homework3/Query.java
javac rdb170002_Homework3/QueryProcess.java

java -classpath $CLASSPATH rdb170002_Homework3/QueryProcess /people/cs/s/sanda/cs6322/Cranfield/ /people/cs/s/sanda/cs6322/resourcesIR/stopwords /people/cs/s/sanda/cs6322/hw3.queries

*********************************************************************************
