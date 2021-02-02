package main;

//import java.io.IOException;
//import java.net.URL;
//import java.net.URLConnection;
import java.util.ArrayList;
import phillips.hue.HueController;
import phillips.hue.Light;

/**
 * @author Alexander Beers
 *BSmart allows interfacing between the buttons of the Display class and the HueController. The 
 *HueController makes calls to the hue api.
 */
public class BSmart {
  private HueController hc; //Interfaces with hue api
  private ArrayList<Light> lightList; // list of lights
  
  public BSmart() {
//Was used to ping for internet connection to prevent app starting prior to the raspberry pi
//accessing the internet which would prevent some values from loading. Commented out for now.
//    boolean internetConnected = false;
//    do {
//      try{
//        URL url = new URL("http://www.google.com");
//        URLConnection connection = url.openConnection();
//        connection.connect();
//        internetConnected = true;
//      }catch(IOException e) {
//        internetConnected = false;
//      }
//      delay(200);
//    }while(!internetConnected);
    
    hc = new HueController();
    ArrayList<String> lightIDS = new ArrayList<String>();
    // Receives light Ids
    try {
      lightIDS = hc.getLightIDs();
    }catch(StringIndexOutOfBoundsException e) {
      System.out.println("Error connecting to bridge.");
    }
    lightList = new ArrayList<Light>();
    for(int i = 0; i < lightIDS.size(); i++) {
      lightList.add(new Light(hc.getLightData(lightIDS.get(i))));
    }
  }
  
  /**
   * @return the list of lights
   */
  public ArrayList<Light> getLightList(){
    return lightList;
  }
  
  /**
   * Refreshes light data
   */
  public void updateLights() {
    for(int i = 0; i < lightList.size(); i++) {
      lightList.get(i).update(hc.getLightData(lightList.get(i).getRealId()));
    }
  }
  
  /**
   * Checks if a light is on
   * @param lightId - light to check
   * @return true if on, false if off
   */
  public boolean isOn(String lightId) {
    for(int i = 0; i < lightList.size(); i++) {
      if(lightList.get(i).getId().equals(lightId)) {
        return lightList.get(i).getOn();
      }
    }
    return false;
  }
  
  /**
   * Turns the light on/off
   * @param on - true to turn light on, false to turn off
   * @param lightId - light to modify
   */
  public void setOn(boolean on, String lightId) {
    for(int i = 0; i < lightList.size(); i++) {
      if(lightList.get(i).getId().equals(lightId)) {
        if(on) {
          lightList.get(i).turnOn();
        }else {
          lightList.get(i).turnOff();
        }
      }
    }
  }
  
  /**
   * Sets the color of the light
   * @param lightId - light to modify
   * @param hue - hue to set. A number between 0 and 360
   * @param sat - saturation to set. A number between 0 and 100
   * @param bri - brightness to set. A number between 0 and 100
   */
  public void setColor(String lightId, double hue, double sat, double bri) {
    for(int i = 0; i < lightList.size(); i++) {
      if(lightList.get(i).getId().equals(lightId)) {
        //Converts to Hue api number range
        double huea = hue/360 * 65535;
        double sata = sat * 254;
        double bria = bri * 254;
        lightList.get(i).setHue((long) huea);
        lightList.get(i).setSat((long) sata);
        lightList.get(i).setBright((long) bria);
      }
    }
  }
  
  /**
   * Gets the current color of a light
   * @param lightId - light Id to look up
   * @return - a double array with hue at index 0, sat at index 1, and bri at index 2.
   */
  public double[] getColor(String lightId) {
    double[] val = new double[3];
    for(int i = 0; i < lightList.size(); i++) {
      if(lightList.get(i).getId().equals(lightId)) {
        double hue = lightList.get(i).getHue();
        double sat = lightList.get(i).getSat();
        double bri = lightList.get(i).getBright();
        val[0] = (hue/65535 * 360);
        val[1] = (sat/254);
        val[2] = (bri/254);
      }
    }
    return val;
  }
  
  /**
   * Gets brightness of light
   * @param lightId - light to look up
   * @return value of brightness 0-100
   */
  public double getBri(String lightId) {
    for(int i = 0; i < lightList.size(); i++) {
      if(lightList.get(i).getId().equals(lightId)) {
        return (double) lightList.get(i).getBright();
      }
    }
    return 1;
  }
  
  /**
   * Sets brightness of light
   * @param lightId - light to modify
   * @param bri - brightness to set
   */
  public void setBri(String lightId, double bri) {
    for(int i = 0; i < lightList.size(); i++) {
      if(lightList.get(i).getId().equals(lightId)) {
        lightList.get(i).setBright((long) bri);
      }
    }
  }
  
}
