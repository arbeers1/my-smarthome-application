package application;


/**
 * @author Alexander Beers
 * Starts a separate thread to run independently of the main cycle. 
 */
public class DisplayThread extends Thread{
  /**
   * Starts the display thread and the display
   */
  @Override
  public void run() {
    Display.main(null);
  }
  
//  public void delay(long time) {
//    try {
//      Thread.sleep(time);
//    }catch(InterruptedException e) {}
//  }
}
