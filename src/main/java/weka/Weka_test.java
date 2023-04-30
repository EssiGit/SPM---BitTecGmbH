package weka;
import weka.WekaAnalyser;

public class Weka_test {
	
	 public static void main(String[] args) throws Exception {
		 
	/*
	 * Test Beispiel für Weka ausführung. Daten werden über Website hochgeladen und unter auf dem Computer gespeichert. 
	 * Dann an ein WekaAnalyer Objekt übergeben und analysiert.
	 * Diese Klasse kann ohne Server zum testen ausgeführt werden
	 */
	
	WekaAnalyser test = new WekaAnalyser("kd100.csv");
	test.clusterAnalyse();

	 }
}
//TODO
/*Pfad als thymeleaf variable
 *Button File namen als thymeleaf
 *weka über button click
 *über thymeleaf variable für analysedaten schicken x y und name als objekt mit arrayListe
 */