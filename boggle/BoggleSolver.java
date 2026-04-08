/* *****************************************************************************
 *  Name: Nguyen Le Nam Khanh
 *  Date: Mar 13, 2026
 *  Description: BoggleSolver
 **************************************************************************** */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

import java.util.HashSet;
import java.util.Set;

public class BoggleSolver {

    private static final int RADIX = 26;

    private static class TrieNode {
        private TrieNode[] next = new TrieNode[RADIX];
        private boolean isWord = false;
    }

    private final TrieNode root;

    // Initialize the data structure using the given array of strings as the dictionary.
    // (You can assume each word in the dictionary contains only the uppercase letters A through Z).
    public BoggleSolver(String[] dictionary) {
        this.root = new TrieNode();
        for (String word : dictionary) {
            addWord(word);
        }
    }

    private void addWord(String word) {
        TrieNode curr = this.root;

        for (int d = 0; d < word.length(); ++d) {
            char c = word.charAt(d);

            if (curr.next[c - 'A'] == null) {
                curr.next[c - 'A'] = new TrieNode();
            }

            curr = curr.next[c - 'A'];
        }

        curr.isWord = true;
    }

    // Returns the set of all valid words in the given Boggle board, as an Iterable.
    public Iterable<String> getAllValidWords(BoggleBoard board) {
        Set<String> validWords = new HashSet<>();
        final int ROWS = board.rows();
        final int COLS = board.cols();

        boolean[][] visited = new boolean[ROWS][COLS];
        StringBuilder sb = new StringBuilder(2 * ROWS * COLS);

        for (int r = 0; r < ROWS; ++r) {
            for (int c = 0; c < COLS; ++c) {
                dfs(board, r, c, this.root, sb, visited, validWords);
            }
        }

        return validWords;
    }

    private void dfs(BoggleBoard board, int r, int c, TrieNode node, StringBuilder sb,
                     boolean[][] visited, Set<String> validWords) {
        final int ROWS = board.rows();
        final int COLS = board.cols();

        char letter = board.getLetter(r, c);
        TrieNode nextNode = node.next[letter - 'A'];

        if (nextNode == null) {
            return;
        }

        if (letter == 'Q') {
            nextNode = nextNode.next['U' - 'A'];

            if (nextNode == null) {
                return;
            }

            sb.append("QU");
        }
        else {
            sb.append(letter);
        }

        if (nextNode.isWord && sb.length() >= 3) {
            validWords.add(sb.toString());
        }

        visited[r][c] = true;

        for (int dr = -1; dr <= 1; ++dr) {
            for (int dc = -1; dc <= 1; ++dc) {
                if (dr == 0 && dc == 0) {
                    continue;
                }

                final int nr = r + dr;
                final int nc = c + dc;

                if (nr >= 0 && nr < ROWS && nc >= 0 && nc < COLS && !visited[nr][nc]) {
                    dfs(board, nr, nc, nextNode, sb, visited, validWords);
                }
            }
        }

        visited[r][c] = false;
        if (letter == 'Q') {
            sb.delete(sb.length() - 2, sb.length());
        }
        else {
            sb.deleteCharAt(sb.length() - 1);
        }
    }

    // Returns the score of the given word if it is in the dictionary, zero otherwise.
    // (You can assume the word contains only the uppercase letters A through Z).
    public int scoreOf(String word) {
        TrieNode curr = this.root;

        for (int d = 0; d < word.length(); ++d) {
            char c = word.charAt(d);

            if (curr.next[c - 'A'] == null) {
                return 0;
            }

            curr = curr.next[c - 'A'];
        }

        if (!curr.isWord) {
            return 0;
        }

        int wordLen = word.length();
        if (wordLen <= 2) {
            return 0;
        }
        else if (wordLen == 3 || wordLen == 4) {
            return 1;
        }
        else if (wordLen == 5) {
            return 2;
        }
        else if (wordLen == 6) {
            return 3;
        }
        else if (wordLen == 7) {
            return 5;
        }

        return 11;
    }

    // do unit testing
    public static void main(String[] args) {
        In in = new In(args[0]);
        String[] dictionary = in.readAllStrings();
        BoggleSolver solver = new BoggleSolver(dictionary);
        BoggleBoard board = new BoggleBoard(args[1]);

        int score = 0;
        for (String word : solver.getAllValidWords(board)) {
            StdOut.println(word);
            score += solver.scoreOf(word);
        }

        StdOut.println("Score = " + score);
    }
}
