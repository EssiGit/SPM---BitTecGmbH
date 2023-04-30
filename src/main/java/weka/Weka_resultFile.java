package weka;

import java.io.BufferedReader; 
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;


public class Weka_resultFile {

	ArrayList<Integer> yData = new ArrayList<>();
	ArrayList<String> xNames = new ArrayList<>();
	String yName;
	String tableName;

	public Weka_resultFile(File resultData) throws FileNotFoundException, IOException {


		String[] tmpName = resultData.getName().split("_");
		tableName = tmpName[1];
		setupXNames(resultData);
		setupYData(resultData);
		setupTableName(resultData);
		yName = "Summe in Euro";

		
		System.out.println(tableName);
		System.out.println(yName);
		for(int i = 0;i<xNames.size();i++) {
			System.out.print(xNames.get(i) + ": ");
			System.out.println(yData.get(i));
		}





	}

	private String setTableName(String table) {
		if(table.contains(",m,")) {
			table = table.replaceFirst(",m,", " Männlich ");
		}else {
			table = table.replaceFirst(",w,", " Weiblich ");
		}
		return table;
	}

	private String readFileLine(File resultFile, int lineNumber) throws IOException {

		try (BufferedReader reader = new BufferedReader(new FileReader(resultFile))) {
			int readingLine = 1;
			String line;
			String returnVal = "";
			while((line = reader.readLine()) != null) {
				if(lineNumber == readingLine) {
					returnVal = line;
				}
				readingLine++;
			}
			return returnVal;
		}
	}

	private void setupXNames(File resultData) throws IOException {
		String result = "";
		result = readFileLine(resultData, 1);
		String[] resultArr = result.split(",");
		for(int i =9;i<resultArr.length;i++) {
			xNames.add(resultArr[i]);
		}
	}
	private void setupYData(File resultData) throws IOException {
		String result = readFileLine(resultData, 2);
		String[] resultArr = result.split(",");
		for(int i =9;i<resultArr.length;i++) {

				double tmpDouble = Double.parseDouble(resultArr[i]);
				yData.add((int)tmpDouble);

		}
	}
	
	private void setupTableName(File resultData) throws IOException {
		String result = readFileLine(resultData, 2);
		String[] resultArr = result.split(",");
		for(int i =0;i<9;i++) {
				tableName = tableName.concat("," + resultArr[i]);
		}
		tableName = setTableName(tableName);
	}
	
	public ArrayList<String> getXnames(){
		return xNames;
	}
	public ArrayList<Integer> getYdata(){
		return yData;
	}
	public String getYname(){
		return yName;
	}
	public String getTableName() {
		return tableName;
	}
}
