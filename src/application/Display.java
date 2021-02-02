/**
 * File: Display.java
 * Author: Alex Beers
 * Control and design of GUI
 */

package application;

import java.util.ArrayList;
import eu.hansolo.medusa.Gauge;
import eu.hansolo.medusa.skins.TileSparklineSkin;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import main.BSmart;
import main.BSmartRunner;
import pc.Monitor;
import phillips.hue.Light;
import regional.Date;
import regional.Weather;

public class Display extends Application {
  private Stage stage;
  private Scene scene;
  private VBox mainRoot;
  private HBox mainContainer;
  private ScrollPane lightContainer;
  private VBox lightEditContainer;
  private VBox messagesContainer;
  private VBox pcContainer;
  private static Label time;
  private static Label date;
  private static Label temp;
  private static Label weather;
  private BSmart bs;
  private Button on;
  private Label editLabel;
  private Button off;
  private Slider brightness;
  private ColorPicker cPick;
  private static Label filler;
  private static Gauge gpuUsage;
  private static Gauge gpuTemp;
  private static Gauge cpuUsage;
  private static Gauge cpuTemp;  

  /**
   * Called on start. Builds and displays GUI.
   * @param - primaryStage: Called via Launch(). No parameter needed.
   */
  @Override
  public void start(Stage primaryStage) throws Exception {
    bs = new BSmart(); //Initializes BSmart an object which interfaces with HueController
    //Build the various screens
    mainRoot = buildMainRoot();
    lightEditContainer = buildLightEditContainer();
    lightContainer = buildLightContainer();
    pcContainer = buildPcContainer();
    //Show the screen
    scene = new Scene(mainRoot, 800, 480);
    stage = new Stage();
    stage.setScene(scene);
    stage.initStyle(StageStyle.UNDECORATED);
    //Method on application close
    stage.setOnCloseRequest(event -> {
      Platform.exit();
      BSmartRunner.killThread();
    });
    stage.show();  
    BSmartRunner.displayReady();
  }
  
  /**
   * Builds the main screen
   * @return a VBox container with the main screen.
   */
  private VBox buildMainRoot() {
    VBox vbox = new VBox(10);
    vbox.setStyle("-fx-background-color: #000000;");
    
    Region spacer = new Region();
    HBox.setHgrow(spacer, Priority.ALWAYS);
    spacer.setMinWidth(Region.USE_PREF_SIZE);
    HBox topBox = new HBox();
    
    VBox timeBox = new VBox();
    time = buildLabel("", 45);
    date = buildLabel("", 23);
    timeBox.getChildren().addAll(time, date);
    
    VBox weatherBox = new VBox();
    temp = buildLabel("", 45);
    temp.setMaxWidth(Double.MAX_VALUE);
    temp.setAlignment(Pos.CENTER_RIGHT);
    weather = buildLabel("", 23);
    weather.setMaxWidth(Double.MAX_VALUE);
    weather.setAlignment(Pos.CENTER_RIGHT);
    weatherBox.getChildren().addAll(temp, weather);
    
    mainContainer = new HBox(5);
    
    Label smallSpacer = new Label("");
    smallSpacer.setMinWidth(10);
    Region smallSpacer2 = new Region();
    smallSpacer2.setMinWidth(10);
    topBox.getChildren().addAll(smallSpacer, timeBox, spacer, weatherBox, smallSpacer2);
    
    VBox buttonBox = new VBox(10);
    
    Region smallSpacer3 = new Region();
    smallSpacer3.setMinHeight(45);
    Button lightButton = buildButton("Lights", true);
    Button messageButton = buildButton("Messages (Unavailable)", true);
    Button pcMonitorButton = buildButton("PC Monitor", true);
    
    buttonBox.getChildren().addAll(smallSpacer3, lightButton, messageButton, pcMonitorButton);
    
    Region smallSpacer4 = new Region();
    smallSpacer4.setMinWidth(5);
    filler = new Label("");
    
    mainContainer.getChildren().addAll(smallSpacer4, buttonBox, filler);
    
    //Button handlers 
    lightButton.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
      public void handle(MouseEvent e) {
        bs.updateLights();
        if(mainContainer.getChildren().get(2).equals(lightContainer) || mainContainer.getChildren().get(2).equals(lightEditContainer)) {
          mainContainer.getChildren().set(2, filler);
        }else {
          mainContainer.getChildren().set(2, lightContainer);
        }
      }
    });
    
    messageButton.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
      public void handle(MouseEvent e) {
        mainContainer.getChildren().set(2, filler);
      }
    });
    
    pcMonitorButton.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
      public void handle(MouseEvent e) {
        if(mainContainer.getChildren().get(2).equals(pcContainer)) {
          mainContainer.getChildren().set(2, filler);
        }else {
          mainContainer.getChildren().set(2, pcContainer);
        }
      }
    });
    vbox.getChildren().addAll(topBox, mainContainer);
    
    return vbox;
  }
  
  /**
   * Builds the PC Monitor screen
   * @return a VBox container with the PC Monitor screen
   */
  private VBox buildPcContainer() {
    VBox vbox = new VBox(5);
    vbox.setMinWidth(200);
    HBox gpu = new HBox(5);
    gpuUsage = newGauge("GPU Usage", "%", 100);
    gpuTemp = newGauge("GPU Temp", "°C", 80);
    gpu.getChildren().addAll(gpuUsage, gpuTemp);
    
    HBox cpu = new HBox(5);
    cpuUsage = newGauge("CPU Load", "%", 100);
    cpuTemp = newGauge("CPU Temp", "°C", 100);
    cpu.getChildren().addAll(cpuUsage, cpuTemp);
    //ram = newGauge("Memory", "MB", 16384);
    vbox.getChildren().addAll(gpu, cpu);
    return vbox;
  }
  
  /**
   * Builds and styles a new Gauge which displays data.
   * @param title - title of gauge to display
   * @param unit - unit of gauge measurement
   * @param maxValue - the max value which the gauge can hit
   * @return Gauge with desired parameters
   */
  private Gauge newGauge(String title, String unit, double maxValue) {
    Gauge gauge = new Gauge();
    gauge.setMaxValue(maxValue);
    gauge.setUnit(unit);
    gauge.setTitle(title);
    gauge.setTitleColor(Color.WHITE);
    gauge.setValueColor(Color.WHITE);
    gauge.setBarColor(Color.CRIMSON);
    gauge.setDecimals(0); 
    gauge.setUnitColor(Color.WHITE);
    gauge.setAnimated(false);
    //gauge.setAnimationDuration(450);
    gauge.setSkin(new TileSparklineSkin(gauge));
    return gauge;
  }
  
  /**
   * Constructs and styles a label.
   * @param text - text to display
   * @param fontSize - font size
   * @return - label with parameters.
   */
  private Label buildLabel(String text, int fontSize) {
    Label label = new Label(text);
    label.setFont(new Font("Arial", fontSize));
    label.setTextFill(Color.web("#338d9e"));
    return label;
  }
  
  /**
   * Builds a button, styles it, and handles button clicking style effect.
   * @param text - text to display
   * @param bool - true if style effect on button click is desired
   * @return
   */
  private Button buildButton(String text, boolean bool) {
    Button button = new Button(text);
    button.setFont(new Font("Arial", 15));
    //button.setTextAlignment(TextAlignment.LEFT);
    button.setTextFill(Color.web("#338d9e"));
    button.setStyle("-fx-border-color: #338d9e; -fx-border-width: 2; -fx-border-style: solid;"
        +"-fx-background-color: #000000; -fx-alignment: BASELINE_LEFT;");
    button.setPrefHeight(75);
    button.setPrefWidth(300);
    if(bool) {
      button.addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
        public void handle(MouseEvent e) {
          button.setStyle("-fx-background-color: rgba(51,141,158,.4);-fx-border-color: #338d9e; "
              + "-fx-border-width: 2; -fx-border-style: solid;-fx-alignment: BASELINE_LEFT;");
        }
      });
      button.addEventHandler(MouseEvent.MOUSE_RELEASED, new EventHandler<MouseEvent>() {
        public void handle(MouseEvent e) {
          button.setStyle("-fx-background-color: #000000;-fx-border-color: #338d9e; "
              + "-fx-border-width: 2; -fx-border-style: solid;-fx-alignment: BASELINE_LEFT;");
        }
      });
    }
    return button;
  }

  /**
   * Screen which will contain buttons for all the hue lights.
   * @return ScrollPane containing all light buttons.
   */
  private ScrollPane buildLightContainer() {

    VBox vbox = new VBox(10);
    Region smallSpacer = new Region();
    smallSpacer.setMinHeight(45);
    vbox.getChildren().add(smallSpacer);
    // gets list of lights and adds them to a new button
    ArrayList<Light> lightList = bs.getLightList();
    for(int i = 0; i < lightList.size(); i++) {
      Button bt = buildButton(lightList.get(i).getId(), true);
      bt.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
        public void handle(MouseEvent e) {
          //Sends to light edit screen upon a light being selected
          updateLightConfig(bt.getText());
          mainContainer.getChildren().set(2, lightEditContainer);
        }
      });
      vbox.getChildren().add(bt);
    }
    ScrollPane sc = new ScrollPane(vbox);
    vbox.setStyle("-fx-background: #000000");
    sc.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
    return sc;
  }
  
  /**
   * Builds the screen which allows light editing to occur
   * @return A VBox container with light editing tools
   */
  private VBox buildLightEditContainer() {
    VBox vbox = new VBox(10);
    Region smallSpacer = new Region();
    smallSpacer.setMinHeight(35);
    
    editLabel = buildLabel("Light x", 20);
    
    HBox onOff = new HBox(5);
    //Sets off/on buttons
    off = buildButton("Off", false);
    on = buildButton("On", false);
    off.setMaxWidth(235); off.setStyle("-fx-border-color: #338d9e; -fx-border-width: 2; -fx-border-style: solid;"
        +"-fx-background-color: #000000; -fx-text-fill: #ff0000; -fx-alignment: BASELINE_CENTER");
    on.setMaxWidth(235); on.setStyle("-fx-border-color: #338d9e; -fx-border-width: 2; -fx-border-style: solid;"
        +"-fx-background-color: #000000; -fx-text-fill: #008000; -fx-alignment: BASELINE_CENTER;");
    onOff.getChildren().addAll(off, on);
    on.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
      public void handle(MouseEvent e) {
        bs.setOn(true, editLabel.getText());
        updateLightConfig(editLabel.getText());
      }
    });
    off.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
      public void handle(MouseEvent e) {
        bs.setOn(false, editLabel.getText());
        updateLightConfig(editLabel.getText());
      }
    });
    //Color picker allows modifying color
    cPick = new ColorPicker();
    cPick.setOnAction((ActionEvent e) -> {
      Color c = cPick.getValue();
      bs.setColor(editLabel.getText(),c.getHue(), c.getSaturation(), c.getBrightness());
      updateLightConfig(editLabel.getText());
    });
    cPick.setMinHeight(150);
    cPick.setMinWidth(400);
    cPick.getStylesheets().add(Display.class.getResource("colorPicker.css").toExternalForm());
    //Slider for adjusting light brightness
    brightness = new Slider(1, 254, 1);
    brightness.setStyle("-fx-control-inner-background: #303030; -fx-background: transparent;-fx-color: #338d9e");
    brightness.valueProperty().addListener(new ChangeListener<Number>() {
      public void changed(ObservableValue<? extends Number> ov,
          Number old_val, Number new_val) {
              bs.setBri(editLabel.getText(), new_val.doubleValue());
              updateLightConfig(editLabel.getText());
      }
    });
    Label title = buildLabel("Brightness", 15);
    
    vbox.getChildren().addAll(smallSpacer, editLabel, onOff, cPick, title, brightness);
    vbox.setPrefWidth(485);
    vbox.setAlignment(Pos.TOP_CENTER);
    
    return vbox;
  }
  
  /**
   * When the light editing screen is accessed this method is called to get the most recent data
   * for that light and update it.
   * @param lightId - The id for the light of which information is being requested
   */
  public void updateLightConfig(String lightId) {
    editLabel.setText(lightId);
    if(bs.isOn(lightId)) {
      on.setStyle("-fx-background-color: rgba(51,141,158,.4);-fx-border-color: #338d9e;"
          + "-fx-border-width: 2; -fx-border-style: solid;-fx-alignment: BASELINE_CENTER;"
          + "-fx-text-fill: #008000;");
      off.setStyle("-fx-background-color: rgba(0,0,0,1);-fx-border-color: #338d9e;"
          + "-fx-border-width: 2; -fx-border-style: solid;-fx-alignment: BASELINE_CENTER;"
          + "-fx-text-fill: #ff0000;");
    }else {
      on.setStyle("-fx-background-color: rgba(0,0,0,1);-fx-border-color: #338d9e;"
          + "-fx-border-width: 2; -fx-border-style: solid;-fx-alignment: BASELINE_CENTER;"
          + "-fx-text-fill: #008000;");
      off.setStyle("-fx-background-color: rgba(51,141,158,.4);-fx-border-color: #338d9e;"
          + "-fx-border-width: 2; -fx-border-style: solid;-fx-alignment: BASELINE_CENTER;"
          + "-fx-text-fill: #ff0000;");
    }
    double[] colors = bs.getColor(lightId);
    Color color = Color.hsb(colors[0], colors[1], colors[2]);
    cPick.setValue(color);
    String value = cPick.getValue().toString().substring(2, 8);
    cPick.setStyle("-fx-background-color: #" + value +";-fx-color-label-visible: false; ");
    brightness.setValue(bs.getBri(editLabel.getText()));
  }
  
  /**
   * Updates the main screen labels
   */
  public static void updateLabels() {
    time.setText(Date.getTime());
    date.setText(Date.getDate());
    temp.setText(Weather.getTemp() + "°");
    weather.setText(Weather.getConditions());
    
    gpuUsage.setValue(Monitor.getGpuUsage());
    gpuTemp.setValue(Monitor.getGpuTemp());
    cpuUsage.setValue(Monitor.getCpuUsage());
    cpuTemp.setValue(Monitor.getCpuTemp());
  }
  
  /**
   * called upon initialization of Display
   * @param args - ignore
   */
  public static void main(String[] args) {
    launch();
  }
}
