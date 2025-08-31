package sudoku;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Board {
    private final Space[][] grid = new Space[9][9];
    private final Random rnd = new Random();

    public Board() {
        for (int y = 0; y < 9; y++) {
            for (int x = 0; x < 9; x++) {
                grid[y][x] = new Space(0, false);
            }
        }
    }

    public void applyArgs(String argLine) {
        if (argLine == null || argLine.trim().isEmpty()) return;
        String[] items = argLine.trim().split("\\s+");
        for (String item : items) {
            String[] parts = item.split(";");
            if (parts.length != 2) continue;
            String[] xy = parts[0].split(",");
            if (xy.length != 2) continue;

            int x = Integer.parseInt(xy[0]);
            int y = Integer.parseInt(xy[1]);

            String[] valFix = parts[1].split(",");
            if (valFix.length != 2) continue;

            int value = Integer.parseInt(valFix[0]);
            boolean fixed = Boolean.parseBoolean(valFix[1]);

            if (x < 0 || x > 8 || y < 0 || y > 8) continue;
            grid[y][x] = new Space(value, fixed);
        }
    }

    public boolean setValue(int x, int y, Integer val) {
        Space s = grid[y][x];
        if (s.isFixed()) return false;
        if (val == null) { s.setReal(null); return true; }
        if (val < 1 || val > 9) return false;
        if (!isPlacementValid(x, y, val)) return false;
        s.setReal(val);
        return true;
    }

    public boolean isPlacementValid(int x, int y, int val) {
        for (int cx = 0; cx < 9; cx++) {
            if (cx == x) continue;
            int comp = grid[y][cx].isFixed()
                    ? grid[y][cx].getExpected()
                    : (grid[y][cx].getReal() == null ? 0 : grid[y][cx].getReal());
            if (comp == val) return false;
        }
        for (int cy = 0; cy < 9; cy++) {
            if (cy == y) continue;
            int comp = grid[cy][x].isFixed()
                    ? grid[cy][x].getExpected()
                    : (grid[cy][x].getReal() == null ? 0 : grid[cy][x].getReal());
            if (comp == val) return false;
        }
        int boxX = (x / 3) * 3, boxY = (y / 3) * 3;
        for (int dy = 0; dy < 3; dy++) {
            for (int dx = 0; dx < 3; dx++) {
                int cx = boxX + dx, cy = boxY + dy;
                if (cx == x && cy == y) continue;
                int comp = grid[cy][cx].isFixed()
                        ? grid[cy][cx].getExpected()
                        : (grid[cy][cx].getReal() == null ? 0 : grid[cy][cx].getReal());
                if (comp == val) return false;
            }
        }
        return true;
    }

    public boolean isSolved() {
        for (int y = 0; y < 9; y++) {
            for (int x = 0; x < 9; x++) {
                Space s = grid[y][x];
                if (!s.isFixed()) {
                    Integer r = s.getReal();
                    if (r == null || r != s.getExpected()) return false;
                }
            }
        }
        return true;
    }

    public Space get(int x, int y) { return grid[y][x]; }

    public String toPrettyString() {
        StringBuilder sb = new StringBuilder();
        for (int y = 0; y < 9; y++) {
            if (y % 3 == 0) sb.append("+-------+-------+-------+\n");
            for (int x = 0; x < 9; x++) {
                if (x % 3 == 0) sb.append("| ");
                Space s = grid[y][x];
                String cell;
                if (s.isFixed()) cell = "[" + s.getExpected() + "]";
                else {
                    Integer r = s.getReal();
                    cell = (r == null || r == 0) ? "." : r.toString();
                }
                sb.append(String.format("%-3s", cell));
            }
            sb.append("|\n");
        }
        sb.append("+-------+-------+-------+\n");
        return sb.toString();
    }

    public void generateRandomPuzzle(int holes) {
        int[][] solved = generateSolvedGrid();
        for (int y = 0; y < 9; y++)
            for (int x = 0; x < 9; x++)
                grid[y][x] = new Space(solved[y][x], true);

        List<int[]> cells = new ArrayList<>();
        for (int y = 0; y < 9; y++)
            for (int x = 0; x < 9; x++)
                cells.add(new int[]{x, y});
        Collections.shuffle(cells, new Random());
        for (int i = 0; i < Math.min(holes, cells.size()); i++) {
            int[] c = cells.get(i);
            int x = c[0], y = c[1];
            grid[y][x] = new Space(solved[y][x], false);
            grid[y][x].setReal(null);
        }
    }

    private int[][] generateSolvedGrid() {
        int base = 3, side = base * base;
        java.util.function.BiFunction<Integer, Integer, Integer> pattern =
                (r, c) -> (base * (r % base) + r / base + c) % side;

        List<Integer> rBase = new ArrayList<>();
        for (int i = 0; i < base; i++) rBase.add(i);

        List<Integer> rows = new ArrayList<>();
        List<Integer> cols = new ArrayList<>();
        for (int g : shuffle(rBase)) for (int r : shuffle(rBase)) rows.add(g * base + r);
        for (int g : shuffle(rBase)) for (int c : shuffle(rBase)) cols.add(g * base + c);
        List<Integer> nums = shuffle(range(1, side + 1));

        int[][] board = new int[side][side];
        for (int r = 0; r < side; r++)
            for (int c = 0; c < side; c++)
                board[r][c] = nums.get(pattern.apply(rows.get(r), cols.get(c)));
        return board;
    }

    private List<Integer> range(int start, int end) {
        List<Integer> list = new ArrayList<>();
        for (int i = start; i < end; i++) list.add(i);
        return list;
    }

    private <T> List<T> shuffle(List<T> list) {
        List<T> copy = new ArrayList<>(list);
        Collections.shuffle(copy);
        return copy;
    }
}
