package helpers;

import java.io.BufferedReader; 
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import org.apache.commons.io.FilenameUtils;

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
		File path = new File(System.getProperty("user.home") + File.separator + "KaufDort_Userfiles"+ File.separator + "usersHandling.txt"); 
		userLine = userLine.replace("\n", "");
		boolean result = false;
		BufferedReader reader = new BufferedReader(new FileReader(path));
		String line = "";
		while(( line = reader.readLine()) != null) {
			System.out.println("check for user ID " + line + ", " + userLine +"as");
			if(line.equals(userLine)) {
				result = true;
				System.out.println("in " + line);
			}
		}

		return result;
	}
	/**
	 * writes a new user handling file
	 * @param userDIR
	 * @param userData
	 */
	private void writeUserFile(File userDIR, String userData) {
		try {
			System.out.println("in Userhandler, writeUserFile :" + userDIR.getAbsolutePath());
			FileWriter file = new FileWriter(userDIR.getAbsolutePath() +  "usersHandling.txt");
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
		File handlerDIR = new File(System.getProperty("user.home") + File.separator + "KaufDort_Userfiles"+ File.separator + "usersHandling.txt");
		if( handlerDIR.exists()) {
			ArrayList<String> allUserNames = getExistentUsers(handlerDIR);
			if(checkForUserName(userName) == false) {
				try {
					System.out.println("in Userhandler, writeUserFile :" + handlerDIR.getAbsolutePath());
					FileWriter file = new FileWriter(handlerDIR.getAbsolutePath(), true);
					System.out.println("before write:" + userName);
					file.write(userName + System.lineSeparator());
					file.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

		private void setupUserID() throws IOException {
			File handlerDIR = new File(System.getProperty("user.home") + File.separator + "KaufDort_Userfiles"+ File.separator + "usersHandling.txt");
			if(!handlerDIR.exists()) {
				System.out.println("new FILE");
				PrintWriter writer = new PrintWriter(handlerDIR.getAbsolutePath(), "UTF-8");
				writer.print("");
				writer.close();
		}
	}
}

	/*if( handlerDIR.exists()) {
		ArrayList<String> allUserNames = getExistentUsers(handlerDIR);
		if(checkForUserName(handlerDIR, name) == false) {
			System.out.println("tmpUsrID is not empty");
			 //because we want the next free user number
			userLine = userLine.replace("\n", "");
			String[] ID = userLine.split(" ");
			returnVal = Integer.parseInt(ID[1]);
			userLine = name + " " + String.valueOf(returnVal) ;
			userData += "\n" + userLine;
			System.out.println("usrdata: "  + userData);
		}else {
			returnVal++;
			userLine = name + " " + String.valueOf(returnVal) ;
			System.out.println("usrline: "  + userLine);
			userData += "\n" + userLine;
			System.out.println("usrdata: "  + userData);

		}

	}
	writeUserFile(userDIR,userData);
	return returnVal;*/ 
	//TODO create USERID Folder BEFORE upload!
