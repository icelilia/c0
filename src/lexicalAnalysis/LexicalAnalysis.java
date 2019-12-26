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

	boolean isComment;

	DFAState curruntState;

	ArrayList<Token> tokenList = new ArrayList<Token>();

	public LexicalAnalysis(FileInputStream fileInputStream) {
		this.inputStream = new BufferedInputStream(fileInputStream);
	}

	public ArrayList<Token> lexicalAnalysis() {
		while (true) {
			curruntState = DFAState.INIT_STATE;
			token = null;
			if (getToken() == 0) {
				if (token != null) {
					tokenList.add(token);
				}
				break;
			} else {
				if (token != null) {
					tokenList.add(token);
				}
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
			System.err.println("FileStream err");
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
		return (65 <= c && c <= 70) || (97 <= c && c <= 102) || isDigit(c);
	}

	private int getToken() {
		tokenString = "";
		while (true) {
			ch = getchar();
			switch (curruntState) {
			case INIT_STATE: {
				if (isEOF(ch)) {
					return 0;
				}
				if (isSpace(ch) || isTab(ch) || isLF(ch) || isCR(ch)) {
					break;
				} else if (isDigit(ch)) {
					if (ch != '0') {
						curruntState = DFAState.DEC_INT_STATE;
					} else {
						ch = getchar();
						if (ch == 'x' || ch == 'X') {
							ch = getchar();
							if (isHex(ch)) {
								curruntState = DFAState.HEX_INT_STATE;
							} else {
								Err.error(ErrEnum.HEX_INT_ERR);
							}
						} else {
							rechar();
							tokenString = tokenString + '0';
							token = new Token(TokenType.DEC_INT, tokenString);
							return isEOF(ch) ? 0 : 1;
						}
					}
				} else if (isLetter(ch)) {
					curruntState = DFAState.ID_STATE;
				} else {
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
						temp = getchar();
						if (temp == '=') {
							tokenString = tokenString + ch;
							ch = temp;
							curruntState = DFAState.EE_STATE;
						} else {
							rechar();
							curruntState = DFAState.E_STATE;
						}
						break;
					case '<':
						temp = getchar();
						if (temp == '=') {
							tokenString = tokenString + ch;
							ch = temp;
							curruntState = DFAState.LE_STATE;
							break;
						} else {
							rechar();
							curruntState = DFAState.L_STATE;
							break;
						}
					case '>':
						temp = getchar();
						if (temp == '=') {
							tokenString = tokenString + ch;
							ch = temp;
							curruntState = DFAState.GE_STATE;
							break;
						} else {
							rechar();
							curruntState = DFAState.G_STATE;
							break;
						}
					case '!':
						temp = getchar();
						if (temp == '=') {
							tokenString = tokenString + ch;
							ch = temp;
							curruntState = DFAState.UE_STATE;
							break;
						} else {
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
					default:
						Err.error(ErrEnum.INPUT_ERR);
						break;
					}
				}
				if (curruntState != DFAState.INIT_STATE) {
					tokenString = tokenString + ch;
				}
				break;
			}
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
				if (isDigit(ch)) {
					tokenString = tokenString + ch;
				} else {
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
			case HEX_INT_STATE: {
				if (isEOF(ch)) {
					try {
						num = Integer.valueOf(tokenString, 16);
					} catch (NumberFormatException e) {
						Err.error(ErrEnum.INT_OF_ERR);
						break;
					}
					token = new Token(TokenType.DEC_INT, new Integer(num).toString());
					return 0;
				}
				if (isHex(ch)) {
					tokenString = tokenString + ch;
				} else {
					rechar();
					try {
						num = Integer.valueOf(tokenString, 16);
					} catch (NumberFormatException e) {
						Err.error(ErrEnum.INT_OF_ERR);
						break;
					}
					token = new Token(TokenType.DEC_INT, new Integer(num).toString());
					return 1;
				}
				break;
			}
			case ID_STATE: {
				if (isEOF(ch)) {
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
				token = new Token(TokenType.PLUS, tokenString);
				if ((isSpace(ch) || isTab(ch) || isLF(ch) || isCR(ch))) {
					return 1;
				} else if (isEOF(ch)) {
					return 0;
				}
				rechar();
				return 1;
			}
			case MINUS_STATE: {
				token = new Token(TokenType.MINUS, tokenString);
				if ((isSpace(ch) || isTab(ch) || isLF(ch) || isCR(ch))) {
					return 1;
				} else if (isEOF(ch)) {
					return 0;
				}
				rechar();
				return 1;
			}
			case MUL_STATE: {
				token = new Token(TokenType.MUL, tokenString);
				if ((isSpace(ch) || isTab(ch) || isLF(ch) || isCR(ch))) {
					return 1;
				} else if (isEOF(ch)) {
					return 0;
				}
				rechar();
				return 1;
			}
			case DIV_STATE: {
				// multi line
				if (ch == '*') {
					while (true) {
						ch = getchar();
						if (isEOF(ch)) {
							Err.error(ErrEnum.EOF_ERR);
						}
						if (ch == '*') {
							ch = getchar();
							if (isEOF(ch)) {
								Err.error(ErrEnum.EOF_ERR);
							}
							if (ch == '/') {
								isComment = true;
								tokenString = "";
								curruntState = DFAState.INIT_STATE;
								break;
							}
							rechar();
						}
					}
				}
				// single line
				else if (ch == '/') {
					while (true) {
						ch = getchar();
						if (isEOF(ch)) {
							isComment = true;
							tokenString = "";
							curruntState = DFAState.INIT_STATE;
							break;
						}
						if (isCR(ch) || isLF(ch)) {
							isComment = true;
							tokenString = "";
							curruntState = DFAState.INIT_STATE;
							break;
						}
					}
				}
				if (isComment == true) {
					isComment = false;
					break;
				}
				token = new Token(TokenType.DIV, tokenString);
				if ((isSpace(ch) || isTab(ch) || isLF(ch) || isCR(ch))) {
					return 1;
				} else if (isEOF(ch)) {
					return 0;
				}
				rechar();
				return 1;
			}
			case E_STATE: {
				token = new Token(TokenType.E, tokenString);
				if ((isSpace(ch) || isTab(ch) || isLF(ch) || isCR(ch))) {
					return 1;
				} else if (isEOF(ch)) {
					return 0;
				}
				rechar();
				return 1;
			}
			case L_STATE: {
				token = new Token(TokenType.L, tokenString);
				if ((isSpace(ch) || isTab(ch) || isLF(ch) || isCR(ch))) {
					return 1;
				} else if (isEOF(ch)) {
					return 0;
				}
				rechar();
				return 1;
			}
			case LE_STATE: {
				token = new Token(TokenType.LE, tokenString);
				if ((isSpace(ch) || isTab(ch) || isLF(ch) || isCR(ch))) {
					return 1;
				} else if (isEOF(ch)) {
					return 0;
				}
				rechar();
				return 1;
			}
			case G_STATE: {
				token = new Token(TokenType.G, tokenString);
				if ((isSpace(ch) || isTab(ch) || isLF(ch) || isCR(ch))) {
					return 1;
				} else if (isEOF(ch)) {
					return 0;
				}
				rechar();
				return 1;
			}
			case GE_STATE: {
				token = new Token(TokenType.GE, tokenString);
				if ((isSpace(ch) || isTab(ch) || isLF(ch) || isCR(ch))) {
					return 1;
				} else if (isEOF(ch)) {
					return 0;
				}
				rechar();
				return 1;
			}
			case UE_STATE: {
				token = new Token(TokenType.UE, tokenString);
				if ((isSpace(ch) || isTab(ch) || isLF(ch) || isCR(ch))) {
					return 1;
				} else if (isEOF(ch)) {
					return 0;
				}
				rechar();
				return 1;
			}
			case EE_STATE: {
				token = new Token(TokenType.EE, tokenString);
				if ((isSpace(ch) || isTab(ch) || isLF(ch) || isCR(ch))) {
					return 1;
				} else if (isEOF(ch)) {
					return 0;
				}
				rechar();
				return 1;
			}
			case COMMA_STATE: {
				token = new Token(TokenType.COMMA, tokenString);
				if ((isSpace(ch) || isTab(ch) || isLF(ch) || isCR(ch))) {
					return 1;
				} else if (isEOF(ch)) {
					return 0;
				}
				rechar();
				return 1;
			}
			case SEM_STATE: {
				token = new Token(TokenType.SEM, tokenString);
				if ((isSpace(ch) || isTab(ch) || isLF(ch) || isCR(ch))) {
					return 1;
				} else if (isEOF(ch)) {
					return 0;
				}
				rechar();
				return 1;
			}
			case LSB_STATE: {
				token = new Token(TokenType.LSB, tokenString);
				if ((isSpace(ch) || isTab(ch) || isLF(ch) || isCR(ch))) {
					return 1;
				} else if (isEOF(ch)) {
					return 0;
				}
				rechar();
				return 1;
			}
			case RSB_STATE: {
				token = new Token(TokenType.RSB, tokenString);
				if ((isSpace(ch) || isTab(ch) || isLF(ch) || isCR(ch))) {
					return 1;
				} else if (isEOF(ch)) {
					return 0;
				}
				rechar();
				return 1;
			}
			case LLB_STATE: {
				token = new Token(TokenType.LLB, tokenString);
				if ((isSpace(ch) || isTab(ch) || isLF(ch) || isCR(ch))) {
					return 1;
				} else if (isEOF(ch)) {
					return 0;
				}
				rechar();
				return 1;
			}
			case RLB_STATE: {
				token = new Token(TokenType.RLB, tokenString);
				if ((isSpace(ch) || isTab(ch) || isLF(ch) || isCR(ch))) {
					return 1;
				} else if (isEOF(ch)) {
					return 0;
				}
				rechar();
				return 1;
			}
			default: {
				Err.error(ErrEnum.INPUT_ERR);
				break;
			}
			}
		}
	}
}