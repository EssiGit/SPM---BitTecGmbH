package file_handling; 

import java.io.File; 
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import check_handling.CSVCheck;
import user_handling.User;
import user_handling.UserHandler;
import marketing.MarketingHelper;

public class FileHandler {
	private User user;
	private static final int MAX_FILES = 5;

	private static final File BASE_DIR = new File(System.getProperty("user.home") + File.separator + "KaufDort_Userfiles"+ File.separator );
	private static File FILE_DIR;
	private static File USER_DIR;
	private static File fileDataPath;

	public FileHandler(User user) {
		this.user = user;
		fileDataPath = new File(BASE_DIR, "users" + File.separator + user.getName() + File.separator + "fileData.xml");
		FILE_DIR = new File(BASE_DIR.getAbsolutePath() +  File.separator + "users" + File.separator + user.getName() + File.separator + "Files" + File.separator );
		USER_DIR = new File(BASE_DIR.getAbsolutePath() +  File.separator + "users" + File.separator + user.getName() + File.separator);
	}

	/**
	 * sets everything up for login, so that even in case Data gets lost there will be no errors
	 * 
	 * @throws IOException
	 * @throws JAXBException 
	 */
	public boolean setupForLogin() {
		try {
			UserHandler userHand = new UserHandler();
			MarketingHelper marked = new MarketingHelper(user);

			setUpDIR();

			marked.newMarketingFile();
			keepFilesEqualToDIR();
			userHand.setupUser(user.getName());
		} catch (IOException | JAXBException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/** Returns all file names in order from oldest added at [0] to newest at max length.
	 * 
	 * @return String[] size 5 with all file names.
	 * If less than 5 files are currently uploaded, empty array indices will be filled with String "Empty"
	 * 
	 * 
	 */
	public String[] getFileNames(){

		List<String> fileNames = readDataFile(fileDataPath).getFileNames();
		String[] lines = new String[MAX_FILES];

		for (int i = 0; i < MAX_FILES; i++) {
			if (i < fileNames.size()) {
				lines[i] = fileNames.get(i);
			} else {
				lines[i] = "Empty";
			}
		}
		return lines;
	}

	/*
	 * check if DIR has same Files as fileData.xml
	 */
	private boolean verifyFilesWithDataFile() throws IOException, JAXBException {
		String[] filesInDIR = fillWithEmpty(getFilenamesInDIR());
		String[] filesInData = getFileNames();
		
		Arrays.sort(filesInDIR);
		Arrays.sort(filesInData);
		return Arrays.equals(filesInDIR, filesInData);
	}


	/**
	 * this method is to check the actually existing filenames in the FILE_DIR
	 * Used to check if the actual files in FILE_DIR match the names in the fileData.txt 
	 * @return actual files in FILE_DIR by name
	 */
	private String[] getFilenamesInDIR() {
		String[] files = FILE_DIR.list();
		return files != null ? files : new String[0];
	}


	/**
	 * writes a new File to the current Users DIR only if it contains no errors.
	 * @param fileName of the file to be written
	 * @param request current request that holds the file parts
	 * @throws IOException exceptions? we dont handle those
	 * @throws ServletException "
	 * @throws JAXBException 
	 */
	public boolean uploadFile(String filename, HttpServletRequest request) throws IOException, ServletException, JAXBException {
		CSVCheck csvChecker = new CSVCheck();
		boolean fileUploaded = true;
		setUpDIR();
		File newFile = new File(FILE_DIR.getAbsolutePath() + File.separator + filename);

		if (newFile.exists()) {
			deleteOldFile(filename);
			
		}
			for (Part part : request.getParts()) {
				part.write(newFile.getAbsolutePath());
			}

			if (csvChecker.checkCSV(newFile.getAbsolutePath())) {
				writeNewDataFile(filename);
				fileUploaded = true;
			} else {
				fileUploaded = false;
				deleteOldFile(filename);
			}

		return fileUploaded;
	}
	/**
	 * this method checks if files in the DIR are the same as in the fileData.xml
	 * to avoid bugs
	 * @throws IOException
	 */
	public void keepFilesEqualToDIR() throws IOException, JAXBException {
		if (!verifyFilesWithDataFile()) {
			System.out.println("not the same");
			String[] filenames = getFilenamesInDIR();
			FileData fileData = new FileData();
			fileData.setFileNames(new ArrayList<>(Arrays.asList(filenames)));
			writeDataFile(fileDataPath, fileData);
		}
	}
	
	/**
	 * takes an array and fills it up with "Empty" for every empty index smaller than max_files
	 * @param array to fill
	 * @return
	 */
	private String[] fillWithEmpty(String[] array) {
		String[] dirFilledWithEmpty = new String[MAX_FILES];
		for(int i = 0;i<MAX_FILES;i++) {
			if (i < array.length) {
				dirFilledWithEmpty[i] = array[i];
			} else {
				dirFilledWithEmpty[i] = "Empty";
			}
		}
		return dirFilledWithEmpty;
	}

	/**
	 * sets up the user DIR by username
	 * TODO DRY (UserHandler.setupUser)
	 * ex: C:\ users\ username\ KaufDort_Userfiles\ user1\
	 * @throws JAXBException 
	 * @throws IOException 
	 */
	private void setUpDIR() throws IOException, JAXBException {
		if (!FILE_DIR.exists()) {
			FILE_DIR.mkdirs();
			writeNewDataFile(""); 
		}
	}

	/** Deletes file.
	 * @param filename to be deleted. 
	 * 
	 * 
	 */
	public void deleteOldFile(String filename) {
		File oldFile = new File(FILE_DIR.getAbsolutePath() + File.separator + filename);
		oldFile.delete();
	}

	/** writes fileData.txt to keep order of last 5 added files.
	 * Should fileData.txt not exist, it will create a new one.
	 * TODO fix bug where wrong upload will be shown in button
	 * @param fileDataPath (I should fix)
	 * @param fileData (I should fix)
	 * 
	 */
	private void writeDataFile(File fileDataPath, FileData fileData) throws IOException, JAXBException {
		JAXBContext jaxbContext = JAXBContext.newInstance(FileData.class);
		Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
		jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		jaxbMarshaller.marshal(fileData, fileDataPath);
	}


	/**
	 * writes new IDFile to organize the user uploads. 
	 * 
	 * @param filename to add
	 * @throws IOException
	 * @throws JAXBException 
	 */
	public void writeNewDataFile(String fileName) throws IOException, JAXBException {
		FileData fileData;

		createDataFile();

		System.out.println("filename: " + fileName);
		fileData = readDataFile(fileDataPath);
		List<String> fileNames = fileData.getFileNames();

		if (fileNames.contains(fileName)) {
			fileNames.remove(fileName); //File already existed, old file removed to keep order
			fileNames.add(fileNames.size(), fileName); //new file added at the end
		}else {
			fileNames.add(fileName);
		}
		
		
		
		checkFilesMoreThanMax(fileNames);

		fileData.setFileNames(fileNames);
		writeDataFile(fileDataPath, fileData);
	}

	public void createDataFile() throws IOException {
		if (!fileDataPath.exists()) {
			fileDataPath.createNewFile();
		} 
	}
	/**
	 * Reads fileData.xml to retrieve the list of file names.
	 * 
	 * @param fileDataFile Path to the fileData.xml.
	 * @return FileData object containing the list of file names.
	 * @throws IOException
	 * @throws JAXBException
	 */
	private FileData readDataFile(File fileDataFile) {
		if (fileDataFile.length() > 0) {
			try {
				JAXBContext jaxbContext = JAXBContext.newInstance(FileData.class);
				Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
				return (FileData) jaxbUnmarshaller.unmarshal(fileDataFile);
			} catch (JAXBException e) {
				List<String> fileNames = new ArrayList<>();
				FileData fileData = new FileData();
				fileData.setFileNames(fileNames);
				e.printStackTrace();
				return fileData;
			}
		} else {
			List<String> fileNames = new ArrayList<>();
			FileData fileData = new FileData();
			fileData.setFileNames(fileNames);
			return fileData;
		}
	}
	/**
	 * helper to check that current files stay less than 6. In case of more than 5 files, oldest will be delted
	 *
	 * @param path path of the current users file handling file
	 * @throws IOException
	 */
	private void checkFilesMoreThanMax(List<String> fileNames) throws IOException {
		if (fileNames.size() > MAX_FILES) {
			String removedFileName = fileNames.remove(0);
			File removedFile = new File(FILE_DIR.getAbsolutePath() + File.separator + removedFileName);
			removedFile.delete();
		}
	}

}
