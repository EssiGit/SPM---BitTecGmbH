package helpers; 

import java.io.BufferedReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
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
import java.io.BufferedWriter;

public class FileHandler {
	private User user;
	private static final int MAX_FILES = 5;
	
	private static final File BASE_DIR = new File(System.getProperty("user.home") + File.separator + "KaufDort_Userfiles"+ File.separator );
	File fileDataPath;

	public FileHandler(User user) {
		this.user = user;
		fileDataPath = new File(BASE_DIR, "users" + File.separator + user.getName() + File.separator + "fileData.txt");

	}



	/** Returns all file names in order from oldest added at [0] to newest at max length.
	 * 
	 * @return String[] size 5 with all file names.
	 * If less than 5 files are currently uploaded, empty array indices will be filled with String "Empty"
	 * 
	 * 
	 */
	public String[] getFileNames() throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileDataPath), StandardCharsets.UTF_8));
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
		File newFileDIR = new File(BASE_DIR.getAbsolutePath() +  File.separator + "users" + File.separator + user.getName() + File.separator + fileName.getName());
		System.out.println("newFile " +  newFileDIR); 
		if(!(newFileDIR.exists())) {
			for(Part part : request.getParts()) {
				part.write(BASE_DIR.getAbsolutePath() + File.separator + "users" + File.separator + user.getName() + File.separator + fileName.getName());
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
		File tmp = new File(BASE_DIR.getAbsolutePath()+ File.separator + "users" + File.separator + user.getName() + File.separator + "Result_Files"+ File.separator);
		if(!(tmp.exists())) {
			System.out.println("in mkdir");

			tmp.mkdirs();
		}

	}

	/** Deletes file.
	 * @param file to be deleted. Both file and the result-file will be deleted!
	 * 
	 * 
	 */
	public void deleteOldFile(String file) {
	    String arffFile = file.replace(".csv", ".csv.arff");
	    File tmpArffFile = new File(BASE_DIR.getAbsolutePath() + File.separator + "users" + File.separator + user.getName() + File.separator + arffFile);
	    File tmpFile = new File(BASE_DIR.getAbsolutePath() + File.separator + "users" + File.separator + user.getName() + File.separator + file);        
	    tmpArffFile.delete();
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
	            writeNewFile(fileDataPath, Arrays.asList(lines));
	        }

	        checkFilesMoreThan5(fileDataPath);
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}
	
	public void writeDataFile_old(String fileName) throws IOException {
		try {
			if(fileDataPath.exists()) {
				BufferedWriter buffWriter = new BufferedWriter(new FileWriter(fileDataPath,true));
	            if (fileDataPath.length() > 0) {
	                buffWriter.newLine(); //nur wenn schon etwas drin steht new line. Bei dem ersten upload würder er sonst bei der zweiten Zeile starten
	            }
				buffWriter.append(fileName);
				buffWriter.close();
			}else {
				System.out.println("first"); //file hat vorher nicht existiert
				String[] lines = {""};
				writeNewFile(fileDataPath,  Arrays.asList(lines));
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
	 * TODO: FIX AFTER 5 STACK
	 */
	private void writeNewFile(File file, List<String> lines) throws IOException {
	    System.out.println(" new file");
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
	private void checkFilesMoreThan5(File path) throws IOException {
	    List<String> lines = Files.readAllLines(path.toPath());

	    while (lines.size() > MAX_FILES) {
	        String firstLine = lines.get(0);
	        lines.remove(0);
	        writeNewFile(path, lines);
	        deleteOldFile(firstLine);
	    }
	}

}
