package weka;

import java.io.BufferedReader; 
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;


public class Weka_resultFile {

	private String[] xData;
	private int[] yData;
	private ArrayList<String> xNames = new ArrayList<>();
	private String yName;
	public String tableName;
	private int lineNumber;
	private int yMax;

	//TODO make dynmaic y and x axis
	public Weka_resultFile(String yName, String[] xData, String[] yData) throws FileNotFoundException, IOException {
		this.xData = xData;
		this.yData = new int[yData.length];
		int yMaxTmp = 0;
		for(int i = 0; i<yData.length;i++) {
			System.out.println("test");
			System.out.println(yData[i]);
			this.yData[i] = (int) Double.parseDouble(yData[i]);
			if(this.yData[i]>yMaxTmp) {
				yMaxTmp= this.yData[i];
			}
		}
		yMax = yMaxTmp;
		
		
		this.yName = yName;

		
		System.out.println(tableName);
		System.out.println(yName);
		for(int i = 0;i<xNames.size();i++) {
			System.out.print(xNames.get(i) + ": ");
			//System.out.println(yData.get(i));
		}





	}



	/**
	 * reads a certain line, for example line 2, in a file.
	 * SLOW AS FUCK and a dumb way to do things but works for now
	 * @param resultFile
	 * @param customLine custom line as int
	 * @return
	 * @throws IOException
	 */
	private String readFileLine(File resultFile,int customLine) throws IOException {

		try (BufferedReader reader = new BufferedReader(new FileReader(resultFile))) {
			int readingLine = 1;
			String line;
			String returnVal = "";
			while((line = reader.readLine()) != null) {
				if(customLine == readingLine) {
					returnVal = line;
				}
				readingLine++;
			}
			return returnVal;
		}
	}


	
	/*private void setupTableName(File resultData) throws IOException {
		String result = readFileLine(resultData, lineNumber);
		String[] resultArr = result.split(",");
		for(int i =0;i<4;i++) { //9 for all
				tableName = tableName.concat("," + resultArr[i]);
		}
		tableName = setTableName(tableName);
	}*/
	
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
		xNames = xNames.replace("\n", "\\n");
		return "{\"xNames\": " +       xNames
				+ ",\n\"yName\": \"" + getYname() + "\""
				+ ",\n\"yMax\": " + getYmax() 
				+ ",\n\"tablename\": " + getTableName() 
				+ ",\n\"yValues\": "  + Arrays.toString(getYdata()).replace("[", "[\"").replace(",", "\",\"").replace("]", "\"]") + "}";
	}

}
