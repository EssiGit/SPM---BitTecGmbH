package helpers;

import java.io.File;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;

import org.apache.commons.io.FilenameUtils;
public class FileHandler {

	private static File DIR = new File(System.getProperty("user.home") + File.separator + "KaufDort_Userfiles"+ File.separator);



	public String[] getFileNames() {

		String fileNames = "";
		int i = 0;
		if(DIR.exists() && DIR.listFiles() != null) {
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
	public void setUpDIR(File fileName, HttpServletRequest request) throws IOException, ServletException {
		if(!(DIR.exists())) {
			File tmp = new File(System.getProperty("user.home") + File.separator + "KaufDort_Userfiles"+ File.separator + "Result_Files");
			tmp.mkdirs();
		}
		File newFileDIR = new File(System.getProperty("user.home") + File.separator + "KaufDort_Userfiles" + File.separator +fileName.getName());
		if(!(newFileDIR.exists())) {
			System.out.println("its a new file");
			for(Part part : request.getParts()) {
				part.write(System.getProperty("user.home") + File.separator + "KaufDort_Userfiles" + File.separator + fileName.getName());
			}
		}
	}
}
