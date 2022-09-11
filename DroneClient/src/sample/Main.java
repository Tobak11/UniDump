package sample;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;

public class Main extends Application {
    private static TextArea cmdArea;
    private static ArrayList<String> commands;

    private static void buttonEventSetter(Button b,String s){
        b.setOnMouseClicked(e->{commands.add(s);cmdArea.setText(cmdStringGen());});
    }

    private static String cmdStringGen(){
        String temp = "";
        if(commands.size()>0){
            temp = "["+commands.get(0)+"]";
            for(int i=1;i<commands.size();i++){
                temp+="->["+commands.get(i)+"]";
            }
        }
        return temp;
    }

    private static VBox buttonContainerGen(){
        VBox temp = new VBox();
        temp.setSpacing(4);
        temp.setMaxHeight(250);
        temp.setMaxWidth(100);

        ArrayList<Button> buttArr = new ArrayList<>();
        ArrayList<String> buttNames = new ArrayList<>(Arrays.asList("Up","Down","Left","Right","Turn left","Turn right","Forward","Backward"));

        for(int i=0;i<8;i++){
            buttArr.add(new Button(buttNames.get(i)));
            buttArr.get(i).setMinWidth(100);
            buttArr.get(i).setMaxWidth(100);
            buttonEventSetter(buttArr.get(i),buttNames.get(i));
        }


        temp.getChildren().addAll(buttArr);
        return temp;
    }

    private static VBox cmdAreaSet(){
        VBox temp = new VBox();
        temp.setMaxHeight(200);
        temp.setMaxWidth(400);
        cmdArea = new TextArea();
        cmdArea.setMinHeight(200);
        cmdArea.setMaxHeight(200);
        cmdArea.setMinWidth(200);
        cmdArea.setMaxWidth(400);
        cmdArea.setWrapText(true);

        HBox buttBox = new HBox();
        Button clr = new Button("Clear");
        clr.setMinWidth(100);
        clr.setMaxWidth(100);
        clr.setOnMouseClicked(e->{
            commands.clear();
            cmdArea.clear();
        });

        Button removeLast = new Button("Remove last");
        removeLast.setMinWidth(100);
        removeLast.setMaxWidth(100);
        removeLast.setOnMouseClicked(e->{
            if(commands.size()>0) {
                commands.remove(commands.size() - 1);
            }
            cmdArea.setText(cmdStringGen());
        });

        buttBox.setSpacing(6);
        buttBox.getChildren().addAll(removeLast,clr);
        buttBox.setAlignment(Pos.CENTER);

        temp.getChildren().add(cmdArea);
        temp.getChildren().add(buttBox);

        return temp;
    }

    private static VBox flyGen(){
        VBox temp = new VBox();

        CheckBox lock = new CheckBox("Lock");
        lock.setSelected(false);

        temp.setMaxWidth(100);
        temp.setMaxHeight(200);
        temp.setSpacing(16);
        temp.setAlignment(Pos.CENTER);

        Button fly = new Button("FLY!");
        fly.setMinWidth(100);
        fly.setMaxWidth(100);
        fly.setMinHeight(200);
        fly.setMaxHeight(200);
        fly.setOnMouseClicked(e->{
            if(!lock.isSelected()) {
                lock.setSelected(true);;

                PrintWriter pw = null;
                try {
                    pw = new PrintWriter(new File("PyInput.txt"));
                } catch (FileNotFoundException e1) {
                    e1.printStackTrace();
                }

                String prevS = commands.get(0);
                int prevStringCounter = 1;

                for (int i = 1; i < commands.size(); i++) {
                    if(commands.get(i).equals(prevS)) {
                        prevStringCounter++;
                    }else{
                        pw.println(prevS+ ":" + prevStringCounter);
                        prevS = commands.get(i);
                        prevStringCounter = 1;
                    }
                }
                pw.println(prevS + ":" + prevStringCounter);
                pw.close();


                try {
                    Process p = Runtime.getRuntime().exec("python droneController.py");
                    p.getOutputStream().close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }

                JOptionPane.showMessageDialog(null, "Flight started!");
            }else{
                JOptionPane.showMessageDialog(null, "Flying button is locked! If the previous flight ended, " +
                        "unlock it to fly again");
            }
        });

        temp.getChildren().add(fly);
        temp.getChildren().add(lock);

        return temp;
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        StackPane mainScene = new StackPane();
        commands = new ArrayList<>();

        VBox buttonContainer = buttonContainerGen();
        StackPane.setAlignment(buttonContainer,Pos.CENTER_LEFT);

        VBox cmdAreaContainer = cmdAreaSet();
        StackPane.setAlignment(cmdAreaContainer,Pos.CENTER);

        VBox flyContainer = flyGen();
        StackPane.setAlignment(flyContainer,Pos.CENTER_RIGHT);

        mainScene.getChildren().add(buttonContainer);
        mainScene.getChildren().add(cmdAreaContainer);
        mainScene.getChildren().add(flyContainer);


        primaryStage.setTitle("Visual Drone Programmer");
        primaryStage.setScene(new Scene(mainScene, 768, 576));
        primaryStage.setResizable(false);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
