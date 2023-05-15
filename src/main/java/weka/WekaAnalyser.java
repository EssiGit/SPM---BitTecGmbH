package weka;

import java.io.BufferedReader;  

import helpers.UserHandler;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

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
			wekaFiles.addAll(USUT(filehandler));
			break;
		case "Kundenhäufigkeit":
			wekaFiles.addAll(KSETU(filehandler));
			break;
		case "uhrzeitProTag":
			wekaFiles.addAll(uhrzeitProTag(filehandler));
			break;

		default:
			// Aktion für andere Fälle
			wekaFiles.addAll(clusterAnalyseMulti(filehandler, analName, clusterAnzahl));
			break;
		}

		// Rückgabe des Ergebnisses (je nach Bedarf)
		return wekaFiles;
	}
	/**
	 * Cluster analyse
	 * @param fileHandler
	 * @param cluster der Name des Attributes, nach dem geclustered wird. Bsp "Einkaufssumme"
	 * @return
	 * @throws Exception
	 */

	public File clusterAnalyse(FileHandler fileHandler, String cluster) throws Exception {
		System.out.println(">>>>>--- Clusteranalyse ueber alle Daten, 5 Cluster ---\n");
		String result = "";
		try (BufferedReader reader = new BufferedReader(new FileReader(DIR))) {
			result = reader.readLine();
		}
		int index = 0;
		Attribute attribute = data.attribute(cluster);
		if (attribute != null) {
			index = attribute.index();
		} else {
			index = 0; // Attribut nicht gefunden
		}
		System.out.println("INDEX : " + index);



		//fileHandler.writeWekaResult(result, fileName);
		return new File(System.getProperty("user.home") + File.separator + "KaufDort_Userfiles" + File.separator + "users"  + File.separator + user.getName() +File.separator +"Result_Files" + File.separator +  "result_cluster_" + fileName);
	}

	/**
	 * cluster analyse multithreaded. saves up to 200 ms
	 * @param fileHandler
	 * @param cluster der Name des Attributes, nach dem geclustered wird. Bsp "Einkaufssumme"
	 * @return
	 * @throws Exception
	 */
	public ArrayList<Weka_resultFile> clusterAnalyseMulti(FileHandler fileHandler, String cluster,int clusterAnzahl) throws Exception {
		System.out.println(">>>>>--- Clusteranalyse ueber alle Daten, 5 Cluster ---\n");
		ArrayList<Weka_resultFile> wekaFiles = new ArrayList<>();

		String result = "";
		try (BufferedReader reader = new BufferedReader(new FileReader(DIR))) {
			result = reader.readLine();
		}
		int index = 0;
		Attribute attribute = data.attribute(cluster);
		if (attribute != null) {
			index = attribute.index();
		} else {
			index = 0; // Attribut nicht gefunden
		}
		System.out.println("INDEX: " + index);
		String clusterResult = analyse.findClusterMulti(data, index, clusterAnzahl);
		System.out.println("X-Werte:");
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
		// Ausgabe der X-Werte
		for (String xValue : xValues) {
			System.out.println(xValue);
		}

		// Ausgabe der Y-Werte
		for (String yValue : yValues) {
			System.out.println(yValue);
		}
		wekaFiles.add(new Weka_resultFile(cluster, xValues, yValues));
		return wekaFiles;
	}
	private String[] setXvalues(String[] xValues) {
		for(int i = 0;i<xValues.length;i++) {
			if(xValues[i].contains(",m,")) {
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

	private String[] checkCluster(String cluster,String[] yValues) {
		System.out.println(" in check cluster");
		if(cluster.equals("Einkaufsuhrzeit")) {
			for(int i = 0;i<yValues.length;i++) {
				String test =yValues[i];
				yValues[i] = changeTime(yValues[i]);	
			}
		}else if(cluster.equals("Wohnort")) {
			for(int i = 0;i<yValues.length;i++) {
				String test =yValues[i];
				System.out.println("yValues[i] before: " + yValues[i]) ;

				yValues[i] = changeWohnort(yValues[i]);	
				System.out.println("yValues[i]: " + yValues[i]) ;
			}
		}else if(cluster.equals("Haushaltsnettoeinkommen")) {
			System.out.println(" Haushaltsnettoeinkommen");
			for(int i = 0;i<yValues.length;i++) {
				String test =yValues[i];
				System.out.println("yValues[i] before: " + yValues[i]) ;
				yValues[i] = changeEinkommen(yValues[i]);	
				System.out.println("yValues[i]: " + yValues[i]) ;
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
			return ""; // Rückgabe eines leeren Strings für den Fall, dass der Wert von "time" nicht erkannt wird
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
	public ArrayList<Weka_resultFile> KSETU(FileHandler fileHandler) throws FileNotFoundException, IOException {
		ArrayList<Weka_resultFile> wekaFiles = new ArrayList<>();

		Map<String, Integer> tage = new HashMap<>();
		tage.put("Montag", 0);
		tage.put("Dienstag", 0);
		tage.put("Mittwoch", 0);
		tage.put("Donnerstag", 0);
		tage.put("Freitag", 0);
		tage.put("Samstag", 0);

		//{'>17 Uhr','12-14 Uhr','<10 Uhr','10-12 Uhr','14-17 Uhr'}
		Map<String, Integer> zeiten = new HashMap<>();
		zeiten.put("<10 Uhr", 0);
		zeiten.put("10-12 Uhr", 0);
		zeiten.put("12-14 Uhr", 0);
		zeiten.put("14-17 Uhr", 0);
		zeiten.put(">17 Uhr", 0);


		try {
			for (int i = 0; i < data.numInstances(); i++) {
				String wochentag = data.instance(i).stringValue(5);
				String zeit = data.instance(i).stringValue(6);
				tage.put(wochentag, (1 + tage.get(wochentag)));
				zeiten.put(zeit, (1 + zeiten.get(zeit)));
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		//String max = Collections.max(tage.entrySet(), Map.Entry.comparingByValue()).getKey();
		String[] xValues = {"Montag", "Dienstag", "Mittwoch", "Donnerstag", "Freitag", "Samstag"};
		String[] yValues = new String[6];

		for(int i = 0;i<xValues.length;i++) {
			yValues[i] = Integer.toString(tage.get(xValues[i]));
		}

		wekaFiles.add(new Weka_resultFile("Kunden nach Tagen", xValues, yValues));


		String[] xValues2 = {"<10 Uhr", "10-12 Uhr", "12-14 Uhr", "14-17 Uhr", ">17 Uhr"};
		String[] yValues2 = new String[5];

		for(int i = 0;i<xValues2.length;i++) {
			yValues2[i] = Integer.toString(zeiten.get(xValues2[i]));
		}
		wekaFiles.add(new Weka_resultFile("Kunden nach Uhrzeit", xValues2, yValues2));

		System.out.println("Montag: " + tage.get("Montag"));
		System.out.println("Dienstag: " + tage.get("Dienstag"));
		System.out.println("Mittwoch: " + tage.get("Mittwoch"));
		System.out.println("Donnerstag: " + tage.get("Donnerstag"));
		System.out.println("Freitag: " + tage.get("Freitag"));
		System.out.println("Samstag: " + tage.get("Samstag"));

		System.out.println("<10 Uhr: " + zeiten.get("<10 Uhr"));
		System.out.println("10-12 Uhr: " + zeiten.get("10-12 Uhr"));
		System.out.println("12-14 Uhr: " + zeiten.get("12-14 Uhr"));
		System.out.println("14-17 Uhr: " + zeiten.get("14-17 Uhr"));
		System.out.println(">17 Uhr: " + zeiten.get(">17 Uhr"));

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
		Map<String, Map<String, Integer>> tageZeiten = new HashMap<>();
		ArrayList<Weka_resultFile> wekaFiles = new ArrayList<>();

		for (String tag : tage) {
			tageZeiten.put(tag, new HashMap<>());
			tageZeiten.get(tag).put("<10 Uhr", 0);
			tageZeiten.get(tag).put("10-12 Uhr", 0);
			tageZeiten.get(tag).put("12-14 Uhr", 0);
			tageZeiten.get(tag).put("14-17 Uhr", 0);
			tageZeiten.get(tag).put(">17 Uhr", 0);
		}

		for (int i = 0; i < data.numInstances(); i++) {
			String wochentag = data.instance(i).stringValue(5);
			String zeit = data.instance(i).stringValue(6);
			Map<String, Integer> tagZeit = tageZeiten.get(wochentag);
			System.out.println("zeit: " + tageZeiten.get(wochentag) + " " + wochentag);
			tagZeit.put(zeit, (int) (data.instance(i).value(9) + tagZeit.get(zeit)));
			System.out.println("zeitTag: " + tagZeit.get(zeit) + " " + wochentag);
		}
		System.out.println(tageZeiten.get(tage[0]).get("<10 Uhr"));

		String cluster = "uhrzeitProTag";
		String[] xValues = new String[zeiten.length];
		String[] yValues = new String[zeiten.length];
		System.out.println("tage len : " +tage.length);
		System.out.println("zeiten len : " +  zeiten.length);
		for (int i = 0; i < tage.length; i++) {
			//int topTime = 0;
			// String weirdTime = "";
			for (int j = 0;j <zeiten.length;j++) {
				System.out.println("i " + i + "j "+ j);
				xValues[j] = zeiten[j];
				System.out.println("zeit: " + zeiten[j] + " " + tage[i]);
				int tmp = tageZeiten.get(tage[i]).get(zeiten[j]);
				System.out.println("tmp: " + tmp);
				yValues[j] = Integer.toString(tmp);
				System.out.println("yValues " +  yValues[j]);
				/*if (topTime < tageZeiten.get(tage[i]).get(zeit)) {
	                topTime = tageZeiten.get(tage[i]).get(zeit);
	                weirdTime = zeit;
	            }*/
			}
			for(String tmp : yValues) {
				System.out.println("yValues: " + tmp);
			}
			wekaFiles.add(new Weka_resultFile(tage[i], xValues, yValues));
		}

		return wekaFiles;
	}


	/**
	 * Umsatzstärkste Einkauftage und Uhrzeiten
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public ArrayList<Weka_resultFile> USUT(FileHandler filehandler) throws FileNotFoundException, IOException {
		String[] tage = {"Montag", "Dienstag", "Mittwoch", "Donnerstag", "Freitag", "Samstag"};
		String[] zeiten = {"<10 Uhr", "10-12 Uhr", "12-14 Uhr", "14-17 Uhr", ">17 Uhr"};
		ArrayList<Weka_resultFile> wekaFiles = new ArrayList<>();
		Map<String, Map<String, Integer>> tageZeiten = new HashMap<>();
		String[] xValues = new String[zeiten.length]; // Array für X-Werte (Uhrzeiten)
		String[] yValuesTime = new String[zeiten.length]; // Array für Y-Werte (Umsatz nach Uhrzeit)
		String[] yValuesDay = new String[tage.length]; // Array für Y-Werte (Umsatz nach Tag)

		for (String tag : tage) {
			tageZeiten.put(tag, new HashMap<>());
			tageZeiten.get(tag).put("<10 Uhr", 0);
			tageZeiten.get(tag).put("10-12 Uhr", 0);
			tageZeiten.get(tag).put("12-14 Uhr", 0);
			tageZeiten.get(tag).put("14-17 Uhr", 0);
			tageZeiten.get(tag).put(">17 Uhr", 0);
		}

		for (int i = 0; i < data.numInstances(); i++) {
			String wochentag = data.instance(i).stringValue(5);
			String zeit = data.instance(i).stringValue(6);
			Map<String, Integer> tagZeit = tageZeiten.get(wochentag);
			tagZeit.put(zeit, (int) (data.instance(i).value(9) + tagZeit.get(zeit)));
		}

		// Generiere Tabelle mit Uhrzeiten und Umsatz
		for (int i = 0; i < zeiten.length; i++) {
			xValues[i] = zeiten[i];
			int sum = 0;
			for (String tag : tage) {
				int currTime = tageZeiten.get(tag).get(zeiten[i]);
				sum += currTime;
				yValuesTime[i] = Integer.toString(sum);
			}
		}

		// Generiere Weka_resultFile für Tage und Umsatz
		for (int i = 0; i < tage.length; i++) {
			int sum = 0;
			for (String zeit : zeiten) {
				int currTime = tageZeiten.get(tage[i]).get(zeit);
				sum += currTime;
			}
			yValuesDay[i] = Integer.toString(sum);
		}

		// Erzeuge Weka_resultFile-Objekte und füge sie zur Liste hinzu
		wekaFiles.add(new Weka_resultFile("Umsatz nach Uhrzeit", xValues, yValuesTime));
		wekaFiles.add(new Weka_resultFile("Umsatz nach Tag", tage, yValuesDay));

		return wekaFiles;
	}


	public File USUT2(FileHandler filehandler) {
		String[] tage = {"Montag", "Dienstag", "Mittwoch", "Donnerstag", "Freitag", "Samstag"};
		String[] zeiten = {"<10 Uhr", "10-12 Uhr", "12-14 Uhr", "14-17 Uhr", ">17 Uhr"};

		ConcurrentMap<String, ConcurrentMap<String, AtomicInteger>> tageZeiten = new ConcurrentHashMap<>();

		for (String tag : tage) {
			ConcurrentMap<String, AtomicInteger> tagZeit = new ConcurrentHashMap<>();
			for (String zeit : zeiten) {
				tagZeit.put(zeit, new AtomicInteger(0));
			}
			tageZeiten.put(tag, tagZeit);
		}

		int batchSize = 1000;
		int numInstances = data.numInstances();

		for (int batchStart = 0; batchStart < numInstances; batchStart += batchSize) {
			int batchEnd = Math.min(batchStart + batchSize, numInstances);

			IntStream.range(batchStart, batchEnd).parallel().forEach(i -> {
				String wochentag = data.instance(i).stringValue(5);
				String zeit = data.instance(i).stringValue(6);
				AtomicInteger count = tageZeiten.get(wochentag).get(zeit);
				count.addAndGet((int) data.instance(i).value(9));
			});
		}

		StringBuilder result = new StringBuilder();
		for (String day : tage) {
			String topTime = zeiten[0];
			AtomicInteger maxCount = tageZeiten.get(day).get(topTime);

			for (String zeit : zeiten) {
				AtomicInteger count = tageZeiten.get(day).get(zeit);
				if (count.get() > maxCount.get()) {
					topTime = zeit;
					maxCount = count;
				}
			}

			result.append(day).append(" ").append(topTime).append("\n");
		}

		System.out.println("result USUT2: \n" + result);

		//filehandler.writeWekaResult(result.toString(), fileName);
		return new File(System.getProperty("user.home") + File.separator + "KaufDort_Userfiles" + File.separator + "users" + File.separator + user.getName() + File.separator + "Result_Files" + File.separator + "result_cluster_" + fileName);
	}
}
