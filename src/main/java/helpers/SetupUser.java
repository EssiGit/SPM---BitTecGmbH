package helpers;

import java.io.File;
import java.io.IOException;

public class SetupUser {
	
	FileHandler fileHand;
	UserHandler userHand = new UserHandler();
	User user;
	public SetupUser(User user)  {
		this.user = user;
		fileHand = new FileHandler(user);
	}
	/**
	 * checks if user already exists 
	 * the first chonky if sequence is to check if the usersHandlings file even exists
	 * @return true if user exists, false if not
	 * @throws IOException 
	 */
	public boolean checkIfexists() throws IOException {
		boolean exists = false;
		if(new File(System.getProperty("user.home") + File.separator + "KaufDort_Userfiles"+ File.separator + "usersHandling.txt").exists()){
			exists = userHand.checkForUserName(user.getName());
		}
		return exists;
		
	}
	
	private void writeNewDataFile() throws IOException{
		fileHand.writeDataFile("");
	}
	public void addUser() throws IOException {
		userHand.addUser(user.getName());
		writeNewDataFile();
	}
}
