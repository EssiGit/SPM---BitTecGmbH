package helpers;

import java.io.File;

public class MarketingHelper {
	
	private static File baseDIR;;

	FileHandler filehandler;
	
	public MarketingHelper(User user){
		this.filehandler = new FileHandler(user);
		baseDIR = new File(System.getProperty("user.home") + File.separator + "KaufDort_Userfiles"+ File.separator + user.getName() + File.separator);
	}
	
	public void newMarketingFile() {
		
	}

}
