
import javafx.animation.AnimationTimer;
import javafx.scene.image.Image;
import javafx.application.Application;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.collections.ObservableList;
import javafx.scene.text.Text;
import javafx.scene.text.Font;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.Node;

import java.util.HashSet;

public class Tap extends Application {
    private static final int FRAME_WIDTH = 640;
    private static final int FRAME_HEIGHT = 360;
    private static HashSet<String> activeKeys = new HashSet<>();
    private static KeyTap KEY_1;
    private static KeyTap KEY_2;
    private static Scene scene;
    private static GraphicsContext gc;
    private static int time;

    private static Image K1 = new Image("/default.png");
    private static Image K1Pressed = new Image("/pressed.png");
    private static Image K2 = new Image("/default.png");
    private static Image K2Pressed = new Image("/pressed.png");
    private static long start = 0;
    private static int topScore = 0;

    private static int imOffset = 50;
    private static int txtOffset = 5;
    private static int FH = (FRAME_HEIGHT / 2) - 64;

    private static class KeyTap {
        String key;
        int numPressed;
        KeyTap(String key) {
            this.key = key;
            numPressed = 0;
        }

        void increment() {
            numPressed += 1;
        }
    }
    private void addText(Text text, String string, int xPos, int yPos) {
        text.setX(xPos);
        text.setY(yPos);
        text.setFont(new Font(45));
        text.setText(string);
    }

    public void start(Stage frame) {
        frame.setTitle("Tap!");
        Group group = new Group();
        scene = new Scene(group);
        scene.setFill(Color.LIGHTGRAY);
        Canvas canvas = new Canvas(FRAME_WIDTH, FRAME_HEIGHT);
        group.getChildren().add(canvas);
        ObservableList<Node> list = group.getChildren();
        frame.setScene(scene);

        Text textKEY1 = new Text();
        Text textKEY2 = new Text();
        addText(textKEY1, KEY_1.key, FRAME_WIDTH / 3, FRAME_HEIGHT/2);
        addText(textKEY2, KEY_2.key, FRAME_WIDTH * 2 / 3, FRAME_HEIGHT/2);
        list.add(textKEY1);
        list.add(textKEY2);

        eventUpdate();
        gc = canvas.getGraphicsContext2D();
        gc.setFont(new Font(30));
        gc.setTextAlign(TextAlignment.CENTER);

        new AnimationTimer() {
            public void handle(long now) {
                gamedraw();
                if (start == 0) {
                    start = now;
                }
                int currSec = ((int) Math.floorDiv(now - start, 1000000000)) + 1;
                int TPS = (KEY_1.numPressed + KEY_2.numPressed) / currSec;
                if (TPS > topScore) {
                    topScore = TPS;
                }
                String TPSstr = "Taps Per Second: " + String.valueOf(TPS);
                gc.fillText(TPSstr, FRAME_WIDTH / 2, FRAME_HEIGHT / 10);
                if (currSec == time) {
                    gameend();
                    stop();
                }
            }
        }.start();

        frame.show();
    }

    private static void eventUpdate() {
        scene.setOnKeyPressed(event -> {
            String input = event.getCode().toString().toUpperCase();
            if (input.equals(KEY_1.key) && !activeKeys.contains(input)) {
                activeKeys.add(input);
                KEY_1.increment();
            } else if(input.equals(KEY_2.key) && !activeKeys.contains(input)) {
                activeKeys.add(input);
                KEY_2.increment();
            }
        });
        scene.setOnKeyReleased(event -> {
            String input = event.getCode().toString().toUpperCase();
            if (input.equals(KEY_1.key) || input.equals(KEY_2.key)) {
                activeKeys.remove(input);
            }
        });
    }
    private static void gameend() {
        gc.clearRect(0, 0, FRAME_WIDTH, FRAME_HEIGHT);
        String score = "Peak TPS: " + String.valueOf(topScore);
        gc.fillText(score, FRAME_WIDTH / 2, FRAME_HEIGHT / 10);

        gc.fillText(String.valueOf(KEY_1.numPressed), FRAME_WIDTH / 3 + txtOffset, FRAME_HEIGHT*4/5);
        gc.fillText(String.valueOf(KEY_2.numPressed), FRAME_WIDTH * 2/3 + txtOffset, FRAME_HEIGHT*4/5);
        gc.drawImage(K1, (FRAME_WIDTH / 3) - imOffset ,FH);
        gc.drawImage(K2, (FRAME_WIDTH * 2 / 3) - imOffset, FH);
    }

    private static void gamedraw() {
        gc.clearRect(0, 0, FRAME_WIDTH, FRAME_HEIGHT);
        gc.fillText(String.valueOf(KEY_1.numPressed), FRAME_WIDTH / 3 + txtOffset, FRAME_HEIGHT*4/5);
        gc.fillText(String.valueOf(KEY_2.numPressed), FRAME_WIDTH * 2/3 + txtOffset, FRAME_HEIGHT*4/5);

        if (activeKeys.contains(KEY_1.key)) {
            gc.drawImage(K1Pressed, (FRAME_WIDTH / 3) - imOffset, FH);
        } else {
            gc.drawImage(K1, (FRAME_WIDTH / 3) - imOffset ,FH);
        }

        if (activeKeys.contains(KEY_2.key)) {
            gc.drawImage(K2Pressed, (FRAME_WIDTH * 2 / 3) - imOffset, FH);
        } else {
            gc.drawImage(K2, (FRAME_WIDTH * 2 / 3) - imOffset, FH);
        }
    }

    public static void main(String[] args) {
        if (args.length > 3) {
            System.out.println("<Time> <KEY_1> <KEY_2> are the parameters");
            System.exit(0);
        } else {
            if (args.length == 3) {
                char input2 = args[2].charAt(0);
                if (args[1].charAt(0) == input2) {
                    System.out.println("Requires 2 distinct KEYS");
                    System.exit(0);
                }
                KEY_2 = new KeyTap(String.valueOf(input2).toUpperCase());
            }
            if (args.length >= 2) {
                String input1 = String.valueOf(args[1].charAt(0));
                if (input1.equals(KEY_2.key)) {
                    System.out.println("Requires 2 distinct KEYS");
                    System.exit(0);
                }
                KEY_1 = new KeyTap(input1);
            }
            if (args.length >= 1) {
                try {
                    time = Integer.valueOf(args[0]);
                } catch (Exception e) {
                    System.out.println("Requires an int for <TIME>");
                    System.exit(0);
                }
            }
            if (args.length == 0) {
                KEY_1 = new KeyTap("Z");
                KEY_2 = new KeyTap("X");
                time = 10;
            }

            System.out.printf("User's keys: %s, %s \n", KEY_1.key, KEY_2.key);
            System.out.printf("Time set to %d seconds", time);
            launch();
        }
    }

}
