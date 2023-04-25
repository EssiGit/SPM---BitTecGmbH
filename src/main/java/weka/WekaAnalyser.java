package weka;

import java.io.File;

import weka.WekaTools;
import weka.core.Instances;
import weka.core.converters.ArffLoader;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVLoader;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.NumericCleaner;


public class WekaAnalyser {
	private static final String DIR = "src" + File.separator +"main" + File.separator+"webapp" + File.separator + "usr_data" + File.separator;;
	private WekaTools analyse = new WekaTools();
	 Instances data;
     Instances arffDaten;
	
	public WekaAnalyser(String file) throws Exception {
		
		String filePath = DIR + file;

        
        String arffDat = DIR + "kd100.arff";

       
		
        CSVLoader loader = new CSVLoader();
        loader.setSource(new File(filePath));
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
    System.out.println(analyse.findCluster(data, 5));
	}
}
