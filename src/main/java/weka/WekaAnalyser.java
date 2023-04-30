package weka;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import weka.WekaTools;
import weka.core.Instances;
import weka.core.converters.ArffLoader;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVLoader;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.NumericCleaner;
import java.io.FileWriter;
import java.io.IOException;

public class WekaAnalyser {
	//private static final String DIR = "src" + File.separator +"main" + File.separator+"webapp" + File.separator + "usr_data" + File.separator;
	private WekaTools analyse = new WekaTools();
	 Instances data;
     Instances arffDaten;
     private String fileName;
     private File DIR;
	
	public WekaAnalyser(String filePassed) throws Exception {
		fileName = filePassed;
		DIR = new File(System.getProperty("user.home") + File.separator + "KaufDort_Userfiles" + File.separator +fileName);

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
	
	
	
	public void clusterAnalyse() throws Exception {

    System.out.println(">>>>>--- Clusteranalyse ueber alle Daten, 5 Cluster ---\n");
    String result = "";
    try (BufferedReader reader = new BufferedReader(new FileReader(DIR))) {
		result = reader.readLine();
	}
    result = result.concat("\n" + analyse.findCluster(data, 5));
    writeWekaResult(result);
	}
	private void writeWekaResult(String csv) {
	      try {
	          FileWriter file = new FileWriter(System.getProperty("user.home") + File.separator + "KaufDort_Userfiles" + File.separator + "result_" + fileName);
	          file.write(csv);
	          file.close();
	       } catch (IOException e) {
	          // TODO Auto-generated catch block
	          e.printStackTrace();
	       }
		
	}
}
