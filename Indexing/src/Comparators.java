package rdb170002_Homework2;

import java.util.Comparator;

public class Comparators implements Comparator<PostingList>{

        public int compare(PostingList pl1, PostingList pl2) {
            return pl1.docID.compareTo(pl2.docID);
        }
}