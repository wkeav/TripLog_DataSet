import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class TripPoint {

	private double lat;	// latitude
	private double lon;	// longitude
	private int time;	// time in minutes
	
	private static ArrayList<TripPoint> trip;	// ArrayList of every point in a trip

	// default constructor
	public TripPoint() {
		time = 0;
		lat = 0.0;
		lon = 0.0;
	}
	
	   // ArrayList of every point in a trip
    private static ArrayList<TripPoint> movingTrip; 
    
	// constructor given time, latitude, and longitude
	public TripPoint(int time, double lat, double lon) {
		this.time = time;
		this.lat = lat;
		this.lon = lon;
	}
	
	// returns time
	public int getTime() {
		return time;
	}
	
	// returns latitude
	public double getLat() {
		return lat;
	}
	
	// returns longitude
	public double getLon() {
		return lon;
	}
	
	// returns a copy of trip ArrayList
	public static ArrayList<TripPoint> getTrip() {
		return new ArrayList<>(trip);
	}
	
	// uses the haversine formula for great sphere distance between two points
	public static double haversineDistance(TripPoint first, TripPoint second) {
		// distance between latitudes and longitudes
		double lat1 = first.getLat();
		double lat2 = second.getLat();
		double lon1 = first.getLon();
		double lon2 = second.getLon();
		
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
 
        // convert to radians
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);
 
        // apply formulae
        double a = Math.pow(Math.sin(dLat / 2), 2) +
                   Math.pow(Math.sin(dLon / 2), 2) *
                   Math.cos(lat1) *
                   Math.cos(lat2);
        double rad = 6371;
        double c = 2 * Math.asin(Math.sqrt(a));
        return rad * c;
	}
	
	// finds the average speed between two TripPoints in km/hr
	 public static double avgSpeed(TripPoint a, TripPoint b) {
	        int timeInMin = Math.abs(a.getTime() - b.getTime());
	        double dis = haversineDistance(a, b);
	        double kmpmin = dis / timeInMin;
	        return kmpmin * 60;
	 }
	// returns the total time of trip in hours
	public static double totalTime() {
		int minutes = trip.get(trip.size()-1).getTime();
		double hours = minutes / 60.0;
		return hours;
	}
	
	// finds the total distance traveled over the trip
	public static double totalDistance() throws FileNotFoundException, IOException {
		
		double distance = 0.0;
		
		if (trip.isEmpty()) {
			readFile("triplog.csv");
		}
		
		for (int i = 1; i < trip.size(); ++i) {
			distance += haversineDistance(trip.get(i-1), trip.get(i));
		}
		
		return distance;
	}
	
	public String toString() {
		
		return null;
	}

	public static void readFile(String filename) throws FileNotFoundException, IOException {

		// construct a file object for the file with the given name.
		File file = new File(filename);

		// construct a scanner to read the file.
		Scanner fileScanner = new Scanner(file);
		
		// initiliaze trip
		trip = new ArrayList<TripPoint>();

		// create the Array that will store each lines data so we can grab the time, lat, and lon
		String[] fileData = null;

		// grab the next line
		while (fileScanner.hasNextLine()) {
			String line = fileScanner.nextLine();

			// split each line along the commas
			fileData = line.split(",");

			// only write relevant lines
			if (!line.contains("Time")) {
				// fileData[0] corresponds to time, fileData[1] to lat, fileData[2] to lon
				trip.add(new TripPoint(Integer.parseInt(fileData[0]), Double.parseDouble(fileData[1]), Double.parseDouble(fileData[2])));
			}
		}

		// close scanner
		fileScanner.close();
		
		
	}

	
	public static int h1StopDetection()throws FileNotFoundException, IOException {
		int stops = 0;
		double displacementThreshold = 0.6; //km
		movingTrip = new ArrayList<>();

		if(!tripisempty())
		{
			readFile("triplog.csv");
		}
		//Initialize and fill the movingTrip ArrayList with only the points that are moving 
		movingTrip.add(trip.get(0));

		for(int i = 1; i < trip.size(); ++i)
		{
			double distance = haversineDistance(trip.get(i - 1), trip.get(i));


			if(distance <= displacementThreshold)
			{
				stops++;
			}

			else
			{
				movingTrip.add(trip.get(i));
			}
		}
		return stops;
	}
	

	  
	private static boolean tripisempty() {
		return false;
	}

	public static int h2StopDetection() throws FileNotFoundException, IOException{

		movingTrip = new ArrayList<TripPoint>();
		double stopRadius = 0.5; //km
		int count = 0;

		ArrayList<TripPoint> zone = new ArrayList<TripPoint>();
		for(TripPoint current: trip) {
			if(zone.size()==0) {
				zone.add(current);
				continue;
			}
			boolean inStopZone = false;
			for(TripPoint stop : zone) {
				if(haversineDistance(stop, current) < stopRadius) {
					inStopZone = true;
				}
			}
			if(inStopZone) {
				zone.add(current);
			} else {
				if(zone.size()>=3) {
					count += zone.size();
				}else {
					for(TripPoint sp : zone) {
						movingTrip.add(sp); 
					}
				}
				zone.clear();
				zone.add(current);
			}
		}
		// calculate the stop zones one last time
		if(zone.size()>=3) {
			count += zone.size();
		}else {
			for(TripPoint stop : zone) {
				movingTrip.add(stop);
			}
		}
		return count;
	}

	public static double movingTime() {
	    int startIndex = trip.size() - movingTrip.size();
	    int startTime = trip.get(startIndex).getTime();
	    int endTime = trip.get(trip.size() - 1).getTime();
	    return (endTime - startTime) / 60.0; //in hours 
	}
	
	public static double stoppedTime()
	{
		return totalTime() - movingTime();
	}

	public static double avgMovingSpeed()
	{ 

		double distance = 0.0;
		for(int j = 1; j < movingTrip.size(); j++)
		{
			distance += haversineDistance(movingTrip.get(j - 1), movingTrip.get(j));
		}
		return distance / movingTime();  //km/hr
	}


	public static ArrayList<TripPoint> getMovingTrip(){

		return new ArrayList<>(movingTrip);

	}

}
