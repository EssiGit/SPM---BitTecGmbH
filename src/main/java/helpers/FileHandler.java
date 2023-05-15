package helpers;
import helpers.User;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;
import helpers.UserHandler;
import org.apache.commons.io.FilenameUtils;
import java.io.BufferedWriter;

public class FileHandler {
	private User user;

	private static File baseDIR = new File(System.getProperty("user.home") + File.separator + "KaufDort_Userfiles"+ File.separator );
	File fileDataPath;

	public FileHandler(User user) {
		this.user = user;
		fileDataPath = new File(System.getProperty("user.home") + File.separator + "KaufDort_Userfiles"+ File.separator + "users" + File.separator + user.getName() + File.separator + "fileData.txt");

	}



	/** Returns all file names in order from oldest added at [0] to newest at max length.
	 * 
	 * @return String[] size 5 with all file names.
	 * If less than 5 files are currently uploaded, empty array indices will be filled with String "Empty"
	 * 
	 * 
	 */
	public String[] getFileNames() throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(fileDataPath));
		String line;
		String[] lines = new String[5];
		int i = 0;
		System.out.println(user.getName());
		while( (line = reader.readLine()) != null && i<5) {		
			lines[i] = line;
			i++;
		}
		lines = setButtonValues(lines);
		return lines;
	}

	/** Sets up Button Values.
	 * If passed Array value is empty, button value will be set to "Empty"
	 * @param  files Array with Button values. If longer than 5, rest will be ignored
	 * 
	 * 
	 * 
	 */
	private String[] setButtonValues(String[] files) {
		String[] file = new String[5];
		System.out.println(files.length);
		for(int i = 0; i<5;i++) {
			if(files[i] != null) {
				file[i] = files[i];
			}else {
				file[i] = "Empty";
			}	

		}

		return file;
	}


	/**
	 * writes a new File to the current Users DIR
	 * @param fileName of the file to be written
	 * @param request current request that holds the file parts
	 * @throws IOException exceptions? we dont handle those
	 * @throws ServletException "
	 */
	public void setUpFILE(File fileName, HttpServletRequest request) throws IOException, ServletException {
		setUpDIR();
		File newFileDIR = new File(System.getProperty("user.home") + File.separator + "KaufDort_Userfiles" + File.separator + "users" + File.separator + user.getName() + File.separator + fileName.getName());
		if(!(newFileDIR.exists())) {
			System.out.println("its a new file");
			for(Part part : request.getParts()) {
				part.write(System.getProperty("user.home") + File.separator + "KaufDort_Userfiles" + File.separator + "users" + File.separator + user.getName() + File.separator + fileName.getName());
			}
			writeDataFile(fileName.getName());
		}
	}

	/**
	 * sets up the user DIR by username
	 * TODO DRY (UserHandler.setupUser)
	 * ex: C:\ users\ username\ KaufDort_Userfiles\ user1\
	 */
	public void setUpDIR() {
		File tmp = new File(System.getProperty("user.home") + File.separator + "KaufDort_Userfiles"+ File.separator + "users" + File.separator + user.getName() + File.separator + "Result_Files"+ File.separator);
		if(!(tmp.exists())) {
			System.out.println("in mkdir");

			tmp.mkdirs();
		}

	}
	/**
	 * writes wekaResultFile to USERDIR/Result_Files/
	 * @param csv
	 * @param fileName
	 */
	public void writeWekaResult(String csv,String fileName) {
		try {
			FileWriter file = new FileWriter(System.getProperty("user.home") + File.separator + "KaufDort_Userfiles"+ File.separator + "users" + File.separator + user.getName() + File.separator +"Result_Files" +  File.separator +  "result_cluster_" + fileName);
			file.write(csv);
			file.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	/** Deletes file.
	 * @param file to be deleted. Both file and the result-file will be deleted!
	 * 
	 * 
	 */
	private void deleteOldFile(String file) {
		File tmpResultFile = new File(System.getProperty("user.home") + File.separator + "KaufDort_Userfiles"+ File.separator + "users" + File.separator + user.getName() + File.separator + "Result_Files"+ File.separator+ "result_cluster_" + file);
		File tmpFile = new File(System.getProperty("user.home") + File.separator + "KaufDort_Userfiles"+ File.separator + "users" + File.separator + user.getName() + File.separator + file);		
		tmpResultFile.delete();
		tmpFile.delete();
	}

	/** writes fileData.txt to keep order of last 5 added files.
	 * Should fileData.txt not exist, it will create a new one.
	 * @param file (I should fix)
	 * @param fileName (I should fix)
	 * 
	 */
	public void writeDataFile(String fileName) throws IOException {
		try {
			System.out.println(fileDataPath.isFile());
			System.out.println(fileDataPath.exists());
			System.out.println("in Filehandler, writeDataFile :" + fileDataPath.getAbsolutePath());

			System.out.println("bytess " + fileDataPath.length());
			if(fileDataPath.exists()) {
				System.out.println("before write:" + fileName);
				BufferedWriter buffWriter = new BufferedWriter(new FileWriter(fileDataPath,true));
	            if (fileDataPath.length() > 0) {
	                buffWriter.newLine(); //nur wenn schon etwas drin steht new line. Bei dem ersten upload würder er sonst bei der zweiten Zeile starten
	            }
				buffWriter.append(fileName);
				buffWriter.close();
			}else {
				System.out.println("first");
				String[] lines = {""};
				writeNewIDFile(fileDataPath,  lines);
			}

			checkFilesMoreThan5(fileDataPath);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	/**
	 * writes new IDFile to organize the user uploads. 
	 * 
	 * @param file the file path with user path
	 * @param lines the already existing files
	 * @throws IOException
	 */
	private void writeNewIDFile(File file,  String[] lines) throws IOException {
		System.out.println(" new file");
		if(file.exists()==false){
			System.out.println(" new gud");
			new FileWriter(file.getAbsolutePath()).close();
		}else {
			new FileWriter(file.getAbsolutePath()).close();
			FileWriter fileWriter = new FileWriter(file);
			BufferedWriter buffWriter = new BufferedWriter(fileWriter);
			System.out.println("lines0 " + lines[0]);
			fileWriter.write(lines[0]);
			for(int i = 1;i<lines.length;i++) {
				if(lines[i] != null) {
					buffWriter.newLine();
					buffWriter.append(lines[i]);
				}

			}
			buffWriter.close();
		}
	}

	/**
	 * helper to check that current files stay less than 6. In case of more than 5 files, oldest will be delted
	 *
	 * @param path path of the current users file handling file
	 * @throws IOException
	 */
	private void checkFilesMoreThan5(File path) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(path));
		String line;
		String[] lines = new String[10];
		int i = 1;	
		line = reader.readLine();
		while(( line = reader.readLine()) != null) {
			lines[i-1] = line;
			i++;
		}
		//firstLine;
		while(i>5) { //more than 5 files in userDIR
			System.out.println("more than 5" + i);
			reader = new BufferedReader(new FileReader(path));
			String firstLine = reader.readLine();
			System.out.println(firstLine);
			writeNewIDFile(path, lines);
			deleteOldFile(firstLine);
			i--;
		}
		reader.close();
	}
}

/*private int getTopFileID() {
File DIR = new File(baseDIR.getAbsolutePath().concat(File.separator + "users" + File.separator +  user.getName()));
int fileID = 0;
if(DIR.exists() && DIR.listFiles() != null) {
	for(File files : DIR.listFiles()) {
		String extension = FilenameUtils.getExtension(files.getAbsolutePath());
		if(extension.equals("csv")) {
			int filetmp = 0;
			String filename = files.getName();
			String[] fileSplit = filename.split("_");
			try{
				filetmp = Integer.parseInt(fileSplit[fileSplit.length-1]);
				if(filetmp>=fileID) fileID = filetmp;
				System.out.println(fileID);
			}catch(NumberFormatException e) {

			}
		}
	}
}
return fileID;
}*/
/*public String[] getFileNames() {
File DIR = new File(baseDIR.getAbsolutePath().concat(File.separator + "users" + File.separator +  user.getName()));
System.out.println(DIR.getAbsolutePath());
String fileNames = "Empty";
int first = 0;
if(DIR.exists() && DIR.listFiles() != null) {
	for(File files : DIR.listFiles()) {
		String extension = FilenameUtils.getExtension(files.getAbsolutePath());
		if(extension.equals("csv")) {
			if(first==0) {
				fileNames = files.getName();
				first++;
			}else {
				fileNames = fileNames.concat("," + files.getName());
			}
		}
	}
}else {
	fileNames = "Empty";
}
System.out.println("all fil:"+ fileNames);
String[] allFiles = fileNames.split(",");

allFiles = setButtonValues(allFiles);
return allFiles;
}*/