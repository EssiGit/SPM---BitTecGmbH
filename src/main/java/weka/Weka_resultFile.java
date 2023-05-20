package weka;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;


public class Weka_resultFile {

	private String[] xData;
	private int[] yData;
	private String yName;
	public String tableName;
	private int yMax;

	//TODO make dynmaic y and x axis
	public Weka_resultFile(String yName, String[] xData, String[] yData) throws FileNotFoundException, IOException {
		this.xData = xData;
		this.yData = new int[yData.length];
		int yMaxTmp = 0;
		for(int i = 0; i<yData.length;i++) {
			this.yData[i] = (int) Double.parseDouble(yData[i]);
			if(this.yData[i]>yMaxTmp) {
				yMaxTmp= this.yData[i];
			}
		}
		yMax = yMaxTmp;


		this.yName = yName;

	}

	public int getYmax(){
		return yMax;
	}

	public String[] getXnames(){
		return xData;
	}
	
	public int[] getYdata(){
		return yData;
	}
	
	public String getYname(){
		return yName;
	}
	
	public String getTableName() {
		return tableName;
	}


	//(nikok) get Json of Result
	public String ajax() {

		String xNames = "";

		for (int i = 0; i < getXnames().length; i++) {
			xNames += getXnames()[i] + "\",\"";
		}

		xNames = "[\""+ xNames.substring(0, xNames.length()-3)+ "\"]";
		xNames = xNames.replace("\n", "\\n").replace("ä", "ae");
		return "{\"xNames\": " +       xNames
				+ ",\n\"yName\": \"" + getYname() + "\""
				+ ",\n\"yMax\": " + getYmax() 
				+ ",\n\"tablename\": " + getTableName() 
				+ ",\n\"yValues\": "  + Arrays.toString(getYdata()).replace("[", "[\"").replace(",", "\",\"").replace("]", "\"]") + "}";
	}

}
