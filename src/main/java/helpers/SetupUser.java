package helpers;

import java.io.File;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.io.IOException;

public class SetupUser {
	

	UserHandler userHand = new UserHandler();
	String userName;
	public SetupUser(String userName)  {
		this.userName = userName;
	}
	/**
	 * checks if user already exists 
	 * the first chonky if sequence is to check if the usersHandlings file even exists
	 * @return true if user exists, false if not
	 * @throws IOException 
	 */
	private boolean checkIfexists() throws IOException {
		boolean exists = false;
		if(new File(System.getProperty("user.home") + File.separator + "KaufDort_Userfiles"+ File.separator + "usersHandling.txt").exists()){
			exists = userHand.checkForUserName(userName);
		}

		return exists;
		
	}
	

	private boolean checkForIllegalChars() {
	    //erlaubte Zeichen
	    Pattern pattern = Pattern.compile("^[a-zA-Z0-9_-]+$");

	    Matcher matcher = pattern.matcher(userName);

	    return matcher.matches();
	}
	public String errorMsg() throws IOException {
		String retVal = "none";
		
		if(checkIfexists()) 
			retVal = "Nutzername existiert bereits!";
		
		if(checkForIllegalChars() == false)
			retVal = "Nur Buchstaben, Zahlen, Unterstriche und Bindestriche erlaubt";
		if(userName.length()>13)
			retVal = "Nutzernamen dürfen nicht länger als 13 Zeichen sein!";
		
		System.out.println(retVal);
		return retVal;
	}
	private void writeNewDataFile(FileHandler fileHand) throws IOException{
		fileHand.writeDataFile("");
	}
	public void addUser(FileHandler fileHand) throws IOException {
		userHand.addUser(userName);
		writeNewDataFile(fileHand);
	}
}
