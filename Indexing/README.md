******************************************************************************* 	
Course Number : CS 6322.001 Information Retrieval - F18
Homework 2
	
I've used already implemented Porter stemmer available in open-source by Tartarus.
	
Porter stemmer in Java. The original paper is in

Porter, 1980, An algorithm for suffix stripping, Program, Vol. 14, no. 3, pp 130-137,

See also http://www.tartarus.org/~martin/PorterStemmer
*******************************************************************************

How to run a program?

	I've attached rdb170002.tar
	Copy the file on your local repository.
	
	Tar file contains ReadMe.txt, Output.txt and rdb170002_Homework2(This folder contains .java files).

Run Commands: 

tar -xvf rdb170002.tar

source /usr/local/corenlp350/classpath.sh

cd rdb170002_Homework2
javac -classpath $CLASSPATH StanfordLemmatizer.java
javac PostingList.java
javac Stemmer.java
cd ..
javac rdb170002_Homework2/Dictionary.java
javac rdb170002_Homework2/Tokenization.java
javac rdb170002_Homework2/IndexCompression.java
javac rdb170002_Homework2/Indexing.java
javac rdb170002_Homework2/Dictionary.java


java -classpath $CLASSPATH rdb170002_Homework2/Indexing /people/cs/s/sanda/cs6322/Cranfield/ /people/cs/s/sanda/cs6322/resourcesIR/stopwords

*********************************************************************************

Output files : 
Two uncompressed files:
	UncompressedIndex (text/plain; charset=us-ascii)
	UncompressedStemIndex (text/plain; charset=us-ascii)

Four compressed files:
	CompressedIndex_version1.compressed (application/octet-stream; charset=binary)
	CompressedIndex_version2.compressed (application/octet-stream; charset=binary)
	CompressedPostings_version1.compressed (application/octet-stream; charset=binary)
	CompressedPostings_version2.compressed (application/octet-stream; charset=binary)

*********************************************************************************

There are two uncompressed files.
One for lemma dictionary
One for stem dictionary

There are four compressed files.
Two files for compressed version of lemma dictionary using Blocked compression.
Two files for compressed version of stem dictionary using Front Coding compression.

Compression using Blocked compression.
First file stores 8*(term_length + term) --> pointer
e.g. 7ability6ablate13ablatedlength8ablation8ablative4able5about5above - -640729725

Second file stores docId, termFrequency, doclen, max_tf

Compression using Front coding compression:
First file stores 8*(term_length + term) --> pointer
e.g. 4ab*il2~il1~l3~lat11~latedlength3~out2~ov7~ovement5~raham - -710985270
**used ~ instead of diamonds.

Second file stores docId, termFrequency, doclen, max_tf

*********************************************************************************
