package weka;
import java.io.File;
import java.util.Map;

import helpers.User;
import weka.WekaAnalyser;
import helpers.FileHandler;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.time.StopWatch;

public class Weka_test {

	public static void main(String[] args) throws Exception {

		/*
		 * Test Beispiel für Weka ausführung. Daten werden über Website hochgeladen und unter auf dem Computer gespeichert. 
		 * Dann an ein WekaAnalyer Objekt übergeben und analysiert.
		 * Diese Klasse kann ohne Server zum testen ausgeführt werden
		 */
		String fileName = "kd10000 - Kopie.csv";
		
		//WekaAnalyser test = new WekaAnalyser("kd1000.csv");


		//Weka_resultFile temp = new Weka_resultFile(test.clusterAnalyse(), 1);

		// Erstelle ein WekaAnalyser-Objekt mit dem Dateinamen und dem Benutzer
		User user = new User("da8050");
		FileHandler fileHandler = new FileHandler(user);
		WekaAnalyser analyser = new WekaAnalyser(fileName, user);
		
		StopWatch watch = new StopWatch();
        watch.start();
        analyser.KSETU(fileHandler);
        watch.stop();
        System.out.println("Kundenstärkste Einkauftage u. Uhzreiten: " + watch.getTime() + "ms");
        watch.reset();
        watch.start();
        analyser.USUT();
        watch.stop();
        System.out.println("Umsatzstärkste Einkauftage u. Uhzreiten: " + watch.getTime() + "ms");
        System.out.println("cluster anal");
        watch.reset();
        watch.start();
        analyser.clusterAnalyse(fileHandler, "Einkaufssumme");
        watch.stop();
		
	    System.out.println("cluster anal end time : "  + watch.getTime() + "ms");
        watch.reset();
        watch.start();
		analyser.uhrzeitProTag(fileHandler);
		watch.stop();
		System.out.println("Umsatzstärkste Uhzreiten pro Tag: " + watch.getTime() + "ms");
		System.out.println("multi");


	}
}

//TODO
/*Pfad als thymeleaf variable
 *Button File namen als thymeleaf
 *weka über button click
 *über thymeleaf variable für analysedaten schicken x y und name als objekt mit arrayListe
 */