package regional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author Alexander Beers
 * Gets local date and time
 */
public class Date {
  //Sets formatter for date/time.
  private static final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("EEE MMM, d");
  private static final DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("h:mma");
  private static final DateTimeFormatter timeMilFormat = DateTimeFormatter.ofPattern("H:m:s");
  
  
  /**
   * @return - String date following dateFormat
   */
  public static String getDate() {
    LocalDateTime date = LocalDateTime.now();
    return date.format(dateFormat);
  }
  
  /**
   * @return - String time following timeFormat
   */
  public static String getTime() {
    LocalDateTime date = LocalDateTime.now();
    return date.format(timeFormat);
  }
  
  /**
   * @return - String time following timeMilFormat
   */
  public static String getTimeMil() {
    LocalDateTime date = LocalDateTime.now();
    return date.format(timeMilFormat);
  }
}
