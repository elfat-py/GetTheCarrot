package application;
import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.util.Random;

public class Basic extends Application {
    private static final int WINDOW_WIDTH = 1280;
    private static final int WINDOW_HEIGHT = 720;
    private static final int CHARACTER_SIZE = 50;
    private static final int CARROT_SIZE = 10;
    private static int USER_SCORE = 1;

    int RANDOMFOOD;
    private long lastCollisionTime;

    private ImageView rabbit;
    private  ImageView food;
    private Pane root;
    private Random random;
    private Label scoreLabel;


    //Our images directory
    Image backgroundImage = new Image("C:\\Users\\User\\Projects\\SnakeGameJavaFX\\src\\main\\images\\background.png");
    private final String lossImagePath= "https://th.bing.com/th/id/R.9d021af4e3b813da9cb9c7ec9f011116?rik=RowRIwa0WKRZdg&riu=http%3a%2f%2fwww.nag.co.za%2fwp-content%2fuploads%2f2013%2f02%2fgame-over.jpg&ehk=6R2XumjfuIKGmWMB3LTBjmMH3bOX4iZsolZyipNzAfc%3d&risl=&pid=ImgRaw&r=0";
    private final String winImagePath = "https://th.bing.com/th/id/OIP.VQfa0R36sb_67itxo0N6-gHaMJ?pid=ImgDet&w=195&h=320&c=7";
    private final Image gameLogo = new Image("C:\\Users\\User\\Projects\\SnakeGameJavaFX\\src\\main\\images\\logoRabbitGame.png");
//https://cdn.dribbble.com/users/2121202/screenshots/4349277/kelinci.png
    @Override
    public void start(Stage primaryStage) {

        root = new Pane(); //Node ku do te mbahet lepuri carrota zjarri etc
        root.setPadding(new Insets(10));


        BackgroundImage backgroundImg = new BackgroundImage(backgroundImage,
                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT);

        Background background = new Background(backgroundImg);
        root.setBackground(background);

        random = new Random();
        scoreLabel();
        mainCharacter();
        generateFood();

        // Set up event handling for arrow key presses
        root.setOnKeyPressed(e -> {
            KeyCode keyCode = e.getCode();
            moveCharacter(keyCode);
        });


        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(3), event -> {
                    root.getChildren().remove(food);
                    generateFood();
                    CheckWinOrLoss(USER_SCORE);
                })
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();



        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        primaryStage.setTitle("Rabbito");
        primaryStage.getIcons().add(gameLogo);
        primaryStage.setScene(scene);
        primaryStage.show();



        //Check collision between rabbit and the food
        AnimationTimer gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                checkCollision();
                checkLastCollisionTime(now);
            }
        };
        gameLoop.start();
        root.requestFocus();
    }


    private void mainCharacter()
    {
        Image rabbitImage = new Image("https://th.bing.com/th/id/OIP.gwMpnJ_UZ7knZlDUqR4MLgHaFj?w=272&h=204&c=7&r=0&o=5&pid=1.7");
        rabbit = new ImageView(rabbitImage);
        rabbit.setFitHeight(CHARACTER_SIZE);
        rabbit.setFitWidth(CHARACTER_SIZE);
        rabbit.setTranslateX((WINDOW_WIDTH - CHARACTER_SIZE) / 2);
        rabbit.setTranslateY((WINDOW_HEIGHT - CHARACTER_SIZE) / 2);
        root.getChildren().add(rabbit);

    }


//This is a method where we check what key the user is pressing
    private void moveCharacter(KeyCode keyCode) {
        switch (keyCode) {
            case UP:
                rabbit.setTranslateY(rabbit.getTranslateY() - 10);
                break;
            case DOWN:
                rabbit.setTranslateY(rabbit.getTranslateY() + 10);
                break;
            case LEFT:
                rabbit.setTranslateX(rabbit.getTranslateX() - 10);
                break;
            case RIGHT:
                rabbit.setTranslateX(rabbit.getTranslateX() + 10);
                break;
        }
        checkCollision();
    }

    private void checkCollision() {
//I think this line is giving us back food while all we want is the value of what random val we getting
         //Ketu ruajme cfare lloj ushqimi eshte garlic apo carrot


        Bounds characterBounds = rabbit.getBoundsInParent();
        Bounds dotBounds = food.getBoundsInParent();

        if ((characterBounds.intersects(dotBounds))&&(RANDOMFOOD ==0))
        {
            root.getChildren().remove(food);
            generateFood();
            USER_SCORE++;
            updateScore();
            lastCollisionTime = System.nanoTime();

            //updateScore(1);//Ketu rrisim score me nje duke qene se useri mori karrot

        }
        else if (characterBounds.intersects(dotBounds))//Nese nuk ha karrota atehere ka ngrene hudhra, do te ulim me nje user score
        {
            root.getChildren().remove(food);
            generateFood();
            USER_SCORE--;
            updateScore();
            lastCollisionTime = System.nanoTime();

            //updateScore(-1);//Ketu ulim score duke qene se useri mori garlic
        }
    }
    private void checkLastCollisionTime(long currentTime) {
        long elapsedTime = currentTime - lastCollisionTime;
        double elapsedTimeInSeconds = elapsedTime / 1_000_000_000.0; // Convert to seconds

        if (elapsedTimeInSeconds >= 20) {
            // No collision in the last 10 seconds
            USER_SCORE=0;
        }
    }

    private void generateFood()
    {
        double carrotX = random.nextDouble() * (WINDOW_WIDTH - CARROT_SIZE);//Ketu marrim nje pozicion x per karroten tone, qe karrota te jete brenda kufijve zbresim nga gjatesia dritares madhesine e karrotes
        double carrotY = random.nextDouble() * (WINDOW_HEIGHT - CARROT_SIZE);



        //Neve duam qe kur lepuri te haj carrot do te shtohet nje pike, kur te ha nje hudher ulet nje, nese qendron
        // ne vend per 5 sec do te ulet nje pike po ashtu

        RANDOMFOOD = random.nextInt(2);
        String dotImagePath;
        if (RANDOMFOOD == 0) {
            dotImagePath = "https://th.bing.com/th/id/R.f495113916c0c3c9b488fa55f8a64021?rik=sQwxiMZS6s4XGA&pid=ImgRaw&r=0";//Ky eshte rasti kur na del karrot
        } else{//It is not always true , compiler error
            dotImagePath = "https://th.bing.com/th/id/OIP.HIndMn9d645-dX1MmbmOhAHaFj?w=233&h=180&c=7&r=0&o=5&pid=1.7";//Rasti kur eshte hudher
        }

        Image carrotOrGarlic = new Image(dotImagePath);
        food = new ImageView(carrotOrGarlic);
        food.setFitHeight(CHARACTER_SIZE);
        food.setFitWidth(CHARACTER_SIZE);
        food.setTranslateX(carrotX);
        food.setTranslateY(carrotY);
        root.getChildren().add(food);
    }
    private void scoreLabel()
    {
        scoreLabel = new Label("Score: " + USER_SCORE);
        scoreLabel.setLayoutX(10);
        scoreLabel.setLayoutY(10);
        root.getChildren().add(scoreLabel);
    }

    private void updateScore()
    {
        scoreLabel.setText("Score: "+ USER_SCORE);

    }


    private void displayLossImage(Pane root, String imagePath, double width, double height) {
        Image lossImage = new Image(imagePath);
        ImageView imageView = new ImageView(lossImage);
        imageView.setFitWidth(width);
        imageView.setFitHeight(height);

        imageView.setLayoutX((root.getWidth() - width) / 2);
        imageView.setLayoutY((root.getHeight() - height) / 2);
        imageView.layoutXProperty().bind(root.widthProperty().subtract(width).divide(2));
        imageView.layoutYProperty().bind(root.heightProperty().subtract(height).divide(2));

        // Add the image view to the pane
        root.getChildren().add(imageView);
    }

    private void displayWinImage(Pane root, String winImagePath, double width, double height) {
        Image winImage = new Image(winImagePath);
        ImageView viewWinImage = new ImageView(winImage);
        viewWinImage.setFitWidth(width);
        viewWinImage.setFitHeight(height);


        viewWinImage.setLayoutX((root.getWidth() - width) / 2);
        viewWinImage.setLayoutY((root.getHeight() - height) / 2);

        viewWinImage.layoutXProperty().bind(root.widthProperty().subtract(width).divide(2));
        viewWinImage.layoutYProperty().bind(root.heightProperty().subtract(height).divide(2));
        root.getChildren().add(viewWinImage);
    }

//This method is okay it just takes a bit of time to load

    private void CheckWinOrLoss(int userCurrentScore)
    {
        if (userCurrentScore<=0)
        {
            displayLossImage(root, lossImagePath,400, 200);



        } else if (userCurrentScore>=10)
        {
            displayWinImage(root, winImagePath, 420, 298);
            //System.exit(0);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
