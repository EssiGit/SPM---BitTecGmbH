package user_handling;

import java.io.File;  
import java.util.regex.Pattern;

import javax.xml.bind.JAXBException;

import java.util.regex.Matcher;
import java.io.IOException;
import helpers.FileHandler;

public class SetupUser {
    private static final String USERS_HANDLING_FILE_PATH = System.getProperty("user.home") + File.separator + "KaufDort_Userfiles" + File.separator + "usersHandling.csv";
    private static final String ERROR_NONE = "none";
    private static final String ERROR_USER_EXISTS = "Nutzername existiert bereits!";
    private static final String ERROR_ILLEGAL_CHARS = "Nur Buchstaben, Zahlen, Unterstriche und Bindestriche erlaubt";
    private static final String ERROR_USERNAME_LENGTH = "Nutzernamen dürfen nicht länger als 13 Zeichen sein!";
    private static final String ERROR_EMPTY_PASSWORD = "Passwort Feld darf nicht leer sein!";
	UserHandler userHand = new UserHandler();
	String csvLine;
	String userName;
	String password;
    private boolean exists;
    private boolean hasNoIllegalChars;
    private boolean isUsernameTooLong;
    private boolean isPasswordEmpty;
    
	public SetupUser(String userName, String password,String hash) throws IOException  {
		this.userName = userName;
		this.password = password;
        exists = checkIfExists();
        hasNoIllegalChars = checkForIllegalChars();
        isUsernameTooLong = (userName.length() > 13);
        isPasswordEmpty = password.isEmpty();
		
		StringBuilder csvBuilder = new StringBuilder();
		csvBuilder.append(userName);
		csvBuilder.append(",");
		csvBuilder.append(hash);
		csvLine = csvBuilder.toString();
	}
	
	/**
	 * checks if user already exists 
	 * @return true if user exists, false if not
	 * @throws IOException 
	 */
    private boolean checkIfExists() throws IOException {
        return new File(USERS_HANDLING_FILE_PATH).exists() && userHand.checkForUserName(userName);
    }
    
    public boolean isSetupValid() {
    	System.out.println("exists && !hasIllegalChars && !isUsernameTooLong && !isPasswordEmpty: " + !exists + hasNoIllegalChars + !isUsernameTooLong  + !isPasswordEmpty);
        return !exists && hasNoIllegalChars && !isUsernameTooLong && !isPasswordEmpty;
    }

    /**
     * checks username for illegal Chars
     * @return
     */
	private boolean checkForIllegalChars() {
	    //erlaubte Zeichen
	    Pattern pattern = Pattern.compile("^[a-zA-Z0-9_-]+$");

	    Matcher matcher = pattern.matcher(userName);

	    return matcher.matches();
	}
	
	/**
	 * returns an error, for example if the addUser failed. 
	 * @return specific error, "none" is default
	 * @throws IOException
	 */
	public String getErrorMsg() throws IOException {
        String retVal = ERROR_NONE;

        if (checkIfExists()) {
            retVal = ERROR_USER_EXISTS;
        }

        if (!checkForIllegalChars()) {
            retVal = ERROR_ILLEGAL_CHARS;
        }

        if (userName.length() > 13) {
            retVal = ERROR_USERNAME_LENGTH;
        }

        if (password.equals("")) {
            retVal = ERROR_EMPTY_PASSWORD;
        }

        System.out.println(retVal);
        return retVal;
    }
	
	/**
	 * writes new fileData.txt for User
	 * @param fileHand
	 * @throws IOException
	 */
	private void writeNewDataFile(FileHandler fileHand) throws IOException{
		fileHand.writeDataFile("");
	}
	
	/**
	 * adds User, sets up DIR and adds the User to the .csv
	 * @param fileHand
	 * @return true if user was added, false if not
	 * @throws IOException
	 * @throws JAXBException 
	 */
	public boolean addUser(FileHandler fileHand) throws IOException, JAXBException {
		if(isSetupValid()) {
		fileHand.setupForLogin();
		userHand.addUser(csvLine);
		userHand.setupUser(userName);
		writeNewDataFile(fileHand);
		return true;
		}else {
			return false;
		}
	}
		
}
