import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.scene.image.Image;
//HAMZA

public class Main extends Application {

    private Stage primaryStage;
    private StackPane root;
    private AudioManager audioManager;
    private int highScore = 0;

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        
        audioManager = new AudioManager();
        audioManager.initMusic();


        Image icon = new Image(getClass().getResourceAsStream("/resources/menu_logo.png"));
        stage.getIcons().add(icon);
        stage.setFullScreen(true);
        root = new StackPane();
        Scene scene = new Scene(root);

        showMainMenu();

        primaryStage.setScene(scene);
        primaryStage.setTitle("Falling Letters Game - Project");
        primaryStage.show();
    }

    public void showMainMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("menu.fxml"));
            StackPane menuPane = loader.load();
            MenuController controller = loader.getController();
            controller.setMainApp(this);
            controller.updateHighScore(highScore);
            root.getChildren().setAll(menuPane);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startGame(String difficulty) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("game.fxml"));
            StackPane gamePane = loader.load();
            GameController controller = loader.getController();
            controller.setMainApp(this, difficulty);
            root.getChildren().setAll(gamePane);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateHighScore(int score) {
        if (score > highScore) {
            highScore = score;
        }
    }

    public int getHighScore() {
        return highScore;
    }

    public Stage getPrimaryStage() { return primaryStage; }
    public StackPane getRoot() { return root; }

    public static void main(String[] args) { launch(args); }
}

//SALAAR
class AudioManager {
    private javafx.scene.media.MediaPlayer musicPlayer;

    public void initMusic() {
        try {
            javafx.scene.media.Media media = new javafx.scene.media.Media(getClass().getResource("/resources/background.mp3").toExternalForm());
            musicPlayer = new javafx.scene.media.MediaPlayer(media);
            musicPlayer.setCycleCount(javafx.scene.media.MediaPlayer.INDEFINITE);
            musicPlayer.setVolume(0.5);
            musicPlayer.play();
        } catch (Exception e) {
            System.out.println("Music file missing! " + e.getMessage());
        }
    }
}