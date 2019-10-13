package ch.heigvd.iict.dmg.labo1.similarities;

import org.apache.lucene.search.similarities.ClassicSimilarity;

public class MySimilarity extends ClassicSimilarity {

	// TODO student
	// Implement the functions described in section "Tuning the Lucene Score"

    @Override
    public float lengthNorm(int numTerms){
        return 1;
    }

    @Override
    public float tf(float freq) {
        return (float) (1 + Math.log(freq));
    }

    @Override
    public float idf(long docFreq, long docCount) {
        long denominator = docFreq + 1;
        long result = docCount / denominator;
        return (float) (Math.log(result) + 1);
    }

}
