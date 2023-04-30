package weka;
import java.io.File;

import weka.WekaAnalyser;
import helpers.FileHandler;

public class Weka_test {
	
	 public static void main(String[] args) throws Exception {
		 
	/*
	 * Test Beispiel für Weka ausführung. Daten werden über Website hochgeladen und unter auf dem Computer gespeichert. 
	 * Dann an ein WekaAnalyer Objekt übergeben und analysiert.
	 * Diese Klasse kann ohne Server zum testen ausgeführt werden
	 */
	String fileName = "kd100.csv";
	FileHandler fileHandler = new FileHandler();
	WekaAnalyser test = new WekaAnalyser("kd1000.csv");
	test.clusterAnalyse();
	File testFile = new File(System.getProperty("user.home") + File.separator + "KaufDort_Userfiles" + File.separator + "Result_Files" + File.separator +  "result_cluster_" + fileName);
	Weka_resultFile temp = new Weka_resultFile(testFile);
	System.out.println(fileHandler.getFileNames()[1]);
	 }
}
//TODO
/*Pfad als thymeleaf variable
 *Button File namen als thymeleaf
 *weka über button click
 *über thymeleaf variable für analysedaten schicken x y und name als objekt mit arrayListe
 */