package phillips.hue;
/**
 * @author Alex Beers
 * Light object represents each light found on the bridge.
 */
public class Light {
  private String realId; //Bridge defined number id
  private String id; //User defined text id
  private boolean on; //on/off status
  private long bright; // brightness value
  private long hue; // hue value
  private long sat; // sat value
  private HueController hc; // used to make hue api requests.
  
  /**
   * Constructor initializes vars
   * @param light - The object[] obtained in BSmart containing light data
   */
  public Light (Object[] light) {
    this.realId = (String) light[0];
    this.id = (String) light[1];
    this.on = (boolean) light[2];
    this.bright = (long) light[3];
    this.hue = (long) light[4];
    this.sat = (long) light[5];
    this.hc = new HueController();
  }
  
  /**
   * Turns light on
   * @return - result
   */
  public String turnOn() {
    hc.setLightData(this.realId, "{\"on\":true}");
    on = true;
    return "light " + this.realId + " turned on.";
  }
  
  /**
   * Turns light off
   * @return - result
   */
  public String turnOff() {
    hc.setLightData(this.realId, "{\"on\":false}");
    on = false;
    return "light " + this.realId + " turned off.";
  }
  
  /**
   * Sets brightness
   * @param value - long brightness value
   * @return - result
   */
  public String setBright(long value) {
    hc.setLightData(this.realId, "{\"bri\":" + value + "}");
    bright = value;
    return "set brightness to " + value;
  }
  
  /**
   * Sets hue
   * @param value - sets hue
   * @return - result
   */
  public String setHue(long value) {
    hc.setLightData(this.realId, "{\"hue\":" + value + "}");
    hue = value;
    return "set hue to " + value;
  }
  
  /**
   * @param value - sat value
   * @return - result
   */
  public String setSat(long value) {
    hc.setLightData(this.realId, "{\"sat\":" + value + "}");
    return "set saturation to " + value;
  }
  
  /**
   * Returns number id in string format
   * @return
   */
  public String getRealId() {
    return realId;
  }
  
  /**
   * Returns user defined id
   * @return
   */
  public String getId() {
    return id;
  }
  
  /**
   * @return - true if on/ false if off
   */
  public boolean getOn() {
    return on;
  }
  
  /**
   * @return - brightness value
   */
  public long getBright() {
    return bright;
  }
  
  /**
   * @return - hue value
   */
  public long getHue() {
    return hue;
  }
  
  /**
   * @return - sat value
   */
  public long getSat() {
    return sat;
  }
  
  /**
   * Updates light
   * @param light - The object[] obtained in BSmart containing light data
   */
  public void update(Object[] light) {
    this.realId = (String) light[0];
    this.id = (String) light[1];
    this.on = (boolean) light[2];
    this.bright = (long) light[3];
    this.hue = (long) light[4];
    this.sat = (long) light[5];
    this.hc = new HueController();
  }
}
