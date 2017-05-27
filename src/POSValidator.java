import java.util.Scanner;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class POSValidator {
	
	private static POSValidator instance;
	private static String[] tags;
	private static String[] ignoreList;
	
	private POSValidator(){
		//1. init tags allowed
		String tagFileName = "inputFiles/PennTagSet_Selection2.txt";
		Scanner scanner = null;
		try{
			//Get Array Size
			scanner = new Scanner(new FileInputStream(tagFileName));
			int tagCount = 0;
			while(scanner.hasNextLine()){
				scanner.nextLine();
				tagCount++;
			}
			tags = new String[tagCount];
			scanner.close();
		
			//Read Tags
			scanner = new Scanner(new FileInputStream(tagFileName));
			tagCount = 0;
			while(scanner.hasNextLine()){
				tags[tagCount] = scanner.nextLine();
				tagCount++;
			}
			
			scanner.close();
		}
		catch(FileNotFoundException e){
			System.out.println(e.getMessage());
			System.out.println("Terminating program...");
			System.exit(0);
		}
		
		//2. init words to ignore
		tagFileName = "inputFiles/ignore_list.txt";
		try{
			//Get Array Size
			scanner = new Scanner(new FileInputStream(tagFileName));
			int ignoreCount = 0;
			while(scanner.hasNextLine()){
				scanner.nextLine();
				ignoreCount++;
			}
			ignoreList = new String[ignoreCount];
			scanner.close();
		
			//Read Tags
			scanner = new Scanner(new FileInputStream(tagFileName));
			ignoreCount = 0;
			while(scanner.hasNextLine()){
				ignoreList[ignoreCount] = scanner.nextLine();
				ignoreCount++;
			}
			
			scanner.close();
		}
		catch(FileNotFoundException e){
			System.out.println(e.getMessage());
			System.out.println("Terminating program...");
			System.exit(0);
		}
	}
	
	public static synchronized POSValidator getInstance() {
		if (instance == null){
			instance = new POSValidator();
		}
		return instance;
	}
	
	public boolean isValid(Word w){
		if(isValidTag(w.getPartOfSpeech()) && !ignorable(w.getLexeme())){
			return true;
		}
		return false;
	}
	private boolean isValidTag(String s){

		int min = 0;
		int max = tags.length - 1;
		while(min <= max){
			int mid = min + (max - min)/2;
			if(s.compareTo(tags[mid]) < 0){
				max = mid - 1;
			}
			else if (s.compareTo(tags[mid]) > 0){
				min = mid + 1;
			}
			else{
				return true;
			}
		}
		return false;
	}
	
	private boolean ignorable(String s){
		for(int i = 0; i < ignoreList.length; i++){
			if(s.equals(ignoreList[i])){
				return true;
			}
		}
		return false;
	}
}
