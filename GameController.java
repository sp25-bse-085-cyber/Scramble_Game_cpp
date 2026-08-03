import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Random;

//ABRISH
public class GameController {
    
    @FXML private Pane gamePane;
    @FXML private HBox scoreBox;
    @FXML private Label scoreLabel;
    @FXML private HBox livesBox;
    @FXML private Label livesLabel;
    @FXML private TextField inputField;
    @FXML private VBox pauseMenu;
    @FXML private Button resumeBtn;
    @FXML private Button menuBtn;
    
    private Main mainApp;
    private GameLogic logic;
    private String difficulty;
    
    @FXML
    public void initialize() {
        // Set game background
        try {
            Image bgImg = new Image(getClass().getResource("/resources/bg.png").toExternalForm());
            BackgroundSize bgSize = new BackgroundSize(1.0, 1.0, true, true, false, false);
            BackgroundImage bg = new BackgroundImage(bgImg, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, bgSize);
            gamePane.setBackground(new Background(bg));
        } catch (Exception e) {
            gamePane.setStyle("-fx-background-color: black;");
        }
        
        // Bind lives box to right edge
        livesBox.layoutXProperty().bind(
            gamePane.widthProperty().subtract(livesBox.widthProperty()).subtract(10)
        );
    }
    
    public void setMainApp(Main mainApp, String difficulty) {
        this.mainApp = mainApp;
        this.difficulty = difficulty;
        
        // Resize game pane to match root
        gamePane.prefWidthProperty().bind(mainApp.getRoot().widthProperty());
        gamePane.prefHeightProperty().bind(mainApp.getRoot().heightProperty());
        
        // Initialize game logic
        logic = new GameLogic(this, gamePane, inputField, difficulty);
        logic.startGame();
        
        // Set up ESC key handler
        mainApp.getRoot().setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ESCAPE) pauseGame();
        });
        
        inputField.requestFocus();
    }
    
    public void pauseGame() {
        logic.pause();
        pauseMenu.setVisible(true);
    }
    
    @FXML
    private void handleResume() {
        pauseMenu.setVisible(false);
        logic.resume();
        inputField.requestFocus();
    }
    
    @FXML
    private void handleReturnToMenu() {
        logic.stop();
        pauseMenu.setVisible(false);
        mainApp.showMainMenu();
    }
    
    public void gameOver(int score) {
        logic.stop();
        mainApp.updateHighScore(score);
        
        VBox gameOverBox = new VBox(25);
        gameOverBox.setAlignment(Pos.CENTER);
        gameOverBox.setPrefSize(800, 600);
        gameOverBox.setStyle("-fx-background-color: rgba(0,0,0,0.9);");

        Label title = new Label("GAME OVER");
        title.setFont(Font.font("Impact", 50));
        title.setTextFill(Color.RED);

        Label finalScore = new Label("Final Score: " + score);
        finalScore.setFont(Font.font("Arial", 28));
        finalScore.setTextFill(Color.WHITE);

        Label highScoreDisplay = new Label("🏆 High Score: " + mainApp.getHighScore());
        highScoreDisplay.setFont(Font.font("Arial", 22));
        highScoreDisplay.setTextFill(Color.GOLD);

        Button retryBtn = createMenuButton("🔄 PLAY AGAIN", "#00CC66");
        retryBtn.setOnAction(e -> mainApp.startGame(difficulty));

        Button menuBtn = createMenuButton("🏠 MAIN MENU", "#4466FF");
        menuBtn.setOnAction(e -> mainApp.showMainMenu());

        gameOverBox.getChildren().addAll(title, finalScore, highScoreDisplay, retryBtn, menuBtn);
        gamePane.getChildren().add(gameOverBox);
    }
    
    private Button createMenuButton(String text, String color) {
        Button btn = new Button(text);
        btn.setPrefWidth(250);
        btn.setPrefHeight(45);
        btn.setFont(Font.font("Arial", 16));
        btn.setStyle(
            "-fx-background-color:" + color + ";" +
            "-fx-text-fill:white;" +
            "-fx-background-radius:10;" +
            "-fx-font-weight:bold;" +
            "-fx-border-color:white;" +
            "-fx-border-width:2;"
        );
        return btn;
    }
    
    public void updateScore(int score) {
        scoreLabel.setText("Score: " + score);
    }

    public void updateLives(int livesCount) {
        String hearts = "";
        for (int i = 0; i < livesCount; i++) hearts += "♥ ";
        livesLabel.setText(hearts.isEmpty() ? "♥" : hearts.trim());
    }
}

// HAMZA
class GameLogic {
    private GameController gameController;
    private Pane gamePane;
    private TextField inputField;
    private ArrayList<FallingLetter> letters = new ArrayList<>();
    private Random random = new Random();
    private int score = 0;
    private int lives = 3;
    private double speed;
    private double spawnRate;
    private Timeline spawnTimeline;
    private AnimationTimer gameLoop;

    public GameLogic(GameController gc, Pane pane, TextField input, String difficulty) {
        this.gameController = gc;
        this.gamePane = pane;
        this.inputField = input;

        switch (difficulty) {
            case "EASY": speed = 1.5; spawnRate = 2.2; break;
            case "HARD": speed = 3.5; spawnRate = 0.8; break;
            default: speed = 2.5; spawnRate = 1.3; break;
        }

        inputField.textProperty().addListener((obs, old, newTxt) -> {
            if (newTxt.length() > 0) {
                checkLetter(newTxt.toUpperCase().charAt(0));
                inputField.clear();
            }
        });
    }

    public void startGame() {
        spawnTimeline = new Timeline(new KeyFrame(Duration.seconds(spawnRate), e -> spawnLetter()));
        spawnTimeline.setCycleCount(Animation.INDEFINITE);
        spawnTimeline.play();

        gameLoop = new AnimationTimer() {
            @Override public void handle(long now) { updateLetters(); }
        };
        gameLoop.start();
    }

    private void spawnLetter() {
        char letter = (char) ('A' + random.nextInt(26));
        double x = 50 + random.nextDouble() * (gamePane.getWidth() - 100);
        FallingLetter fl = new FallingLetter(letter, x, speed);
        letters.add(fl);
        gamePane.getChildren().add(fl.text);
    }

    private void updateLetters() {
        ArrayList<FallingLetter> toRemove = new ArrayList<>();

        for (FallingLetter fl : letters) {
            fl.y += fl.speed;
            fl.text.setY(fl.y);

            if (fl.y > gamePane.getHeight() - 50) {
                loseLife();
                toRemove.add(fl);
            }
        }

        // Remove after iteration
        for (FallingLetter fl : toRemove) {
            letters.remove(fl);
            gamePane.getChildren().remove(fl.text);
        }
    }

    private void checkLetter(char typed) {
        FallingLetter matched = null;

        for (FallingLetter fl : letters) {
            if (fl.letter == typed) {
                matched = fl;
                score++;
                gameController.updateScore(score);

                FadeTransition fade = new FadeTransition(Duration.millis(150), fl.text);
                fade.setToValue(0);
                fade.setOnFinished(e2 -> gamePane.getChildren().remove(fl.text));
                fade.play();
                break;
            }
        }

        if (matched != null) {
            letters.remove(matched);
        }

    }

    private void loseLife() {
        lives--;
        gameController.updateLives(lives);
        if (lives <= 0) gameController.gameOver(score);
    }

    public void pause() {
        spawnTimeline.pause();
        gameLoop.stop();
    }

    public void resume() {
        spawnTimeline.play();
        gameLoop.start();
    }

    public void stop() {
        spawnTimeline.stop();
        gameLoop.stop();
        letters.clear();
    }

    class FallingLetter {
        char letter;
        double x, y, speed;
        Text text;

        FallingLetter(char letter, double x, double speed) {
            this.letter = letter;
            this.x = x;
            this.y = 50;
            this.speed = speed;
            this.text = new Text(String.valueOf(letter));
            text.setFont(Font.font("Arial Black", 28));
            text.setFill(Color.YELLOW);
            text.setX(x);
            text.setY(y);
            DropShadow shadow = new DropShadow(3, Color.BLACK);
            text.setEffect(shadow);
        }
    }
}