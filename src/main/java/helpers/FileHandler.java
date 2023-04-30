package helpers;

import java.io.File;
import java.util.ArrayList;
import org.apache.commons.io.FilenameUtils;
public class FileHandler {

	private static File DIR = new File(System.getProperty("user.home") + File.separator + "KaufDort_Userfiles"+ File.separator);



	public String[] getFileNames() {

		String fileNames = "";
		int i = 0;
		if(DIR.exists()) {
			for(File files : DIR.listFiles()) {
				String extension = FilenameUtils.getExtension(files.getAbsolutePath());
				if(extension.equals("csv")) {
					if(i==0) {
						fileNames = fileNames.concat(files.getName());
						i++;
					}else {
						fileNames = fileNames.concat("," + files.getName());
					}
				}
			}
		}else {
			fileNames = "Empty";
		}
		String[] allFiles = fileNames.split(",");
		allFiles = setButtonValues(allFiles);
		return allFiles;
	}
	private String[] setButtonValues(String[] files) {
		int len = files.length;
		String[] file = new String[5];
		if(len<5) {
			for(int i = 0; i<5;i++) {
				if(i<files.length) {
					file[i] = files[i];
				}else {
					file[i] = "Empty";
				}	
			}
		}	
		return file;
	}
}
