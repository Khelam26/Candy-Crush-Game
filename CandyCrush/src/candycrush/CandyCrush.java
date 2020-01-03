/*
Files needed: CandyCrush.java (main file), playerInfo.java (an abstract class file)

Description:
The game basically works like Candy Crush. If you have 3,4,5,6 same color circles in a row, then you get x number of points and at the same time it randomly 
assigns new circle replacing the old circles. 

How to play: 
        1. Enter your Name in the first pane. The game wont start until you have entered something, the textfield can't be empty. (I have an exception in that)
        2. After you have entered your name, a new scene will appear with a dropdown menu on the left corner. The available menu options are: 
                a. Load - which loads the previous saved score
                b. Save - Saves the score, player name, and no of Swaps used
                c. Exit - Exits the program wihtout showing the final scene.
        3. You can swap any circle you want. This is where it is not like candy crush. You have to make a line (horizontal or vertical) with 3,4,5,6 circles with same color. 
        4. You earn 10,25,50,100 points for making a line of 3,4,5,6 same color circles. 
        5. A player has a maximum of 5 swaps. the score is automatically stored in the file after the maximum swap number is reached. 
            After the fifth swap, a new scene is popped with a message, players name, and their score. 
            There is a button in the center called "DONE", which when clicked exits the program completely. 

Name: Candy Crush
programmer: Khelan Modi
Date Modified: Dec 17th, 2019 
 */
 /*
 * This project incorporates:
 * ● Loops                                                                         Done-> For-each loop, to check the status
 * ● Random numbers                                                                Done-> Randomly assigning colors from the array of colors
 * ● Classes & objects : including abstract classes and interfaces                 Done-> Player Info abstract class to store players information
 * ● Arrays of objects                                                             Done-> Array of Colors! (Black, red, green, blue, yellow, magenta)
 * ● Methods                                                                       Done-> calculating points (checkCombo method and many more......)
 * ● User Interface / User Experience                                              Done-> mouse events (Used a mouse event to swap to circles)
 * ● Binary file i/o                                                               Done-> Storing scores (Player name and score is stored in a binary file called "Data.dat")
 * ● Menus                                                                         Done-> drop down menu, in the second scene (options available load, save, exit)
 * ● Exceptions                                                                    Done-> exception in playerInfo class, it throws an exception if the name field is left empty
 */
package candycrush;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import static java.lang.System.exit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * Date Modified: 12/17/2019
 * @author khelan
 */
public class CandyCrush extends Application {

    private static final int WIDTH = 6;
    private static final int HEIGTH = 6;
    private static final int SIZE_OF_JEWEL = 100;
    int count = 0;
    TextField nameField = new TextField();

    // number of swaps a player makes
    public IntegerProperty noOfSwaps = new SimpleIntegerProperty();

    // array of color, it gets randomly assign
    private Color[] colors = new Color[]{
        Color.BLACK, Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW, Color.MAGENTA
    };

    private Candy selected = null; // the candy that is selected
    private List<Candy> jewels; // list of candy objects named jewels

    private IntegerProperty score = new SimpleIntegerProperty();

    /**
     * The method checks the state of the game
     * The maximum swaps a player can make is 5, after that a scene is created and the data is stored
     * @param none
     * @return none
     */
    private void checkState() {
        if (noOfSwaps.getValue() > 4) {
            
            try {
                storeBinaryFile(score.getValue(), PlayerInfo.getName());
            } catch (IOException ex) {
                Logger.getLogger(CandyCrush.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            Stage secondaryStage = new Stage();
            secondaryStage.setTitle("CONGRATS!");
            
            Button doneButton = new Button("DONE");

            BorderPane done = new BorderPane();
            done.setBackground(new Background(new BackgroundFill(Color.AQUA, CornerRadii.EMPTY, Insets.EMPTY)));
            
            Label doneLabel = new Label("You have reached the maximum number of Swaps! \n\t"
                    + "Player Name: " + PlayerInfo.getName()
                    + "\n\tScore: " + score.getValue()
                    + "\nData has been stored to a file");
            doneLabel.setFont(Font.font("Comic Sans MS", FontWeight.BOLD, FontPosture.ITALIC, 18));
            doneLabel.setTextFill(Color.DARKORANGE);

            doneButton.setMaxSize(100, 50);
            done.setTop(doneLabel);
            done.setCenter(doneButton);

            doneButton.setOnAction(e -> {
                exit(0);
                System.out.println("Program terminated Successfully");
            });

            doneButton.setAlignment(Pos.CENTER);

            Scene scene = new Scene(done, 400, 300);
            secondaryStage.setScene(scene);
            secondaryStage.show();
        }

        // it basically obtains a stream to a list of candies, so it produces a stream of those elements withtin the list 
        // then the elements are collected into groups, the group is represented by a list. The list is grouped by rows and coloumns
        // if the element in the list returns the same value in the previous then they are grouped in a list. 
        // the key to the map is given by integer, rows /columns (contains values from 0 - 5). 
        Map<Integer, List<Candy>> rows = jewels.stream().collect(Collectors.groupingBy(Candy::getRow));
        Map<Integer, List<Candy>> columns = jewels.stream().collect(Collectors.groupingBy(Candy::getColumn));

        // for each of the element in a row/column, check whetther all the elements have the same color.
        // values() returns the values from the list
        rows.values().forEach(this::checkCombo);
        columns.values().forEach(this::checkCombo);
    }

    /**
     * jewel is the first element of the list that is being checked
     * the method basically checks if the same colored candy are in line.
     * Different points are assigned according to the line count of the candies.
     * After the line is found, the candies in the list are randomly assigned a new color by using the for each loop. 
     * @param CandyLine -> A list of Candy objects (line can be a row or column)
     * @return nothing
     */
    private void checkCombo(List<Candy> CandyLine) {
        Candy jewel = CandyLine.get(0); // first element from the list
        
        // j are elements of the stream/list. 
        // The colors are compared of each element
        // counts returns the number of elements that have the same color
        // 0 means that all the elements have the same color (Whole row/column contains the same column)
        // 6 means each element has a different color.
        long lineCount = CandyLine.stream().filter(j -> j.getColor() != jewel.getColor()).count();

        switch ((int) lineCount) {
            case 6: break;
            case 5:
                break;
            case 4:
                break;
            case 3:
                score.set(score.get() + 10);
                CandyLine.forEach(Candy::randomize);
                break;
            case 2:
                score.set(score.get() + 25);
                CandyLine.forEach(Candy::randomize);
                break;
            case 1:
                score.set(score.get() + 50);
                CandyLine.forEach(Candy::randomize);
                break;
            case 0:
                score.set(score.get() + 100);
                CandyLine.forEach(Candy::randomize);
                break;
        }
    }

    /**
     * It increments the number Of swaps by 1
     * It basically swaps two candies, selected by the mouse.
     * @param a -> Candy 1
     * @param b -> Candy 2
     */
    private void swap(Candy a, Candy b) {
        Paint color = a.getColor();
        a.setColor(b.getColor());
        b.setColor(color);
        noOfSwaps.set(noOfSwaps.get() + 1);
    }

    /**
     * The doAction method is used to perform a specific task that is in the dropDown Menu
     * The option is converted into a string 
     * according to the string value, the task is performed
     * Exit: exits the program 
     * Load: retrieves the previous score
     * save: saves the player name and score to a binary file.
     * @param x -> Combo option that was selected
     * @return nothing
     * @throws IOException 
     */
    private void doAction(ComboBox x) throws IOException {
        String listItem = (String) x.getValue();

        switch (listItem) {
            case "Exit":
                exit(0);
                System.out.println("The program exited successfully");
                break;
            case "Load":
                score.setValue(readBinaryFile(score.getValue(), PlayerInfo.getName()));
                break;
            case "Save":
                storeBinaryFile(score.getValue(), PlayerInfo.getName());
                break;
            default:
                System.out.println("Invalid");
                break;
        }
    }

    /**
     * 
     * @param primaryStage
     * @throws Exception 
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Candy Crush");

        Label instructions = new Label("Welcome to Candy Crush.\n  Every player gets 5 swaps for every turn.\n  Player with maximum Points WIN!");
        instructions.setFont(Font.font("Comic Sans MS", FontWeight.BOLD, FontPosture.ITALIC, 14));
        instructions.setTextFill(Color.RED);

        Label nameLabel = new Label("Enter Your Name: ");

        TextField nameField = new TextField();
        nameField.setPrefColumnCount(10);
        nameField.setPrefWidth(90);
        nameField.setMaxWidth(150);

        Button playButton = new Button("Play");
        playButton.setAlignment(Pos.BOTTOM_CENTER);

        // when the play button is hit, a new scene appears
        playButton.setOnAction((ActionEvent event) -> {

            Pane root = new Pane();
            root.setBackground(new Background(new BackgroundFill(Color.ANTIQUEWHITE, CornerRadii.EMPTY, Insets.EMPTY)));
            try {
                PlayerInfo.setName(nameField.getText());
            } catch (IllegalArguementException ex) {
                System.out.println("Enter a valid Name");
            }  // exception if the name field is left empty

            // size of the pane
            root.setPrefSize(WIDTH * SIZE_OF_JEWEL + 370, HEIGTH * SIZE_OF_JEWEL + 70);

            /*
            a stream of integers from 0 to 35, each of the integer is mapped to a point
            which gives their position, then map it to candies where it obtains a stream of jewels
            or stream of instances of class jewels. Finally, everything is collected to a list
            */
            jewels = IntStream.range(0, WIDTH * HEIGTH)
                    .mapToObj(i -> new Point2D(i % WIDTH, i / WIDTH))
                    .map(Candy::new)
                    .collect(Collectors.toList());

            // adds the list to the pane
            root.getChildren().addAll(jewels);

            Text textScore = new Text();
            textScore.setTranslateX(WIDTH * SIZE_OF_JEWEL + 50);
            textScore.setTranslateY(120);
            textScore.setFont(Font.font(68));
            textScore.textProperty().bind(score.asString("Score: [%d]"));
            Text swapText = new Text();
            swapText.setFont(Font.font(68));
            swapText.setTranslateX(WIDTH * SIZE_OF_JEWEL + 50);
            swapText.setTranslateY(180);
            swapText.textProperty().bind(noOfSwaps.asString("Swap: [%d]"));

            // dropDown Menu options
            ArrayList<String> options = new ArrayList<>();
            options.add("Load"); // retrives the previous saved score
            options.add("Save"); // saves the plaayer name and score in a binary file
            options.add("Exit"); // exits the program

            ComboBox menuBox = new ComboBox();
            ComboBox save = new ComboBox();
            menuBox.setValue("Choose One: ");
            menuBox.getStyleClass().add("center-aligned");

            menuBox.getItems().addAll(options);
            menuBox.setOnAction(e -> {
                try {
                    doAction(menuBox); // respective actions are performed 
                } catch (IOException ex) {
                }
            });

            VBox menuHBox = new VBox(menuBox);

            menuHBox.setAlignment(Pos.BOTTOM_RIGHT);

            root.getChildren().add(menuHBox);
            root.getChildren().add(textScore);
            root.getChildren().add(swapText);

            primaryStage.setScene(new Scene(root));
            primaryStage.show();
        });

        BorderPane root = new BorderPane();
        root.setBackground(new Background(new BackgroundFill(Color.CORNSILK, CornerRadii.EMPTY, Insets.EMPTY)));

        root.setLeft(nameLabel);
        root.setCenter(nameField);
        BorderPane.setAlignment(nameLabel, Pos.CENTER_LEFT);
        root.setTop(instructions);
        root.setBottom(playButton);
        BorderPane.setAlignment(playButton, Pos.CENTER);
        Scene scene = new Scene(root, 310, 250);

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Candy class - generates new candies, assigns random colors, and sets their coordinates
     * I included the class in the main for readability and easy access. 
     */
    private class Candy extends Parent {

        private Circle circle = new Circle(SIZE_OF_JEWEL / 2);

       
        public Candy(Point2D point) {  // point determines the starting point of the jewel
          
            circle.setCenterX(SIZE_OF_JEWEL / 2); // sets the x coordinate of the center
            circle.setCenterY(SIZE_OF_JEWEL / 2); // sets the y coordinate of the center
            circle.setFill(colors[new Random().nextInt(colors.length)]); // random colors from the array

            
            setTranslateX(point.getX() * SIZE_OF_JEWEL + 50);
            setTranslateY(point.getY() * SIZE_OF_JEWEL + 50);
            getChildren().add(circle); // adds circle to the scene

            // selects two different circles
            // then swaps them
            // later assigns the selected to null, to swap two new candies. 
            setOnMouseClicked((MouseEvent event) -> {
                if (selected == null) {
                    selected = this;
                } else {
                    swap(selected, this);
                    checkState();
                    selected = null;
                }
            });
        }

        /**
         * @param none
         * @return nothing
         * randomly assigns a new color
         */
        public void randomize() {
            circle.setFill(colors[new Random().nextInt(colors.length)]);
        }
 
        /**
         * 
         * @return column  
         */
        public int getColumn() {
            return (int) getTranslateX() / SIZE_OF_JEWEL;
        }

        /**
         * @param none
         * @return row
         */
        public int getRow() {
            return (int) getTranslateY() / SIZE_OF_JEWEL;
        }

        /**
         * @return none
         * sets the colr of the circle
         * @param color 
         */
        public void setColor(Paint color) {
            circle.setFill(color);
        }

        /**
         * @param none
         * @return the color of the candy
         */
        public Paint getColor() {
            return circle.getFill();
        }
    }

    /**
     * reads a binary file named ("Data.dat"). assigns the last score to points.
     *
     * @param points - total points (int)
     * @param name - player name (string)
     * @return the last score that was stored in the file
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static int readBinaryFile(int points, String name) throws FileNotFoundException, IOException {
        DataInputStream file = new DataInputStream(new FileInputStream("Data.dat"));
        points = file.readInt();
        return points;
    }

    /**
     * Stores (appends) the playerName, the total points they earned in a binary
     * file called "Data.dat"
     *
     * @param points - int
     * @param name - string
     * @throws FileNotFoundException - it throws an exception if the file is not
     * found
     * @throws IOException
     */
    public static void storeBinaryFile(int points, String name) throws FileNotFoundException, IOException {
        DataOutputStream output = new DataOutputStream(new FileOutputStream("Data.dat", true));
        output.writeUTF(name + "=");
        output.writeInt(points);
        output.writeUTF("\n");
        
        System.out.println("Data Stored");
    }

    /**
     * The main method
     *
     * @param args
     */
    public static void main(String[] args) {
        launch(args);
    }
}
