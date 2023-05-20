package marketing;
import user_handling.User;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import file_handling.FileHandler;

public class MarketingHelper {

    private static File marketingFile;
    private String BASE_MARKETING_FILE_PATH;

    FileHandler filehandler;

    public MarketingHelper(User user) {
        this.filehandler = new FileHandler(user);
        BASE_MARKETING_FILE_PATH = System.getProperty("user.home")
                + File.separator + "KaufDort_Userfiles" + File.separator + "users" + File.separator + user.getName()
                + File.separator + "marketing.xml";
        marketingFile = new File(BASE_MARKETING_FILE_PATH);
    }

    /**
     * Creates new marketing xml file if it didnt exist before
     * @throws IOException
     * @throws JAXBException
     */
    public void newMarketingFile() throws IOException, JAXBException {
        if (!marketingFile.exists()) {
            System.out.println("Marketing path: " + marketingFile.getAbsolutePath());
            marketingFile.createNewFile();
            // Write the base values to the marketing XML file
            MarketingData marketingData = new MarketingData();
            marketingData.setValues(getBaseValues());
            writeMarketingData(marketingData);
        }
    }

    /**
     * Add line to the marketng file
     * @param text to add
     * @throws IOException
     * @throws JAXBException
     */
    public void addToMarketingFile(String text) throws IOException, JAXBException {
        MarketingData marketingData = readMarketingData();
        marketingData.getValues().add(text);
        writeMarketingData(marketingData);
    }

    /**
     * gibt alle Inhalte des Marketingfiles wieder
     * @return
     * @throws IOException
     * @throws JAXBException
     */
    public ArrayList<String> getValues() throws IOException, JAXBException {
        MarketingData marketingData = readMarketingData();
        return marketingData.getValues();
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

    /**
     * Daten auslesen aus marketing.xml
     * @return
     * @throws IOException
     * @throws JAXBException
     */
    private MarketingData readMarketingData() throws IOException, JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(MarketingData.class);
        return (MarketingData) jaxbContext.createUnmarshaller().unmarshal(marketingFile);
    }

    /**
     * An das marketingData File neue Daten anhängen
     * @param marketingData
     * @throws IOException
     * @throws JAXBException
     */
    private void writeMarketingData(MarketingData marketingData) throws IOException, JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(MarketingData.class);
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.marshal(marketingData, marketingFile);
    }
}
