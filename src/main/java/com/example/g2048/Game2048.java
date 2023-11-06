package com.example.g2048;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.io.*;
import java.util.Objects;
import java.util.Random;


public class Game2048 extends Application {
    private static final int SIZE = 4; // Size of the grid
    private static final int TILE_SIZE = 130; // Size of each tile
    private static final int WIDTH = SIZE * TILE_SIZE;
    private static final int HEIGHT = SIZE * TILE_SIZE;
    private final Random random = new Random();
    private int[][] grid = new int[SIZE][SIZE];
    private final Rectangle[][] tiles = new Rectangle[SIZE][SIZE];
    private final Label scoreLabel = new Label("Score: 0");
    private int score = 0;
    private int highestScore = 0;

    private final String SCORE_FILE = "highest_score.txt";

    @Override
    public void start(Stage primaryStage) {
        GridPane root = createGridPane();
        initializeGrid();
        addNewNumber();
        addNewNumber();
        updateGrid();

        Scene scene = new Scene(root,800,800);
        responsiveness(root,scene);

        scene.setOnKeyPressed(event -> handleKeyPress(event.getCode()));

        primaryStage.setTitle("2048");
        primaryStage.setScene(scene);
        //primaryStage.setResizable(false);
        primaryStage.show();

    }




    private void createScoreStage() {

        loadHighestScore();

        if(score >  highestScore){
            highestScore = score;
            saveHighestScore();
        }
        else {
            loadHighestScore();

        }



        Stage scoreStage = new Stage();
        scoreStage.setTitle("Score");

        BorderPane root = new BorderPane();
        root.setPrefSize(300, 250);
        root.setPadding(new javafx.geometry.Insets(10));

        Label highestScoreLabel = new Label("Highest Score: " + highestScore);
        Label currentScoreLabel = new Label("Your Score: " + score);

        highestScoreLabel.setFont(Font.font(15));
        currentScoreLabel.setFont(Font.font(25));


        Button refreshButton = new Button("Refresh Game");
        Button resetHighScore = new Button("Reset High Score");

        refreshButton.setOnAction(event -> {
            resetGame();
            updateGrid();
            scoreStage.close();

        });
        resetHighScore.setOnAction(event -> {
            resetHighScore();
            highestScoreLabel.setText("Highest Score: " + highestScore);
        });


        root.setTop(currentScoreLabel);
        BorderPane.setAlignment(currentScoreLabel, Pos.CENTER);


        root.setCenter(highestScoreLabel);
        BorderPane.setAlignment(highestScoreLabel, Pos.CENTER);

        refreshButton.setBackground(Background.fill(Color.GREENYELLOW));
        resetHighScore.setBackground(Background.fill(Color.INDIANRED));

        VBox buttons = new VBox(refreshButton,resetHighScore);
        buttons.setAlignment(Pos.CENTER);
        buttons.setSpacing(5);

        root.setBottom(buttons);
        BorderPane.setAlignment(buttons, Pos.CENTER);


        Scene scoreScene = new Scene(root);
        scoreStage.setScene(scoreScene);

        scoreStage.setOnCloseRequest(event -> {
            resetGame();
            updateGrid();
        }); // Reset the game when window is closed

        scoreStage.show();
    }

    private void resetGame() {
        score = 0;
        grid = new int[SIZE][SIZE];
        initializeGrid();
        addNewNumber();
        addNewNumber();
    }

    private GridPane createGridPane() {
        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setPrefSize(WIDTH, HEIGHT);
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setStyle("-fx-background-color: #BBADA0");



        Label guidelineLabel = new Label("Guide");
        guidelineLabel.setFont(Font.font("Arial",FontWeight.BOLD,25));

        Label guideLabel = new Label("""

                Press A or Left Arrow to move Left\t\tPress B or Right Arrow to move Right
                Press S or Down Arrow to move Down\t\tPress W or Up Arrow to move Up\s""");
        guideLabel.setFont(Font.font(17));


        Button resetGameButton = new Button("Reset Game");
        resetGameButton.setBackground(Background.fill(Color.INDIANRED));

        resetGameButton.setOnAction(actionEvent -> {
            resetGame();
             updateGrid();
        });

        Label gameLabel = new Label("Welcome Gamer To 2048");
        gameLabel.setFont(Font.font("Arial",FontWeight.BOLD,25));


        HBox topBox = new HBox(gameLabel,resetGameButton);
        topBox.setSpacing(170);
        topBox.setPadding(new Insets(-610,0,0,0));





        scoreLabel.setStyle("-fx-font-size: 24;");
        guideLabel.setPadding(new Insets(15,0,0,5));


        gridPane.add(scoreLabel, 0, SIZE, SIZE, 1);
        gridPane.add(topBox, 0,SIZE,SIZE, 1);
        gridPane.add(guidelineLabel, 0, 5,5,3);
        gridPane.add(guideLabel, 0, 5,5,4);

        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                Rectangle tile = new Rectangle(TILE_SIZE, TILE_SIZE);
                tile.setFill(Color.rgb(205, 193, 180));
                gridPane.add(tile, col, row);
                tiles[row][col] = tile;
            }
        }

        return gridPane;
    }

    private void initializeGrid() {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                grid[row][col] = 0;
            }
        }
    }

    private void addNewNumber() {
        int row, col;
        do {
            row = random.nextInt(SIZE);
            col = random.nextInt(SIZE);
        } while (grid[row][col] != 0);

        grid[row][col] = random.nextInt(1) +2; // Generate either 1 or 2 (to represent 2 or 4)
    }

    private void updateGrid() {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                int value = grid[row][col];


                Rectangle tile = tiles[row][col];
                tile.setFill(getTileColor(value));
                tile.setStroke(Color.BLACK);
                tile.setStrokeWidth(3);

                Label label;
                if (value > 0) {
                    int labelValue =  Math.addExact(0, value );
                    label = new Label(String.valueOf(labelValue));
                } else {
                    label = new Label("");
                }
                label.setStyle("-fx-font-size: 25; -fx-font-weight: bold;");
                label.setTextFill(getTextColor(value));


                GridPane.setHalignment(label, Pos.CENTER.getHpos());
                GridPane.setValignment(label, Pos.CENTER.getVpos());
                GridPane.setMargin(label, new javafx.geometry.Insets((double) TILE_SIZE / 4));


                GridPane.setRowIndex(tile, row);
                GridPane.setColumnIndex(tile, col);
                GridPane.setRowIndex(label, row);
                GridPane.setColumnIndex(label, col);


                GridPane gridPane;


                gridPane = (GridPane) tile.getParent();
                gridPane.getChildren().remove(tile);
                gridPane.getChildren().add(tile);
                 gridPane.getChildren(); // This should work without throwing a NullPointerException


                gridPane.getChildren().remove(label);
                gridPane.getChildren().add(label);


            }
        }

        scoreLabel.setText("Score: " + score);
    }
    @SuppressWarnings("DuplicateBranchesInSwitch")
    private Color getTileColor(int value) {
        return switch (value) {
            case 0 -> Color.rgb(205, 193, 180);
            case 1 -> Color.rgb(238, 228, 218);
            case 2 -> Color.rgb(237, 224, 200);
            case 3 -> Color.rgb(242, 177, 121);
            case 4 -> Color.rgb(245, 149, 99);
            case 5 -> Color.rgb(246, 124, 95);
            case 6 -> Color.rgb(246, 94, 59);
            case 7 -> Color.rgb(237, 207, 114);
            case 8 -> Color.rgb(237, 204, 97);
            case 9 -> Color.rgb(237, 200, 80);
            case 10 -> Color.rgb(237, 197, 63);
            case 11 -> Color.rgb(237, 194, 46);
            default -> Color.rgb(237, 194, 46);
        };
    }
    private Color getTextColor(int value) {
        return value < 3 ? Color.rgb(119, 110, 101) : Color.rgb(249, 246, 242);
    }
    private void handleKeyPress(KeyCode keyCode) {
        switch (keyCode) {
            case W, UP -> {
                    if (canMoveUp()) {
                        moveUp();
                        addNewNumber();
                        updateGrid();
                    }
                }
            case A, LEFT -> {
                    if (canMoveLeft()) {
                        moveLeft();
                        addNewNumber();
                        updateGrid();
                    }
                }
            case S, DOWN -> {
                    if (canMoveDown()) {
                        moveDown();
                        addNewNumber();
                        updateGrid();
                    }
                }
            case D, RIGHT -> {
                    if (canMoveRight()) {
                        moveRight();
                        addNewNumber();
                        updateGrid();
                    }
                }
            default -> {
                }
            }

        if (isGameOver()) {
                createScoreStage();
                System.out.println("Game over!");
        }
    }
    private boolean canMoveUp() {
        for (int col = 0; col < SIZE; col++) {
            for (int row = 1; row < SIZE; row++) {
                if (grid[row][col] != 0 && (grid[row - 1][col] == 0 || grid[row - 1][col] == grid[row][col])) {
                    return true;
                }
            }
        }
        return false;
    }
    private boolean canMoveLeft() {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 1; col < SIZE; col++) {
                if (grid[row][col] != 0 && (grid[row][col - 1] == 0 || grid[row][col - 1] == grid[row][col])) {
                    return true;
                }
            }
        }
        return false;
    }
    private boolean canMoveDown() {
        for (int col = 0; col < SIZE; col++) {
            for (int row = SIZE - 2; row >= 0; row--) {
                if (grid[row][col] != 0 && (grid[row + 1][col] == 0 || grid[row + 1][col] == grid[row][col])) {
                    return true;
                }
            }
        }
        return false;
    }
    private boolean canMoveRight() {
        for (int row = 0; row < SIZE; row++) {
            for (int col = SIZE - 2; col >= 0; col--) {
                if (grid[row][col] != 0 && (grid[row][col + 1] == 0 || grid[row][col + 1] == grid[row][col])) {
                    return true;
                }
            }
        }
        return false;
    }
    private void moveUp() {
        for (int col = 0; col < SIZE; col++) {
            for (int row = 1; row < SIZE; row++) {
                if (grid[row][col] != 0) {
                    int currentRow = row;
                    while (currentRow > 0 && (grid[currentRow - 1][col] == 0)) {
                        grid[currentRow - 1][col] = grid[currentRow][col];
                        grid[currentRow][col] = 0;
                        currentRow--;
                    }
                    if (currentRow > 0 && (grid[currentRow - 1][col] == grid[currentRow][col])) {
                        grid[currentRow - 1][col] *= 2;
                        score += grid[currentRow - 1][col]; // Update the score with the merged tile value
                        grid[currentRow][col] = 0;
                    }
                }
            }
        }
    }
    private void moveLeft() {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 1; col < SIZE; col++) {
                if (grid[row][col] != 0) {
                    int currentCol = col;
                    while (currentCol > 0 && (grid[row][currentCol - 1] == 0)) {
                        grid[row][currentCol - 1] = grid[row][currentCol];
                        grid[row][currentCol] = 0;
                        currentCol--;
                    }
                    if (currentCol > 0 && (grid[row][currentCol - 1] == grid[row][currentCol])) {
                        grid[row][currentCol - 1] *= 2;
                        score += grid[row][currentCol - 1]; // Update the score with the merged tile value
                        grid[row][currentCol] = 0;
                    }
                }
            }
        }
    }
    private void moveDown() {
        for (int col = 0; col < SIZE; col++) {
            for (int row = SIZE - 2; row >= 0; row--) {
                if (grid[row][col] != 0) {
                    int currentRow = row;
                    while (currentRow < SIZE - 1 && (grid[currentRow + 1][col] == 0)) {
                        grid[currentRow + 1][col] = grid[currentRow][col];
                        grid[currentRow][col] = 0;
                        currentRow++;
                    }
                    if (currentRow < SIZE - 1 && (grid[currentRow + 1][col] == grid[currentRow][col])) {
                        grid[currentRow + 1][col] *= 2;
                        score += grid[currentRow + 1][col]; // Update the score with the merged tile value
                        grid[currentRow][col] = 0;
                    }
                }
            }
        }
    }
    private void moveRight() {
        for (int row = 0; row < SIZE; row++) {
            for (int col = SIZE - 2; col >= 0; col--) {
                if (grid[row][col] != 0) {
                    int currentCol = col;
                    while (currentCol < SIZE - 1 && (grid[row][currentCol + 1] == 0)) {
                        grid[row][currentCol + 1] = grid[row][currentCol];
                        grid[row][currentCol] = 0;
                        currentCol++;
                    }
                    if (currentCol < SIZE - 1 && (grid[row][currentCol + 1] == grid[row][currentCol])) {
                        grid[row][currentCol + 1] *= 2;
                        score += grid[row][currentCol + 1]; // Update the score with the merged tile value
                        grid[row][currentCol] = 0;
                    }
                }
            }
        }
    }
    private boolean isGameOver() {
        // Game is over if no more empty cells or no possible moves
        return !hasEmptyCell() && !canMoveUp() && !canMoveLeft() && !canMoveDown() && !canMoveRight();
    }
    private boolean hasEmptyCell() {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                if (grid[row][col] == 0) {
                    return true;
                }
            }
        }
        return false;
    }
    public static void responsiveness(GridPane root, Scene scene) {
        root.prefHeightProperty().bind(scene.heightProperty());
        root.prefWidthProperty().bind(scene.widthProperty());
    }

    private void loadHighestScore() {
        try {
            File file = new File(SCORE_FILE);
            if (file.exists()) {
                BufferedReader reader = new BufferedReader(new FileReader(file));
                highestScore = Integer.parseInt(reader.readLine());
                reader.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveHighestScore() {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(SCORE_FILE));
            writer.write(String.valueOf(highestScore));
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void resetHighScore(){
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(SCORE_FILE));
            writer.write(String.valueOf(0));
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args ){
        launch(args);
    }
}
