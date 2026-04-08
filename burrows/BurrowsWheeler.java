/* *****************************************************************************
 *  Name: Nguyen Le Nam Khanh
 *  Date: Mar 22, 2026
 *  Description: BurrowsWheeler
 **************************************************************************** */

import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

public class BurrowsWheeler {

    private static final int R = 256;

    // apply Burrows-Wheeler transform,
    // reading from standard input and writing to standard output
    public static void transform() {
        String s = BinaryStdIn.readString();
        CircularSuffixArray csa = new CircularSuffixArray(s);
        final int N = csa.length();

        for (int i = 0; i < N; ++i) {
            if (csa.index(i) == 0) {
                BinaryStdOut.write(i);
                break;
            }
        }

        for (int i = 0; i < N; ++i) {
            int base = csa.index(i);
            int index = (base == 0) ? N - 1 : base - 1;
            BinaryStdOut.write(s.charAt(index));
        }

        BinaryStdOut.close();
    }

    // apply Burrows-Wheeler inverse transform,
    // reading from standard input and writing to standard output
    public static void inverseTransform() {
        int first = BinaryStdIn.readInt();
        char[] t = BinaryStdIn.readString().toCharArray();

        final int N = t.length;
        int[] next = new int[N];
        char[] f = sort(t, next);

        for (int i = 0; i < N; ++i) {
            BinaryStdOut.write(f[first]);
            first = next[first];
        }

        BinaryStdOut.close();
    }

    private static char[] sort(char[] t, int[] next) {
        final int N = t.length;
        int[] count = new int[R + 1];

        for (int i = 0; i < N; ++i) {
            count[t[i] + 1]++;
        }

        for (int r = 0; r < R; ++r) {
            count[r + 1] += count[r];
        }

        char[] f = new char[N];
        for (int i = 0; i < N; ++i) {
            f[count[t[i]]] = t[i];
            next[count[t[i]]] = i;
            count[t[i]]++;
        }

        return f;
    }

    // if args[0] is "-", apply Burrows-Wheeler transform
    // if args[0] is "+", apply Burrows-Wheeler inverse transform
    public static void main(String[] args) {
        switch (args[0]) {
            case "-":
                transform();
                break;
            case "+":
                inverseTransform();
                break;
            default:
                throw new IllegalArgumentException("Unknown argument: " + args[0]);
        }
    }
}
