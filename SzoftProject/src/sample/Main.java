package sample;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.control.Button;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner;
import javax.sound.sampled.*;

public class Main extends Application {
    //CLIENT INFO
    private String micID;
    private String depID;
    private String depName;


    //BACKGROUNDS
    private Background oneColorBG;
    private Background mainMenuBG;
    private Background recBG;
    {
        try {
            oneColorBG = new Background(new BackgroundImage(new Image(new FileInputStream(new File("1ColorBG.jpg"))),
                BackgroundRepeat.NO_REPEAT,BackgroundRepeat.NO_REPEAT,null,null));
            mainMenuBG = new Background(new BackgroundImage(new Image(new FileInputStream(new File("mainBG.jpg"))),
                    BackgroundRepeat.NO_REPEAT,BackgroundRepeat.NO_REPEAT,null,null));
            recBG = new Background(new BackgroundImage(new Image(new FileInputStream(new File("CB1ColorBG.jpg"))),
                    BackgroundRepeat.NO_REPEAT,BackgroundRepeat.NO_REPEAT,null,null));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    //SCENES
    private Scene mainMenuScene;
    private Scene recordScene;
    private Scene statGenScene;
    private Scene settingsScene;

    //CONTROLLERS
    private boolean isRecording = false;

    //ETC
    private File settFile;

    //FILE HANDLING FUNCTIONS
    private void readSett() throws IOException {
        Scanner fsc = new Scanner(settFile);

        if(fsc.hasNextLine()) {
            micID = fsc.nextLine();
            depID = fsc.nextLine();
            depName = fsc.nextLine();
        }else{
            micID = "";
            depID = "";
            depName = "";
        }

        fsc.close();
    }

    private void writeSett() throws IOException {
        PrintWriter pw = new PrintWriter(settFile);
        pw.println(micID);
        pw.println(depID);
        pw.println(depName);

        pw.close();
    }

    //CHART GEN FUNCTIONS
    private XYChart.Series seriesGen(LinkedList<Integer> data) {
        XYChart.Series series = new XYChart.Series();

        for (int i = 0; i < data.size(); i++) {
            series.getData().add(new XYChart.Data((double) i , data.get(i)));
        }

        return series;
    }

    private LineChart<Number,Number> lineChartGen(){
        final NumberAxis xAxis = new NumberAxis("Time(sec)",0,120,4);
        final NumberAxis yAxis = new NumberAxis("Volume",2048,4096,256);

        final LineChart<Number,Number> lineChart = new LineChart<>(xAxis,yAxis);
        lineChart.getStylesheets().add(getClass().getResource("recCSS.css").toExternalForm());
        lineChart.setTitle("Sound volume Visualized");
        lineChart.setScaleX(0.85);
        lineChart.setScaleY(0.85);

        return lineChart;
    }

    //OTHER FUNCTIONS
    public String requestGenerator(ArrayList<CheckBox> boxList){
        return "Dummy";
    }

    private int getValueFromMic(){
        TargetDataLine targetDataLine;
        byte[] tempBuffer = new byte[4096];
        int inputData;

        //Format definition
        AudioFormat audioFormat = new AudioFormat(8192.0F, 16, 1, true, false);
        DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class, audioFormat);

        try {
            targetDataLine = (TargetDataLine) AudioSystem.getLine(dataLineInfo);
            targetDataLine.open(audioFormat);
            targetDataLine.start();
            targetDataLine.read(tempBuffer, 0, tempBuffer.length);
            inputData = 0;
            for (int i = 0; i < tempBuffer.length; i++) {
                if (tempBuffer[i] != 0) {
                    inputData++;
                    //System.out.println(tempBuffer[i]);
                }
            }

            //System.out.println(inputData);
            targetDataLine.stop();
            targetDataLine.close();

            return inputData;

        } catch (StringIndexOutOfBoundsException e) {
            System.out.println(e.getMessage());
        } catch(LineUnavailableException ex){
            ex.printStackTrace();
        }

        return -1;
    }


    private void buttonSetter(Button butt, int pixWidth, int pixHeight, int pixLocHor, int pixLocVer, Image buttImg){
        butt.setMinHeight(pixHeight);
        butt.setMinWidth(pixWidth);
        butt.setMaxHeight(pixHeight);
        butt.setMaxWidth(pixWidth);
        butt.relocate(pixLocHor, pixLocVer);
        butt.setBackground(new Background(new BackgroundImage(buttImg, BackgroundRepeat.NO_REPEAT,BackgroundRepeat.NO_REPEAT,null,null)));
    }

    private Background imageToBg(Image temp){
        return new Background(new BackgroundImage(temp, BackgroundRepeat.NO_REPEAT,BackgroundRepeat.NO_REPEAT,null,null));
    }

    //SCENE SETTING FUNCTIONS
    private Scene mainMenuSetter (Stage primaryStage) throws Exception {
        Pane layout = new Pane();
        layout.setBackground(mainMenuBG);
        Scene temp = new Scene(layout, 1024, 768);

        //Adding buttons
        Button startRec = new Button();
        Button statGen = new Button();
        Button exit = new Button();
        layout.getChildren().add(startRec);
        layout.getChildren().add(statGen);
        layout.getChildren().add(exit);

        Button settings = new Button();
        layout.getChildren().add(settings);

        //Setting up button properties
        Image startRecImg = new Image(new FileInputStream(new File("startRecImg.png")));
        Image startRecImgHover = new Image(new FileInputStream(new File("startRecImgHover.png")));
        buttonSetter(startRec, 256,48,80,470,startRecImg);
        Image statGenImg = new Image(new FileInputStream(new File("statGenImg.png")));
        Image statGenImgHover = new Image(new FileInputStream(new File("statGenImgHover.png")));
        buttonSetter(statGen, 256,48,80,518, statGenImg);
        Image exitImg = new Image(new FileInputStream(new File("exitImg.png")));
        Image exitImgHover = new Image(new FileInputStream(new File("exitImgHover.png")));
        buttonSetter(exit,256,48, 80,578, exitImg);

        Image settingsImg = new Image(new FileInputStream(new File("settingsImg.png")));
        Image settingsImgHover = new Image(new FileInputStream(new File("settingsImgHover.png")));
        buttonSetter(settings,32,32, 980,12, settingsImg);

        //Functions for buttons

        //START REC
        startRec.setOnMouseClicked(e -> primaryStage.setScene(recordScene));
        startRec.setOnMouseEntered(e -> startRec.setBackground(imageToBg(startRecImgHover)));
        startRec.setOnMouseExited(e -> startRec.setBackground(imageToBg(startRecImg)));

        //STAT GEN
        statGen.setOnMouseClicked(e -> primaryStage.setScene(statGenScene));
        statGen.setOnMouseEntered(e -> statGen.setBackground(imageToBg(statGenImgHover)));
        statGen.setOnMouseExited(e -> statGen.setBackground(imageToBg(statGenImg)));

        //EXIT
        exit.setOnMouseEntered(e -> exit.setBackground(imageToBg(exitImgHover)));
        exit.setOnMouseExited(e -> exit.setBackground(imageToBg(exitImg)));
        exit.setOnMouseClicked(e -> primaryStage.close());

        //SETTINGS
        settings.setOnMouseClicked(e -> primaryStage.setScene(settingsScene));
        settings.setOnMouseEntered(e -> settings.setBackground(imageToBg(settingsImgHover)));
        settings.setOnMouseExited(e -> settings.setBackground(imageToBg(settingsImg)));

        return temp;
    }

    private Scene recordSetter(Stage primaryStage) throws Exception {
        StackPane layout = new StackPane();
        layout.setBackground(recBG);
        Scene temp = new Scene(layout,1024,834);

        LineChart<Number, Number> recChart = lineChartGen();
        recChart.setAnimated(false);
        recChart.setLegendVisible(false);
        recChart.setCreateSymbols(false);
        layout.setAlignment(recChart,Pos.BASELINE_CENTER);
        layout.getChildren().add(recChart);

        //...

        Button recButt = new Button();
        Image startRecImg = new Image(new FileInputStream(new File("startRecImg.png")));
        Image startRecImgHover = new Image(new FileInputStream(new File("startRecImgHover.png")));
        buttonSetter(recButt, 256,48,0,0,startRecImg);

        Button stopButt = new Button();
        Image stopRecImg = new Image(new FileInputStream(new File("stopRecImg.png")));
        Image stopRecImgHover = new Image(new FileInputStream(new File("stopRecImgHover.png")));
        buttonSetter(stopButt, 256,48,0,0,stopRecImg);

        Button backToMenu = new Button();
        Image BTMImg = new Image(new FileInputStream(new File("backToMainImg.png")));
        Image BTMImgHover = new Image(new FileInputStream(new File("backToMainImgHover.png")));
        buttonSetter(backToMenu, 256,48,0,0,BTMImg);


        layout.getChildren().add(recButt);
        layout.getChildren().add(stopButt);
        layout.getChildren().add(backToMenu);
        layout.setAlignment(recButt,Pos.BOTTOM_LEFT);
        layout.setAlignment(stopButt,Pos.BOTTOM_CENTER);
        layout.setAlignment(backToMenu,Pos.BOTTOM_RIGHT);

        recButt.setOnMouseClicked(e ->{
            Thread graphThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    LinkedList<Integer> audioList = new LinkedList<>();
                    isRecording=true;
                    while(isRecording){
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                int audioInput = getValueFromMic();

                                if(audioList.size()<121){
                                    audioList.addFirst(audioInput);
                                }else {
                                    audioList.removeLast();
                                    audioList.addFirst(audioInput);
                                }

                                XYChart.Series<Number,Number> mainSeries = seriesGen(audioList);

                                try {
                                    recChart.getData().clear();
                                    recChart.getData().add(mainSeries);
                                } catch (Exception e1) {
                                    e1.printStackTrace();
                                }
                            }
                        });

                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
            });
            graphThread.start();
        });

        recButt.setOnMouseEntered(e -> recButt.setBackground(imageToBg(startRecImgHover)));

        recButt.setOnMouseExited(e -> recButt.setBackground(imageToBg(startRecImg)));

        stopButt.setOnMouseClicked(e -> isRecording=false);

        stopButt.setOnMouseEntered(e -> stopButt.setBackground(imageToBg(stopRecImgHover)));

        stopButt.setOnMouseExited(e -> stopButt.setBackground(imageToBg(stopRecImg)));

        backToMenu.setOnMouseEntered(e -> backToMenu.setBackground(imageToBg(BTMImgHover)));

        backToMenu.setOnMouseExited(e -> backToMenu.setBackground(imageToBg(BTMImg)));

        backToMenu.setOnMouseClicked(e -> {primaryStage.setScene(mainMenuScene);isRecording=false;});

        return temp;
    }

    private Scene statGenSetter(Stage primaryStage) {
        VBox layout = new VBox();
        layout.setBackground(oneColorBG);
        layout.setAlignment(Pos.TOP_CENTER);
        Scene temp = new Scene(layout,1024,768);
        temp.getStylesheets().add(getClass().getResource("defaultCSS.css").toExternalForm());

        //Checkboxes
        HBox loudBoxCont = new HBox();
        loudBoxCont.setAlignment(Pos.CENTER);
        CheckBox loudBox = new CheckBox("Max Interval");
        loudBoxCont.getChildren().add(loudBox);

        //Buttons
        Button stringGen = new Button("StringGen");
        Button backToMain = new Button("Back");
        backToMain.setOnMouseClicked(e -> primaryStage.setScene(mainMenuScene));

        layout.getChildren().add(loudBoxCont);
        layout.getChildren().add(stringGen);
        layout.getChildren().add(backToMain);

        return temp;
    }

    private Scene settingsSetter(Stage primaryStage) throws IOException {
        VBox VBlayout = new VBox();
        VBlayout.setAlignment(Pos.TOP_CENTER);
        VBlayout.setSpacing(16);
        VBlayout.setPadding(new Insets(50,0,0,0));

        StackPane SPlayout = new StackPane();
        SPlayout.setBackground(oneColorBG);
        SPlayout.getChildren().add(VBlayout);
        Scene temp = new Scene(SPlayout,1024,768);
        temp.getStylesheets().add(getClass().getResource("defaultCSS.css").toExternalForm());

        //Textfields
        Label micIDLabel = new Label("Microphone ID");
        TextField micIDField = new TextField();
        micIDField.setMaxWidth(256);
        micIDField.setText(micID);

        Label depIDLabel = new Label("Department ID");
        TextField depIDField = new TextField();
        depIDField.setMaxWidth(256);
        depIDField.setText(depID);

        Label depNameLabel = new Label("Department Name");
        TextField depNameField = new TextField();
        depNameField.setMaxWidth(256);
        depNameField.setText(depName);


        //BUTTONS
        Button saveButt = new Button();
        Button backButt = new Button();

        Image saveImg = new Image(new FileInputStream(new File("saveImg.png")));
        Image saveImgHover = new Image(new FileInputStream(new File("saveImgHover.png")));
        buttonSetter(saveButt, 256,48,0,0,saveImg);

        Image BTMImg = new Image(new FileInputStream(new File("backToMainImg.png")));
        Image BTMImgHover = new Image(new FileInputStream(new File("backToMainImgHover.png")));
        buttonSetter(backButt, 256,48,0,0,BTMImg);

        backButt.setOnMouseEntered(e->backButt.setBackground(imageToBg(BTMImgHover)));
        backButt.setOnMouseExited(e->backButt.setBackground(imageToBg(BTMImg)));
        backButt.setOnMouseClicked(e->primaryStage.setScene(mainMenuScene));

        saveButt.setOnMouseEntered(e->saveButt.setBackground(imageToBg(saveImgHover)));
        saveButt.setOnMouseExited(e->saveButt.setBackground(imageToBg(saveImg)));
        saveButt.setOnMouseClicked(e->primaryStage.setScene(mainMenuScene));

        //ADDING ELEMENTS TO LAYOUT
        VBlayout.getChildren().addAll(micIDLabel,micIDField);
        VBlayout.getChildren().addAll(depIDLabel,depIDField);
        VBlayout.getChildren().addAll(depNameLabel,depNameField);
        VBlayout.getChildren().add(saveButt);
        VBlayout.getChildren().add(backButt);

        saveButt.setOnMouseClicked(e-> {
            micID = micIDField.getText();
            depID = depIDField.getText();
            depName = depNameField.getText();

            try {
                writeSett();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        });

        backButt.setOnMouseClicked(e -> primaryStage.setScene(mainMenuScene));

        return temp;
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        //CREATE/LOAD SETTINGS FILE
        settFile = new File("settings.txt");
        settFile.createNewFile();
        readSett();

        //SETTING SCENES
        mainMenuScene = mainMenuSetter(primaryStage);
        recordScene = recordSetter(primaryStage);
        statGenScene = statGenSetter(primaryStage);
        settingsScene = settingsSetter(primaryStage);

        //SETTING PRIMARYSTAGE ATTRIBUTES
        primaryStage.setScene(mainMenuScene);
        primaryStage.sizeToScene();
        primaryStage.setResizable(false);
        primaryStage.setTitle("Office Noise Analyzer");
        primaryStage.show();
    }

    public static void main(String[] args){
        launch(args);
    }
}