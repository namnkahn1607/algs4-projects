/* *****************************************************************************
 *  Name: Nguyen Le Nam Khanh
 *  Date: Jan 21 - 22, 2026
 *  Description: Seam Carver
 **************************************************************************** */

import edu.princeton.cs.algs4.Picture;

public class SeamCarver {

    private static final int RED = 16;
    private static final int GREEN = 8;
    private static final int BLUE = 0;
    private static final double BORDER_ENERGY = 1000.0;
    private static final int[][] VERTICAL_DIRECTIONS = { { 1, -1 }, { 1, 0 }, { 1, 1 } };
    private static final int[][] HORIZONTAL_DIRECTIONS = { { -1, 1 }, { 0, 1 }, { 1, 1 } };
    private int[][] colorMap;
    private double[][] energyMap;

    // create a seam carver object based on the given picture
    public SeamCarver(Picture picture) {
        checkNonNull(picture);
        final int COL = picture.width();
        final int ROW = picture.height();

        this.colorMap = new int[ROW][COL];
        this.energyMap = new double[ROW][COL];

        for (int r = 0; r < ROW; ++r) {
            for (int c = 0; c < COL; ++c) {
                this.colorMap[r][c] = picture.getRGB(c, r);
            }
        }

        for (int r = 0; r < ROW; ++r) {
            for (int c = 0; c < COL; ++c) {
                this.energyMap[r][c] = energy(c, r);
            }
        }
    }

    // current picture
    public Picture picture() {
        final int ROW = height();
        final int COL = width();

        Picture picture = new Picture(COL, ROW);
        for (int r = 0; r < ROW; ++r) {
            for (int c = 0; c < COL; ++c) {
                picture.setRGB(c, r, colorMap[r][c]);
            }
        }

        return picture;
    }

    // width of the current picture
    public int width() {
        return this.colorMap[0].length;
    }

    // height of the current picture
    public int height() {
        return this.colorMap.length;
    }

    // energy of pixel at column 'col' and row 'row'
    public double energy(int col, int row) {
        final int ROW = height();
        final int COL = width();

        if (!checkInBound(col, row)) {
            throw new IllegalArgumentException("pixel out of bounds");
        }

        if (col == 0 || col == COL - 1 || row == 0 || row == ROW - 1) {
            return BORDER_ENERGY;
        }

        return Math.sqrt(xGradientSq(col, row) + yGradientSq(col, row));
    }

    // sequence of indices for horizontal seam
    public int[] findHorizontalSeam() {
        final int ROW = height();
        final int COL = width();
        double[][] distTo = new double[ROW][COL];
        int[][] edgeTo = new int[ROW][COL];

        for (int r = 0; r < ROW; ++r) {
            for (int c = 0; c < COL; ++c) {
                distTo[r][c] = Double.POSITIVE_INFINITY;
            }
        }

        for (int r = 0; r < ROW; ++r) {
            distTo[r][0] = BORDER_ENERGY;
        }

        for (int c = 0; c < COL - 1; ++c) {
            for (int r = 0; r < ROW; ++r) {
                for (int[] dir : HORIZONTAL_DIRECTIONS) {
                    final int newR = r + dir[0];
                    final int newC = c + dir[1];

                    if (checkInBound(newC, newR)) {
                        double newDist = distTo[r][c] + energyMap[newR][newC];
                        if (newDist < distTo[newR][newC]) {
                            distTo[newR][newC] = newDist;
                            edgeTo[newR][newC] = r;
                        }
                    }
                }
            }
        }

        double minDist = Double.POSITIVE_INFINITY;
        int minEnd = -1;
        for (int r = 0; r < ROW; ++r) {
            if (distTo[r][COL - 1] < minDist) {
                minDist = distTo[r][COL - 1];
                minEnd = r;
            }
        }

        int[] seam = new int[COL];
        for (int c = COL - 1; c >= 0; --c) {
            seam[c] = minEnd;
            minEnd = edgeTo[minEnd][c];
        }

        return seam;
    }

    // sequence of indices for vertical seam
    public int[] findVerticalSeam() {
        final int ROW = height();
        final int COL = width();
        double[][] distTo = new double[ROW][COL];
        int[][] edgeTo = new int[ROW][COL];

        for (int r = 0; r < ROW; ++r) {
            for (int c = 0; c < COL; ++c) {
                distTo[r][c] = Double.POSITIVE_INFINITY;
            }
        }

        for (int c = 0; c < COL; ++c) {
            distTo[0][c] = BORDER_ENERGY;
        }

        for (int r = 0; r < ROW - 1; ++r) {
            for (int c = 0; c < COL; ++c) {
                for (int[] dir : VERTICAL_DIRECTIONS) {
                    final int newR = r + dir[0];
                    final int newC = c + dir[1];

                    if (checkInBound(newC, newR)) {
                        double newDist = distTo[r][c] + energyMap[newR][newC];
                        if (newDist < distTo[newR][newC]) {
                            distTo[newR][newC] = newDist;
                            edgeTo[newR][newC] = c;
                        }
                    }
                }
            }
        }

        double minDist = Double.POSITIVE_INFINITY;
        int minEnd = -1;
        for (int c = 0; c < COL; ++c) {
            if (distTo[ROW - 1][c] < minDist) {
                minDist = distTo[ROW - 1][c];
                minEnd = c;
            }
        }

        int[] seam = new int[ROW];
        for (int r = ROW - 1; r >= 0; --r) {
            seam[r] = minEnd;
            minEnd = edgeTo[r][minEnd];
        }

        return seam;
    }

    // remove horizontal seam from current picture
    public void removeHorizontalSeam(int[] seam) {
        tranpose();
        removeVerticalSeam(seam);
        tranpose();
    }

    // remove vertical seam from current picture
    public void removeVerticalSeam(int[] seam) {
        checkNonNull(seam);
        final int ROW = height();
        final int COL = width();

        if (seam.length != ROW) {
            throw new IllegalArgumentException("miscomputed vertical seam");
        }

        for (int i = 0; i < ROW; ++i) {
            if (seam[i] < 0 || seam[i] >= COL) {
                throw new IllegalArgumentException("seam entry out of bounds");
            }

            if (i > 0 && Math.abs(seam[i] - seam[i - 1]) > 1) {
                throw new IllegalArgumentException("disconnected seam");
            }
        }

        if (COL <= 1) {
            throw new IllegalArgumentException("cannot carve more vertical seam");
        }

        final int newCOL = COL - 1;
        int[][] newColorMap = new int[ROW][newCOL];
        double[][] newEnergyMap = new double[ROW][newCOL];

        for (int r = 0; r < ROW; ++r) {
            int c = seam[r];
            System.arraycopy(colorMap[r], 0, newColorMap[r], 0, c);
            System.arraycopy(colorMap[r], c + 1, newColorMap[r], c, newCOL - c);
            System.arraycopy(energyMap[r], 0, newEnergyMap[r], 0, c);
            System.arraycopy(energyMap[r], c + 1, newEnergyMap[r], c, newCOL - c);
        }

        this.colorMap = newColorMap;
        this.energyMap = newEnergyMap;

        for (int r = 0; r < ROW; ++r) {
            int c = seam[r];

            if (c < newCOL) {
                energyMap[r][c] = energy(c, r);
            }

            if (c > 0) {
                energyMap[r][c - 1] = energy(c - 1, r);
            }
        }
    }

    private void checkNonNull(Object arg) {
        if (arg == null) {
            throw new IllegalArgumentException("argument is null");
        }
    }

    private boolean checkInBound(int col, int row) {
        return !(Math.min(row, col) < 0 || col >= width() || row >= height());
    }

    private double xGradientSq(int col, int row) {
        int leftRGB = colorMap[row][col - 1];
        int rightRGB = colorMap[row][col + 1];
        return dualGradient(leftRGB, rightRGB);
    }

    private double yGradientSq(int col, int row) {
        int topRGB = colorMap[row - 1][col];
        int bottomRGB = colorMap[row + 1][col];
        return dualGradient(topRGB, bottomRGB);
    }

    private double dualGradient(int rgb1, int rgb2) {
        double dr = colorFactor(rgb1, RED) - colorFactor(rgb2, RED);
        double dg = colorFactor(rgb1, GREEN) - colorFactor(rgb2, GREEN);
        double db = colorFactor(rgb1, BLUE) - colorFactor(rgb2, BLUE);
        return dr * dr + dg * dg + db * db;
    }

    private double colorFactor(int rgb, int colorOffset) {
        return (rgb >> colorOffset) & 0xFF;
    }

    private void tranpose() {
        final int ROW = height();
        final int COL = width();
        int[][] transColorMap = new int[COL][ROW];
        double[][] transEnergyMap = new double[COL][ROW];

        for (int r = 0; r < ROW; ++r) {
            for (int c = 0; c < COL; ++c) {
                transColorMap[c][r] = colorMap[r][c];
                transEnergyMap[c][r] = energyMap[r][c];
            }
        }

        this.colorMap = transColorMap;
        this.energyMap = transEnergyMap;
    }
}
