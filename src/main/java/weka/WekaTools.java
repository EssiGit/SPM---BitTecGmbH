package weka;

import java.io.File;  
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.List;
import weka.associations.Apriori;
import weka.associations.AssociationRule;
import weka.classifiers.rules.ZeroR;
import weka.clusterers.SimpleKMeans;
import weka.core.Instance;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;
import weka.filters.unsupervised.attribute.NumericCleaner;
import weka.filters.unsupervised.attribute.NumericToNominal;

public class WekaTools {


	/**
	 * Cluster analyse
	 * @param daten
	 * @param clusterIndex
	 * @param number
	 * @return
	 * @throws Exception
	 */
	String findClusterMulti(final Instances daten, final int clusterIndex, final int number) throws Exception {
		ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
		try {
			Callable<String> task = new Callable<String>() {
				public String call() throws Exception {
					SimpleKMeans model = new SimpleKMeans();
					model.setNumClusters(number);
					Remove attributeFilter = new Remove();
					attributeFilter.setAttributeIndicesArray(new int[]{0,1, 2, 3, 4, clusterIndex}); //columns die wir behalten
					attributeFilter.setInvertSelection(true);
					attributeFilter.setInputFormat(daten);
					Instances filteredData = Filter.useFilter(daten, attributeFilter);
					model.buildClusterer(filteredData);

					String[] result = model.getClusterCentroids().toString().split("@data\n");//cluster centroids holen
					return result[1];
				}
			};

			Future<String> future = executor.submit(task);
			String result = future.get();
			return result + "\n";
		} finally {
			executor.shutdown();
		}
	}
	/**
	 * Sets up the Name for the Frontend
	 * @param xValues
	 * @return
	 */
	public String[] setXvalues(String[] xValues) {
		for(int i = 0;i<xValues.length;i++) {
			if(xValues[i].contains("m,")) {
				xValues[i] = xValues[i].replaceFirst("m,", " Maennlich, Alter: ");
			}else {
				xValues[i] = xValues[i].replaceFirst("w,", " Weiblich, Alter: ");
			}
			if(xValues[i].contains(",nein")) {
				xValues[i] = xValues[i].replaceFirst(",nein", ", keine Kinder \n");
			}else {
				xValues[i] = xValues[i].replaceFirst(",ja", ", Kinder \n");
			}
			if(xValues[i].contains(",ledig")) {
				xValues[i] = xValues[i].replaceFirst(",ledig", ", ledig");
			}else {
				xValues[i] = xValues[i].replaceFirst(",Partnerschaft", ", Partnerschaft");
			}
			if(xValues[i].contains(",ja")) {
				xValues[i] = xValues[i].replaceFirst(",ja", ", Berufstaetig");
			}else {
				xValues[i] = xValues[i].replaceFirst(",nein", ", Arbeitslos");
			}
		}
		return xValues;
	}

	/**
	 * 
	 * checks which kind of cluster analysis
	 * @param cluster
	 * @param yValues
	 * @return
	 */
	public String[] checkCluster(String cluster, String[] yValues) {
		if (cluster.equals("Einkaufsuhrzeit")) {
			for (int i = 0; i < yValues.length; i++) {
				yValues[i] = changeTime(yValues[i]);
			}
		} else if (cluster.equals("Wohnort")) {
			for (int i = 0; i < yValues.length; i++) {
				yValues[i] = changeWohnort(yValues[i]);
			}
		} else if (cluster.equals("Haushaltsnettoeinkommen")) { 
			for (int i = 0; i < yValues.length; i++) {
				yValues[i] = changeEinkommen(yValues[i]);
			}
		}
		return yValues;
	}
	public String getClusterName(String cluster) {
		switch (cluster) {
		case "Einkaufsuhrzeit":
			return "Einkaufsuhrzeit"; 
		case "Wohnort":
			return "Entfernung zum Kunden in km";
		case "Haushaltsnettoeinkommen":
			return "Haushaltsnettoeinkommen in €";
		default:
			return "Einkaufssumme in €"; // in €
		}

	}

	public String changeTime(String time) {
		switch (time) {
		case "'<10 Uhr'":
			return "9";
		case "'10-12 Uhr'":
			return "11";
		case "'12-14 Uhr'":
			return "13";
		case "'14-17 Uhr'":
			return "15";
		case "'>17 Uhr'":
			return "18";
		default:
			System.out.println("error Zeit");
			return ""; 
		}
	}
	public String changeWohnort(String ort) {
		System.out.println(ort);
		switch (ort) {
		case "'< 10 km'":
			return "9";
		case "'10 - 25 km'":
			return "18";
		case "'> 25 km'":
			return "30";
		default:
			System.out.println("error Wohnort");
			return ""; // Rückgabe eines leeren Strings für den Fall, dass der Wert von "time" nicht erkannt wird
		}
	}
	public String changeEinkommen(String einkommen) {
		switch (einkommen) {
		case "3200-<4500":
			return "4000";
		case "<1000":
			return "900";
		case "1000-<2000":
			return "1500";	
		case "2000-<3200":
			return "2750";
		case ">4500":
			return "5800";
		default:
			return ""; // Rückgabe eines leeren Strings für den Fall, dass der Wert von "time" nicht erkannt wird
		}
	}

	// Hilfsmethode, um fuer die Auswertung unnoetige Angaben rauszuloeschen
	private String clearAprioriList(String oneRule) {
		String temp = "";

		// Weka-blabla raus loeschen
		for (int i = 0; i < oneRule.length(); i++) {
			Character a = oneRule.charAt(i);
			if ((Character.isLetter(a)) || (a == ',')) {
				temp = temp + a;
			}
		}

		return temp;
	}


	String[] makeApriori(Instances daten) throws Exception {

		// umwandeln in gekauft / nicht gekauft (0/1)
		NumericCleaner nc = new NumericCleaner();
		nc.setMaxThreshold(1.0); // Schwellwert auf 1 setzen
		nc.setMaxDefault(1.0); // alles ueber Schwellwert durch 1 ersetzen
		nc.setInputFormat(daten);
		daten = Filter.useFilter(daten, nc); // Filter anwenden.

		// Die Daten als nominale und nicht als numerische Daten setzen
		NumericToNominal num2nom = new NumericToNominal();
		num2nom.setAttributeIndices("first-last");
		num2nom.setInputFormat(daten);
		daten = Filter.useFilter(daten, num2nom);

		Apriori model = new Apriori();
		model.buildAssociations(daten);

		List<AssociationRule> rulesA = model.getAssociationRules().getRules();
		int anzRules = rulesA.size();

		String[] tmp = new String[anzRules];

		// Ergebnis huebsch zusammensetzen
		for (int i = 0; i < anzRules; i++) {
			tmp[i] = clearAprioriList(rulesA.get(i).getPremise().toString()) + " ==> "
					+ clearAprioriList(rulesA.get(i).getConsequence().toString());
		}
		return tmp;
	}

	/**
	 * liefert den haeufigsten Wert eines Attributs zurueck benutzt ZeroR, eine
	 * Wekafunktion fuer das haeufigste Element der nominalen Attribute, bei
	 * numerischen Werten wird der Mittelwert geliefert!
	 *
	 * @param daten Hier wichtig: Daten im <b>arffFormat!</b>
	 *
	 * @param index - Fuer welches Attribut soll das Maximum bestimmt werden (0..9
	 *              hier sinnvoll, da nur diese Daten nominal sind)
	 * @return haeufigstes Element als String
	 * @throws Exception Fehlerbehandlung muss noch erledigt werden
	 */
	String findMaximum(Instances daten, int index) throws Exception {
		String[] max;

		ZeroR za = new ZeroR(); // wekafunktion

		daten.setClass(daten.attribute(index)); // Attribut dessen Maximum
		// ermittelt werden soll
		za.buildClassifier(daten);

		max = za.toString().split(": "); // weka -blabla wegnehmen

		return (max[1]);
	}

	/**
	 * Verteilung der einzelnen Attribute Kundendaten und Einkaufsverhalten, als
	 * <b>absolute</b> Werte
	 *
	 * @param daten - alleDaten
	 * @param index - welches Attribut soll ausgewertet werden?
	 * @return Verteilung des Attributs
	 */
	String attDistributionAbsolute(Instances daten, int index) {
		int attCount;
		int attNum = index;
		String result;

		result = daten.attribute(attNum).name() + ": ";

		// Anzahl der moeglichen Werte
		attCount = daten.attributeStats(attNum).distinctCount;

		for (int i = 0; i < attCount; i++) {
			result += "\"" + daten.attribute(attNum).value(i) + "\"" + " = "
					+ daten.attributeStats(attNum).nominalCounts[i] + "  ";
		}

		return result;
	}


}
