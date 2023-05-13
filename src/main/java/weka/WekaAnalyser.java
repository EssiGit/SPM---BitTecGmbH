package weka;

import java.io.BufferedReader; 
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import helpers.UserHandler;
import java.io.File;
import java.io.FileReader;
import weka.WekaTools;
import weka.classifiers.rules.ZeroR;
import weka.core.Instances;
import weka.core.converters.ArffLoader;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVLoader;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.NumericCleaner;
import java.io.FileWriter;
import java.io.IOException;
import helpers.User;
import helpers.FileHandler;
import weka.core.converters.ConverterUtils.DataSource;
import weka.classifiers.Classifier;
import weka.classifiers.functions.LinearRegression;
import weka.filters.unsupervised.attribute.Remove;

import java.util.HashMap;
import java.util.Map;

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



	public File clusterAnalyse(FileHandler fileHandler) throws Exception {

		System.out.println(">>>>>--- Clusteranalyse ueber alle Daten, 5 Cluster ---\n");
		String result = "";
		try (BufferedReader reader = new BufferedReader(new FileReader(DIR))) {
			result = reader.readLine();
		}
		result = result.concat("\n" + analyse.findCluster(data, 5));
		fileHandler.writeWekaResult(result, fileName);
		UserHandler userhand = new UserHandler();
		return new File(System.getProperty("user.home") + File.separator + "KaufDort_Userfiles" + File.separator + "users"  + File.separator + user.getName() +File.separator +"Result_Files" + File.separator +  "result_cluster_" + fileName);
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

    public Map<String, Integer> findBusiestShoppingTimePerDay() {
        Map<String, Integer> busiestTimes = new HashMap<>();

        for (int i = 0; i < arffDaten.numInstances(); i++) {
            String day = arffDaten.instance(i).stringValue(arffDaten.attribute("Einkaufstag"));
            String time = arffDaten.instance(i).stringValue(arffDaten.attribute("Einkaufsuhrzeit"));
            double sales = arffDaten.instance(i).value(arffDaten.attribute("Einkaufssumme"));

            // Extrahiere die Uhrzeit ohne Leerzeichen
            String cleanedTime = time.replace(" ", "");

            // Prüfe, ob der Tag bereits in der Map enthalten ist
            if (!busiestTimes.containsKey(day)) {
                busiestTimes.put(day, 0);
            }

            // Aktualisiere die Anzahl der Einkäufe für die entsprechende Uhrzeit
            if (cleanedTime.equals(">17Uhr") || cleanedTime.equals("14-17Uhr") ||
                    cleanedTime.equals("12-14Uhr") || cleanedTime.equals("10-12Uhr") ||
                    cleanedTime.equals("<10Uhr")) {
                int count = busiestTimes.get(day);
                busiestTimes.put(day, count + 1);
            }
        }

        return busiestTimes;
    }
	public void umsatzstärksteUhrzeit() throws Exception {
		DataSource source = new DataSource(arffDaten);
		Instances data = source.getDataSet();

		// Setze den Index des Attributs für die Uhrzeit (Annahme: Attributindex 0)
		int uhrzeitIndex = 0;

		// Entferne alle anderen Attribute, die nicht für die Vorhersage benötigt werden
		String[] options = new String[]{"-R", "1-" + (data.numAttributes() - 1)};
		Remove remove = new Remove();
		remove.setOptions(options);
		remove.setInputFormat(data);
		Instances filteredData = Filter.useFilter(data, remove);

		// Setze das Zielattribut auf den Umsatz (Annahme: Letztes Attribut)
		filteredData.setClassIndex(filteredData.numAttributes() - 1);

		// Erstelle ein lineares Regressionsmodell
		Classifier classifier = new LinearRegression();
		classifier.buildClassifier(filteredData);

		// Erstelle eine Map zur Speicherung der Umsatzsummen für jede Uhrzeit
		Map<Integer, Double> uhrzeitUmsatzMap = new HashMap<>();

		// Iteriere über die Instanzen und berechne die Vorhersage für jede Uhrzeit
		for (int i = 0; i < filteredData.numInstances(); i++) {
			double uhrzeit = filteredData.instance(i).value(uhrzeitIndex);
			double umsatz = classifier.classifyInstance(filteredData.instance(i));

			// Aktualisiere die Umsatzsumme für die entsprechende Uhrzeit
			if (uhrzeitUmsatzMap.containsKey(uhrzeit)) {
				umsatz += uhrzeitUmsatzMap.get(uhrzeit);
			}
			uhrzeitUmsatzMap.put((int) uhrzeit, umsatz);
		}

		// Suche die umsatzstärkste Uhrzeit pro Tag
		for (int i = 0; i < 24; i++) {
			double maxUmsatz = 0.0;
			int umsatzstarkeUhrzeit = -1;
			for (int j = i; j < i + 24; j++) {
				double umsatz = uhrzeitUmsatzMap.getOrDefault(j % 24, 0.0);
				if (umsatz > maxUmsatz) {
					maxUmsatz = umsatz;
					umsatzstarkeUhrzeit = j % 24;
				}
			}
			System.out.println("Tag " + (i + 1) + ": Umsatzstärkste Uhrzeit: " + umsatzstarkeUhrzeit);
		}
	}
}
