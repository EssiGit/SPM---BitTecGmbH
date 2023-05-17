package helpers;
import org.apache.commons.csv.CSVFormat; 
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.time.StopWatch;

import java.util.HashMap;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class CSVCheck {
	
	private static final String[] GESCHLECHT_VALUES = {"m", "w"};
	private static final String[] ALTER_VALUES = {">60", "18-30", "41-50", "31-40", "51-60"};
	private static final String[] KINDER_VALUES = {"nein", "ja"};
	private static final String[] FAMILIENSTAND_VALUES = {"ledig", "Partnerschaft"};
	private static final String[] BERUFSTAETIG_VALUES = {"nein", "ja"};
	private static final String[] EINKAUFTAG_VALUES = {"Montag", "Dienstag", "Mittwoch", "Donnerstag", "Freitag", "Samstag"};
	private static final String[] EINKAUFUHRZEIT_VALUES = {"<10 Uhr", "10-12 Uhr", "12-14 Uhr", "14-17 Uhr", ">17 Uhr"};
	private static final String[] WOHNORT_VALUES = {"< 10 km", "10 - 25 km", "> 25 km"};
	private static final String[] HAUSHALTSNETTOEINKOMMEN_VALUES = {"3200-<4500", "<1000", "1000-<2000", "2000-<3200", ">4500"};
	public boolean checkCSV(String csvFilePath) {
		StopWatch watch = new StopWatch();
		watch.start();
		try (FileReader reader = new FileReader(csvFilePath);
				CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT)) {

			List<CSVRecord> records = csvParser.getRecords();
			int numRecords = records.size();
			int numThreads = Runtime.getRuntime().availableProcessors();
			ExecutorService executor = Executors.newFixedThreadPool(numThreads);
			List<Future<Boolean>> futures = new ArrayList<>();
			
	        for (int i = 1; i < numRecords; i++) {
	            CSVRecord csvRecord = records.get(i);
	            //hier sollte (hoffentlich) die call methode in einem anderen thread ausgeführt werden für jede Zeile
	            Future<Boolean> future = executor.submit(new FormatCheckTask(csvRecord));
	            futures.add(future);
	        }


			for (Future<Boolean> future : futures) {// tasks fertig werden lassen
				try {
					if (future.get()) {
						System.out.println("Falsches CSV Format");
						executor.shutdownNow();
						return false;
					}
				} catch (InterruptedException | ExecutionException e) {
					watch.stop();
					e.printStackTrace();
				}
			}

			executor.shutdown();
			System.out.println("csv format korrekt"); // muss zur main
		} catch (IOException e) {
			watch.stop();
			e.printStackTrace();
		}
		watch.stop();
		System.out.println("time: " + watch.getTime() +" ms");
		return true;
	}

	private static class FormatCheckTask implements Callable<Boolean> {
	    private final CSVRecord csvRecord;

	    private final Map<Integer, String[]> columnValidValues;

	    public FormatCheckTask(CSVRecord csvRecord) {
	        this.csvRecord = csvRecord;

	        
	        this.columnValidValues = new HashMap<>();
	        columnValidValues.put(0, GESCHLECHT_VALUES);
	        columnValidValues.put(1, ALTER_VALUES);
	        columnValidValues.put(2, KINDER_VALUES);
	        columnValidValues.put(3, FAMILIENSTAND_VALUES);
	        columnValidValues.put(4, BERUFSTAETIG_VALUES);
	        columnValidValues.put(5, EINKAUFTAG_VALUES);
	        columnValidValues.put(6, EINKAUFUHRZEIT_VALUES);
	        columnValidValues.put(7, WOHNORT_VALUES);
	        columnValidValues.put(8, HAUSHALTSNETTOEINKOMMEN_VALUES);
	    }

	    @Override
	    public Boolean call() {
	        if (csvRecord.size() != 6) {
	            System.out.println("Falsches CSV Format");
	            return false;
	        }

	        for (Map.Entry<Integer, String[]> entry : columnValidValues.entrySet()) {
	            int columnIndex = entry.getKey();
	            String[] validValues = entry.getValue();
	            String col = csvRecord.get(columnIndex);

	            if (!isValidValue(col, validValues)) {
	                System.out.println("Wert falsch: " + columnIndex + ": " + col);
	                return false;
	            }
	        }

	        for (int i = 9; i <= 24; i++) {
	            String col = csvRecord.get(i);
	            if (!isNumeric(col)) {
	                System.out.println("Wert falsch: " + i + ": " + col);
	                return false;
	            }
	        }

	        return true;
	    }

	    private boolean isValidValue(String value, String[] validValues) {
	        for (String validValue : validValues) {
	            if (value.equals(validValue)) {
	                return true;
	            }
	        }
	        return false;
	    }

	    private boolean isNumeric(String value) {
	        try {
	            double numericValue = Double.parseDouble(value);
	            return numericValue >= 0;
	        } catch (NumberFormatException e) {
	            return false;
	        }
	    }
	}

}
