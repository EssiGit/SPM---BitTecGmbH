package weka;

import java.io.BufferedReader;  
import org.apache.commons.lang3.time.StopWatch;
import helpers.UserHandler;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.IntStream;
import weka.classifiers.rules.ZeroR;
import weka.core.Attribute;
import weka.core.Instances;
import weka.core.SystemInfo;
import weka.core.converters.ArffLoader;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVLoader;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.NumericCleaner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import helpers.User;
import helpers.FileHandler;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;
import java.util.concurrent.ConcurrentMap;

public class WekaAnalyser {
	//private static final String DIR = "src" + File.separator +"main" + File.separator+"webapp" + File.separator + "usr_data" + File.separator;
	private WekaTools analyse = new WekaTools();
	Instances data;
	Instances arffDaten;
	private String fileName;
	private File DIR;
	private User user;
	public WekaAnalyser(String filePassed, User user) throws Exception {
		this.user= user;
		fileName = filePassed;
		DIR = new File(System.getProperty("user.home") + File.separator + "KaufDort_Userfiles" + File.separator + "users" + File.separator + user.getName() + File.separator +  fileName);

		String arffDat = DIR + ".arff";

		CSVLoader loader = new CSVLoader();
		loader.setSource(DIR);
		data = loader.getDataSet();

		// 0 durch ? ersetzen, um fuer die Auswertung nur die Waren zu
		// beruecksichtigen, die gekauft wurden
		NumericCleaner nc = new NumericCleaner();
		nc.setMinThreshold(1.0); // Schwellwert auf 1 setzen
		nc.setMinDefault(Double.NaN); // alles unter 1 durch ? ersetzen
		nc.setInputFormat(data);
		data = Filter.useFilter(data, nc); // Filter anwenden
		ArffSaver saver = new ArffSaver();
		saver.setInstances(data);
		saver.setFile(new File(arffDat));
		saver.writeBatch();

		// Arff-Datei laden
		ArffLoader aLoader = new ArffLoader();
		aLoader.setSource(new File(arffDat));
		arffDaten = aLoader.getDataSet();




	}


	public ArrayList<Weka_resultFile> getCorrectAnalysis(FileHandler filehandler, String analName, int clusterAnzahl) throws Exception {
		ArrayList<Weka_resultFile> wekaFiles = new ArrayList<>();
		switch (analName) {
		case "Umsatzstärkstertag/Uhrzeit":
			wekaFiles = umsatzstaerksteTagUhrzeit(filehandler);
			break;
		case "Kundenhäufigkeit":
			wekaFiles = kundenhaeufigkeit(filehandler);
			break;
		case "uhrzeitProTag":
			wekaFiles = uhrzeitProTag(filehandler);
			break;

		default:
			// Aktion für andere Fälle
			wekaFiles = clusterAnalyseMulti(filehandler, analName, clusterAnzahl);
			break;
		}

		// Rückgabe des Ergebnisses (je nach Bedarf)
		return wekaFiles;
	}



	/**
	 * cluster analyse multithreaded. saves up to 200 ms
	 * @param fileHandler
	 * @param cluster der Name des Attributes, nach dem geclustered wird. Bsp "Einkaufssumme"
	 * @return
	 * @throws Exception
	 */
	public ArrayList<Weka_resultFile> clusterAnalyseMulti(FileHandler fileHandler, String cluster,int clusterAnzahl) throws Exception {
		StopWatch watch = new StopWatch();
		watch.reset();
		watch.start();
		ArrayList<Weka_resultFile> wekaFiles = new ArrayList<>();


		int index = 0;
		Attribute attribute = data.attribute(cluster);
		if (attribute != null) {
			index = attribute.index();
		} else {
			index = 0; // Attribut nicht gefunden
		}
		System.out.println("INDEX: " + index);
		String clusterResult = analyse.findClusterMulti(data, index, clusterAnzahl);
		String[] values = clusterResult.split("\n");
		String[] xValues = new String[values.length];
		String[] yValues = new String[values.length];
		Pattern pattern = Pattern.compile(",([^,]+)$");
		for (int i = 0; i < values.length; i++) {
			Matcher matcher = pattern.matcher(values[i]);
			if (matcher.find()) {
				yValues[i] = matcher.group(1);
				xValues[i] = values[i].substring(0, matcher.start());
			}
		}
		yValues = checkCluster(cluster,yValues);
		xValues = setXvalues(xValues);
		wekaFiles.add(new Weka_resultFile(cluster, xValues, yValues));
	    watch.stop();
	    System.out.println("time for cluster: " + watch.getTime() + "ms");
		return wekaFiles;
	}
	
	private String[] setXvalues(String[] xValues) {
		for(int i = 0;i<xValues.length;i++) {
			if(xValues[i].contains("m,")) {
				xValues[i] = xValues[i].replaceFirst("m,", " Männlich, Alter: ");
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

	private String[] checkCluster(String cluster, String[] yValues) {
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

	private String changeTime(String time) {
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
			return ""; 
		}
	}
	private String changeWohnort(String ort) {
		switch (ort) {
		case "'< 10 km'":
			return "9";
		case "'10 - 25 km'":
			return "18";
		case "'> 25 km'":
			return "30";
		default:
			return ""; // Rückgabe eines leeren Strings für den Fall, dass der Wert von "time" nicht erkannt wird
		}
	}
	private String changeEinkommen(String einkommen) {
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
	 * Kundenstärkste Einkauftage und Uhrzeiten
	 * 
	 * @param fileHandler
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public ArrayList<Weka_resultFile> kundenhaeufigkeit(FileHandler fileHandler) throws FileNotFoundException, IOException {
		StopWatch watch = new StopWatch();
		watch.reset();
		watch.start();
	    ArrayList<Weka_resultFile> wekaFiles = new ArrayList<>();

	    Map<String, Integer> tage = new HashMap<>();
	    tage.put("Montag", 0);
	    tage.put("Dienstag", 0);
	    tage.put("Mittwoch", 0);
	    tage.put("Donnerstag", 0);
	    tage.put("Freitag", 0);
	    tage.put("Samstag", 0);

	    Map<String, Integer> zeiten = new HashMap<>();
	    zeiten.put("<10 Uhr", 0);
	    zeiten.put("10-12 Uhr", 0);
	    zeiten.put("12-14 Uhr", 0);
	    zeiten.put("14-17 Uhr", 0);
	    zeiten.put(">17 Uhr", 0);

	    int numInstances = data.numInstances();
	    System.out.println("INstances: " + numInstances);
	    for (int i = 0; i < numInstances; i++) {
	        String wochentag = data.instance(i).stringValue(5);
	        String zeit = data.instance(i).stringValue(6);
	        tage.put(wochentag, tage.get(wochentag) + 1);
	        zeiten.put(zeit, zeiten.get(zeit) + 1);
	    }

	    String[] xValues = {"Montag", "Dienstag", "Mittwoch", "Donnerstag", "Freitag", "Samstag"};
	    String[] yValues = new String[6];
	    for (int i = 0; i < xValues.length; i++) {
	        yValues[i] = Integer.toString(tage.get(xValues[i]));
	    }
	    wekaFiles.add(new Weka_resultFile("Kunden nach Tagen", xValues, yValues));

	    String[] xValues2 = {"<10 Uhr", "10-12 Uhr", "12-14 Uhr", "14-17 Uhr", ">17 Uhr"};
	    String[] yValues2 = new String[5];
	    for (int i = 0; i < xValues2.length; i++) {
	        yValues2[i] = Integer.toString(zeiten.get(xValues2[i]));
	    }

	    wekaFiles.add(new Weka_resultFile("Kunden nach Uhrzeit", xValues2, yValues2));

	    return wekaFiles;
	}



	/**
	 * Top Einkaufsuhrzeit pro Tag
	 * @param filehandler
	 * @return File with 
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */

	public ArrayList<Weka_resultFile> uhrzeitProTag(FileHandler filehandler) throws FileNotFoundException, IOException {
	    String[] tage = {"Montag", "Dienstag", "Mittwoch", "Donnerstag", "Freitag", "Samstag"};
	    String[] zeiten = {"<10 Uhr", "10-12 Uhr", "12-14 Uhr", "14-17 Uhr", ">17 Uhr"};
	    Map<String, Map<String, Integer>> tageZeiten = new ConcurrentHashMap<>();
	    ArrayList<Weka_resultFile> wekaFiles = new ArrayList<>();

	    for (String tag : tage) {
	        tageZeiten.put(tag, new HashMap<>());
	        Map<String, Integer> tagZeit = tageZeiten.get(tag);
	        for (String zeit : zeiten) {
	            tagZeit.put(zeit, 0);
	        }
	    }

	    // Extract necessary values from data
	    String[] wochentage = IntStream.range(0, data.numInstances())
	            .parallel()
	            .mapToObj(i -> data.instance(i).stringValue(5))
	            .toArray(String[]::new);

	    String[] zeitenData = IntStream.range(0, data.numInstances())
	            .parallel()
	            .mapToObj(i -> data.instance(i).stringValue(6))
	            .toArray(String[]::new);

	    double[] values = IntStream.range(0, data.numInstances())
	            .parallel()
	            .mapToDouble(i -> data.instance(i).value(9))
	            .toArray();

	    // Update tageZeiten map with computed values
	    IntStream.range(0, data.numInstances())
	            .parallel()
	            .forEach(i -> {
	                String wochentag = wochentage[i];
	                String zeit = zeitenData[i];
	                synchronized (tageZeiten) { // Synchronize access to shared data
	                    Map<String, Integer> tagZeit = tageZeiten.get(wochentag);
	                    tagZeit.compute(zeit, (k, v) -> (int) (values[i] + (v != null ? v : 0)));
	                }
	            });

	    for (String tag : tage) {
	        Map<String, Integer> tagZeit = tageZeiten.get(tag);
	        String[] xValues = zeiten.clone();
	        String[] yValues = new String[zeiten.length];
	        for (int i = 0; i < zeiten.length; i++) {
	            yValues[i] = Integer.toString(tagZeit.get(zeiten[i]));
	        }
	        wekaFiles.add(new Weka_resultFile(tag, xValues, yValues));
	    }

	    return wekaFiles;
	}


	/**
	 * Umsatzstärkste Einkauftage und Uhrzeiten
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public ArrayList<Weka_resultFile> umsatzstaerksteTagUhrzeit(FileHandler filehandler) throws FileNotFoundException, IOException {
	    String[] tage = {"Montag", "Dienstag", "Mittwoch", "Donnerstag", "Freitag", "Samstag"};
	    String[] zeiten = {"<10 Uhr", "10-12 Uhr", "12-14 Uhr", "14-17 Uhr", ">17 Uhr"};
	    ArrayList<Weka_resultFile> wekaFiles = new ArrayList<>();
	    Map<String, Map<String, AtomicInteger>> tageZeiten = new HashMap<>();

	    for (String tag : tage) {
	        tageZeiten.put(tag, new HashMap<>());
	        Map<String, AtomicInteger> tagZeit = tageZeiten.get(tag);
	        for (String zeit : zeiten) {
	            tagZeit.put(zeit, new AtomicInteger(0));
	        }
	    }

	    IntStream.range(0, data.numInstances())
	            .parallel()
	            .forEach(i -> {
	                String wochentag = data.instance(i).stringValue(5);
	                String zeit = data.instance(i).stringValue(6);
	                Map<String, AtomicInteger> tagZeit = tageZeiten.get(wochentag);
	                tagZeit.get(zeit).addAndGet((int) data.instance(i).value(9));
	            });

	    String[] xValues = new String[zeiten.length];
	    String[] yValuesTime = new String[zeiten.length];
	    String[] yValuesDay = new String[tage.length];

	    // Generiere Tabelle mit Uhrzeiten und Umsatz
	    for (int i = 0; i < zeiten.length; i++) {
	        xValues[i] = zeiten[i];
	        int sum = 0;
	        for (String tag : tage) {
	            int currTime = tageZeiten.get(tag).get(zeiten[i]).get();
	            sum += currTime;
	        }
	        yValuesTime[i] = Integer.toString(sum);
	    }

	    // Generiere Weka_resultFile für Tage und Umsatz
	    for (int i = 0; i < tage.length; i++) {
	        int sum = 0;
	        for (String zeit : zeiten) {
	            int currTime = tageZeiten.get(tage[i]).get(zeit).get();
	            sum += currTime;
	        }
	        yValuesDay[i] = Integer.toString(sum);
	    }

	    // Erzeuge Weka_resultFile-Objekte und füge sie zur Liste hinzu
	    wekaFiles.add(new Weka_resultFile("Umsatz nach Uhrzeit", xValues, yValuesTime));
	    wekaFiles.add(new Weka_resultFile("Umsatz nach Tag", tage, yValuesDay));

	    return wekaFiles;
	}

}
