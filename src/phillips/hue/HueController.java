package phillips.hue;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * @author Alexander Beers
 * The HueController interacts with the Hue Bridge. The 'bridge' is a hue provided hardware that 
 * connects to all of the lights and allows control through the api.
 * NOTE: The phillips hue bridge URL is hardcoded and thus can not be set to a new bridge system
 * in the user interface.
 */
public class HueController {
  
  //Base URL for api request. Final urls are built using cURL
  private static final String BASE_URL = "http://192.168.0.100/api/dQ8uv5kgFtCvVDdCUensB9HgujFEeXQyFb7M80Lu/lights";
  
  public HueController() {
  }
  
  /**
   * @return - the String list of number ids of all lights that are connected to the bridge
   *           NOTE: Not the user given id but the hue assigned number for each light.
   */
  public ArrayList<String> getLightIDs() {
    ArrayList<String> result = new ArrayList<String>();
    try {
      String data = hueAPIRequest("GET", "", "");
      result.add(Character.toString(data.charAt(2)));
      int index = 2;
      int lastIndex = 0;
      //Sorts through the metadata for the light ids
      while(true) {
        index = data.indexOf("},", index);
        if(index < lastIndex) { 
          break;
        }
        index = index + 3;
        try {
          Integer.parseInt(Character.toString(data.charAt(index)));
          result.add(Character.toString(data.charAt(index)));
        }catch(NumberFormatException e) {}
        lastIndex = index;
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return result;
  }
  
  /**
   * Gets data for the requested light
   * @param lightID - light to search
   * @return Object[] containing [(0)String: lightID, (1)String: User given light Id, (2) T/F On/Off,
   *  (3) long: Bri, (4) long: Hue, (5) long: sat]
   */
  public Object[] getLightData(String lightID) {
    Object[] result = new Object[6];
    try {
      String data = hueAPIRequest("GET", "/" + lightID, "");
      Object obj = new JSONParser().parse(data);
      JSONObject jo = (JSONObject) obj;
      
      result[0] = lightID;
      result[1] = (String) jo.get("name");
      
      Object stateO = new JSONParser().parse(jo.get("state").toString());
      JSONObject state = (JSONObject) stateO;
      result[2] = state.get("on");
      result[3] = state.get("bri");
      result[4] = state.get("hue");
      result[5] = state.get("sat");
    }catch(IOException | ParseException e) {}
    return result;
  }
  
  /**
   * Sends request to api for light editing
   * @param LightID - light id to change
   * @param dataJson - json containing data for modification following hue api docs syntax.
   */
  public void setLightData(String LightID, String dataJson) {
    try {
      hueAPIRequest("PUT", "/" + LightID + "/state", dataJson);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  /**
   * All Get and Put commands are sent through here. This method uses cURL to exchange data with the
   * hue api
   * @param Command - Get or Put to get or send data respectively.
   * @param urlExtension - extension to add to url (set proper light)
   * @param dataJson - data to send, leave blank if none
   * @return - The response of the request
   * @throws IOException
   */
  private String hueAPIRequest(String Command, String urlExtension, String dataJson) throws IOException {
    if(dataJson.equals("")) {
      dataJson = "{}";
    }
    String[] cURL = new String[] {"curl", "-X", Command, BASE_URL + urlExtension, "-H", 
        "\"Content-Type: application/json\"", "--data-binary", dataJson};
    Process process = Runtime.getRuntime().exec(cURL);
    BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
    String line;
    String response = "";
    while ((line = reader.readLine()) != null) {
      response += line;
    }
    return response;
  }
}
