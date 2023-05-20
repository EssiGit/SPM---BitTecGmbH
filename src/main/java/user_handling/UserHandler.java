package user_handling;

import java.io.BufferedReader;  
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import org.apache.commons.io.FilenameUtils;
import user_handling.PasswordHasher;

public class UserHandler {

	private static File DIR = new File(System.getProperty("user.home") + File.separator + "KaufDort_Userfiles"+ File.separator + "users" + File.separator );
	private static final File handling_DIR =  new File(System.getProperty("user.home") + File.separator + "KaufDort_Userfiles"+ File.separator + "usersHandling.csv");



	/**
	 * gets path from passed Username
	 * @param userName 
	 * @return path of user DIR
	 */
	public String getUserPath(String userName) {
		String path = new String(DIR.getAbsolutePath().concat( File.separator + userName + File.separator));
		return path;
	}

	/**
	 * sets up User folder
	 * @param name
	 * @throws IOException
	 */
	public void setupUser(String name) throws IOException {
		File userDIR = new File(getUserPath(name)  + "Files"+ File.separator);

		if(!(userDIR.exists())) {
			userDIR.mkdirs();	
		}

	}	
	
	/**
	 * check the userHandling file for the given username
	 * @param userLine name of the user 
	 * @return false if user not found, true if user found
	 * @throws IOException
	 */
	public boolean checkForUserName(String userLine) throws IOException {

	    if (handling_DIR.exists()) {
	        try (BufferedReader reader = new BufferedReader(new FileReader(handling_DIR))) {
	            final String finalUserLine = userLine;
	            System.out.println("finalUserLine " + finalUserLine);
	            return reader.lines()
	                    .map(line -> line.split(",")[0]) // erste Spalte 
	                    .anyMatch(username -> username.equals(finalUserLine));
	        }
	    }

	    return false;
	}


	/**
	 * searches for the hash that belongs to the passed Username
	 * @param userName
	 * @return the hash that belongs to the given username
	 * @throws IOException
	 */
	public String getHash(String userName) throws IOException {

	    if (handling_DIR.exists()) {
	        try (BufferedReader reader = new BufferedReader(new FileReader(handling_DIR))) {
	            final String finalUserLine = userName;
	            return reader.lines()
	                    .filter(line -> line.split(",")[0].equals(finalUserLine)) 
	                    .map(line -> line.split(",")[1]) // Get salt
	                    .findFirst() // Returned den gefundenen salt
	                    .orElse(null); // oder null
	        }
	    }

	    return null;
	}
	
	
	/**
	 * checks hash and userName
	 * @param userName
	 * @param Password
	 * @return
	 * @throws IOException 
	 */
	public boolean checkForUser(String userName, String password) throws IOException {
		
		
		boolean exists = false;
		exists = checkForUserName(userName);
		if(exists) {
			String salt = getHash(userName);
			PasswordHasher hasher = new PasswordHasher();
			exists =  hasher.checkPassword(password, getHash(userName));
			//exists = checkForPassword(hashedPW);
		}
		

		return exists;
	}


	/**
	 * adds user to user handling file
	 * @param userName
	 * @throws IOException
	 */
	public void addUser(String userName) throws IOException {
		setupUserID();
		if( handling_DIR.exists()) {
			if(checkForUserName(userName) == false) {
				try {
					System.out.println("in Userhandler, writeUserFile :" + handling_DIR.getAbsolutePath());
					FileWriter file = new FileWriter(handling_DIR.getAbsolutePath(), true);
					file.write(userName + System.lineSeparator());
					file.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * creates the  user handling csv file if it didnt exist before
	 * @throws IOException
	 */
	private void setupUserID() throws IOException {

		if (!handling_DIR.exists()) {
			System.out.println("Creating new file: " + handling_DIR.getAbsolutePath());
			handling_DIR.createNewFile();
		}
	}


}

