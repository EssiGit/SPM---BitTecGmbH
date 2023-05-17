package helpers;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class CSVCheck {
    public static void main(String[] args) {
        String csvFilePath = "path/to/your/csv/file.csv";

        try (FileReader reader = new FileReader(csvFilePath);
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT)) {

            List<CSVRecord> records = csvParser.getRecords();
            int numRecords = records.size();
            int numThreads = Runtime.getRuntime().availableProcessors();
            ExecutorService executor = Executors.newFixedThreadPool(numThreads);
            List<Future<Boolean>> futures = new ArrayList<>();

            for (CSVRecord csvRecord : records) {
                Future<Boolean> future = executor.submit(new FormatCheckTask(csvRecord));
                futures.add(future);
            }

            // Wait for all tasks to complete
            for (Future<Boolean> future : futures) {
                try {
                    if (!future.get()) {
                        System.out.println("Invalid format detected");
                        executor.shutdownNow();
                        return;
                    }
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }

            executor.shutdown();
            System.out.println("CSV file has the correct format");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class FormatCheckTask implements Callable<Boolean> {
        private final CSVRecord csvRecord;

        public FormatCheckTask(CSVRecord csvRecord) {
            this.csvRecord = csvRecord;
        }

        @Override
        public Boolean call() {
            if (csvRecord.size() != 6) {
                System.out.println("Falsches CSV Format"); //muss noch zum frontend redirected werden
                return false;
            }

            String col1 = csvRecord.get(0);
            String col2 = csvRecord.get(1);
            String col3 = csvRecord.get(2);
            String col4 = csvRecord.get(3);
            String col5 = csvRecord.get(4);
            String col6 = csvRecord.get(5);

            if (!isString(col1) || !isString(col2) || !isString(col3)
                    || !isString(col4) || !isNumeric(col5) || !isNumeric(col6)) {
                System.out.println("Invalid format: " + csvRecord.toString());
                return false;
            }

            return true;
        }

        private boolean isString(String value) {
            return value.matches("[a-zA-Z]+");
        }

        private boolean isNumeric(String value) {

            try {
            	double test =  Double.parseDouble(value);
                if(test<0) {
                	return false;
                }
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
            
        }
    }
}
