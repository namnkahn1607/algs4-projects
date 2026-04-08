/* *****************************************************************************
 *  Name: Nguyen Le Nam Khanh
 *  Date: Mar 20, 2026
 *  Description: MoveToFront
 **************************************************************************** */

import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

public class MoveToFront {

    private static final int R = 256;

    // apply move-to-front encoding, reading from standard input and writing to standard output
    public static void encode() {
        final char[] sequence = new char[R];
        for (int i = 0; i < R; ++i) {
            sequence[i] = (char) i;
        }

        while (!BinaryStdIn.isEmpty()) {
            char ch = BinaryStdIn.readChar();

            for (int i = 0; i < R; ++i) {
                if (sequence[i] == ch) {
                    BinaryStdOut.write((byte) i);

                    for (int j = i; j > 0; --j) {
                        sequence[j] = sequence[j - 1];
                    }

                    break;
                }
            }


            sequence[0] = ch;
        }

        BinaryStdOut.close();
    }

    // apply move-to-front decoding, reading from standard input and writing to standard output
    public static void decode() {
        final char[] sequence = new char[R];
        for (int i = 0; i < R; ++i) {
            sequence[i] = (char) i;
        }

        while (!BinaryStdIn.isEmpty()) {
            char pos = BinaryStdIn.readChar();
            char ch = sequence[pos];
            BinaryStdOut.write(ch);

            for (int i = pos; i > 0; --i) {
                sequence[i] = sequence[i - 1];
            }

            sequence[0] = ch;
        }

        BinaryStdOut.close();
    }

    // if args[0] is "-", apply move-to-front encoding
    // if args[0] is "+", apply move-to-front decoding
    public static void main(String[] args) {
        switch (args[0]) {
            case "-":
                encode();
                break;
            case "+":
                decode();
                break;
            default:
                throw new IllegalArgumentException("Unknown argument: " + args[0]);
        }
    }
}
