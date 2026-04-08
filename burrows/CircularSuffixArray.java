/* *****************************************************************************
 *  Name: Nguyen Le Nam Khanh
 *  Date: Mar 22, 2026
 *  Description: CircularSuffixArray
 **************************************************************************** */

public class CircularSuffixArray {

    private final int[] indices;

    // circular suffix array of s
    public CircularSuffixArray(String s) {
        if (s == null) {
            throw new IllegalArgumentException("constructor argument cannot be null");
        }

        final int N = s.length();

        this.indices = new int[N];
        for (int i = 0; i < N; ++i) {
            indices[i] = i;
        }

        sort(s.toCharArray(), indices, 0, N - 1, 0);
    }

    private void sort(char[] text, int[] index, int low, int high, int d) {
        if (low >= high) {
            return;
        }

        int leftWall = low;
        int rightWall = high;
        int pivot = charAt(text, index[low], d);

        int i = low + 1;
        while (i <= rightWall) {
            int curr = charAt(text, index[i], d);

            if (curr < pivot) {
                swap(index, leftWall++, i++);
            }
            else if (curr > pivot) {
                swap(index, i, rightWall--);
            }
            else {
                i++;
            }
        }

        sort(text, index, low, leftWall - 1, d);

        if (pivot >= 0) {
            sort(text, index, leftWall, rightWall, d + 1);
        }

        sort(text, index, rightWall + 1, high, d);
    }

    private int charAt(char[] text, int base, int offset) {
        final int N = text.length;

        if (offset == N) {
            return -1;
        }

        int index = base + offset;
        if (index >= N) {
            index -= N;
        }

        return text[index];
    }

    private void swap(int[] index, int i, int j) {
        int tmp = index[i];
        index[i] = index[j];
        index[j] = tmp;
    }

    // length of s
    public int length() {
        return indices.length;
    }

    // returns index of ith sorted suffix
    public int index(int i) {
        if (i < 0 || i >= length()) {
            throw new IllegalArgumentException("index out of range");
        }

        return indices[i];
    }
}
