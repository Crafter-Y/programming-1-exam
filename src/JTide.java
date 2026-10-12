import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class JTide {
	/**
	 * Formatter for time only (<code>HH:mm</code>)
	 */
	private final static DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm").withZone(ZoneId.systemDefault());
	/**
	 * Formatter for date and time (code>dd.MM.yyyy HH:mm</code>)
	 */
    private final static DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm").withZone(ZoneId.systemDefault());
    /**
     * Formatter for date only (<code>dd.MM.yyyy</code>)
     */
    private final static DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy").withZone(ZoneId.systemDefault());

    /**
     * Main entry point
     * @param args CLI arguments (not used)
     */
	public static void main(String[] args) {
		List<Location> locations = JTide.readLocations();
		List<TideValue> values = JTide.readValues();
		
		try {
			new JTideTerm(locations,values);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

    private static List<String> readFileLines(String filename) {
        try {
            Path input = Paths.get(filename);

            return Files.readAllLines(input);
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }
	
	/**
	 * Read the locations
	 * @return locations
	 */
	public static List<Location> readLocations(){
        return JTide.readFileLines("./locations.txt")
                .stream()
                .map(JTide::parseLocation)
                .sorted(Comparator.comparing(a -> a.getName()))
                .toList();
	}
	
	
	/**
	 * Read the tide values
	 * @return tide values
	 */
	public static List<TideValue> readValues(){
        return JTide.readFileLines("./values.txt").stream().map(JTide::parseTideValue).toList();
	}
	
	/**
	 * Parse location from CSV format line
	 * @param line line to parse
	 * @return parsed location or <code>null</code> on error
	 */
	public static Location parseLocation(String line){
		try {
			String[] parts = line.split(";");
			return new Location(parts[0], parts[1], Double.parseDouble(parts[2]), Double.parseDouble(parts[3]));
		} catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * Parse tide value from CSV format line
	 * @param line line to parse
	 * @return parsed value or <code>null</code> on error
	 */
	public static TideValue parseTideValue(String line){
		try {
			String[] parts = line.split(";");
			return new TideValue(parts[0], Long.parseLong(parts[1]), TideType.valueOf(parts[2]), Float.parseFloat(parts[3]));
		} catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * Format time stamp as date with time
	 * @param timestamp time stamp to format
	 * @return date with time
	 */
	public static String formatDateTime(long timestamp) {
		return formatTime(timestamp, JTide.DATETIME_FORMATTER);
	}
	
	/**
	 * Format time stamp as time only
	 * @param timestamp time stamp to format
	 * @return time only
	 */
	public static String formatTime(long timestamp) {
		return formatTime(timestamp, JTide.TIME_FORMATTER);
	}
	
	/**
	 * Format time stamp as date only
	 * @param timestamp time stamp to format
	 * @return date only
	 */
	public static String formatDate(long timestamp) {
		return formatTime(timestamp, JTide.DATE_FORMATTER);
	}
	
	/**
	 * Format a time stamp according to provided formatter
	 * @param timestamp time stamp to format
	 * @param formatter formatter to use
	 * @return formatted time stamp as string
	 */
	private static String formatTime(long timestamp, DateTimeFormatter formatter) {
		try {
			// just in case for whatever reason we get milliseconds, try to detect it
			Instant tsInst =  timestamp >= 1_000_000_000_000l ? Instant.ofEpochMilli(timestamp) : Instant.ofEpochSecond(timestamp); 
			return formatter.format(tsInst);
		} catch (Exception e) {
			// Should not happen, but do not crash for invalid inputs, just return them
			return Long.toString(timestamp);
		}
	}	
}
