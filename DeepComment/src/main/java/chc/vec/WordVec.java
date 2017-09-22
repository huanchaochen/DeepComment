package chc.vec;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import chc.nlputil.Tokenizer;

public class WordVec {
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
	
	public static Map<String, float[]> getWordVecMap(String modelFilePath){
		//readByJava(textFilePath, modelFilePath);
		VectorModel vm = VectorModel.loadFromFile(modelFilePath);
		Map<String, float[]> wordMap = new HashMap<String, float[]>();
		wordMap = vm.getWordMap();
		return wordMap;
	}
	
	public static void generateWordVecModel(String textFilePath, String modelFilePath) {
		readByJava(textFilePath, modelFilePath);
	}
}
