package weka;

import java.io.File; 
import java.util.List;

import weka.associations.Apriori;
import weka.associations.AssociationRule;
import weka.classifiers.rules.ZeroR;
import weka.clusterers.SimpleKMeans;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.NumericCleaner;
import weka.filters.unsupervised.attribute.NumericToNominal;

public class WekaTools {
	
	
    String findCluster(Instances daten, int number) throws Exception {
        String[] result;

        SimpleKMeans model = new SimpleKMeans();
        model.setNumClusters(number);

        model.buildClusterer(daten);

        // Final cluster centroids holen
        result = model.getClusterCentroids().toString().split("@data\n");
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
