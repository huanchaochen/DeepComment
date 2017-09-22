package chc.preprocess;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class SyntaxList {
	List<String> syntaxStatements;

	public SyntaxList() {
		super();
		this.syntaxStatements = new ArrayList<String>();
	}

	public List<String> getStatements() {
		return syntaxStatements;
	}

	public void setStatements(List<String> statements) {
		this.syntaxStatements = statements;
	}

	public void addStatement(String statement) {
		this.syntaxStatements.add(statement);
	}

	public void addStatementList(List<String> statementList) {
		this.syntaxStatements.addAll(statementList);
	}

	public void save() {
		String outputFileName = "syntaxStatementList.txt";
		File log = new File(outputFileName);
		try {
			FileWriter fw = new FileWriter(log, true);
			BufferedWriter bw = new BufferedWriter(fw);

			for (String s : syntaxStatements) {
				bw.write(s);
				bw.write("\n");
			}
			bw.flush();
			bw.close();
			fw.close();

		} catch (Exception e) {
			// TODO: handle exception
		}
	}
}
