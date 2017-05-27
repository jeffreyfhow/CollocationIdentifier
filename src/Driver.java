import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;
import org.apache.commons.math3.distribution.TDistribution;
import org.apache.commons.math3.stat.inference.TTest;
import org.apache.commons.math3.stat.inference.ChiSquareTest;

public class Driver {

	public static void main(String[] args) {
		
		// 1. Get available file choices
    	ArrayList<String> fileNames = new ArrayList<String>();
    	File[] listOfFiles = (new File("./corpora")).listFiles();
    	for(int i = 0; i < listOfFiles.length; i++){
        	if(listOfFiles[i].isFile()){
        		fileNames.add(listOfFiles[i].getName());
        	}
        }
    	
    	// 2. Opening Interface -> Accept Input File
    	System.out.println("Welcome to Jeff's Collocation Analyzer.");
    	System.out.println("Please choose a file by file number: ");
    	for(int i = 0; i < fileNames.size(); i++){
    		System.out.println("\tFile # " + (i+1) + ": " + fileNames.get(i));
    	}
        System.out.print("Awaiting File Number from User... (Don't forget to press enter) ");
        Scanner key = new Scanner(System.in);
        int fileChoice = key.nextInt();
        
        while(fileChoice <= 0 || fileChoice > fileNames.size()){
        	System.out.print("Invalid entry: Awaiting File Number from User... (Don't forget to press enter) ");
            key = new Scanner(System.in);
            fileChoice = key.nextInt();
        }
        String fileName = fileNames.get(fileChoice - 1);
        
        // 3. Convert text file to xml file (tagged POS)
        System.out.println();
        System.out.println("POS Tagging: START -" + fileName);
        System.out.println("Please wait...");
        String xmlFile = textToXml(fileName);
        System.out.println("POS Tagging: END");
        fileNames = null;
        listOfFiles = null;
        
        
		BigramDatabase b = new BigramDatabase();
		System.out.println("");
		b.parseFile("inputFiles/" + fileName.substring(0, fileName.lastIndexOf('.')) + ".xml");
		b.printResults("outputFiles/output_" + fileName);
		System.out.println("END");

	}
	
	public static String textToXml(String fileName){
		String textFile = "corpora/" + fileName;
		String xmlFile = "inputFiles/" + fileName.substring(0, fileName.lastIndexOf('.')) + ".xml";
		try{
        	Runtime rt = Runtime.getRuntime();
        	Process proc = rt.exec("cmd /c start /wait cmd.exe /K \"cd stanford-postagger-2013-04-04 && " +
        							"java -mx300m -classpath stanford-postagger.jar edu.stanford.nlp.tagger.maxent.MaxentTagger -model models/wsj-0-18-left3words-distsim.tagger -textFile ../" + textFile + " -outputFormat xml > ..\\" + xmlFile +
        							" && exit \b 0\"");
        	proc.waitFor();
		}
        catch(Exception e){
        	System.out.println("commandlinestuff");
        }
		return xmlFile;
	}

}
