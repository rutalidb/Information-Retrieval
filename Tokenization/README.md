******************************************************************************* 	
Course Number : CS 6322.001 Information Retrieval - F18
Homework 1
	
I've used already implemented Porter stemmer available in open-source by Tartarus.
	
Porter stemmer in Java. The original paper is in

Porter, 1980, An algorithm for suffix stripping, Program, Vol. 14, no. 3, pp 130-137,

See also http://www.tartarus.org/~martin/PorterStemmer
*******************************************************************************

1. How to compile and run the program?
	I've attached rdb170002.tar
	Copy the file on your local repository.

	Untar the file using
	1) tar -xvf rdb170002.tar
	
	Tar file contains RunScript.sh , ReadMe.txt, Output.txt and rdb170002_Homework1(This folder contains .java files).
	
	To run using shell script - 
	
	1) cd rdb170002
	2) chmod 777 RunScript.sh
	3) ./RunScript.sh /people/cs/s/sanda/cs6322/Cranfield


	To run without using shell script -
	
	1) cd rdb170002
	2) javac rdb170002_Homework1/*
	3) java rdb170002_Homework1.Tokenization /people/cs/s/sanda/cs6322/Cranfield

	OR you can also pass the command line argument for the directory path containing Cranfield files

	java rdb170002_Homework1.Tokenization
	
	Check the output.
	
	
2. External library or code used for implementing stemming.
	I've used external online available code for Porter Steeming by Tartarus.
	
	https://tartarus.org/martin/PorterStemmer/java.txt
	
	
3. How long the program took to acquire the text characteristics.
	I ran the program 3 times.
	First runtime = 1559 milliseconds
	Second runtime = 1249 milliseconds
	Third runtime = 1216 milliseconds

	Average runtime = 1341 milliseconds

	
4. How the program handles:
	A. Upper and lower case words (e.g. "People", "people", "Apple", "apple");
	I’ve converted all words to lower case.
	If “People” and “people” words are present in the file, code will return “people” = 2 (Count of people in tokens dictionary)

	B. Words with dashes (e.g. "1996-97", "middle-class", "30-year", "tean-ager")
	I’ve split tokens by dashes.

	Also, I’ve considered few more special characters ()=,!%\/-+* to split the dictionary.
	For example,
	“Hi + Hello” will return Hi and Hello
	“Hi / Hello” will return Hi and Hello
	“Hi % Hello” will return Hi and Hello
	“Hi – Hello” will return Hi and Hello

	C. Possessives (e.g. "sheriff's", "university's")
	I’ve replaced ‘s by Null.

	For example,
	“University’s” will return University
	“Uni’versity” will return University

	D. Acronyms (e.g., "U.S.", "U.N.")
	I’ve replaced . with Null.

	For example,
	“U.S.” will return US

	
5. Data structures used
	TreeMap for storing tokens and stems.
	LinkedHashmap for sorting TreeMap dictionaries by values.

	
6. How is my program working?
	I'm taking each file at a time, then converting it into a string.
	
	Then I'm splitting that string by ()=,!%\\s\/-+* special characters and storing in String array.
	\\s -> White space
	I'm taking each word from the String array and checking if it contains characters other than a to z.
		
	If yes, I'm removing them and adding the words in tokens dictionary.
	(
	I'm not adding numerical values in token dictionary.
	Also, if a word is alphanumeric, I'm removing numerical values from that word.
	For example,
	For word = "30-year" , I'm keeping token as "year"
	For word = "1996-97", I'm kepping token as ""
	For word = "University's", I'm keeping token as "University"
	For word = "1996", I'm keeping token as ""
	)
	
	If no, I'm directly adding the words in tokens dictionary.
	
	I'm passing each token to Stemmer.class file which is returning a stemmed word of that token.
	I'm adding that returned stem to stems dictionary.
	
	This process will repeat for all the files.
	
	At the end I'm sorting both the dictionaries by values.
