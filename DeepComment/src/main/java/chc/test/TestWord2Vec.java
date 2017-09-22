package chc.test;

import java.io.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import chc.nlputil.Tokenizer;
import chc.vec.VectorModel;
import chc.vec.Word2Vec;

/**
 * @author siegfang
 */
public class TestWord2Vec {

	public static void readByJava(String textFilePath, String modelFilePath) {

		Word2Vec wv = new Word2Vec.Factory().setMethod(Word2Vec.Method.Skip_Gram).setNumOfThread(1).build();
		
		try {
			FileReader fr = new FileReader(textFilePath);
			BufferedReader br = new BufferedReader(fr);
			int lineCount = 0;
			for (String line = br.readLine(); line != null; line = br.readLine()) {
				wv.readTokens(new Tokenizer(line, " "));
				// System.out.println(line);
				lineCount++;

			}

		} catch (IOException ioe) {
			ioe.printStackTrace();
		}

		wv.training();
		wv.saveModel(new File(modelFilePath));
	}

	public static Map<String, float[]>  testVector(String modelFilePath) {

		VectorModel vm = VectorModel.loadFromFile(modelFilePath);
		Map<String, float[]> wordMap = new HashMap<String, float[]>();
		wordMap = vm.getWordMap();
		//float[] vec1 = wordMap.get("IfStatement");
		//float[] vec2 = wordMap.get("Elseif");
		float[] vec = wordMap.get("first");
		System.out.println(vec.length);
		float dist = 0;
		for(int i = 0; i < vec.length; i++) {
			System.out.print(vec[i] + " ");
			//dist += vec1[i] * vec2[i];
		}
		//System.out.println(dist);
		
//	    Iterator<Map.Entry<String, float[]>> it = wordMap.entrySet().iterator();
//	    while (it.hasNext()) {
//	         Map.Entry<String, float[]> entry = it.next();
//	           if(entry.getKey().equals("if")){
//	        	   System.out.println("if");
//	        	   for(float i:entry.getValue()){
//	        		   System.out.print(i+",");
//	        	   }
//	        	   
//	           }
//	           
//	     }
		return wordMap;
	}

	public static void main(String[] args) {

		String textFilePath = "semantic.dat";
		String modelFilePath = "semantic.nn";
		readByJava(textFilePath, modelFilePath);
		testVector(modelFilePath);
	}
	
//    public static Map<String, float[]> getWordVecMap(){
//    	String textFilePath = "word.dat";
//		String modelFilePath = "word.nn";
//		readByJava(textFilePath, modelFilePath);
//		return testVector(modelFilePath);
//    }
	
	public static Map<String, float[]> getWordVecMap(String textFilePath, String modelFilePath){
		readByJava(textFilePath, modelFilePath);
		VectorModel vm = VectorModel.loadFromFile(modelFilePath);
		Map<String, float[]> wordMap = new HashMap<String, float[]>();
		wordMap = vm.getWordMap();
		return wordMap;
	}

}
