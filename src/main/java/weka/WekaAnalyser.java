package weka;

import java.io.BufferedReader;  

import helpers.UserHandler;
import java.io.File;
import java.io.FileReader;
import weka.classifiers.rules.ZeroR;
import weka.core.Attribute;
import weka.core.Instances;
import weka.core.SystemInfo;
import weka.core.converters.ArffLoader;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVLoader;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.NumericCleaner;

import java.util.Arrays;
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
	public Weka_resultFile clusterAnalyseMulti(FileHandler fileHandler, String cluster) throws Exception {
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
	    System.out.println("INDEX: " + index);
	    String clusterResult = analyse.findClusterMulti(data, index, 5);
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

	    // Ausgabe der X-Werte
	    for (String xValue : xValues) {
	        System.out.println(xValue);
	    }

	    // Ausgabe der Y-Werte
	    for (String yValue : yValues) {
	        System.out.println(yValue);
	    }
	    return new Weka_resultFile(cluster, xValues, yValues);
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
	 */
	public void KSETU(FileHandler fileHandler) {
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
	}



	/**
	 * Top Einkaufsuhrzeit pro Tag
	 * @param filehandler
	 * @return File with 
	 */
	public File uhrzeitProTag(FileHandler filehandler) {
		String[]  tage = {"Montag", "Dienstag", "Mittwoch", "Donnerstag" , "Freitag", "Samstag"};
		String[]  zeiten = {"<10 Uhr", "10-12 Uhr", "12-14 Uhr", "14-17 Uhr" , ">17 Uhr"};
		Map<String, Map<String, Integer>> tageZeiten = new HashMap<>();


		for(String tag : tage) {
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
		String result = "";
		Map<String,String> topDays = new HashMap<String, String>();
		for(String tag : tage) {
			int topTime = 0;
			String weirdTime = "";
			System.out.println(tag);
			for(String zeit : zeiten) {
				System.out.println(zeit);
				System.out.println("TOP TIME: " + topTime);
				System.out.println("weirdTime: " + weirdTime);
				if( topTime<tageZeiten.get(tag).get(zeit)) {
					topTime = tageZeiten.get(tag).get(zeit);
					weirdTime = zeit;
				}

			}
			result = result.concat(tag + " " + weirdTime + " \n");
		}
		System.out.println("MY TIME:");
		System.out.println("result: \n" + result);
		filehandler.writeWekaResult(result, fileName);
		return new File(System.getProperty("user.home") + File.separator + "KaufDort_Userfiles" + File.separator + "users"  + File.separator + user.getName() +File.separator +"Result_Files" + File.separator +  "result_cluster_" + fileName);

	}


	/**
	 * Umsatzstärkste Einkauftage und Uhrzeiten
	 */
	public File USUT(FileHandler filehandler) {
		String[] tage = {"Montag", "Dienstag", "Mittwoch", "Donnerstag", "Freitag", "Samstag"};
		String[] zeiten = {"<10 Uhr", "10-12 Uhr", "12-14 Uhr", "14-17 Uhr", ">17 Uhr"};

		Map<String, Map<String, Integer>> tageZeiten = new HashMap<>();

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

		StringBuilder result = new StringBuilder();
		for (String day : tage) {
			int time = 0;
			String topTime = "";
			for (String zeit : zeiten) {
				int currTime = tageZeiten.get(day).get(zeit);
				if (currTime > time) {
					topTime = zeit;
					time = currTime;
				}
			}
			result.append(day).append(" ").append(topTime).append(" \n");
		}
		System.out.println("result USUT: \n" + result);
		filehandler.writeWekaResult(result.toString(), fileName);
		return new File(System.getProperty("user.home") + File.separator + "KaufDort_Userfiles" + File.separator + "users" + File.separator + user.getName() + File.separator + "Result_Files" + File.separator + "result_cluster_" + fileName);
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

		filehandler.writeWekaResult(result.toString(), fileName);
		return new File(System.getProperty("user.home") + File.separator + "KaufDort_Userfiles" + File.separator + "users" + File.separator + user.getName() + File.separator + "Result_Files" + File.separator + "result_cluster_" + fileName);
	}
}
