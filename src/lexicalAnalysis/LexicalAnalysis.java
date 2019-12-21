package lexicalAnalysis;

import java.io.FileInputStream;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.ArrayList;

import error.*;

public class LexicalAnalysis {
	BufferedInputStream inputStream;

	char ch;
	char temp;
	String tokenString;
	int num;
	Token token;

	int row = 1;
	int col = 1;

	boolean isComment;

	DFAState curruntState;

	ArrayList<Token> tokenList = new ArrayList<Token>(512);

	public LexicalAnalysis(FileInputStream fileInputStream) {
		this.inputStream = new BufferedInputStream(fileInputStream);
	}

	public ArrayList<Token> lexicalAnalysis() {
		// 不为0表示还没到EOF
		while (true) {
			curruntState = DFAState.INIT_STATE;
			if (getToken() == 0) {
				tokenList.add(token);
				break;
			} else {
				tokenList.add(token);
			}
		}
		return tokenList;
	}

	private char getchar() {
		char temp = '\0';
		try {
			inputStream.mark(1);
			temp = (char) inputStream.read();
		} catch (IOException e) {
			System.err.println("输入流读取错误");
			System.exit(-1);
		}
		return temp;
	}

	private void rechar() {
		try {
			inputStream.reset();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		}
	}

	private boolean isSpace(char c) {
		return c == ' ';
	}

	private boolean isCR(char c) {
		return c == '\r';
	}

	private boolean isLF(char c) {
		return c == '\n';
	}

	private boolean isTab(char c) {
		return c == '\t';
	}

	private boolean isEOF(char c) {
		return c == (char) -1;
	}

	private boolean isLetter(char c) {
		return Character.isLetter(c);
	}

	private boolean isDigit(char c) {
		return Character.isDigit(c);
	}

	private boolean isHex(char c) {
		// A-F，a-f，0-9
		return (65 <= c && c <= 70) || (97 <= c && c <= 102) || isDigit(c);
	}

	// int返回值，-1表示出错，0表示到达EOF，1表示正常读取
	// 实际情况用不到-1，因为一旦出错就直接调用Err.error进行错误处理，并且执行System.exit(-1)
	private int getToken() {
		// 初始化
		tokenString = "";
		while (true) {
			ch = getchar();
			switch (curruntState) {
			case INIT_STATE: {
				if (isEOF(ch)) {
					return 0;
				}
				// 空白字符直接跳过
				if (isSpace(ch) || isTab(ch) || isLF(ch) || isCR(ch)) {
					break;
				}
				// 为数字
				else if (isDigit(ch)) {
					// 不是0开头，十进制整数
					if (ch != '0') {
						curruntState = DFAState.DEC_INT_STATE;
					}
					// 0开头，十六进制整数或十进制0
					else {
						ch = getchar();
						// 十六进制整数
						if (ch == 'x' || ch == 'X') {
							// 向tokenString里插入"0x"
							// tokenString = tokenString + "0x";
							// Integer.valueOf()方法中输入的字符串不要求包含"0x"
							// 这里再预读一个字符保证"0x"后面跟有数字
							ch = getchar();
							if (isHex(ch)) {
								curruntState = DFAState.HEX_INT_STATE;
							}
							// 词法中要求至少有一位数字，没有数字则报错
							else {
								Err.error(ErrEnum.INPUT_ERR);
							}
						}
						// 视作单独的一个十进制0
						else {
							rechar();
							tokenString = tokenString + '0';
							token = new Token(TokenType.DEC_INT, tokenString);
							return isEOF(ch) ? 0 : 1;
						}
					}
				}
				// 为英文字母
				else if (isLetter(ch)) {
					curruntState = DFAState.ID_STATE;
				}
				// 其他符号
				else {
					switch (ch) {
					case '+':
						curruntState = DFAState.PLUS_STATE;
						break;
					case '-':
						curruntState = DFAState.MINUS_STATE;
						break;
					case '*':
						curruntState = DFAState.MUL_STATE;
						break;
					case '/':
						curruntState = DFAState.DIV_STATE;
						break;
					case '=':
						// 预读
						temp = getchar();
						// 双等号
						if (temp == '=') {
							// 预先添加第一个字符
							tokenString = tokenString + ch;
							ch = temp;
							curruntState = DFAState.EE_STATE;
						}
						// 单等号，回退
						else {
							rechar();
							curruntState = DFAState.E_STATE;
						}
						break;
					case '<':
						// 预读
						temp = getchar();
						// 小于等于号
						if (temp == '=') {
							tokenString = tokenString + ch;
							ch = temp;
							curruntState = DFAState.LE_STATE;
							break;
						}
						// 小于号，回退
						else {
							rechar();
							curruntState = DFAState.L_STATE;
							break;
						}
					case '>':
						// 预读
						temp = getchar();
						// 大于等于号
						if (temp == '=') {
							tokenString = tokenString + ch;
							ch = temp;
							curruntState = DFAState.GE_STATE;
							break;
						}
						// 大于号，回退
						else {
							rechar();
							curruntState = DFAState.G_STATE;
							break;
						}
					case '!':
						// 预读
						temp = getchar();
						// 不等于号
						if (temp == '=') {
							tokenString = tokenString + ch;
							ch = temp;
							curruntState = DFAState.UE_STATE;
							break;
						}
						// 异常字符组合，报错
						else {
							Err.error(ErrEnum.INPUT_ERR);
							break;
						}
					case ',':
						curruntState = DFAState.COMMA_STATE;
						break;
					case ';':
						curruntState = DFAState.SEM_STATE;
						break;
					case '(':
						curruntState = DFAState.LSB_STATE;
						break;
					case ')':
						curruntState = DFAState.RSB_STATE;
						break;
					case '{':
						curruntState = DFAState.LLB_STATE;
						break;
					case '}':
						curruntState = DFAState.RLB_STATE;
						break;
					// 其余非法字符
					default:
						Err.error(ErrEnum.INPUT_ERR);
						break;
					}
				}
				// 状态发生改变，添加字符
				if (curruntState != DFAState.INIT_STATE) {
					tokenString = tokenString + ch;
				}
				break;
			}
			// miniplc0中，在此状态下读到字母时选择跳转到标识符状态
			// 这里选择遵从词法分析最大吞噬的原则，并不跳转
			case DEC_INT_STATE: {
				if (isEOF(ch)) {
					try {
						num = Integer.parseInt(tokenString);
					} catch (NumberFormatException e) {
						Err.error(ErrEnum.INT_OF_ERR);
						break;
					}
					token = new Token(TokenType.DEC_INT, tokenString);
					return 0;
				}
				// 读到数字则直接合并
				if (isDigit(ch)) {
					tokenString = tokenString + ch;
				}
				// 其余情况，回退字符，分析
				else {
					rechar();
					try {
						num = Integer.parseInt(tokenString);
					} catch (NumberFormatException e) {
						Err.error(ErrEnum.INT_OF_ERR);
						break;
					}
					token = new Token(TokenType.DEC_INT, tokenString);
					return 1;
				}
				break;
			}
			// 十六进制基本同十进制
			case HEX_INT_STATE: {
				if (isEOF(ch)) {
					try {
						// 十六进制的直接转换
						num = Integer.valueOf(tokenString, 16);
					} catch (NumberFormatException e) {
						Err.error(ErrEnum.INT_OF_ERR);
						break;
					}
					// 这里选择直接将十六进制转化成十进制
					token = new Token(TokenType.DEC_INT, new Integer(num).toString());
					return 0;
				}
				// 读到十六进制合法字符则直接合并
				if (isHex(ch)) {
					tokenString = tokenString + ch;
				}
				// 其余情况，回退字符，分析
				else {
					rechar();
					try {
						// 十六进制的直接转换
						num = Integer.valueOf(tokenString, 16);
					} catch (NumberFormatException e) {
						Err.error(ErrEnum.INT_OF_ERR);
						break;
					}
					// 这里选择直接将十六进制转化成十进制
					token = new Token(TokenType.DEC_INT, new Integer(num).toString());
					return 1;
				}
				break;
			}
			case ID_STATE: {
				if (isEOF(ch)) {
					// 比较保留字
					if (tokenString.contentEquals("const")) {
						token = new Token(TokenType.CONST, tokenString);
					} else if (tokenString.contentEquals("void")) {
						token = new Token(TokenType.VOID, tokenString);
					} else if (tokenString.contentEquals("int")) {
						token = new Token(TokenType.INT, tokenString);
					} else if (tokenString.contentEquals("char")) {
						token = new Token(TokenType.CHAR, tokenString);
					} else if (tokenString.contentEquals("double")) {
						token = new Token(TokenType.DOUBLE, tokenString);
					} else if (tokenString.contentEquals("struct")) {
						token = new Token(TokenType.STRUCT, tokenString);
					} else if (tokenString.contentEquals("if")) {
						token = new Token(TokenType.IF, tokenString);
					} else if (tokenString.contentEquals("else")) {
						token = new Token(TokenType.ELSE, tokenString);
					} else if (tokenString.contentEquals("switch")) {
						token = new Token(TokenType.SWITCH, tokenString);
					} else if (tokenString.contentEquals("case")) {
						token = new Token(TokenType.CASE, tokenString);
					} else if (tokenString.contentEquals("default")) {
						token = new Token(TokenType.DEFAULT, tokenString);
					} else if (tokenString.contentEquals("while")) {
						token = new Token(TokenType.WHILE, tokenString);
					} else if (tokenString.contentEquals("for")) {
						token = new Token(TokenType.FOR, tokenString);
					} else if (tokenString.contentEquals("do")) {
						token = new Token(TokenType.DO, tokenString);
					} else if (tokenString.contentEquals("return")) {
						token = new Token(TokenType.RETURN, tokenString);
					} else if (tokenString.contentEquals("break")) {
						token = new Token(TokenType.BREAK, tokenString);
					} else if (tokenString.contentEquals("continue")) {
						token = new Token(TokenType.CONTINUE, tokenString);
					} else if (tokenString.contentEquals("print")) {
						token = new Token(TokenType.PRINT, tokenString);
					} else if (tokenString.contentEquals("scan")) {
						token = new Token(TokenType.SCAN, tokenString);
					} else {
						token = new Token(TokenType.ID, tokenString);
					}
					return 0;
				}
				if (isDigit(ch) || isLetter(ch)) {
					tokenString = tokenString + ch;
				} else {
					rechar();
					if (tokenString.contentEquals("const")) {
						token = new Token(TokenType.CONST, tokenString);
					} else if (tokenString.contentEquals("void")) {
						token = new Token(TokenType.VOID, tokenString);
					} else if (tokenString.contentEquals("int")) {
						token = new Token(TokenType.INT, tokenString);
					} else if (tokenString.contentEquals("char")) {
						token = new Token(TokenType.CHAR, tokenString);
					} else if (tokenString.contentEquals("double")) {
						token = new Token(TokenType.DOUBLE, tokenString);
					} else if (tokenString.contentEquals("struct")) {
						token = new Token(TokenType.STRUCT, tokenString);
					} else if (tokenString.contentEquals("if")) {
						token = new Token(TokenType.IF, tokenString);
					} else if (tokenString.contentEquals("else")) {
						token = new Token(TokenType.ELSE, tokenString);
					} else if (tokenString.contentEquals("switch")) {
						token = new Token(TokenType.SWITCH, tokenString);
					} else if (tokenString.contentEquals("case")) {
						token = new Token(TokenType.CASE, tokenString);
					} else if (tokenString.contentEquals("default")) {
						token = new Token(TokenType.DEFAULT, tokenString);
					} else if (tokenString.contentEquals("while")) {
						token = new Token(TokenType.WHILE, tokenString);
					} else if (tokenString.contentEquals("for")) {
						token = new Token(TokenType.FOR, tokenString);
					} else if (tokenString.contentEquals("do")) {
						token = new Token(TokenType.DO, tokenString);
					} else if (tokenString.contentEquals("return")) {
						token = new Token(TokenType.RETURN, tokenString);
					} else if (tokenString.contentEquals("break")) {
						token = new Token(TokenType.BREAK, tokenString);
					} else if (tokenString.contentEquals("continue")) {
						token = new Token(TokenType.CONTINUE, tokenString);
					} else if (tokenString.contentEquals("print")) {
						token = new Token(TokenType.PRINT, tokenString);
					} else if (tokenString.contentEquals("scan")) {
						token = new Token(TokenType.SCAN, tokenString);
					} else {
						token = new Token(TokenType.ID, tokenString);
					}
					return 1;
				}
				break;
			}
			case PLUS_STATE: {
				rechar();
				token = new Token(TokenType.PLUS, tokenString);
				return isEOF(ch) ? 0 : 1;
			}
			case MINUS_STATE: {
				rechar();
				token = new Token(TokenType.MINUS, tokenString);
				return isEOF(ch) ? 0 : 1;
			}
			case MUL_STATE: {
				rechar();
				token = new Token(TokenType.MUL, tokenString);
				return isEOF(ch) ? 0 : 1;
			}
			case DIV_STATE: {
				rechar();
				token = new Token(TokenType.DIV, tokenString);
				return isEOF(ch) ? 0 : 1;
			}
			case E_STATE: {
				rechar();
				token = new Token(TokenType.E, tokenString);
				return isEOF(ch) ? 0 : 1;
			}
			case L_STATE: {
				rechar();
				token = new Token(TokenType.L, tokenString);
				return isEOF(ch) ? 0 : 1;
			}
			case LE_STATE: {
				rechar();
				token = new Token(TokenType.LE, tokenString);
				return isEOF(ch) ? 0 : 1;
			}
			case G_STATE: {
				rechar();
				token = new Token(TokenType.G, tokenString);
				return isEOF(ch) ? 0 : 1;
			}
			case GE_STATE: {
				rechar();
				token = new Token(TokenType.GE, tokenString);
				return isEOF(ch) ? 0 : 1;
			}
			case UE_STATE: {
				rechar();
				token = new Token(TokenType.UE, tokenString);
				return isEOF(ch) ? 0 : 1;
			}
			case EE_STATE: {
				rechar();
				token = new Token(TokenType.EE, tokenString);
				return isEOF(ch) ? 0 : 1;
			}
			case COMMA_STATE: {
				rechar();
				token = new Token(TokenType.COMMA, tokenString);
				return isEOF(ch) ? 0 : 1;
			}
			case SEM_STATE: {
				rechar();
				token = new Token(TokenType.SEM, tokenString);
				return isEOF(ch) ? 0 : 1;
			}
			case LSB_STATE: {
				rechar();
				token = new Token(TokenType.LSB, tokenString);
				return isEOF(ch) ? 0 : 1;
			}
			case RSB_STATE: {
				rechar();
				token = new Token(TokenType.RSB, tokenString);
				return isEOF(ch) ? 0 : 1;
			}
			case LLB_STATE: {
				rechar();
				token = new Token(TokenType.LLB, tokenString);
				return isEOF(ch) ? 0 : 1;
			}
			case RLB_STATE: {
				rechar();
				token = new Token(TokenType.RLB, tokenString);
				return isEOF(ch) ? 0 : 1;
			}
			default: {
				Err.error(ErrEnum.INPUT_ERR);
				break;
			}
			}
		}
	}
}