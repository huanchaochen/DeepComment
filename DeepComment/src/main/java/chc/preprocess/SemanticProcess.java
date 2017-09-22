package chc.preprocess;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import chc.bean.ClassMessage;
import chc.bean.CommentEntry;
import chc.bean.EndLineVerify;
import chc.bean.Line;
import chc.bean.Token;
import chc.repository.ClassMessageRepository;
import chc.repository.CommentRepository;
import chc.repository.EndLineVerifyRepository;
import chc.repository.RepositoryFactory;


public class SemanticProcess {
	public static void main(String[] args) throws IOException {

		String outputFileName = "semantic.dat";

		EndLineVerifyRepository endLineVerifyRepo = RepositoryFactory.getEndLineVerifyRepository();
		ClassMessageRepository classRepo = RepositoryFactory.getClassRepository();
		CommentRepository commentRepo = RepositoryFactory.getCommentRepository();

		// List<List<Integer>> vectorList = new ArrayList<List<Integer>>();

		List<EndLineVerify> endLineVerifyList = endLineVerifyRepo.findAll();
		int count = 0;
		System.out.println(endLineVerifyList.size());
		endLineVerifyList = sameVerifyFilter(endLineVerifyList);
		System.out.println(endLineVerifyList.size());

		Set<Integer> commentIDSet = new HashSet<Integer>();
		StatementList statementIndexList = new StatementList();// 相当于wordlist
		SyntaxList syntaxList = new SyntaxList();// 相当于wordlist
		for (EndLineVerify endLineVerify : endLineVerifyList) {
			count++;
			System.out.println(count);
			int commentID = endLineVerify.getCommentID();

			if (commentIDSet.contains(commentID)) {
				continue;
			} else {
				commentIDSet.add(commentID);

			}
			CommentEntry comment = commentRepo.findASingleByCommentID(commentID);
			// if(comment.getProject().equals("jgit")) {
			// continue;
			// }
			if (endLineVerify.getEndLine() <= comment.getOld_comment_endLine()) {
				continue;
			}
			ClassMessage clazz = classRepo.findASingleByProjectAndCommitIDAndClassName(comment.getProject(),
					comment.getCommitID(), comment.getClassName());

			//List<Token> tokenList = clazz.getOldTokenList();

			List<String> codeList = new ArrayList<String>();
			List<Line> codes = clazz.getOldCode();
			String source = "";
			for (Line line : codes) {
				codeList.add(line.getLine());
				// System.out.println(line.getLine());
				source += line.getLine() + "\n";
			}
			System.out.println("CommentID: " + commentID);
			
			ASTParser parser = ASTParser.newParser(AST.JLS8);
			parser.setSource(source.toCharArray());
			CompilationUnit unit = (CompilationUnit) parser.createAST(null);
			Tokenizer2 tokenizer = Parser2.parseAST2Tokens(unit);
			List<Token> tokenList = tokenizer.getTokenList();	
			TypeDeclaration type = (TypeDeclaration) unit.types().get(0);

			MethodDeclaration[] methods = type.getMethods();

			List<Statement> statementList = new ArrayList<Statement>();
			for (MethodDeclaration method : methods) {
				int methodStartLine = unit.getLineNumber(method.getStartPosition());
				int methodEndLine = unit.getLineNumber(method.getStartPosition() + method.getLength() - 1);

				if (methodStartLine <= comment.getOld_comment_startLine()
						&& methodEndLine >= comment.getOld_comment_endLine()) {
					List<Statement> statements = (List<Statement>) method.getBody().statements();
					for (Statement statement : statements) {
						int statementStartLine = unit.getLineNumber(statement.getStartPosition());
						int statementEndLine = unit
								.getLineNumber(statement.getStartPosition() + statement.getLength() - 1);
						if (statementStartLine >= comment.getOld_scope_startLine()
								&& statementEndLine <= comment.getOld_scope_endLine()) {
							statementList.add(statement);
							// System.out.println(statement.toString());
						}
					}
				}
			}

			String commentString = "";
			for (String str : comment.getOldComment()) {
				commentString += str + " ";
			}
			if (statementList.size() != 0) {
				List<String> tempCommentList = WordSpliter.split(commentString, true);

				// 写入文件
				File log = new File(outputFileName);
				try {
					FileWriter fw = new FileWriter(log, true);
					BufferedWriter bw = new BufferedWriter(fw);
					String s = "";
					for (String commentWord : tempCommentList) {
						s += commentWord + " ";
						bw.write(commentWord);
						bw.write(" ");
					}
					statementIndexList.addStatement(s);
					syntaxList.addStatement("comment");
					System.out.println(statementList.size());
					for (int i = 0; i < statementList.size(); i++) {
						int statementStartline = unit.getLineNumber(statementList.get(i).getStartPosition());
						int statementEndLine = unit.getLineNumber(
								statementList.get(i).getStartPosition() + statementList.get(i).getLength() - 1);
						// System.out.println(line);
						String str = "";
						String str2 = "";
						for (int line = statementStartline; line <= statementEndLine; line++) {

							if (line <= endLineVerify.getEndLine()) {
								System.out.println(line + codeList.get(line - 1).toString());
								List<String> tempCodeList = WordSpliter.split(codeList.get(line - 1), false);

								for (String codeWord : tempCodeList) {
									str += codeWord + " ";
									bw.write(codeWord);
									bw.write(" ");
								}
								if(tempCodeList.size() != 0) {
									for(Token t : tokenList) {
										if(t.getStartLine() == line && t.getEndLine() == line) {
											System.out.println(line + t.getTokenName());
											str2 += t.getTokenName() + " ";
										}
									}
								}
							}		
						}
						
						
						if (!str.equals("")) {
							statementIndexList.addStatement(str);
						}
						if(!str2.equals("")) {
							syntaxList.addStatement(str2);
						}
					}
					System.out.println("verify:" + endLineVerify.getEndLine());
					bw.write("\n");
					boolean flag = false;
					for (int i = 0; i < statementList.size(); i++) {
						int statementStartline = unit.getLineNumber(statementList.get(i).getStartPosition());
						int statementEndLine = unit.getLineNumber(
								statementList.get(i).getStartPosition() + statementList.get(i).getLength() - 1);
						// System.out.println(line);
						String str = "";
						String str2 = "";
						for (int line = statementStartline; line <= statementEndLine; line++) {
							if (line > endLineVerify.getEndLine()) {
								flag = true;
								System.out.println(line + codeList.get(line - 1).toString());
								List<String> tempCodeList = WordSpliter.split(codeList.get(line - 1), false);
								for (String codeWord : tempCodeList) {
									str += codeWord + " ";
									bw.write(codeWord);
									bw.write(" ");
								}
								if(tempCodeList.size() != 0) {
									for(Token t : tokenList) {
										
										if(t.getStartLine() == line && t.getEndLine() == line) {
											System.out.println(line + t.getTokenName());
											str2 += t.getTokenName() + " ";
										}
									}
								}
							}
						}
						if (!str.equals("")) {
							statementIndexList.addStatement(str);
						}
						if(!str2.equals("")) {
							syntaxList.addStatement(str2);
						}
					}
					if(flag) {
						bw.write("\n");
					}
					bw.flush();
					bw.close();
					fw.close();
				} catch (IOException e1) {

					e1.printStackTrace();
				}
			}
		}

		statementIndexList.save();
		syntaxList.save();
	}

	private static List<EndLineVerify> sameVerifyFilter(List<EndLineVerify> verifyList) {
		Map<Integer, List<Integer>> verifyMap = new HashMap<Integer, List<Integer>>();
		for (int i = 0; i < verifyList.size(); i++) {
			if (verifyMap.containsKey(verifyList.get(i).getCommentID())) {
				verifyMap.get(verifyList.get(i).getCommentID()).add(i);
			} else {
				List<Integer> list = new ArrayList<Integer>();
				list.add(i);
				verifyMap.put(verifyList.get(i).getCommentID(), list);
			}
		}
		System.out.println(verifyMap.keySet().size());
		List<EndLineVerify> resultList = new ArrayList<EndLineVerify>();
		for (Map.Entry<Integer, List<Integer>> entry : verifyMap.entrySet()) {
			List<Integer> commentIdList = entry.getValue();
			boolean flag = true;
			for (int i = 0; i < commentIdList.size() - 1; i++) {
				if (verifyList.get(commentIdList.get(i)).getEndLine() != verifyList.get(commentIdList.get(i + 1))
						.getEndLine()) {
					flag = false;
					break;
				}
			}
			if (flag) {
				resultList.add(verifyList.get(commentIdList.get(0)));
			}
		}
		return resultList;
	}
}
