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
	
	
    String findCluster(Instances daten,int clusterIndex, int number) throws Exception {
        String[] result;

        SimpleKMeans model = new SimpleKMeans();
        model.setNumClusters(number);
        Remove attributeFilter = new Remove();
        attributeFilter.setAttributeIndicesArray(new int[] {1,2,3,4, clusterIndex });
        attributeFilter.setInvertSelection(true);
        attributeFilter.setInputFormat(daten);
        daten = Filter.useFilter(daten, attributeFilter);
        model.buildClusterer(daten);

        // Final cluster centroids holen
        result = model.getClusterCentroids().toString().split("@data\n");
        System.out.println("temp : " + result[1]);
        return (result[1] + "\n");
    }
    String findClusterMulti(final Instances daten, final int clusterIndex, final int number) throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        try {
            Callable<String> task = new Callable<String>() {
                public String call() throws Exception {
                    SimpleKMeans model = new SimpleKMeans();
                    model.setNumClusters(number);
                    Remove attributeFilter = new Remove();
                    attributeFilter.setAttributeIndicesArray(new int[]{0,1, 2, 3, 4, clusterIndex});
                    attributeFilter.setInvertSelection(true);
                    attributeFilter.setInputFormat(daten);
                    Instances filteredData = Filter.useFilter(daten, attributeFilter);
                    model.buildClusterer(filteredData);

                    // Final cluster centroids holen
                    String[] result = model.getClusterCentroids().toString().split("@data\n");
                    System.out.println("temp : " + result[1]);
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
    
    public String findClusterNew(Instances daten, int clusterIndex, int number) throws Exception {
        String[] result;
        String resultTest = "";
        int numFields = 5;
        SimpleKMeans model = new SimpleKMeans();
        model.setNumClusters(number);
        // Attribut entfernen, außer dem gewünschten Attribut für die Clusteranalyse
        Remove attributeFilter = new Remove();
        attributeFilter.setAttributeIndicesArray(new int[] {1,2,3,4, clusterIndex });
        attributeFilter.setInvertSelection(true);
        attributeFilter.setInputFormat(daten);
        Instances clusterData = Filter.useFilter(daten, attributeFilter);
        
        model.buildClusterer(clusterData);
        
        Instances centroids = model.getClusterCentroids();
        // Nur die ersten numFields Felder der Clusterzentroide anzeigen
        for (int i = 0; i < centroids.numInstances(); i++) {
            Instance centroid = centroids.instance(i);
            StringBuilder clusterFields = new StringBuilder();
            for (int j = 0; j < numFields; j++) {
            	System.out.println(j);
                clusterFields.append(centroid.stringValue(j)).append(",");
            }
            resultTest += clusterFields.toString() + "\n";
        }

        // Nur die ersten numFields Felder der Clusterzentroide anzeigen
        result = model.getClusterCentroids().toString().split("@data\n");
        System.out.println("temp : " + result[1]);
        System.out.println("tempTest : " + resultTest);
        return (result[1] + "\n");
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
