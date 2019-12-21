package main;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import error.*;
import lexicalAnalysis.*;
import syntaxAnalysis.SyntaxAnalysis;

public class Main {

	static String inputPath;
	static String outputPath = "out";
	static FileInputStream fileInputStream;
	static FileOutputStream fileOutputStream;
	static ArrayList<Token> tokenList;

	public static void main(String[] args) {
		int argsLength = args.length;
		// 不提供任何参数时，默认为"-h"
		if (argsLength == 0) {
			h();
		}
		// 一个参数时，只能为"-h"
		else if (argsLength == 1) {
			if (args[0].contentEquals("-h")) {
				h();
			} else {
				Err.error(ErrEnum.PARA_ERR);
			}
		}
		// 两个参数时，outputPath为默认
		else if (argsLength == 2) {
			inputPath = args[1];
			if (args[0].contentEquals("-s")) {
				s(inputPath, outputPath);
			} else if (args[0].contentEquals("-c")) {
				c(inputPath, outputPath);
			} else {
				Err.error(ErrEnum.PARA_ERR);
			}
		}
		// 两个参数时，outputPath为指定路径
		else if (argsLength == 4) {
			inputPath = args[1];
			outputPath = args[3];
			if (args[0].contentEquals("-s") && args[2].contentEquals("-o")) {
				s(inputPath, outputPath);
			} else if (args[0].contentEquals("-c") && args[2].contentEquals("-o")) {
				c(inputPath, outputPath);
			} else {
				Err.error(ErrEnum.PARA_ERR);
			}
		} else {
			Err.error(ErrEnum.PARA_ERR);
		}

	}

	private static void h() {
		System.out.println("Usage:\n" + "  cc0 [options] input [-o file]\n" + "or\n" + "  cc0 [-h]\n" + "options:\n"
				+ "  -s        将输入的c0源代码翻译为文本汇编文件\n" + "  -c        将输入的c0源代码翻译为二进制目标文件\n"
				+ "  -h        显示关于编译器使用的帮助\n" + "  -o file   输出到指定的文件 file\n" + "\n" + "不提供任何参数时，默认为 -h\n"
				+ "提供input不提供-o file时，默认为-o out");
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
		viewTokenList(); // 写出tokenList至output.txt
		SyntaxAnalysis syntaxAnalysis = new SyntaxAnalysis(tokenList);
		syntaxAnalysis.syntaxAnalysis();

		try {
			fileInputStream.close();
			fileOutputStream.close();
		} catch (IOException e) {
			System.err.println("文件流关闭出错");
			System.exit(-1);
		}
	}

	private static void c(String inputPath, String outputPath) {
		System.out.println("将输入的c0源代码翻译为二进制目标文件");
		System.out.println("inputPath：" + inputPath);
		System.out.println("outputPath：" + outputPath);
	}

	private static void viewTokenList() {
		String temp = "Type" + "\t" + "Value" + "\n";
		char[] tempArr = temp.toCharArray();
		try {
			for (char ch : tempArr) {
				fileOutputStream.write(ch);
			}
		} catch (IOException e) {
			Err.error(ErrEnum.OUTPUT_ERR);
		}
		temp = "";
		for (Token token : tokenList) {
			temp = temp + token.toString();
		}
		tempArr = temp.toCharArray();
		try {
			for (char ch : tempArr) {
				fileOutputStream.write(ch);
			}
		} catch (IOException e) {
			Err.error(ErrEnum.OUTPUT_ERR);
		}
	}
}
