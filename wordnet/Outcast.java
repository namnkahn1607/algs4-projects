/* *****************************************************************************
 *  Name: Nguyen Le Nam Khanh
 *  Date: Jan 20, 2026
 *  Description: Outcast
 **************************************************************************** */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

public class Outcast {

    private final WordNet wordnet;

    // constructor takes a WordNet object
    public Outcast(WordNet wordnet) {
        if (wordnet == null) {
            throw new IllegalArgumentException("constructor argument is null");
        }

        this.wordnet = wordnet;
    }

    // given an array of WordNet nouns, return an outcast
    public String outcast(String[] nouns) {
        if (nouns == null) {
            throw new IllegalArgumentException("string array is null");
        }

        String xt = null;
        int dt = Integer.MIN_VALUE;

        for (int i = 0; i < nouns.length; ++i) {
            int di = 0;

            for (int j = 0; j < nouns.length; ++j) {
                if (i != j) {
                    di += wordnet.distance(nouns[i], nouns[j]);
                }
            }

            if (di > dt) {
                dt = di;
                xt = nouns[i];
            }
        }

        return xt;
    }

    // test client
    public static void main(String[] args) {
        WordNet wordnet = new WordNet(args[0], args[1]);
        Outcast outcast = new Outcast(wordnet);
        for (int t = 2; t < args.length; ++t) {
            In in = new In(args[t]);
            String[] nouns = in.readAllStrings();
            StdOut.println(args[t] + ": " + outcast.outcast(nouns));
        }
    }
}
