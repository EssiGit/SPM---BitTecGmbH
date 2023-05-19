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




	/**
	 * gets path from passed Username
	 * @param userName 
	 * @return path of user DIR
	 */
	public String getUserPath(String userName) {
		String path = new String(DIR.getAbsolutePath().concat( File.separator + userName + File.separator));
		return path;
	}

	public void setupUser(String name) throws IOException {
		File userDIR = new File(getUserPath(name)  + "Result_Files"+ File.separator);

		if(!(userDIR.exists())) {
			userDIR.mkdirs();	
		}

	}


	private ArrayList<String> getExistentUsers(File path) throws IOException {
		ArrayList<String> result = new ArrayList<String>();
		BufferedReader reader = new BufferedReader(new FileReader(path));
		String line = "";
		while(( line = reader.readLine()) != null) {
			result.add(line);
		}
		return result;

	}
	/**
	 * check the userHandling file for the given username
	 * @param userLine name of the user 
	 * @return false if user not found, true if user found
	 * @throws IOException
	 */
	public boolean checkForUserName(String userLine) throws IOException {
	    File path = new File(System.getProperty("user.home") + File.separator + "KaufDort_Userfiles" + File.separator + "usersHandling.csv");

	    if (path.exists()) {
	        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
	            final String finalUserLine = userLine;
	            System.out.println("finalUserLine " + finalUserLine);
	            return reader.lines()
	                    .map(line -> line.split(",")[0]) // erste Spalte 
	                    .anyMatch(username -> username.equals(finalUserLine));
	        }
	    }

	    return false;
	}

	public boolean checkForPassword(String hash) throws FileNotFoundException, IOException {
	    File path = new File(System.getProperty("user.home") + File.separator + "KaufDort_Userfiles" + File.separator + "usersHandling.csv");

	    if (path.exists()) {
	        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
	            final String finalLine = hash;
	            return reader.lines()
	                    .map(line -> line.split(",")[1]) // erste Spalte 
	                    .anyMatch(username -> username.equals(finalLine));
	        }
	    }

	    return false;
	}
	
	public String getHash(String userName) throws IOException {
	    File path = new File(System.getProperty("user.home") + File.separator + "KaufDort_Userfiles" + File.separator + "usersHandling.csv");

	    if (path.exists()) {
	        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
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
			System.out.println("hash: " + salt);
			exists =  hasher.checkPassword(password, getHash(userName));
			//exists = checkForPassword(hashedPW);
		}
		

		return exists;
	}

	/**
	 * writes a new user handling file
	 * @param userDIR
	 * @param userData
	 */
	private void writeUserFile(File userDIR, String userData) {
		try {
			System.out.println("in Userhandler, writeUserFile :" + userDIR.getAbsolutePath());
			FileWriter file = new FileWriter(userDIR.getAbsolutePath() +  "usersHandling.csv");
			System.out.println("before write:" + userData);
			file.write(userData);
			file.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * adds user to user handling file
	 * @param userName
	 * @throws IOException
	 */
	public void addUser(String userName) throws IOException {
		setupUserID();
		File handlerDIR = new File(System.getProperty("user.home") + File.separator + "KaufDort_Userfiles"+ File.separator + "usersHandling.csv");
		if( handlerDIR.exists()) {
			if(checkForUserName(userName) == false) {
				try {
					System.out.println("in Userhandler, writeUserFile :" + handlerDIR.getAbsolutePath());
					FileWriter file = new FileWriter(handlerDIR.getAbsolutePath(), true);
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
		File handlerFile = new File(System.getProperty("user.home") + File.separator + "KaufDort_Userfiles" + File.separator + "usersHandling.csv");

		if (!handlerFile.exists()) {
			System.out.println("Creating new file: " + handlerFile.getAbsolutePath());
			handlerFile.createNewFile();
		}
	}

	private void setupUserID_old() throws IOException {
		File handlerDIR = new File(System.getProperty("user.home") + File.separator + "KaufDort_Userfiles"+ File.separator + "usersHandling.csv");
		if(!handlerDIR.exists()) {
			System.out.println("new FILE");
			PrintWriter writer = new PrintWriter(handlerDIR.getAbsolutePath(), "UTF-8");
			writer.print("");
			writer.close();
		}
	}
}

