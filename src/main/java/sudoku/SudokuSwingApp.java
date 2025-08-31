package sudoku;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;

public class SudokuSwingApp {
    public static void main(String[] args) {
        String joined = String.join(" ", args).trim();
        Board board = new Board();
        if (!joined.isEmpty()) board.applyArgs(joined);
        else board.generateRandomPuzzle(40); // sem args: gera puzzle médio

        SwingUtilities.invokeLater(() -> showUI(board));
    }

    private static void showUI(Board board) {
        JFrame frame = new JFrame("Sudoku - DIO");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(560, 620);
        frame.setLayout(new BorderLayout());

        JLabel status = new JLabel("Jogo iniciado");
        status.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        frame.add(status, BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridLayout(9, 9));
        frame.add(grid, BorderLayout.CENTER);

        // flag para não abrir o diálogo de vitória múltiplas vezes
        final boolean[] winShown = { false };

        for (int y = 0; y < 9; y++) {
            for (int x = 0; x < 9; x++) {
                // cópias finais para usar no listener
                final int cx = x;
                final int cy = y;
                final Board b = board;
                final JFrame owner = frame;

                Space s = b.get(cx, cy);
                JTextField field = new JTextField();
                field.setHorizontalAlignment(JTextField.CENTER);
                field.setFont(field.getFont().deriveFont(Font.BOLD, 20f));
                field.setBorder(createCellBorder(cx, cy));

                if (s.isFixed()) {
                    field.setText(String.valueOf(s.getExpected()));
                    field.setEditable(false);
                    field.setBackground(new Color(235, 235, 235));
                } else {
                    field.getDocument().addDocumentListener(new DocumentListener() {
                        private void update() {
                            String txt = field.getText().trim();
                            if (txt.isEmpty()) {
                                b.setValue(cx, cy, null);
                                field.setBackground(Color.WHITE);
                                status.setText("Jogada apagada");
                                return;
                            }
                            if (txt.length() > 1 || !txt.matches("[1-9]")) {
                                field.setText("");
                                b.setValue(cx, cy, null);
                                field.setBackground(Color.WHITE);
                                status.setText("Somente 1 dígito (1-9)");
                                return;
                            }
                            int v = Integer.parseInt(txt);
                            if (!b.isPlacementValid(cx, cy, v)) {
                                field.setBackground(new Color(255, 220, 220)); // conflito
                                status.setText("Conflito na linha/coluna/subgrade");
                                return;
                            }
                            b.setValue(cx, cy, v);
                            field.setBackground(b.get(cx, cy).isCorrect()
                                    ? new Color(210, 255, 210) // correto
                                    : Color.WHITE);            // válido mas diferente do expected

                            if (b.isSolved()) {
                                status.setText("Resolvido! Parabéns 🎉");
                                if (!winShown[0]) {
                                    winShown[0] = true;
                                    SwingUtilities.invokeLater(() -> showWinDialog(owner));
                                }
                            } else {
                                status.setText("Jogada válida");
                            }
                        }

                        public void insertUpdate(DocumentEvent e) { update(); }
                        public void removeUpdate(DocumentEvent e) { update(); }
                        public void changedUpdate(DocumentEvent e) { update(); }
                    });
                }
                grid.add(field);
            }
        }

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    /** Bordas mais grossas a cada bloco 3x3 para destacar a grade. */
    private static javax.swing.border.Border createCellBorder(int x, int y) {
        int top = (y % 3 == 0) ? 3 : 1;
        int left = (x % 3 == 0) ? 3 : 1;
        int bottom = (y == 8) ? 3 : 1;
        int right = (x == 8) ? 3 : 1;
        return BorderFactory.createMatteBorder(top, left, bottom, right, Color.GRAY);
    }

    /** Diálogo de vitória com opções de Novo jogo e Sair. */
    private static void showWinDialog(JFrame owner) {
        JDialog dialog = new JDialog(owner, "Você venceu! 🎉", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(380, 220);

        JLabel title = new JLabel("🎉 Sudoku Resolvido! 🎉", SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 22f));
        title.setBorder(BorderFactory.createEmptyBorder(16, 16, 8, 16));

        JLabel msg = new JLabel("Parabéns! Você completou o puzzle.", SwingConstants.CENTER);
        msg.setBorder(BorderFactory.createEmptyBorder(0, 16, 16, 16));

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 8));
        JButton novo = new JButton("Novo jogo");
        JButton sair = new JButton("Sair");

        // novo jogo: fecha a janela atual e abre outro tabuleiro
        novo.addActionListener(e -> {
            dialog.dispose();
            owner.dispose();
            Board nb = new Board();
            nb.generateRandomPuzzle(40);
            SwingUtilities.invokeLater(() -> showUI(nb));
        });

        sair.addActionListener(e -> {
            dialog.dispose();
            owner.dispose();
        });

        buttons.add(novo);
        buttons.add(sair);

        dialog.add(title, BorderLayout.NORTH);
        dialog.add(msg, BorderLayout.CENTER);
        dialog.add(buttons, BorderLayout.SOUTH);
        dialog.setLocationRelativeTo(owner);
        dialog.setVisible(true);
    }
}
    