package helpers; 

import java.io.BufferedReader; 
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.nio.charset.StandardCharsets;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import user_handling.User;
import user_handling.UserHandler;
import helpers.MarketingHelper;
public class FileHandler {
	private User user;
	private static final int MAX_FILES = 5;

	private static final File BASE_DIR = new File(System.getProperty("user.home") + File.separator + "KaufDort_Userfiles"+ File.separator );
	private static File FILE_DIR;
	private static File USER_DIR;
	private static File fileDataPath;

	public FileHandler(User user) {
		this.user = user;
		fileDataPath = new File(BASE_DIR, "users" + File.separator + user.getName() + File.separator + "fileData.txt");
		FILE_DIR = new File(BASE_DIR.getAbsolutePath() +  File.separator + "users" + File.separator + user.getName() + File.separator + "Files" + File.separator );
		USER_DIR = new File(BASE_DIR.getAbsolutePath() +  File.separator + "users" + File.separator + user.getName() + File.separator);
	}

	/**
	 * sets everything up for login, so that even in case Data gets lost there will be no errors
	 * 
	 * @throws IOException
	 */
	public void setupForLogin() throws IOException {
		UserHandler userHand = new UserHandler();
		MarketingHelper marked = new MarketingHelper(user);
		marked.newMarketingFile();
		setUpDIR();
		keepFilesEqualToDIR();
		userHand.setupUser(user.getName());
	}

	/** Returns all file names in order from oldest added at [0] to newest at max length.
	 * 
	 * @return String[] size 5 with all file names.
	 * If less than 5 files are currently uploaded, empty array indices will be filled with String "Empty"
	 * 
	 * 
	 */
	public String[] getFileNames() throws IOException {
		String[] lines = new String[5];
		if(fileDataPath.exists()) {
		System.out.println(getFilenamesInDIR());
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileDataPath), StandardCharsets.UTF_8));
		String line;
		
		int i = 0;
		while( (line = reader.readLine()) != null && i<5) {		
			lines[i] = line;
			i++;
		}
		lines = setButtonValues(lines);
		}
		return lines;
	}

	public boolean verifyFilesWithDataFile() throws IOException {
		
		String[] filesInDIR = getFilenamesInDIR();
		String[] filesInData = getFileNames();
		try {
		Arrays.sort(filesInDIR);
		Arrays.sort(filesInData);
		}catch(java.lang.NullPointerException e) {
			return false;
		}
		// Compare the filenames
		return Arrays.equals(filesInDIR, filesInData);
	}

	/**
	 * this method is to check the actually existing filenames in the FILE_DIR
	 * Used to check if the actual files in FILE_DIR match the names in the fileData.txt 
	 * @return actual files in FILE_DIR by name
	 */
	private String[] getFilenamesInDIR() {
		String[] files  = FILE_DIR.list();

		return files ;
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
	 * writes a new File to the current Users DIR only if it contains no errors.
	 * @param fileName of the file to be written
	 * @param request current request that holds the file parts
	 * @throws IOException exceptions? we dont handle those
	 * @throws ServletException "
	 */
	public boolean uploadFile(String filename, HttpServletRequest request) throws IOException, ServletException{
		CSVCheck csvchecker = new CSVCheck();
		boolean fileUploaded = true;
		setUpDIR();
		File newFileDIR = new File(FILE_DIR.getAbsolutePath() +  File.separator + filename);
		System.out.println("newFile " +  newFileDIR); 
		
		if(!(newFileDIR.exists())) {
			for(Part part : request.getParts()) {
				part.write(FILE_DIR.getAbsolutePath()  + File.separator + filename);
			}
			
			if(csvchecker.checkCSV(FILE_DIR.getAbsolutePath()  + File.separator + filename)) {
			writeDataFile(filename);
			fileUploaded = true;
			}else {
				fileUploaded = false;
				deleteOldFile(filename);
			}

		}
		return fileUploaded;

	}
/**
 * this method checks if files in the DIR are the same as in the fileData.txt 
 * to avoid bugs
 * @throws IOException
 */
	private void keepFilesEqualToDIR() throws IOException {
		System.out.println("verify: " + verifyFilesWithDataFile());
		if(!(verifyFilesWithDataFile())){
			String[] filenames = getFilenamesInDIR();

			System.out.println("files:" + Arrays.asList(filenames));
			writeNewIDFile(fileDataPath,Arrays.asList(filenames)); //if unequal it writes a new ID File, order will now be random
		}
	}
	
	/**
	 * sets up the user DIR by username
	 * TODO DRY (UserHandler.setupUser)
	 * ex: C:\ users\ username\ KaufDort_Userfiles\ user1\
	 */
	private void setUpDIR() {
		File tmp = new File(FILE_DIR.getAbsolutePath());
		if(!(tmp.exists())) {
			System.out.println("in mkdir");

			tmp.mkdirs();
		}

	}

	/** Deletes file.
	 * @param filename to be deleted. 
	 * 
	 * 
	 */
	public void deleteOldFile(String filename) {
		File tmpFile = new File(FILE_DIR.getAbsolutePath() + File.separator  + filename);
		tmpFile.delete();
	}

	/** writes fileData.txt to keep order of last 5 added files.
	 * Should fileData.txt not exist, it will create a new one.
	 * TODO fix bug where wrong upload will be shown in button
	 * @param file (I should fix)
	 * @param fileName (I should fix)
	 * 
	 */
	public void writeDataFile(String fileName) throws IOException {
		try {
			if (fileDataPath.exists()) {
				List<String> lines = Collections.singletonList(fileName);
				Files.write(fileDataPath.toPath(), lines, StandardCharsets.UTF_8, StandardOpenOption.APPEND); //append zum am ende anfügen
			} else {
				System.out.println("first"); // file hat vorher nicht existiert
				String[] lines = {""};
				writeNewIDFile(fileDataPath, Arrays.asList(lines));
			}

			checkFilesMoreThanMax(fileDataPath);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	/**
	 * writes new IDFile to organize the user uploads. 
	 * 
	 * @param file the file path with user path
	 * @param lines with names of the already existing files
	 * @throws IOException
	 * TODO: FIX AFTER 5 STACK
	 */
	private void writeNewIDFile(File file, List<String> lines) throws IOException {
		System.out.println("new file ");
		if (!file.exists()) {
			file.createNewFile();
		} else {
			Path filePath = file.toPath();
			Files.write(filePath, lines);
		}
	}
	/**
	 * helper to check that current files stay less than 6. In case of more than 5 files, oldest will be delted
	 *
	 * @param path path of the current users file handling file
	 * @throws IOException
	 */
	private void checkFilesMoreThanMax(File path) throws IOException {
		List<String> lines = Files.readAllLines(path.toPath());

		while (lines.size() > MAX_FILES) {
			String firstLine = lines.get(0);
			lines.remove(0);
			writeNewIDFile(path, lines);
			deleteOldFile(firstLine);
		}
	}

}
