/* *****************************************************************************
 *  Name: Nguyen Le Nam Khanh
 *  Date: Jan 19, 2026
 *  Description: WordNet
 **************************************************************************** */

import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.ResizingArrayBag;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;
import java.util.HashMap;

public class WordNet {

    private static final int UNMARKED = 0;
    private static final int MARKING = 1;
    private static final int MARKED = 2;

    private final HashMap<String, ResizingArrayBag<Integer>> nounToIds;
    private final ArrayList<String> idToNoun;
    private final SAP sap;

    // constructor takes the name of the two input files
    public WordNet(String synsets, String hypernyms) {
        if (synsets == null || hypernyms == null) {
            throw new IllegalArgumentException("constructor argument is null");
        }

        this.nounToIds = new HashMap<>();
        this.idToNoun = new ArrayList<>();

        readSynsets(synsets);
        Digraph G = new Digraph(idToNoun.size());
        readHypernyms(hypernyms, G);

        if (!isRootedDag(G)) {
            throw new IllegalArgumentException("input Digraph is not a rooted DAG");
        }

        this.sap = new SAP(G);
    }

    // returns all WordNet nouns
    public Iterable<String> nouns() {
        return nounToIds.keySet();
    }

    // is the word a WordNet noun?
    public boolean isNoun(String word) {
        if (word == null) {
            throw new IllegalArgumentException("word argument is null");
        }

        return nounToIds.containsKey(word);
    }

    // distance between nounA and nounB
    public int distance(String nounA, String nounB) {
        if (!isNoun(nounA) || !isNoun(nounB)) {
            throw new IllegalArgumentException("noun does not belong to WordNet");
        }

        Iterable<Integer> v = nounToIds.get(nounA);
        Iterable<Integer> w = nounToIds.get(nounB);
        return sap.length(v, w);
    }

    // a synset that is the common ancestor of nounA and nounB
    // in a shortest ancestral path
    public String sap(String nounA, String nounB) {
        if (!isNoun(nounA) || !isNoun(nounB)) {
            throw new IllegalArgumentException("noun does not belong to WordNet");
        }

        Iterable<Integer> v = nounToIds.get(nounA);
        Iterable<Integer> w = nounToIds.get(nounB);
        return idToNoun.get(sap.ancestor(v, w));
    }

    private void readSynsets(String synsets) {
        In in = new In(synsets);
        while (in.hasNextLine()) {
            String line = in.readLine();
            String[] tokens = line.split(",");

            int id = Integer.parseInt(tokens[0]);
            String synsetNouns = tokens[1];
            idToNoun.add(synsetNouns);

            String[] nouns = synsetNouns.split(" ");
            for (String noun : nouns) {
                if (nounToIds.containsKey(noun)) {
                    ResizingArrayBag<Integer> ids = nounToIds.get(noun);
                    ids.add(id);
                }
                else {
                    ResizingArrayBag<Integer> ids = new ResizingArrayBag<>();
                    ids.add(id);
                    nounToIds.put(noun, ids);
                }
            }
        }
    }

    private void readHypernyms(String hypernyms, Digraph G) {
        In in = new In(hypernyms);
        while (in.hasNextLine()) {
            String line = in.readLine();
            String[] tokens = line.split(",");

            int src = Integer.parseInt(tokens[0]);
            for (int i = 1; i < tokens.length; ++i) {
                G.addEdge(src, Integer.parseInt(tokens[i]));
            }
        }
    }

    private boolean isRootedDag(Digraph G) {
        int sinkCount = 0;
        for (int v = 0; v < G.V(); ++v) {
            if (G.outdegree(v) == 0) {
                ++sinkCount;
            }
        }

        if (sinkCount != 1) {
            return false;
        }

        return !hasCycle(G);
    }

    private boolean hasCycle(Digraph G) {
        int[] marker = new int[G.V()];

        for (int v = 0; v < G.V(); ++v) {
            if (marker[v] == UNMARKED && dfs(G, v, marker)) {
                return true;
            }
        }

        return false;
    }

    private boolean dfs(Digraph G, int v, int[] marker) {
        marker[v] = MARKING;

        for (int w : G.adj(v)) {
            if (marker[w] == MARKING) {
                return true;
            }

            if (marker[w] == UNMARKED && dfs(G, w, marker)) {
                return true;
            }
        }

        marker[v] = MARKED;
        return false;
    }

    // do unit testing of this class
    public static void main(String[] args) {
        WordNet wordNet = new WordNet(args[0], args[1]);
        while (!StdIn.isEmpty()) {
            String line = StdIn.readLine();
            String[] tokens = line.split(" ");

            String nounA = tokens[0];
            String nounB = tokens[1];

            int length = wordNet.distance(nounA, nounB);
            String ancestor = wordNet.sap(nounA, nounB);
            StdOut.printf("length = %d, ancestor = %s\n", length, ancestor);
        }
    }
}
