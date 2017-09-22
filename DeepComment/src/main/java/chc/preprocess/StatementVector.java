package chc.preprocess;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import chc.vec.WordVec;

public class StatementVector {
	
	public static double[][] sematicVector = new double[1102][100];
	public static double[][] syntaxVector = new double[1102][100];
	public static String statementListFile = "statementList.txt";
	public static String syntaxStatementListFile = "syntaxStatementList.txt";
	public static String semanticDataFile = "semantic.dat";
	public static String semanticModelFile = "semantic.nn";
	public static String syntaxDataFile = "syntax.dat";
	public static String syntaxModelFile = "syntax.nn";
	public static String semanticVectorFile = "semanticVector.txt";
	public static String syntaxVectorFile = "syntaxVector.txt";
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		
		File semanticModel = new File(semanticModelFile);
		if(!semanticModel.exists()) {
			WordVec.generateWordVecModel(semanticDataFile, semanticModelFile);
		}
		File syntaxModel = new File(syntaxModelFile);
		if(!syntaxModel.exists()) {
			WordVec.generateWordVecModel(syntaxDataFile, syntaxModelFile);	
		}
		
		getSemanticVector(statementListFile);
		getSyntaxVector(syntaxStatementListFile);
		
		saveSemanticFile(semanticVectorFile);
		saveSyntaxFile(syntaxVectorFile);
		
	}
	
	public static void getSemanticVector(String statementListFile){
		try {
			FileReader fr = new FileReader(statementListFile);
			BufferedReader br = new BufferedReader(fr);
			int count = 0;
			for (String line = br.readLine(); line != null; line = br.readLine()) {
				
				double[] semanticVec = new double[100];
				
				String[] words = line.split(" ");
				//float[] vec = WordVec.getWordVecMap("semantic.nn").get(word[0]);
				for(String w : words) {
					float[] tempSemanticVec = WordVec.getWordVecMap(semanticModelFile).get(w);
					for(int i = 0; i < tempSemanticVec.length; i++) {
						semanticVec[i] += tempSemanticVec[i];
					}
				}
				for(int i = 0; i < semanticVec.length; i++) {
					//System.out.println(vec[i] + " ");
					sematicVector[count][i] = semanticVec[i];
				}
				count++;
			}
			br.close();
			fr.close();
			
			
			
			for(int i = 0; i < 1102; i++) {
				for(int j = 0; j < 100; j++) {
					System.out.print(sematicVector[i][j] + " ");
				}
				System.out.println();
			}
			
		}catch (Exception e) {
			// TODO: handle exception
		}
		
	}
	
	public static void getSyntaxVector(String syntaxStatementListFile) {
		try {
			FileReader fr = new FileReader(syntaxStatementListFile);
			BufferedReader br = new BufferedReader(fr);
			int count = 0;
			for (String line = br.readLine(); line != null; line = br.readLine()) {
				
				double[] syntaxVec = new double[100];
				
				String[] words = line.split(" ");
				//float[] vec = WordVec.getWordVecMap("semantic.nn").get(word[0]);
				for(String w : words) {
					
					if(w.equals("comment")) {
						for(int i = 0; i < syntaxVec.length; i++) {
							syntaxVec[i] = 1.0;
						}
						break;
					}
					
					float[] tempSyntaxVec = WordVec.getWordVecMap(syntaxModelFile).get(w);
					for(int i = 0; i < tempSyntaxVec.length; i++) {
						syntaxVec[i] += tempSyntaxVec[i];
					}
				}
				for(int i = 0; i < syntaxVec.length; i++) {
					//System.out.println(vec[i] + " ");
					syntaxVector[count][i] = syntaxVec[i];
				}
				count++;
			}
			br.close();
			fr.close();
			
			
			
			for(int i = 0; i < 1102; i++) {
				for(int j = 0; j < 100; j++) {
					System.out.print(syntaxVector[i][j] + " ");
				}
				System.out.println();
			}
			
		}catch (Exception e) {
			// TODO: handle exception
		}
	}

	public static void saveSemanticFile(String semanticFile) {
		File f = new File(semanticFile);
		try {
			FileWriter fw = new FileWriter(f, true);
			BufferedWriter bw = new BufferedWriter(fw);
			
			for(int i = 0; i < 1102; i++) {
				for(int j = 0; j < 99; j++) {
					bw.write(String.valueOf(sematicVector[i][j]) + " ");
				}
				bw.write(String.valueOf(sematicVector[i][99]));
				bw.write("\n");
			}
			
			bw.flush();
			bw.close();
			fw.close();
			
		}catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	public static void saveSyntaxFile(String syntaxFile) {
		File f = new File(syntaxFile);
		try {
			FileWriter fw = new FileWriter(f, true);
			BufferedWriter bw = new BufferedWriter(fw);
			
			for(int i = 0; i < 1102; i++) {
				for(int j = 0; j < 99; j++) {
					bw.write(String.valueOf(syntaxVector[i][j]) + " ");
				}
				bw.write(String.valueOf(syntaxVector[i][99]));
				bw.write("\n");
			}
			
			bw.flush();
			bw.close();
			fw.close();
			
		}catch (Exception e) {
			// TODO: handle exception
		}
	}
	
}
