package weka;
import weka.WekaAnalyser;

public class Weka_test {
	
	 public static void main(String[] args) throws Exception {
		 
	/*
	 * Test Beispiel für Weka ausführung. Daten werden über Website hochgeladen und unter usr_data gespeichert. 
	 * Dann an ein WekaAnalyer Objekt übergeben und analysiert.
	 * Diese Klasse kann ohne Server zum testen ausgeführt werden
	 */
	
	WekaAnalyser test = new WekaAnalyser("kd100.csv");
	test.clusterAnalyse();

	 }
}
