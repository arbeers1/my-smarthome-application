package regional;

import java.io.BufferedReader;
import java.net.URL;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * @author Alexander Beers
 * Gets the weather using openweather api.
 * NOTE: Weather is hardcoded and location can not be changed in ui.
 */
public class Weather {
  private static final String URL = "http://api.openweathermap.org/data/2.5/weather?q=baraboo&appid"
      + "=1d5625b036b3668f000b321a50a3f82a&units=imperial";

  /**
   * @return - Integer rounded temperature in F
   */
  public static int getTemp() {
    try {
      Object obj = new JSONParser().parse(getWeather());
      JSONObject jo = (JSONObject) obj;
      JSONObject main = (JSONObject) jo.get("main");
      Double d = (Double) main.get("temp");
      return d.intValue();
    } catch (ParseException e) {
      System.out.println(e.getLocalizedMessage());
    }
    return 0;
  }
  
  /**
   * @return - Brief text description of weather conditions (Ex: cloudy)
   */
  public static String getConditions() {
    try {
      Object obj = new JSONParser().parse(getWeather());
      JSONObject jo = (JSONObject) obj;
      JSONArray ar = (JSONArray) jo.get("weather");
      JSONObject weather = (JSONObject) ar.get(0);
      return (String) weather.get("description");
    }catch(ParseException e) {
      System.out.println(e.getLocalizedMessage());
    }
    return "";
  }
  
  /**
   * @return - String containing weather data
   */
  private static String getWeather() {
    String weatherReport = "";
    try {
      URL url = new URL(URL);
      java.net.URLConnection conn = url.openConnection();
      BufferedReader rd = new BufferedReader(new java.io.InputStreamReader(conn.getInputStream()));
      String line;
      while ((line = rd.readLine()) != null) {
        weatherReport += line;
      }
      rd.close();
    } catch (Exception e) {
    }
    return weatherReport;
  }
}
