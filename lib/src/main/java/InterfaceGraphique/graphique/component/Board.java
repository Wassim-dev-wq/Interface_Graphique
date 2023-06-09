package InterfaceGraphique.graphique.component;

import InterfaceGraphique.Game.Game;
import InterfaceGraphique.Game.LevelSelection;
import InterfaceGraphique.algorithm.ElectricityHandler;
import InterfaceGraphique.algorithm.Level;
import InterfaceGraphique.graphique.ComponentType.Component;
import InterfaceGraphique.graphique.ComponentType.Source;

import java.io.File;
import java.util.ArrayList;
import java.util.logging.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import static InterfaceGraphique.algorithm.ElectricityHandler.LOGGER;

public class Board extends JPanel {
    private Game game;
    private Level level;
    private int currentLevel;

    private Component[][] components;
    private Map<String, Component> directionToComponentMap = new HashMap<>(); // to keep track on the directions of the components
    private String levelsType;
    private LevelSelection levelSelection;
    static int score;
    static int niveau;



    public Board(Game game, int level, String levelsType, LevelSelection levelSelection) {
        this.levelSelection = levelSelection;
        this.currentLevel = level;
        this.levelsType = levelsType;
        this.game = game;
        setLayout(new BorderLayout());
        add(new ControlPanel(game, level, levelsType,levelSelection), BorderLayout.NORTH);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                System.out.println("Mouse clicked at: (" + e.getX() + ", " + e.getY() + ")");
                handleClick(e.getX(), e.getY());
            }
        });

    }

    public int getLevelWidth() {
        return level.getWidth();
    }

    public int getLevelHeight() {
        return level.getHeight();

    }

    // TODO
    public void updateScore() {
        this.score +=1;
        ControlPanel controlPanel = (ControlPanel) getComponent(0);
        controlPanel.updateScore(score);
    }

    public void updateLevel() {
        ControlPanel controlPanel = (ControlPanel) getComponent(0);
        controlPanel.updateLevel();
    }
    public void loadAndDisplayLevel(String levelFilePath) {
        level = new Level(levelFilePath);
        components = new Component[level.getHeight()][level.getWidth()];
        game.updateWindowSize(level.getHeight(), level.getWidth());
    }

    private void handleClick(int x, int y) {
        int rowIndex;
        int columnIndex;

        // Handling for square tiles
        if (level.getFormat().equals("S")) {
            rowIndex = (y - 70) / 120; // the -70 because I added it to the window in the game
            columnIndex = (x - 50) / 120;
            LOGGER.info("Handling square tiles - calculated rowIndex: " + rowIndex + ", columnIndex: " + columnIndex);
        }
        // Handling for hexagonal tiles
        else {
            rowIndex = (y - 60) / 104;
            if (rowIndex % 2 == 0) {
                columnIndex = (x - 70) / 90;
            } else {
                columnIndex = (x - 50) / 90;
            }
            LOGGER.info("Handling hexagonal tiles - calculated rowIndex: " + rowIndex + ", columnIndex: " + columnIndex);
        }

        // Handle click on a component.
        if (rowIndex >= 0 && rowIndex < components.length &&
                columnIndex >= 0 && columnIndex < components[rowIndex].length) {
            LOGGER.info("Click is within the grid at rowIndex: " + rowIndex + ", columnIndex: " + columnIndex);
            Component clickedComponent = components[rowIndex][columnIndex];
            if (clickedComponent != null && clickedComponent.getClass() != Source.class) {
                LOGGER.info("Component at clicked position is not null, handling the click.");
                if (clickedComponent.containsPoint(x, y)) {
                    List<Integer>[][] directions = level.getElectricityHandler().getDirections();
                    directions[rowIndex][columnIndex] = clickedComponent.previewDirections();
                    level.getElectricityHandler().setDirections(directions);
                    for (int i = 0; i < directions.length; i++) {
                        for (int j = 0; j < directions[i].length; j++) {
                            for (int k = 0; k < directions[i][j].size(); k++) {
                                System.out.print(directions[i][j].get(k));
                                if (k < directions[i][j].size() - 1) {
                                    System.out.print(", ");
                                }
                            }
                        }
                        System.out.println();
                    }
                    ElectricityHandler electricityHandler = level.getElectricityHandler();
                    electricityHandler.resetElectricity();
                    electricityHandler.propagateElectricity();
                    for (int i = 0; i < components.length; i++) {
                        for (int j = 0; j < components[i].length; j++) {
                            Component component = components[i][j];
                            if (component != null) {
                                boolean isOn = component.getIsOn();
                                System.out.println("Component at rowIndex: " + i + ", columnIndex: " + j + " isOn: " + isOn);
                            }
                        }
                    }
                    boolean[][] hasElectric = electricityHandler.getHasElectric();
                    boolean allComponentsPowered = true;
                    for (boolean[] row : hasElectric) {
                        for (boolean powered : row) {
                            System.out.println(row +" "+powered);
                            if (!powered) {
                                allComponentsPowered = false;
                                break;
                            }
                        }
                    }
                    if (allComponentsPowered) {
                        updateScore();
                        updateLevel();
                        String fileName = "level" + (currentLevel + 1) + ".nrg";
                        String nextLevelFilePath = "/Levels/"+levelsType+"/" + fileName;
                        File nextLevelFile = new File(nextLevelFilePath);
                        if (nextLevelFile.exists()) {
                            currentLevel++;
                            loadAndDisplayLevel(nextLevelFilePath);
                            this.repaint();
                        } else {
                            currentLevel = 1;
                            loadAndDisplayLevel(nextLevelFilePath);
                            this.repaint();
                        }
                    }
                    LOGGER.info("Component was clicked. It was rotated and its directions updated.");
                }

                LOGGER.info("Electricity propagation completed, component states updated. Requesting repaint.");
                this.repaint();
            }
        } else {
            LOGGER.info("Click is outside of the grid, no action taken.");
        }
        LOGGER.info("Exiting handleClick method.");
    }


    private void createComponents() {
        int horizontalAlignment = (getWidth() - level.getWidth() * 120) / 2;
        int verticalAlignment = (getHeight() - level.getHeight() * 120) / 2;
        for (int j = 0; j < level.getHeight(); j++) {
            for (int k = 0; k < level.getWidth(); k++) {
                String tileType = level.getComponents()[j][k];
                boolean has_electric = level.getElectricityHandler().getHasElectric()[j][k];
                int y_value = 0;
                int x_value = 0;
                int width = 120;
                int height = 0;
                if (level.getFormat().equals("S")) {
                    x_value = k * 120 + horizontalAlignment;
                    y_value = j * 120 + verticalAlignment;
                    height = 120;
                } else {
                    int y = 0;
                    if (k % 2 == 1) y = 52;
                    x_value = k * 90 + horizontalAlignment;
                    y_value = (j * 104) + y + verticalAlignment;
                    height = 104;
                }
                Component component = Components.createComponent(tileType, x_value, y_value, width, height, has_electric, level.getDirections()[j][k], level.getFormat());
                components[j][k] = component;
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (level != null) {
            game.updateWindowSize(level.getHeight(), level.getWidth());
        }
        // need to be called only when level change //TODO
        createComponents();

        for (int i = 0; i < components.length; i++) {
            for (int j = 0; j < components[i].length; j++) {
                Component component = components[i][j];
                if (component != null) {
                    component.draw(g);
                }
            }
        }
    }
    public Component getComponentInDirection(String direction) {
        return directionToComponentMap.get(direction);
    }

}
