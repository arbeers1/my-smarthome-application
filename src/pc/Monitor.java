package pc;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Scanner;

/**
 * @author Alexander Beers
 * Thread for reading and storing pc data. This works through a pc monitoring service which logs and
 * uploads a file to Microsoft Onedrive. The file is downloaded by the raspberry pi to display temps
 * and other statistics from a different computer different than the raspberry pi which the program
 * is ran on.
 * NOTE: The file link is hardcoded and thus not changable from the user interface.
 */
public class Monitor {
  private static int gpuUsage = 0;
  private static int gpuTemp = 0;
  private static int cpuUsage = 0;
  private static int cpuTemp = 0;
  private static final String DATA_URL = "https://onedrive.live.com/download?cid=58E5EC6DA03E791D&resid=58E5EC6DA03E791D%212435&authkey=AD0-8HoP5egEJJ8";
  
  /**
   * Acquire the most recent file log and updates the appropriate values.
   */
  public static void update() {
    String[] list = new String[7];
    String line = "";
    try {
      Scanner sc = new Scanner(getStats(), "ISO-8859-1");
      while(sc.hasNextLine()) {
        line = sc.nextLine();
      }
      sc.close();
    }catch(NullPointerException e) {}
    Scanner lineReader = new Scanner(line);
    lineReader.useDelimiter(",");
    int i = 0;
    while(lineReader.hasNext()) {
      list[i] = lineReader.next();
      i++;
    }
    lineReader.close();
    try {
      if(list[2] != null) {
        gpuTemp = (int) Double.parseDouble(list[2]);
        gpuUsage = (int) Double.parseDouble(list[3]);
        cpuUsage = (int) Double.parseDouble(list[4]);
        cpuTemp = (int) Double.parseDouble(list[6]);
      }
    }catch(NumberFormatException e) {}
    
  }
  
  /**
   * Gets the file data from the internet and stores it in a parsable format.
   * @return - ReadableByteChannel containing log file
   */
  private static ReadableByteChannel getStats() {
    try {
      URL download = new URL(DATA_URL);
      ReadableByteChannel s = Channels.newChannel(download.openStream());
      return s;
    } catch (IOException e) {
    } 
    return null;
  }
  
  /**
   * @return gpu usage
   */
  public static int getGpuUsage() {
    return gpuUsage;
  }
  
  /**
   * @return gpu temp
   */
  public static int getGpuTemp() {
    return gpuTemp;
  }
  
  /**
   * @return the cpu usage
   */
  public static int getCpuUsage() {
    return cpuUsage;
  }
  
  /**
   * @return the cpu temp
   */
  public static int getCpuTemp() {
    return cpuTemp;
  }
  
}
