/* *****************************************************************************
 *  Name: Nguyen Le Nam Khanh
 *  Date: Jan 19, 2026
 *  Description: SAP
 **************************************************************************** */

import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.ResizingArrayBag;
import edu.princeton.cs.algs4.ResizingArrayQueue;
import edu.princeton.cs.algs4.ResizingArrayStack;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

public class SAP {

    private final Digraph graph;
    private final int[] distFromV;
    private final int[] distFromW;
    private final ResizingArrayStack<Integer> visited;

    // constructor takes a digraph (not necessarily a DAG)
    public SAP(Digraph G) {
        if (G == null) {
            throw new IllegalArgumentException("argument for Digraph is null");
        }

        this.graph = G;
        this.distFromV = new int[G.V()];
        this.distFromW = new int[G.V()];
        this.visited = new ResizingArrayStack<>();

        for (int i = 0; i < G.V(); ++i) {
            distFromV[i] = -1;
            distFromW[i] = -1;
        }
    }

    // length of shortest ancestral path between v and w; -1 if no such path
    public int length(int v, int w) {
        ResizingArrayBag<Integer> vSet = new ResizingArrayBag<>();
        vSet.add(v);

        ResizingArrayBag<Integer> wSet = new ResizingArrayBag<>();
        wSet.add(w);

        return length(vSet, wSet);
    }

    // a common ancestor of v and w that participates in a shortest ancestral path; -1 if no such path
    public int ancestor(int v, int w) {
        ResizingArrayBag<Integer> vSet = new ResizingArrayBag<>();
        vSet.add(v);

        ResizingArrayBag<Integer> wSet = new ResizingArrayBag<>();
        wSet.add(w);

        return ancestor(vSet, wSet);
    }

    // length of shortest ancestral path between any vertex in v and any vertex in w; -1 if no such path
    public int length(Iterable<Integer> v, Iterable<Integer> w) {
        return shortestAncestralPath(v, w)[0];
    }

    // a common ancestor that participates in shortest ancestral path; -1 if no such path
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        return shortestAncestralPath(v, w)[1];
    }

    private int[] shortestAncestralPath(Iterable<Integer> v, Iterable<Integer> w) {
        validate(v);
        validate(w);

        bfs(v, distFromV);
        bfs(w, distFromW);

        int minLen = Integer.MAX_VALUE;
        int ancestor = -1;

        while (!visited.isEmpty()) {
            int curr = visited.pop();

            if (distFromV[curr] != -1 && distFromW[curr] != -1) {
                int currLen = distFromV[curr] + distFromW[curr];

                if (currLen < minLen) {
                    minLen = currLen;
                    ancestor = curr;
                }
            }

            distFromV[curr] = -1;
            distFromW[curr] = -1;
        }

        if (ancestor == -1) {
            return new int[] { -1, -1 };
        }

        return new int[] { minLen, ancestor };
    }

    private void validate(Iterable<Integer> iterable) {
        if (iterable == null) {
            throw new IllegalArgumentException("iterable argument is null");
        }

        for (Integer val : iterable) {
            if (val == null) {
                throw new IllegalArgumentException("iterable contains a null");
            }

            if (val < 0 || val >= graph.V()) {
                throw new IllegalArgumentException("vertex out of range");
            }
        }
    }

    private void bfs(Iterable<Integer> source, int[] distFromSource) {
        ResizingArrayQueue<Integer> queue = new ResizingArrayQueue<>();
        for (Integer src : source) {
            queue.enqueue(src);
            distFromSource[src] = 0;
            visited.push(src);
        }

        while (!queue.isEmpty()) {
            int v = queue.dequeue();

            for (int w : graph.adj(v)) {
                if (distFromSource[w] == -1) {
                    distFromSource[w] = distFromSource[v] + 1;
                    queue.enqueue(w);
                    visited.push(w);
                }
            }
        }
    }

    // do unit testing of this class
    public static void main(String[] args) {
        In in = new In(args[0]);
        Digraph G = new Digraph(in);
        SAP sap = new SAP(G);

        while (!StdIn.isEmpty()) {
            int v = StdIn.readInt();
            int w = StdIn.readInt();

            int length = sap.length(v, w);
            int ancestor = sap.ancestor(v, w);
            StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
        }
    }
}
