import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

//AMMARA
public class MenuController {
    
    @FXML private VBox mainMenu;
    @FXML private VBox imageSection;
    @FXML private ImageView menuImageView;
    @FXML private Label fallbackLabel;
    @FXML private ToggleGroup difficultyGroup;
    @FXML private RadioButton easyBtn;
    @FXML private RadioButton mediumBtn;
    @FXML private RadioButton hardBtn;
    @FXML private Label highScoreLabel;
    @FXML private Button startBtn;
    @FXML private Button instructionsBtn;
    @FXML private Button exitBtn;
    
    private Main mainApp;
    private String difficulty = "MEDIUM";
    
    @FXML
    public void initialize() {
        // Load menu background image
        try {
            Image bgImg = new Image(getClass().getResource("/resources/menu_bg.png").toExternalForm());
            BackgroundSize bgSize = new BackgroundSize(1.0, 1.0, true, true, false, false);
            BackgroundImage bg = new BackgroundImage(bgImg, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, bgSize);
            mainMenu.setBackground(new Background(bg));
        } catch (Exception e) {
            mainMenu.setStyle("-fx-background-color: linear-gradient(to bottom, #0f0c29, #302b63, #24243e);");
        }
        
        // Load menu icon image
        try {
            Image img = new Image(getClass().getResource("/resources/menu_bg.png").toExternalForm());
            menuImageView.setImage(img);
        } catch (Exception e) {
            fallbackLabel.setVisible(true);
        }
        
        // Set up difficulty selection listener
        difficultyGroup.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            if (newToggle != null) {
                RadioButton selected = (RadioButton) newToggle;
                difficulty = selected.getText();
            }
        });

        // Make buttons more attractive
        styleButton(startBtn, "#00FFAA", "#00CC88");
        styleButton(instructionsBtn, "#0099FF", "#0066CC");
        styleButton(exitBtn, "#FF4466", "#CC3344");
    }
    //method for styling buttons with hover effects
    private void styleButton(Button btn, String color1, String color2) {
        // Base style
        btn.setStyle("-fx-background-radius: 12;" +
                "-fx-background-color: linear-gradient(to bottom, " + color1 + ", " + color2 + ");" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-border-radius: 12;" +
                "-fx-border-color: white;" +
                "-fx-border-width: 2;");

        btn.setEffect(new javafx.scene.effect.DropShadow(6, javafx.scene.paint.Color.BLACK));

        // Hover effect
        btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-radius: 12;" +
                "-fx-background-color: linear-gradient(to bottom, " + color2 + ", " + color1 + ");" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-border-radius: 12;" +
                "-fx-border-color: white;" +
                "-fx-border-width: 2;"));

        btn.setOnMouseExited(e -> btn.setStyle("-fx-background-radius: 12;" +
                "-fx-background-color: linear-gradient(to bottom, " + color1 + ", " + color2 + ");" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-border-radius: 12;" +
                "-fx-border-color: white;" +
                "-fx-border-width: 2;"));
    }
    public void setMainApp(Main mainApp) {
        this.mainApp = mainApp;
    }
    
    public void updateHighScore(int score) {
        highScoreLabel.setText("🏆 High Score: " + score);
    }
    
    @FXML
    private void handleStartGame() {
        mainApp.startGame(difficulty);
    }
    
    @FXML
    private void handleInstructions() {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setHeaderText("How To Play");
        a.setContentText("• Letters fall from the top.\n• Type the letter before it reaches the bottom.\n• Each correct letter = +1 score.\n• If a letter reaches the bottom, you lose 1 life.\n• Press ESC to pause the game.");
        a.show();
    }
    
    @FXML
    private void handleExit() {
        mainApp.getPrimaryStage().close();
    }
}