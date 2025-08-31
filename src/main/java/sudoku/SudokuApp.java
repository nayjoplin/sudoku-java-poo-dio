package sudoku;

public class SudokuApp {
    public static void main(String[] args) {
        String joined = String.join(" ", args).trim();
        GameStatus status = new GameStatus("Jogo iniciado");
        Board board = new Board();

        if (!joined.isEmpty()) {
            board.applyArgs(joined);
            System.out.println("Status: " + status.getLabel() + " (modo: parâmetros)");
        } else {
            System.out.println("Status: " + status.getLabel() + " (tabuleiro vazio)");
        }

        System.out.println(board.toPrettyString());
    }
}
