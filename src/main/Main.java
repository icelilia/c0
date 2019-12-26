package main;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import error.*;
import lexicalAnalysis.*;
import syntaxAnalysis.*;

// Author: Andersen.
// Notice: Please use "javac -encoding GBK [-d outputPath] -cp . @javaFile.txt" to compile.
public class Main {

	static String inputPath;
	static String outputPath = "out";
	static FileInputStream fileInputStream;
	static FileOutputStream fileOutputStream;

	static ArrayList<Token> tokenList;
	static ArrayList<Func> funcList;

	public static void main(String[] args) {
		int argsLength = args.length;
		if (argsLength == 0) {
			h();
		} else if (argsLength == 1) {
			if (args[0].contentEquals("-h")) {
				h();
			} else {
				Err.error(ErrEnum.CLI_PARA_ERR);
			}
		} else if (argsLength == 2) {
			inputPath = args[1];
			if (args[0].contentEquals("-s")) {
				s(inputPath, outputPath);
			} else if (args[0].contentEquals("-c")) {
				c(inputPath, outputPath);
			} else {
				Err.error(ErrEnum.CLI_PARA_ERR);
			}
		} else if (argsLength == 4) {
			inputPath = args[1];
			outputPath = args[3];
			if (args[0].contentEquals("-s") && args[2].contentEquals("-o")) {
				s(inputPath, outputPath);
			} else if (args[0].contentEquals("-c") && args[2].contentEquals("-o")) {
				c(inputPath, outputPath);
			} else {
				Err.error(ErrEnum.CLI_PARA_ERR);
			}
		} else {
			Err.error(ErrEnum.CLI_PARA_ERR);
		}
	}

	private static void h() {
		System.out.println("Usage:\n" + "  cc0 [options] input [-o file]\n" + "or \n" + "  cc0 [-h]\n" + "Options:\n"
				+ "  -s        c0 code to .s\n" + "  -c        c0 code to .o\n" + "  -h        for help\n"
				+ "  -o file   output to specified file\n" + "\n" + "default: -h\n" + "default output path: out");
	}

	private static void s(String inputPath, String outputPath) {
		try {
			fileInputStream = new FileInputStream(inputPath);
		} catch (FileNotFoundException e) {
			Err.error(ErrEnum.INPUT_FILE_ERR);
		}
		try {
			fileOutputStream = new FileOutputStream(outputPath);
		} catch (FileNotFoundException e) {
			Err.error(ErrEnum.OUTPUT_FILE_ERR);
		}

		LexicalAnalysis lexicalAnalysis = new LexicalAnalysis(fileInputStream);
		tokenList = lexicalAnalysis.lexicalAnalysis();
		SyntaxAnalysis syntaxAnalysis = new SyntaxAnalysis(tokenList);
		funcList = syntaxAnalysis.syntaxAnalysis().getFuncList();
		viewS();

		try {
			fileInputStream.close();
			fileOutputStream.close();
		} catch (IOException e) {
			System.err.println("FileOutputStream close err");
			System.exit(-1);
		}
	}

	private static void c(String inputPath, String outputPath) {
		try {
			fileInputStream = new FileInputStream(inputPath);
		} catch (FileNotFoundException e) {
			Err.error(ErrEnum.INPUT_FILE_ERR);
		}
		try {
			fileOutputStream = new FileOutputStream(outputPath);
		} catch (FileNotFoundException e) {
			Err.error(ErrEnum.OUTPUT_FILE_ERR);
		}

		LexicalAnalysis lexicalAnalysis = new LexicalAnalysis(fileInputStream);
		tokenList = lexicalAnalysis.lexicalAnalysis();
		SyntaxAnalysis syntaxAnalysis = new SyntaxAnalysis(tokenList);
		funcList = syntaxAnalysis.syntaxAnalysis().getFuncList();
		viewO();
		try {
			fileInputStream.close();
			fileOutputStream.close();
		} catch (IOException e) {
			System.err.println("FileOutputStream close err");
			System.exit(-1);
		}
	}

	private static void viewS() {
		String temp;
		char[] tempArr;
		// .constants
		temp = ".constants:" + "\n";
		tempArr = temp.toCharArray();
		try {
			for (char ch : tempArr) {
				fileOutputStream.write(ch);
			}
		} catch (Exception e) {
			Err.error(ErrEnum.OUTPUT_ERR);
		}
		temp = "";
		Integer funcIndex = 0;
		int i;
		for (i = 1; i < funcList.size(); i++) {
			temp = temp + " " + funcIndex.toString() + " " + "S" + " " + "\"" + funcList.get(i).name + "\"" + "\n";
			funcIndex = funcIndex + 1;
		}
		tempArr = temp.toCharArray();
		try {
			for (char ch : tempArr) {
				fileOutputStream.write(ch);
			}
		} catch (Exception e) {
			Err.error(ErrEnum.OUTPUT_ERR);
		}

		Text text;
		ArrayList<Code> codeList;

		// .start
		temp = ".start:" + "\n";
		tempArr = temp.toCharArray();
		try {
			for (char ch : tempArr) {
				fileOutputStream.write(ch);
			}
		} catch (Exception e) {
			Err.error(ErrEnum.OUTPUT_ERR);
		}
		temp = "";
		text = funcList.get(0).text;
		codeList = text.getCodesList();
		for (Code code : codeList) {
			temp = temp + code.toString();
		}
		tempArr = temp.toCharArray();
		try {
			for (char ch : tempArr) {
				fileOutputStream.write(ch);
			}
		} catch (Exception e) {
			Err.error(ErrEnum.OUTPUT_ERR);
		}

		// .functions
		temp = ".functions:" + "\n";
		tempArr = temp.toCharArray();
		try {
			for (char ch : tempArr) {
				fileOutputStream.write(ch);
			}
		} catch (Exception e) {
			Err.error(ErrEnum.OUTPUT_ERR);
		}
		temp = "";
		funcIndex = 0;
		for (i = 1; i < funcList.size(); i++) {
			temp = temp + " " + funcIndex.toString() + " " + funcIndex.toString() + " "
					+ funcList.get(i).paraNum.toString() + " " + "1" + "\n";
			funcIndex = funcIndex + 1;
		}
		tempArr = temp.toCharArray();
		try {
			for (char ch : tempArr) {
				fileOutputStream.write(ch);
			}
		} catch (Exception e) {
			Err.error(ErrEnum.OUTPUT_ERR);
		}

		// function body
		temp = "";
		funcIndex = 0;
		for (i = 1; i < funcList.size(); i++) {
			codeList = funcList.get(i).text.getCodesList();
			temp = temp + ".F" + funcIndex.toString() + ":" + "\n";
			for (Code code : codeList) {
				temp = temp + code.toString();
			}

			tempArr = temp.toCharArray();
			try {
				for (char ch : tempArr) {
					fileOutputStream.write(ch);
				}
			} catch (Exception e) {
				Err.error(ErrEnum.OUTPUT_ERR);
			}
			funcIndex = funcIndex + 1;
			temp = "";
		}
	}

	private static void viewO() {
		DataOutputStream out = new DataOutputStream(fileOutputStream);
		// magic
		int magic = 0x43303A29;
		try {
			out.writeInt(magic);
		} catch (IOException e) {
			Err.error(ErrEnum.OUTPUT_ERR);
		}
		// version
		int version = 0x00000001;
		try {
			out.writeInt(version);
		} catch (IOException e) {
			Err.error(ErrEnum.OUTPUT_ERR);
		}
		// constants_count
		int constantsCount = (funcList.size() - 1);
		try {
			out.writeShort(constantsCount);
		} catch (IOException e) {
			Err.error(ErrEnum.OUTPUT_ERR);
		}
		// constant_info
		byte type = 0x00; // just String
		int length;
		String name; // just name of function
		try {
			for (int i = 0; i < constantsCount; i++) {
				name = funcList.get(i + 1).name;
				length = name.length();
				// type
				out.writeByte(type);
				// length
				out.writeShort(length);
				// value
				out.write(name.getBytes("ASCII"));
			}
		} catch (IOException e) {
			Err.error(ErrEnum.OUTPUT_ERR);
		}
		// start_code
		int instructionsCount = funcList.get(0).text.getCodesList().size();
		try {
			out.writeShort(instructionsCount);
			ArrayList<Code> codeList = funcList.get(0).text.getCodesList();
			Code code;
			for (int i = 0; i < instructionsCount; i++) {
				code = codeList.get(i);
				if (code.opcode.contentEquals("bipush")) {
					out.write(0x01);
					out.write(Integer.parseInt(code.operands1));
				} else if (code.opcode.contentEquals("ipush")) {
					out.write(0x02);
					out.writeInt(Integer.parseInt(code.operands1));
				} else if (code.opcode.contentEquals("loadc")) {
					out.write(0x09);
					out.writeShort(Integer.parseInt(code.operands1));
				} else if (code.opcode.contentEquals("loada")) {
					out.write(0x0a);
					out.writeShort(Integer.parseInt(code.operands1));
					out.writeInt(Integer.parseInt(code.operands2));
				} else if (code.opcode.contentEquals("iload")) {
					out.write(0x10);
				} else if (code.opcode.contentEquals("istore")) {
					out.write(0x20);
				} else if (code.opcode.contentEquals("iadd")) {
					out.write(0x30);
				} else if (code.opcode.contentEquals("isub")) {
					out.write(0x34);
				} else if (code.opcode.contentEquals("imul")) {
					out.write(0x38);
				} else if (code.opcode.contentEquals("idiv")) {
					out.write(0x3c);
				} else if (code.opcode.contentEquals("jmp")) {
					out.write(0x70);
					out.writeShort(Integer.parseInt(code.operands1));
				} else if (code.opcode.contentEquals("je")) {
					out.write(0x71);
					out.writeShort(Integer.parseInt(code.operands1));
				} else if (code.opcode.contentEquals("jne")) {
					out.write(0x72);
					out.writeShort(Integer.parseInt(code.operands1));
				} else if (code.opcode.contentEquals("jl")) {
					out.write(0x73);
					out.writeShort(Integer.parseInt(code.operands1));
				} else if (code.opcode.contentEquals("jge")) {
					out.write(0x74);
					out.writeShort(Integer.parseInt(code.operands1));
				} else if (code.opcode.contentEquals("jg")) {
					out.write(0x75);
					out.writeShort(Integer.parseInt(code.operands1));
				} else if (code.opcode.contentEquals("jle")) {
					out.write(0x76);
					out.writeShort(Integer.parseInt(code.operands1));
				} else if (code.opcode.contentEquals("call")) {
					out.write(0x80);
					out.writeShort(Integer.parseInt(code.operands1));
				} else if (code.opcode.contentEquals("ret")) {
					out.write(0x88);
				} else if (code.opcode.contentEquals("iret")) {
					out.write(0x89);
				} else if (code.opcode.contentEquals("iprint")) {
					out.write(0xa0);
				} else if (code.opcode.contentEquals("cprint")) {
					out.write(0xa2);
				} else if (code.opcode.contentEquals("printl")) {
					out.write(0xaf);
				} else if (code.opcode.contentEquals("iscan")) {
					out.write(0xb0);
				}
			}
		} catch (IOException e) {
			Err.error(ErrEnum.OUTPUT_ERR);
		}
		// function_count
		try {
			out.writeShort(constantsCount);
		} catch (IOException e) {
			Err.error(ErrEnum.OUTPUT_ERR);
		}
		// function_info
		try {
			for (int i = 1; i < funcList.size(); i++) {
				out.writeShort(i - 1);
				out.writeShort(funcList.get(i).paraNum);
				out.writeShort(1);
				ArrayList<Code> codeList = funcList.get(i).text.getCodesList();
				out.writeShort(codeList.size());
				Code code;
				for (int j = 0; j < codeList.size(); j++) {
					code = codeList.get(j);
					if (code.opcode.contentEquals("bipush")) {
						out.write(0x01);
						out.write(Integer.parseInt(code.operands1));
					} else if (code.opcode.contentEquals("ipush")) {
						out.write(0x02);
						out.writeInt(Integer.parseInt(code.operands1));
					} else if (code.opcode.contentEquals("loadc")) {
						out.write(0x09);
						out.writeShort(Integer.parseInt(code.operands1));
					} else if (code.opcode.contentEquals("loada")) {
						out.write(0x0a);
						out.writeShort(Integer.parseInt(code.operands1));
						out.writeInt(Integer.parseInt(code.operands2));
					} else if (code.opcode.contentEquals("iload")) {
						out.write(0x10);
					} else if (code.opcode.contentEquals("istore")) {
						out.write(0x20);
					} else if (code.opcode.contentEquals("iadd")) {
						out.write(0x30);
					} else if (code.opcode.contentEquals("isub")) {
						out.write(0x34);
					} else if (code.opcode.contentEquals("imul")) {
						out.write(0x38);
					} else if (code.opcode.contentEquals("idiv")) {
						out.write(0x3c);
					} else if (code.opcode.contentEquals("jmp")) {
						out.write(0x70);
						out.writeShort(Integer.parseInt(code.operands1));
					} else if (code.opcode.contentEquals("je")) {
						out.write(0x71);
						out.writeShort(Integer.parseInt(code.operands1));
					} else if (code.opcode.contentEquals("jne")) {
						out.write(0x72);
						out.writeShort(Integer.parseInt(code.operands1));
					} else if (code.opcode.contentEquals("jl")) {
						out.write(0x73);
						out.writeShort(Integer.parseInt(code.operands1));
					} else if (code.opcode.contentEquals("jge")) {
						out.write(0x74);
						out.writeShort(Integer.parseInt(code.operands1));
					} else if (code.opcode.contentEquals("jg")) {
						out.write(0x75);
						out.writeShort(Integer.parseInt(code.operands1));
					} else if (code.opcode.contentEquals("jle")) {
						out.write(0x76);
						out.writeShort(Integer.parseInt(code.operands1));
					} else if (code.opcode.contentEquals("call")) {
						out.write(0x80);
						out.writeShort(Integer.parseInt(code.operands1));
					} else if (code.opcode.contentEquals("ret")) {
						out.write(0x88);
					} else if (code.opcode.contentEquals("iret")) {
						out.write(0x89);
					} else if (code.opcode.contentEquals("iprint")) {
						out.write(0xa0);
					} else if (code.opcode.contentEquals("cprint")) {
						out.write(0xa2);
					} else if (code.opcode.contentEquals("printl")) {
						out.write(0xaf);
					} else if (code.opcode.contentEquals("iscan")) {
						out.write(0xb0);
					}
				}
			}
		} catch (Exception e) {
			Err.error(ErrEnum.OUTPUT_ERR);
		}
		try {
			out.close();
		} catch (IOException e) {
			Err.error(ErrEnum.OUTPUT_ERR);
		}
	}
}
