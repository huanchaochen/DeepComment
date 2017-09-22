package chc.preprocess;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class StatementList {
	
	List<String> statements;
	
	

	public StatementList() {
		super();
		this.statements = new ArrayList<String>();
	}

	public List<String> getStatements() {
		return statements;
	}

	public void setStatements(List<String> statements) {
		this.statements = statements;
	}

	public void addStatement(String statement) {
		this.statements.add(statement);
	}
	
	public void addStatementList(List<String> statementList) {
		this.statements.addAll(statementList);
	}
	
	public void save() {
		String outputFileName = "statementList.txt";
		File log = new File(outputFileName);
		try {
			FileWriter fw = new FileWriter(log, true);
			BufferedWriter bw = new BufferedWriter(fw);
			
			for(String s : statements) {
				bw.write(s);
				bw.write("\n");
			}
			bw.flush();
			bw.close();
			fw.close();
			
		}catch (Exception e) {
			// TODO: handle exception
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
