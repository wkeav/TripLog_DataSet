import static org.junit.jupiter.api.Assertions.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

import org.junit.BeforeClass;
import org.junit.jupiter.api.Test;

class TripPointTest {
	
	@BeforeClass
	void setup() throws FileNotFoundException, IOException {
		TripPoint.readFile("triplog.csv");
	}

	@Test
	void testHeuristic1() throws FileNotFoundException, IOException {
		int stops = TripPoint.h1StopDetection();
		assertEquals(226, stops);
		ArrayList<TripPoint> moving = TripPoint.getMovingTrip();
		assertEquals(35.211037, moving.get(0).getLat());
		assertEquals(35.261436, moving.get(2).getLat());
		assertEquals(35.340107, moving.get(3).getLat());
		assertEquals(35.262207, moving.get(moving.size()-1).getLat());
	}
	
	@Test
	void testHeuristic2() throws FileNotFoundException, IOException {
		int stops = TripPoint.h2StopDetection();
		assertEquals(239, stops);
		ArrayList<TripPoint> moving = TripPoint.getMovingTrip();
		assertEquals(35.211037, moving.get(0).getLat());
		assertEquals(35.340107, moving.get(2).getLat());
		assertEquals(35.260788, moving.get(moving.size()-1).getLat());
	}
	
	@Test
	void testMovingTime() throws FileNotFoundException, IOException {
		TripPoint.h1StopDetection();
		BigDecimal actual = new BigDecimal(Double.toString(TripPoint.movingTime()));
		actual = actual.setScale(3, RoundingMode.HALF_UP);
		assertEquals(46.833, actual.doubleValue());
		
		TripPoint.h2StopDetection();
		actual = new BigDecimal(Double.toString(TripPoint.movingTime()));
		actual = actual.setScale(3, RoundingMode.HALF_UP);
		assertEquals(45.750, actual.doubleValue());
	}
	
	@Test
	void testStoppedTime() throws FileNotFoundException, IOException {
		TripPoint.h1StopDetection();
		BigDecimal actual = new BigDecimal(Double.toString(TripPoint.stoppedTime()));
		actual = actual.setScale(3, RoundingMode.HALF_UP);
		assertEquals(18.833, actual.doubleValue());
		
		TripPoint.h2StopDetection();
		actual = new BigDecimal(Double.toString(TripPoint.stoppedTime()));
		actual = actual.setScale(3, RoundingMode.HALF_UP);
		assertEquals(19.917, actual.doubleValue());
	}
	
	@Test
	void testAvgMovingSpeed() throws FileNotFoundException, IOException {
		TripPoint.h1StopDetection();
		BigDecimal actual = new BigDecimal(Double.toString(TripPoint.avgMovingSpeed()));
		actual = actual.setScale(1, RoundingMode.HALF_UP);
		assertEquals(103.7, actual.doubleValue());
		
		TripPoint.h2StopDetection();
		actual = new BigDecimal(Double.toString(TripPoint.avgMovingSpeed()));
		actual = actual.setScale(1, RoundingMode.HALF_UP);
		assertEquals(105.6, actual.doubleValue());
	}

}
