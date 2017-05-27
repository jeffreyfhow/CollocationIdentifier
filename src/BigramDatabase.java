import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

//XML parser libraries
import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
 
import org.apache.commons.math3.stat.inference.ChiSquareTest;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.PrintWriter;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;

//Some of parseFile inspired by: http://viralpatel.net/blogs/parsing-reading-xml-file-in-java-xml-reading-java-tutorial/

public class BigramDatabase {

	private int MAX_SENTENCES = 10000;
	private int LIMIT = 100;
	//ordered by name
	public TreeMap<Bigram,Integer> bigrams;
	public TreeMap<Word,Integer> words;
	public int tokenCount;
	//ordered by frequency
	public TreeMap<Integer, ArrayList<Bigram>> bigramsByFreq;
	
	public BigramDatabase(){
		bigrams = new TreeMap<Bigram,Integer>();
		bigramsByFreq = new TreeMap<Integer,ArrayList<Bigram>>();
		words = new TreeMap<Word,Integer>();
		tokenCount = 0;
	}
	
	public void parseFile(String fileName){
		try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            File file = new File(fileName);
            if (file.exists()) {
                Document doc = db.parse(file);
                Element docEle = doc.getDocumentElement();
 
                // Print root element of the document
                System.out.println("Root element of the document: "
                        + docEle.getNodeName());
 
                NodeList sentenceList = docEle.getElementsByTagName("sentence");
                int sentenceCount = sentenceList.getLength();
                // Print total student elements in document
                System.out.println("Total sentences: " + sentenceCount);
                int sentenceBound = Math.min(MAX_SENTENCES, sentenceCount);
                if(sentenceList != null && sentenceCount > 0){
                	for (int i = 0; i < sentenceBound; i++){
                		 Element e = (Element) sentenceList.item(i);
                         
                         NodeList wordList = e.getElementsByTagName("word");
                         int wordCount = wordList.getLength();
                    
                         Word[] wordArray = new Word[wordCount];
                         for(int j = 0; j < wordCount; j++){
                         	Element e1 = (Element) wordList.item(j);
                         	wordArray[j] = new Word(e1.getChildNodes().item(0).getNodeValue(), e1.getAttribute("pos"));
                         }
                         tokenCount += wordArray.length;
                         Word w1;
                         Word w2;
                         insertWord(wordArray[0]);
                         for(int j = 0; j < wordCount-1; j++){
                        	w1 = wordArray[j];
                        	w2 = wordArray[j+1];
                        	insertWord(w2);
                     		insertBigram(new Bigram(w1,w2));
                         }
                	}
                }
                System.out.println("Total word tokens: " + tokenCount);
            }
        } catch (SAXException e) {
            System.out.println(e);
            System.out.println("Terminating program...");
            System.exit(1);
        } catch (IOException e) {
            System.out.println(e);
            System.out.println("Terminating program...");
            System.exit(1);
        } catch (ParserConfigurationException e) {
        	System.out.println(e);
            System.out.println("Terminating program...");
            System.exit(1);
		}
		
		for (Map.Entry<Bigram, Integer> entry : bigrams.entrySet())
		{
			int currFreq = entry.getValue();
			Bigram currBigram = entry.getKey();
			
			ArrayList<Bigram> currList = bigramsByFreq.get(currFreq);
			if(currList == null){
				currList = new ArrayList<Bigram>();
				currList.add(currBigram);
				bigramsByFreq.put(currFreq, currList);
			}
			else{
				currList.add(currBigram);
			}
		}
	}
	
	public void printResults(String fileName){
		//1. File Ops
		PrintWriter pw = null;
		try{
			pw = new PrintWriter(new FileOutputStream(fileName));
		}
		catch(FileNotFoundException e){
			System.out.println("File not found. Terminating program...");
		}
		
		//2. Header
		pw.println("Number\tWord 1\tWord 2\tFrequency\tTScore\tChiScore");
		
		//3. Get highest Frequency
		int counter = 0;
		Entry<Integer,ArrayList<Bigram>> currEntry = bigramsByFreq.lastEntry();
		Bigram currBigram = null;
		while(counter < LIMIT && currEntry != null){
			for(int i = 0; i < currEntry.getValue().size() && counter < LIMIT; i++){
				counter++;
				currBigram = currEntry.getValue().get(i);
				pw.println(counter+"\t"+currBigram.getWord1().getLexeme() + "\t" + 
						   currBigram.getWord2().getLexeme() + "\t" + currEntry.getKey() + "\t" +
						   getTScore(currBigram) + "\t" + getChiScore(currBigram));
			}
			currEntry = bigramsByFreq.lowerEntry(currEntry.getKey());
		}
		pw.close();
	}
	
	public void insertWord(Word w){
		Integer searchValue = words.get(w);
		if(searchValue == null){
			words.put(w,1);
		}
		else{
			words.put(w, searchValue+1);
		}
	}
	
	public void insertBigram(Bigram b){
		if(POSValidator.getInstance().isValid(b.getWord1()) &&
           POSValidator.getInstance().isValid(b.getWord2())){
     		Integer searchValue = bigrams.get(b);
     		if(searchValue == null){
     			bigrams.put(b,1);
     		}
     		else{
     			bigrams.put(b, searchValue+1);
     		}
      	}
	}
	
	public double getTScore(Bigram b){
		
		Word w1 = b.getWord1();
		Word w2 = b.getWord2();
		double bigramFreq = getBigramFreq(b);
		double w1Freq = getWordFreq(w1);
		double w2Freq = getWordFreq(w2);
		double obsMean = bigramFreq/tokenCount;
		double oppObsMean = 1 - obsMean;
		double expMean = (w1Freq * w2Freq)/((double)tokenCount*tokenCount);
		double sampleVar = obsMean * oppObsMean;
		return (obsMean - expMean)/Math.sqrt(sampleVar/tokenCount);
	}
	
	public double getChiScore(Bigram b){
		long[] obs = new long[4];
		long w1Cnt = getWordFreq(b.getWord1());
		long w2Cnt = getWordFreq(b.getWord2());
		long nonW1Cnt = tokenCount - w1Cnt;
		long nonW2Cnt = tokenCount - w2Cnt;
		
		obs[0] = getBigramFreq(b);
		obs[1] = w2Cnt - obs[0];
		obs[2] = w1Cnt - obs[0];
		obs[3] = nonW1Cnt - obs[1];
		
		double[] exp = new double[4];
		exp[0] = ((double)w1Cnt * w2Cnt)/tokenCount;
		exp[1] = ((double)nonW1Cnt * w2Cnt)/tokenCount;
		exp[2] = ((double)w1Cnt * nonW2Cnt)/tokenCount;
		exp[3] = ((double)nonW1Cnt * nonW2Cnt)/tokenCount;
		return ((new ChiSquareTest()).chiSquare(exp,obs));
	}
	
	public Integer getWordFreq(Word w){
		Integer i = words.get(w);
		if(i == null){
			i = 0;
		}
		return i;
	}
	
	public Integer getBigramFreq(Bigram b){
		Integer i = bigrams.get(b);
		if(i == null){
			i = 0;
		}
		return i;
	}
}
