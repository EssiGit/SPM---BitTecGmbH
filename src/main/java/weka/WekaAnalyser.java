package weka;



import java.io.File; 
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.IntStream;

import file_handling.FileHandler;
import weka.core.Attribute;
import weka.core.Instances;
import weka.core.converters.CSVLoader;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.NumericCleaner;
import java.util.ArrayList;
import java.util.Arrays;
import user_handling.User;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WekaAnalyser {
	private WekaTools analyse = new WekaTools();
	Instances data;
	Instances arffDaten;
	private String fileName;
	private File DIR;
	private User user;
	public WekaAnalyser(String filePassed, User user)  {
		this.user= user;
		fileName = filePassed;
		DIR = new File(System.getProperty("user.home") + File.separator + "KaufDort_Userfiles" + File.separator + "users" + File.separator + user.getName() + File.separator  + "Files" + File.separator +  fileName);



		CSVLoader loader = new CSVLoader();
		try {
			loader.setSource(DIR);
			data = loader.getDataSet();
		} catch (IOException e) {
			e.printStackTrace();
			return;
			
		}
		

		// 0 durch ? ersetzen, um fuer die Auswertung nur die Waren zu
		// beruecksichtigen, die gekauft wurden
		NumericCleaner nc = new NumericCleaner();
		nc.setMinThreshold(1.0); // Schwellwert auf 1 setzen
		nc.setMinDefault(Double.NaN); // alles unter 1 durch ? ersetzen
		try {
			nc.setInputFormat(data);
			data = Filter.useFilter(data, nc);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		 // Filter anwenden
		
		//arff Datei brauchen wir nicht für unsere analysen

		//String arffDat = DIR + ".arff";
		/*ArffSaver saver = new ArffSaver();
		saver.setInstances(data);
		saver.setFile(new File(arffDat));
		saver.writeBatch();

		// Arff-Datei laden
		ArffLoader aLoader = new ArffLoader();
		aLoader.setSource(new File(arffDat));
		arffDaten = aLoader.getDataSet();
		System.out.println("filename" + fileName);*/
	}


	public ArrayList<Weka_resultFile> getCorrectAnalysis(FileHandler filehandler, String analName, int clusterAnzahl) {
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
			// wenn nichts davon, dann cluster
			wekaFiles = clusterAnalyseMulti(filehandler, analName, clusterAnzahl);
			break;
		}

		// Rückgabe des Ergebnisses (je nach Bedarf)
		return wekaFiles;
	}



	/**
	 * cluster analyse multithreaded
	 * @param fileHandler
	 * @param cluster der Name des Attributes, nach dem geclustered wird. Bsp "Einkaufssumme"
	 * @return
	 * @throws Exception
	 */
	public ArrayList<Weka_resultFile> clusterAnalyseMulti(FileHandler fileHandler, String cluster,int clusterAnzahl) {
		ArrayList<Weka_resultFile> wekaFiles = new ArrayList<>();


		int index = 0;
		Attribute attribute = data.attribute(cluster);
		if (attribute != null) {
			index = attribute.index();
		} else {
			index = 0; // Attribut nicht gefunden
		}
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
		yValues = analyse.checkCluster(cluster,yValues);
		xValues = analyse.setXvalues(xValues);
		wekaFiles.add(new Weka_resultFile(analyse.getClusterName(cluster), xValues, yValues));
		return wekaFiles;
	}

	/**
	 * Kundenstärkste Einkauftage und Uhrzeiten
	 * 
	 * @param fileHandler
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public ArrayList<Weka_resultFile> kundenhaeufigkeit(FileHandler fileHandler){
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
		wekaFiles.add(new Weka_resultFile("Kundenanzahl nach Tagen", xValues, yValues));

		String[] xValues2 = {"<10 Uhr", "10-12 Uhr", "12-14 Uhr", "14-17 Uhr", ">17 Uhr"};
		String[] yValues2 = new String[5];
		for (int i = 0; i < xValues2.length; i++) {
			yValues2[i] = Integer.toString(zeiten.get(xValues2[i]));
		}

		wekaFiles.add(new Weka_resultFile("Kundenanzahl nach Uhrzeit", xValues2, yValues2));

		return wekaFiles;
	}



	/**
	 * Top Einkaufsuhrzeit pro Tag
	 * @param filehandler
	 * @return File with 
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */

	public ArrayList<Weka_resultFile> uhrzeitProTag(FileHandler filehandler){
		String[] tage = {"Montag", "Dienstag", "Mittwoch", "Donnerstag", "Freitag", "Samstag"};
		String[] zeiten = {"<10 Uhr", "10-12 Uhr", "12-14 Uhr", "14-17 Uhr", ">17 Uhr"};
		Map<String, Map<String, AtomicInteger>> tageZeiten = new ConcurrentHashMap<>();
		ArrayList<Weka_resultFile> wekaFiles = new ArrayList<>();

		Arrays.stream(tage).parallel().forEach(tag -> {
			Map<String, AtomicInteger> zeitMap = new ConcurrentHashMap<>();
			zeitMap.put("<10 Uhr", new AtomicInteger(0));
			zeitMap.put("10-12 Uhr", new AtomicInteger(0));
			zeitMap.put("12-14 Uhr", new AtomicInteger(0));
			zeitMap.put("14-17 Uhr", new AtomicInteger(0));
			zeitMap.put(">17 Uhr", new AtomicInteger(0));
			tageZeiten.put(tag, zeitMap);
		});

		//index 5 = Tag, index 6= uhrzeit, index 9 = Einkaufssumme
		/*da Daten in loops unabhängige sind kann man gut multithreaden fucking int streams are breaking my brain 
		 */

		IntStream.range(0, data.numInstances()).parallel().forEach(i -> {
			String day = data.instance(i).stringValue(5);
			String time = data.instance(i).stringValue(6); 
			Map<String, AtomicInteger> tagZeit = tageZeiten.get(day);
			tagZeit.get(time).addAndGet((int) data.instance(i).value(9));
		});

		
		//TODO cleanup
		for (int j = 0;j<tage.length;j++) {
			String[] yValues = new String[zeiten.length];
			for (int i = 0; i < zeiten.length; i++) {
				yValues[i] = Integer.toString(tageZeiten.get(tage[j]).get(zeiten[i]).intValue());
			}
			wekaFiles.add(new Weka_resultFile(tage[j].concat(" in Euro"), zeiten, yValues));
		}
		

		return wekaFiles;
	}
	
	/**
	 * Umsatzstärkste Einkauftage und Uhrzeiten
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public ArrayList<Weka_resultFile> umsatzstaerksteTagUhrzeit(FileHandler filehandler){

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
		//AtomicInteger => Es dürfen keine Befehle eine Operation an einem AtomicInteger unterbrechen
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

		// Uhrzeiten und Umsatz
		for (int i = 0; i < zeiten.length; i++) {
			xValues[i] = zeiten[i];
			int sum = 0;
			for (String tag : tage) {
				int currTime = tageZeiten.get(tag).get(zeiten[i]).get();
				sum += currTime;
			}
			yValuesTime[i] = Integer.toString(sum);
		}

		// tage und Umsatz
		for (int i = 0; i < tage.length; i++) {
			int sum = 0;
			for (String zeit : zeiten) {
				int currTime = tageZeiten.get(tage[i]).get(zeit).get();
				sum += currTime;
			}
			yValuesDay[i] = Integer.toString(sum);
		}

		// Erzeuge 2 Weka_resultFiles für Tag und Umsatz
		wekaFiles.add(new Weka_resultFile("Umsatz nach Uhrzeit in €", xValues, yValuesTime));
		wekaFiles.add(new Weka_resultFile("Umsatz nach Tag in €", tage, yValuesDay));
		return wekaFiles;
	}

}
