package tests;

import helpers.FileHandler; 
import helpers.User;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.jupiter.api.Test;
import weka.WekaAnalyser;
import weka.Weka_resultFile;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;

public class AnalysenTest {
/**
 * !!!!!!!!!!!!!!!!!!!!!README!!!!!!!!!!!!!!!!!!
 * !!!!!!!!!!!!!!!!!!!!!README!!!!!!!!!!!!!!!!!!
 * 
 * 
 * VOR DEM TEST MUSS DER USER "testuser" REGISTERIERT WORDEN SEIN.
 * DANACH MUSS MAN SICH EINMAL EINLOGGEN UND DIE "kd100.csv" HOCHLADEN!!!!
 * DANN KLAPPT DER TEST!
 * 
 * !!!!!!!!!!!!!!!!!!!!!README!!!!!!!!!!!!!!!!!!
 * !!!!!!!!!!!!!!!!!!!!!README!!!!!!!!!!!!!!!!!!
 */
	@Test
	public void testUmsatzstaerksteTagUhrzeit() throws Exception {
		StopWatch watch = new StopWatch();
		watch.reset();
		watch.start();
		String fileName = "kd100.csv";

		User user = new User("testuser");
		FileHandler filehandler = new FileHandler(user);
		WekaAnalyser analyser = new WekaAnalyser(fileName, user);
		System.out.println("watch time testUmsatzstaerksteTagUhrzeit1: " + watch.getTime());
		ArrayList<Weka_resultFile> wekaFiles = analyser.umsatzstaerksteTagUhrzeit(filehandler);
		
		assertEquals(1933, wekaFiles.get(1).getYdata()[0]);
		assertEquals(1182, wekaFiles.get(1).getYdata()[1]);
		assertEquals(1395, wekaFiles.get(1).getYdata()[2]);
		assertEquals(411, wekaFiles.get(1).getYdata()[3]);
		assertEquals(2497, wekaFiles.get(1).getYdata()[4]);
		assertEquals(8976, wekaFiles.get(1).getYdata()[5]);

		assertEquals(4625, wekaFiles.get(0).getYdata()[0]);
		assertEquals(3928, wekaFiles.get(0).getYdata()[1]);
		assertEquals(2204, wekaFiles.get(0).getYdata()[2]);
		assertEquals(2470, wekaFiles.get(0).getYdata()[3]);
		assertEquals(3167, wekaFiles.get(0).getYdata()[4]);
		System.out.println("watch time testUmsatzstaerksteTagUhrzeit: " + watch.getTime());
		watch.stop();
	}
	@Test
	void testUhrzeitProTag() throws Exception {
		StopWatch watch = new StopWatch();
		watch.reset();
		watch.start();
		String fileName = "kd100.csv";

		User user = new User("testuser");
		FileHandler filehandler = new FileHandler(user);
		WekaAnalyser analyser = new WekaAnalyser(fileName, user);

		ArrayList<Weka_resultFile> wekaFiles = analyser.uhrzeitProTag(filehandler);

		assertEquals(287, wekaFiles.get(0).getYdata()[0]);
		assertEquals(613, wekaFiles.get(0).getYdata()[1]);
		assertEquals(272, wekaFiles.get(0).getYdata()[2]);
		assertEquals(59, wekaFiles.get(0).getYdata()[3]);
		assertEquals(702, wekaFiles.get(0).getYdata()[4]);

		assertEquals(285, wekaFiles.get(1).getYdata()[0]);
		assertEquals(307, wekaFiles.get(1).getYdata()[1]);
		assertEquals(0, wekaFiles.get(1).getYdata()[2]);
		assertEquals(365, wekaFiles.get(1).getYdata()[3]);
		assertEquals(225, wekaFiles.get(1).getYdata()[4]);

		assertEquals(602, wekaFiles.get(2).getYdata()[0]);
		assertEquals(171, wekaFiles.get(2).getYdata()[1]);
		assertEquals(0, wekaFiles.get(2).getYdata()[2]);
		assertEquals(0, wekaFiles.get(2).getYdata()[3]);
		assertEquals(622, wekaFiles.get(2).getYdata()[4]);

		assertEquals(205, wekaFiles.get(3).getYdata()[0]);
		assertEquals(129, wekaFiles.get(3).getYdata()[1]);
		assertEquals(77, wekaFiles.get(3).getYdata()[2]);
		assertEquals(0, wekaFiles.get(3).getYdata()[3]);
		assertEquals(0, wekaFiles.get(3).getYdata()[4]);

		assertEquals(1386, wekaFiles.get(4).getYdata()[0]);
		assertEquals(350, wekaFiles.get(4).getYdata()[1]);
		assertEquals(240, wekaFiles.get(4).getYdata()[2]);
		assertEquals(0, wekaFiles.get(4).getYdata()[3]);
		assertEquals(521, wekaFiles.get(4).getYdata()[4]);

		assertEquals(1860, wekaFiles.get(5).getYdata()[0]);
		assertEquals(2358, wekaFiles.get(5).getYdata()[1]);
		assertEquals(1615, wekaFiles.get(5).getYdata()[2]);
		assertEquals(2046, wekaFiles.get(5).getYdata()[3]);
		assertEquals(1097, wekaFiles.get(5).getYdata()[4]);
		System.out.println("watch time uhrzeitProTag: " + watch.getTime());
		watch.stop();
	}
	@Test
	public void testKSETU() throws Exception {
		StopWatch watch = new StopWatch();
		watch.reset();
		watch.start();
		String fileName = "kd100.csv";

		User user = new User("testuser");
		FileHandler filehandler = new FileHandler(user);
		WekaAnalyser analyser = new WekaAnalyser(fileName, user);
		ArrayList<Weka_resultFile> wekaFiles = analyser.kundenhaeufigkeit(filehandler);

		// Überprüfe die Kundenmengen nach Tagen
		assertEquals(13, wekaFiles.get(0).getYdata()[0]);
		assertEquals(7, wekaFiles.get(0).getYdata()[1]);
		assertEquals(10, wekaFiles.get(0).getYdata()[2]);
		assertEquals(4, wekaFiles.get(0).getYdata()[3]);
		assertEquals(17, wekaFiles.get(0).getYdata()[4]);
		assertEquals(49, wekaFiles.get(0).getYdata()[5]);

		// Überprüfe die Kundenmengen nach Uhrzeit
		assertEquals(30, wekaFiles.get(1).getYdata()[0]);
		assertEquals(23, wekaFiles.get(1).getYdata()[1]);
		assertEquals(14, wekaFiles.get(1).getYdata()[2]);
		assertEquals(11, wekaFiles.get(1).getYdata()[3]);
		assertEquals(22, wekaFiles.get(1).getYdata()[4]);
		System.out.println("watch time kundenhäufigkeit: " + watch.getTime());
		watch.stop();
	}
}


