package rdb170002_Homework3;
import java.util.List;
import java.util.Properties;

import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

class StanfordLemmatizer {
	StanfordCoreNLP pipeline;
    public StanfordLemmatizer() {
        Properties properties;
        properties = new Properties();
        properties.put("annotators", "tokenize, ssplit, pos, lemma");
        this.pipeline = new StanfordCoreNLP(properties);
    }

    public String lemmatize(String dictionary_token)
    {
        Annotation tokenAnnotation = new Annotation(dictionary_token);
        pipeline.annotate(tokenAnnotation);
        List<CoreMap> list = tokenAnnotation.get(SentencesAnnotation.class);
        String tokenLemma = list
                                .get(0).get(TokensAnnotation.class)
                                .get(0).get(LemmaAnnotation.class);
        return tokenLemma;
    }
}
