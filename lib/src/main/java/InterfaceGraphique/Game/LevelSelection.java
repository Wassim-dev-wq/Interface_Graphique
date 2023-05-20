package InterfaceGraphique.Game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.InputStream;

import InterfaceGraphique.graphique.component.Board;
import InterfaceGraphique.graphique.component.Empty_Board;

public class LevelSelection extends JPanel {
    private Game game;
    private Board board;
    private Empty_Board emptyBoard;

    public LevelSelection(Game game) {
        this.game = game;

        setLayout(new BorderLayout());

        // Add a title label
        JLabel titleLabel = new JLabel("Energy Game", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Serif", Font.BOLD, 30));
        add(titleLabel, BorderLayout.NORTH);

        // Use a GridBagLayout to allow more control over button placement
        JPanel levelsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gridBag = new GridBagConstraints();
        gridBag.insets = new Insets(10, 10, 10, 10);

        int total_level = 10;
        for (int i = 1; i <= total_level; i++) {
            addButton("Level " + i, i, levelsPanel, gridBag);
        }
        JButton button_create = new JButton("Create");
        button_create.setPreferredSize(new Dimension(120, 120));
        button_create.setFont(new Font("Arial", Font.PLAIN, 18));
        button_create.setBackground(Color.DARK_GRAY);
        int row = (total_level+1 - 1) / 3;
        int col = (total_level+1 - 1) % 3;
        gridBag.gridx = col;
        gridBag.gridy = row;
        button_create.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                String height[]={"2","3","4","5","6","7","8"};
                String width[]={"2","3","4","5","6","7","8"};
                String format[] = {"Square", "Hexagone"};

                JComboBox combo_width=new JComboBox(width);
                JComboBox combo_height=new JComboBox(height);
                JComboBox combo_format=new JComboBox(format);
                JPanel myPanel = new JPanel();
                myPanel.add(new JLabel("Width:"));
                myPanel.add(combo_width);
                myPanel.add(Box.createHorizontalStrut(15)); // a spacer
                myPanel.add(new JLabel("Height:"));
                myPanel.add(combo_height);
                myPanel.add(Box.createHorizontalStrut(15)); // a spacer
                myPanel.add(new JLabel("Format:"));
                myPanel.add(combo_format);
                JOptionPane.showMessageDialog(null, myPanel);
                int height_ = Integer.parseInt(combo_height.getSelectedItem().toString());
                int width_ =Integer.parseInt(combo_width.getSelectedItem().toString());
                JPanel gamePanel = new JPanel(new BorderLayout());
                emptyBoard = new Empty_Board(game, width_, height_, combo_format.getSelectedItem().toString());
                emptyBoard.setBackground(Color.BLACK);
                game.updateWindowSize(emptyBoard.getHeight(), emptyBoard.getWidth());
                Dimension boardSize = new Dimension(10 * 120, 10 * 120);
                emptyBoard.setPreferredSize(boardSize);
                gamePanel.add(emptyBoard, BorderLayout.CENTER);
                game.setContentPane(gamePanel);
                game.pack();
                game.setLocationRelativeTo(null);
            }
        });
        levelsPanel.add(button_create,gridBag);

        add(levelsPanel, BorderLayout.CENTER);
    }

    private void addButton(String label, int levelNumber, JPanel levelsPanel, GridBagConstraints gridBag) {
        JButton button = new JButton(label);

        // Style the buttons
        button.setPreferredSize(new Dimension(120, 120));
        button.setFont(new Font("Arial", Font.PLAIN, 18));
        button.setBackground(Color.DARK_GRAY);
//        button.setForeground(Color.WHITE);
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);

        // grid position
        int row = (levelNumber - 1) / 3;
        int col = (levelNumber - 1) % 3;
        gridBag.gridx = col;
        gridBag.gridy = row;

//        if (levelNumber == 10) {
//            gridBag.gridwidth = 1;
//            gridBag.gridx = 1;
//        } else {
//            gridBag.gridwidth = 1;
//        }
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JPanel gamePanel = new JPanel(new BorderLayout());
                board = new Board(game, levelNumber);
                board.setBackground(Color.BLACK);
                String levelFilePath = "/Levels/game_levels/level" + levelNumber + ".nrg";
                board.loadAndDisplayLevel(levelFilePath);
                Dimension boardSize = new Dimension(board.getLevelWidth() * 120, board.getLevelHeight() * 120);
                board.setPreferredSize(boardSize);
                gamePanel.add(board, BorderLayout.CENTER);
                game.setContentPane(gamePanel);
                game.pack();
                game.setLocationRelativeTo(null);
            }
        });

        levelsPanel.add(button, gridBag);
    }
}
