package main;

import application.Display;
import application.DisplayThread;
import javafx.application.Platform;
import pc.Monitor;

/**
 * @author Alex Beers
 * The runner of the application. Starts display then cycles through continuously refreshing data.
 */
public class BSmartRunner {  
  private static boolean run = false; // condition to wait for display to start
  
  public static void main(String[] args) {
    new DisplayThread().start(); // starts display
    loop();
  }
  
  /**
   * Cycles the update of UI and Labels.
   */
  public static void loop() {
  while(true) {  
      while(run) {
        Monitor.update();
        Platform.runLater(new Runnable() {
          @Override
          public void run() {
            Display.updateLabels();
          }
        });
        delay(1000);
      }
      delay(100);
    }
  }
  
  /**
   * Allows the display to tell the main thread that it is ready.
   */
  public static void displayReady() {
    run = true;
  }
  
  /**
   * Sets thread to sleep
   * @param time - time in ms to sleep
   */
  public static void delay(long time) {
    try {
      Thread.sleep(time);
    }catch(InterruptedException e) {}
  }
  
  /**
   * Stops the loop/main thread on display thread close.
   */
  public static void killThread() {
    System.exit(0);
  }
}
