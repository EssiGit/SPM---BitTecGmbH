package helpers;


import user_handling.User; 
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.nio.charset.StandardCharsets;
import java.nio.file.StandardOpenOption;

public class MarketingHelper {

    private static File marketingFile;
    private String BASE_MARKETING_FILE_PATH;

    FileHandler filehandler;

    public MarketingHelper(User user) {
        this.filehandler = new FileHandler(user);
        BASE_MARKETING_FILE_PATH = System.getProperty("user.home")
                + File.separator + "KaufDort_Userfiles" + File.separator + "users" + File.separator + user.getName()
                + File.separator + "marketing.txt";
        marketingFile = new File(BASE_MARKETING_FILE_PATH);
    }
/**
 * creates new marketing File
 * @throws IOException
 */
    public void newMarketingFile() throws IOException {
        if (!marketingFile.exists()) {
        	System.out.println("markting path: " + marketingFile.getAbsolutePath());
            marketingFile.createNewFile();
            // Schreibe die Base Values in das Marketing-Textfile
            List<String> baseValues = getBaseValues();
            Files.write(Paths.get(BASE_MARKETING_FILE_PATH), baseValues);
        }
    }
/**
 * add lines to marketing file
 * @param text to add
 * @throws IOException
 */
    public void addToMarketingFile(String text) throws IOException {
    	List<String> lines = Collections.singletonList(text);
        Files.write(marketingFile.toPath(), lines, StandardCharsets.UTF_8, StandardOpenOption.APPEND);

    }
/**
 * returned alle Zeilen des Marketing Files
 * @return
 * @throws IOException
 */
    public ArrayList<String> getValues() throws IOException {
        List<String> lines = Files.readAllLines(Paths.get(BASE_MARKETING_FILE_PATH));
        return new ArrayList<>(lines);
    }
/**
 * base values, die jedes Marketing File immer hat
 * @return
 */
    private ArrayList<String> getBaseValues() {
        ArrayList<String> items = new ArrayList<>();

        items.add("Gestaltung ansprechender Schaufenster, um das Interesse von Passanten zu wecken und neue Kunden zu gewinnen.");
        items.add("Durchführung von Rabattaktionen, Verlosungen oder anderen Promotionen, um Kunden anzulocken und die Umsätze zu steigern.");
        items.add("Angebot von kostenlosen Produktproben, um Kunden von neuen Produkten zu überzeugen und die Markentreue zu stärken.");
        items.add("Durchführung von Kundenumfragen, um Feedback zu erhalten und das Angebot an Produkten und Dienstleistungen zu optimieren.");
        items.add("Organisation von Events wie Verkostungen oder Workshops, um Kunden in den Laden zu locken und das Image des Ladens zu stärken.");
        items.add("Einrichtung eines Treueprogramms, um bestehende Kunden an den Laden zu binden und die Wiederholungskäufe zu steigern.");

        return items;
    }
}
