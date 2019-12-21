package syntaxAnalysis;

import java.util.ArrayList;

import lexicalAnalysis.Token;
import lexicalAnalysis.TokenType;
import out.Output;
import out.Text;
import error.*;

public class SyntaxAnalysis {

	private ArrayList<Token> tokenList;
	private int index = 0; // 下一个要取的Token标号
	private Token token; // 全局使用的token

	// 函数表
	private FuncTable funcTable = new FuncTable();
	// 函数返回类型
	private TokenType resType;
	// 函数参数列表
	private ArrayList<TokenType> paraList;

	// 语句块
	private Block block;
	// 语句块编号
	private int NO = 1;
	// 变量表
	private Table table = new Table();

	// 栈偏移
	private int offset;

	// 指令文本
	private Text text;
	// 输出文件列表
	private Output output = new Output();

	public SyntaxAnalysis(ArrayList<Token> tokenList) {
		this.tokenList = tokenList;
	}

	private Token getToken() {
		if (index == tokenList.size()) {
			return null;
		} else {
			return tokenList.get(index++);
		}
	}

	private void reToken() {
		index--;
	}

	// 所有递归下降子程序返回值均为int类型
	// 其中
	// 1表示当前分析完成
	// 0表示由于读到末尾而无法从头开始匹配
	// -1表示由于头符号集不匹配而导致的无法匹配
	// -2表示由于语法或语义错误，但实际中应该无法得到该返回值
	// 途中产生语法错误或者语义错误时会直接调用Err.error()进行报错并直接退出程序

	// 语法分析的入口
	public void syntaxAnalysis() {
		if (analyseC0Program() == 1) {
			System.out.println("语法分析完成");
		}
	}

	// 由于常、变量声明和函数声明的头符号集有交集，所以定义一个特殊方法
	// 只用于判断是哪种声明，判断完成后reToken()，不影响后续操作
	// 只关心头符号集，不关心后续是否还能匹配上
	// 返回0表示已经读到EOF
	// 返回1表示是常、变量声明
	// 返回2表示是函数声明
	// 其余返回均出现异常
	private int analyseVarOrFunc() {
		token = getToken();
		// 开始就读到EOF，返回0
		if (token == null) {
			return 0;
		}
		// 读到CONST，肯定是常、变量声明（具体地说是常量声明）
		if (token.getType() == TokenType.CONST) {
			reToken(); // 回退，不影响后续操作
			return 1;
		}
		// 读到VOID，肯定是函数声明
		if (token.getType() == TokenType.VOID) {
			reToken(); // 回退，不影响后续操作
			return 2;
		}
		// 读到INT
		// int a;
		// int a, ...
		// int a = ...
		// int a( ...
		if (token.getType() == TokenType.INT) {
			token = getToken();
			if (token == null) {
				Err.error(ErrEnum.EOF_ERR); // 过程中EOF，错误处理
				return -2;
			}
			// INT后肯定是一个ID，否则报错
			if (token.getType() != TokenType.ID) {
				Err.error(ErrEnum.ID_ERR);
				return -2;
			}
			token = getToken();
			if (token == null) {
				Err.error(ErrEnum.EOF_ERR); // 过程中EOF，错误处理
				return -2;
			}
			// 只有LSB才匹配函数声明，其余的都匹配到变量声明，至于后续是否继续匹配并不关心
			if (token.getType() == TokenType.LSB) {
				// 至今为止读了三个token，回退三次
				reToken();
				reToken();
				reToken();
				return 2;
			}
			// 至今为止读了三个token，回退三次
			reToken();
			reToken();
			reToken();
			return 1;
		}
		// 与二者的头符号集都不匹配，报错
		Err.error(ErrEnum.UK_TYPE_ERR);
		return -2;
	}

	// <C0-program> ::= {<variable-declaration>}{<function-definition>}
	private int analyseC0Program() {
		// {<variable-declaration>}
		// 初始化
		block = new Block(0, -1);
		table.addBlock(block);
		text = new Text();
		output.addText(text);
		while (true) {
			int res = analyseVarOrFunc();
			// 常、变量声明
			if (res == 1) {
				// 非法返回值只有-2
				if (analyseVariableDeclaration(0) == -2) {
					Err.error(ErrEnum.SP_ERR);
					return -2;
				}
			}
			// 函数声明
			else if (res == 2) {
				// 非法返回值为-1，-2
				int temp = analyseFunctionDefinition();
				if (temp == -1 || temp == -2) {
					Err.error(ErrEnum.SP_ERR);
					return -2;
				}
			}
			// 模拟情景，当最后一个声明被解析完后
			// 再次调用analyseVarOrFunc()，遇到EOF，返回0
			else if (res == 0) {
				return 1;
			}
			// 保险起见，调用调试用的错误处理
			else {
				Err.error(ErrEnum.SP_ERR);
				return -2;
			}
		}
	}

	// <variable-declaration> ::=
	// [<const-qualifier>]<type-specifier><init-declarator-list>';'
	private int analyseVariableDeclaration(int no) {
		token = getToken();
		if (token == null) {
			return 0;
		}
		// 常量声明
		if (token.getType() == TokenType.CONST) {
			token = getToken();
			if (token == null) {
				Err.error(ErrEnum.EOF_ERR);
				return -2;
			}
			// 基础C0中接下来的数据类型只能是INT
			if (token.getType() == TokenType.INT) {
				// <init-declarator-list>必须被显示初始化
				// 为常量声明添加的特殊方法 analyseInitDeclaratorListForConst()
				if (analyseInitDeclaratorListForConst(no) == 1) {

					token = getToken();
					if (token == null) {
						Err.error(ErrEnum.EOF_ERR);
						return -2;
					}
					if (token.getType() != TokenType.SEM) {
						Err.error(ErrEnum.SEM_ERR);
						return -2;
					}
					return 1;
				}
				Err.error(ErrEnum.CONST_DECL_ERR);
				return -2;
			}
			// 不支持的变量类型
			else {
				Err.error(ErrEnum.UK_TYPE_ERR);
				return -2;
			}
		}
		// 变量声明
		else if (token.getType() == TokenType.INT) {
			// <init-declarator-list>
			if (analyseInitDeclaratorList(no) == 1) {
				token = getToken();
				if (token == null) {
					Err.error(ErrEnum.EOF_ERR);
					return -2;
				}
				if (token.getType() != TokenType.SEM) {
					Err.error(ErrEnum.SEM_ERR);
					return -2;
				}
				return 1;
			}
			Err.error(ErrEnum.VAR_DECL_ERR);
			return -2;
		}
		// 头符号集不匹配，不是常、变量声明
		reToken();
		return -1;
	}

	// <init-declarator-list> ::= <init-declarator>{','<init-declarator>}
	// 为常量声明添加的方法，要求<init-declarator>中必须显式初始化
	private int analyseInitDeclaratorListForConst(int no) {
		// 声明的常量至少有一个
		if (analyseInitDeclaratorForConst(no) == 1) {
			while (true) {
				token = getToken();
				if (token == null) {
					return 1;
				}
				if (token.getType() != TokenType.COMMA) {
					reToken();
					return 1;
				}
				if (analyseInitDeclaratorForConst(no) != 1) {
					Err.error(ErrEnum.CONST_DECL_ERR);
					return -2;
				}
			}
		}
		// 没有常量被声明，语法错误
		Err.error(ErrEnum.CONST_DECL_ERR);
		return -2;
	}

	// <init-declarator> ::= <identifier><initializer>
	// 为常量声明添加的方法，更改了语法，要求必须有<initializer>
	// 这里直接将<initializer>转化为'='<expression>
	// 最终语法：<init-declarator> ::= <identifier>'='<expression>
	private int analyseInitDeclaratorForConst(int no) {
		token = getToken();
		if (token == null) {
			return 0;
		}
		if (token.getType() == TokenType.ID) {
			String name = token.getValue();
			// 查找是否有重定义
			if (block.containsKey(name)) {
				Err.error(ErrEnum.ID_REDECL_ERR);
				return -2;
			}
			token = getToken();
			// 没有显式初始化
			if (token == null) {
				// 这里还是返回EOF_ERR
				Err.error(ErrEnum.EOF_ERR);
				return -2;
			}
			if (token.getType() != TokenType.E) {
				Err.error(ErrEnum.CONST_INIT_ERR);
				return -2;
			}
			// 显式初始化
			if (analyseExpression(no) != 1) {
				Err.error(ErrEnum.EXP_ERR);
				return -2;
			}
			// 符号表上记录栈偏移
			// 此处不会重定义，不会未初始化
			block.put(0, name, offset++);
			return 1;
		}
		// 头符号集不匹配
		reToken();
		return -1;
	}

	// <init-declarator-list> ::= <init-declarator>{','<init-declarator>}
	private int analyseInitDeclaratorList(int no) {
		// 至少有一个变量被声明
		if (analyseInitDeclarator(no) == 1) {
			while (true) {
				token = getToken();
				if (token == null) {
					return 1;
				}
				if (token.getType() != TokenType.COMMA) {
					reToken();
					return 1;
				}
				if (analyseInitDeclarator(no) != 1) {
					Err.error(ErrEnum.VAR_DECL_ERR);
					return -2;
				}
			}
		}
		// 没有变量被声明
		Err.error(ErrEnum.VAR_DECL_ERR);
		return -2;
	}

	// <init-declarator> ::= <identifier>[<initializer>]
	// 这里直接将<initializer>转化为'='<expression>
	private int analyseInitDeclarator(int no) {
		token = getToken();
		if (token == null) {
			return 0;
		}
		if (token.getType() == TokenType.ID) {
			String name = token.getValue();
			// 查找是否有重定义
			if (block.containsKey(name)) {
				Err.error(ErrEnum.ID_REDECL_ERR);
				return -2;
			}
			token = getToken();
			// 没有显式初始化
			if (token == null) {
				// 未初始化占位符
				text.addCode("ipush", "0", "");
				block.put(1, name, offset++);
				return 1;
			}
			if (token.getType() != TokenType.E) {
				reToken();
				// 未初始化占位符
				text.addCode("ipush", "0", "");
				block.put(1, name, offset++);
				return 1;
			}
			// 显式初始化
			if (analyseExpression(no) != 1) {
				Err.error(ErrEnum.EXP_ERR);
				return -2;
			}
			// 符号表上记录栈偏移
			block.put(1, name, offset++);
			return 1;
		}
		// 头符号集不匹配
		reToken();
		return -1;
	}

	// <expression> ::= <add-expression>
	private int analyseExpression(int no) {
		if (analyseAddExpression(no) != 1) {
			Err.error(ErrEnum.EXP_ERR);
			return -2;
		}
		return 1;
	}

	// <add-expression> ::= <mul-expression>{<add-operator><mul-expression>}
	// 加法型表达式：项 '+' / '-' 项 ...
	private int analyseAddExpression(int no) {
		if (analyseMulExpression(no) == 1) {
			while (true) {
				token = getToken();
				if (token == null) {
					return 1;
				}
				// 用type来记录operator的类型
				TokenType type = token.getType();
				if (type != TokenType.PLUS && type != TokenType.MINUS) {
					// 不是加减型
					reToken();
					return 1;
				}
				if (analyseMulExpression(no) != 1) {
					Err.error(ErrEnum.EXP_ERR);
					return -2;
				}
				// 当前项分析成功
				if (type == TokenType.PLUS) {
					text.addCode("iadd", "", "");
				} else if (type == TokenType.MINUS) {
					text.addCode("isub", "", "");
				}
			}
		}
		Err.error(ErrEnum.EXP_ERR);
		return -2;
	}

	// <mul-expression> ::= <cast-expression>{<mul-operator><cast-expression>}
	// 乘法型表达式：因子 '*' / '/' 因子
	private int analyseMulExpression(int no) {
		if (analyseCastExpression(no) == 1) {
			while (true) {
				token = getToken();
				if (token == null) {
					return 1;
				}
				TokenType type = token.getType();
				if (type != TokenType.MUL && type != TokenType.DIV) {
					// 回退
					reToken();
					return 1;
				}
				if (analyseCastExpression(no) != 1) {
					Err.error(ErrEnum.EXP_ERR);
					return -2;
				}
				// 当前因子分析成功
				if (type == TokenType.MUL) {
					text.addCode("imul", "", "");
				} else if (type == TokenType.DIV) {
					text.addCode("idiv", "", "");
				}
			}
		}
		Err.error(ErrEnum.EXP_ERR);
		return -2;
	}

	// <cast-expression> ::= {'('<type-specifier>')'}<unary-expression>
	// 由于基础C0中并不存在类型转换，所以缩减语法
	// <cast-expression> ::= <unary-expression>
	private int analyseCastExpression(int no) {
		if (analyseUnaryExpression(no) != 1) {
			Err.error(ErrEnum.EXP_ERR);
			return -2;
		}
		return 1;
	}

	// <unary-expression> ::= [<unary-operator>]<primary-expression>
	private int analyseUnaryExpression(int no) {
		token = getToken();
		if (token == null) {
			return 0;
		}
		@SuppressWarnings("unused") // 暂时加上，防止warning
		int prefix = 1; // 符号位，没有符号位默认为正，即1
		// 符号位储存在type中
		TokenType type = token.getType();
		if (type == TokenType.PLUS || type == TokenType.MINUS) {
			if (type == TokenType.PLUS) {
				prefix = 1;
			} else if (type == TokenType.MINUS) {
				prefix = -1;
				text.addCode("ipush", "0", "");
			}
			// 解析<primary-expression>
			if (analysePrimaryExpression(no) != 1) {
				Err.error(ErrEnum.EXP_ERR);
				return -2;
			}
			if (type == TokenType.MINUS) {
				text.addCode("isub", "", "");
			}
			return 1;
		}
		// 没有符号位
		else {
			reToken();
			// 解析<primary-expression>
			if (analysePrimaryExpression(no) == 1) {
				return 1;
			}
			// 这里仔细考虑后是头符号集不匹配的情况
			else {
				// 这里不用reToken()
				return -1;
			}
		}

	}

	// <primary-expression> ::=
	// '('<expression>')'
	// |<identifier>
	// |<integer-literal>
	// |<char-literal>
	// |<floating-literal>
	// |<function-call>
	private int analysePrimaryExpression(int no) {
		token = getToken();
		if (token == null) {
			return 0;
		}
		// '('<expression>')'
		if (token.getType() == TokenType.LSB) {
			if (analyseExpression(no) == 1) {
				token = getToken();
				if (token == null) {
					Err.error(ErrEnum.EOF_ERR);
					return -2;
				}
				// 缺少右-小括号
				if (token.getType() != TokenType.RSB) {
					Err.error(ErrEnum.RSB_ERR);
					return -2;
				}
				return 1;
			}
			Err.error(ErrEnum.EXP_ERR);
			return -2;
		}
		// <identifier>
		else if (token.getType() == TokenType.ID) {
			String name = token.getValue();
			// 获得栈偏移
			Offset off = table.getOffset(name, no);
			if (off.offset == null) {
				Err.error(ErrEnum.UNINIT_ERR);
				return -2;
			}
			// level = 1
			if (off.no == 0 && no != 0) {
				text.addCode("loada", "1", off.offset.toString());
			}
			// level = 0
			else {
				text.addCode("loada", "0", off.offset.toString());
			}
			return 1;
		}
		// <integer-literal>
		else if (token.getType() == TokenType.DEC_INT) {
			text.addCode("ipush", token.getValue(), "");
			return 1;
		}
		// 暂时先不考虑函数调用
		// else if (analyseFunctionCall() == 1) {
		//
		// }
		// 头符号集不匹配
		else {
			reToken();
			return -1;
		}
	}

	// <function-definition> ::=
	// <type-specifier><identifier><parameter-clause><compound-statement>
	private int analyseFunctionDefinition() {
		// 基础C0中，<function-definition>的FIRST集为INT和VOID
		token = getToken();
		// 到达结尾
		if (token == null) {
			return 0;
		}
		// 记录函数返回类型
		resType = token.getType();
		if (resType == TokenType.INT || resType == TokenType.VOID) {
			token = getToken();
			if (token == null) {
				Err.error(ErrEnum.EOF_ERR);
				return -2;
			}
			if (token.getType() == TokenType.ID) {
				String name = token.getValue();
				// 有函数重名
				if (funcTable.containsKey(name)) {
					Err.error(ErrEnum.FUNC_REDECL_ERR);
					return -2;
				}
				// 函数表中添加该函数
				funcTable.put(name, null);
				output.addText(text);
				text = new Text();
				output.addText(text);
				int no = NO++;
				block = new Block(no, 0);
				table.addBlock(block);
				offset = 0;
				// 开始分析参数列表
				// 注意，参数也算作局部常、变量
				if (analyseParameterClause() == 1) {
					// 这里手动填入fatherNo
					if (analyseCompoundStatement(no) == 1) {
						// 在这里整合函数的信息，回填入函数表中
						return 1;
					}
					// 函数语句语法错误
					else {
						Err.error(ErrEnum.FUNC_STATMENT_ERR);
						return -2;
					}
				}
				// 函数参数声明时语法错误
				else {
					Err.error(ErrEnum.FUNC_PARA_DECL_ERR);
					return -2;
				}
			}
			// 缺少标识符
			else {
				Err.error(ErrEnum.ID_ERR);
				return -2;
			}
		}
		reToken();
		return -1;
	}

	// <parameter-clause> ::= '(' [<parameter-declaration-list>] ')'
	private int analyseParameterClause() {
		token = getToken();
		if (token == null) {
			return 0;
		}
		// 新建参数列表
		paraList = new ArrayList<TokenType>();
		if (token.getType() == TokenType.LSB) {
			// 这里先预读一个token判断是否有参数
			token = getToken();
			if (token == null) {
				Err.error(ErrEnum.EOF_ERR);
				return -2;
			}
			// 直接是右-小括号，表明没有参数
			if (token.getType() == TokenType.RSB) {
				// 直接匹配了<parameter-clause> ::= '(' ')'
				// 不对参数列表进行任何修改
				return 1;
			}
			// 不是右-小括号，表明有参数
			else {
				reToken(); // 先回退
				// 这里合法返回值只有1
				if (analyseParameterDeclarationList() == 1) {
					token = getToken();
					if (token == null) {
						Err.error(ErrEnum.EOF_ERR);
						return -2;
					}
					if (token.getType() != TokenType.RSB) {
						Err.error(ErrEnum.RSB_ERR);
						return -2;
					}
					return 1;
				}
				Err.error(ErrEnum.FUNC_PARA_DECL_ERR);
				return -2;
			}
		}
		// 头符号集不匹配
		reToken(); // 回退
		return -1;
	}

	// <parameter-declaration-list> ::=
	// <parameter-declaration>{','<parameter-declaration>}
	private int analyseParameterDeclarationList() {
		// 进入这个方法就表明一定会有参数
		if (analyseParameterDeclaration() == 1) {
			while (true) {
				token = getToken();
				if (token == null) {
					return 1;
				}
				if (token.getType() != TokenType.COMMA) {
					reToken();
					return 1;
				}
				if (analyseParameterDeclaration() != 1) {
					Err.error(ErrEnum.FUNC_PARA_DECL_ERR);
					return -2;
				}
				// 当前参数解析完成
			}
		}
		// 没有参数，报错
		Err.error(ErrEnum.FUNC_PARA_DECL_ERR);
		return -2;
	}

	// <parameter-declaration> ::= [<const-qualifier>]<type-specifier><identifier>
	private int analyseParameterDeclaration() {
		token = getToken();
		if (token == null) {
			return 0;
		}
		// CONST参数
		if (token.getType() == TokenType.CONST) {
			token = getToken();
			if (token == null) {
				Err.error(ErrEnum.EOF_ERR);
				return -2;
			}
			if (token.getType() == TokenType.INT) {
				// 参数列表中添加参数
				paraList.add(TokenType.INT);
				token = getToken();
				if (token == null) {
					Err.error(ErrEnum.EOF_ERR);
					return -2;
				}
				if (token.getType() == TokenType.ID) {
					String name = token.getValue();
					// 检查重定义
					if (block.containsKey(name)) {
						Err.error(ErrEnum.ID_REDECL_ERR);
						return -2;
					}
					// 传入参数需要仔细考虑
					block.put(0, name, offset++);
					return 1;
				}
				// 缺少ID
				Err.error(ErrEnum.ID_ERR);
				return -2;
			}
			// 不支持的数据类型
			Err.error(ErrEnum.UK_TYPE_ERR);
			return -2;
		}
		// 非CONST参数
		else if (token.getType() == TokenType.INT) {
			// 参数列表中添加参数
			paraList.add(TokenType.INT);
			token = getToken();
			if (token == null) {
				Err.error(ErrEnum.EOF_ERR);
				return -2;
			}
			if (token.getType() == TokenType.ID) {
				String name = token.getValue();
				// 检查重定义
				if (block.containsKey(name)) {
					Err.error(ErrEnum.ID_REDECL_ERR);
					return -2;
				}
				// 传入参数需要仔细考虑
				block.put(1, name, offset++);
				return 1;
			}
			// 缺少ID
			Err.error(ErrEnum.ID_ERR);
			return -2;
		}
		// 暂定不支持的数据（参数）类型
		Err.error(ErrEnum.UK_TYPE_ERR);
		return -2;
	}

	// <compound-statement> ::= '{' {<variable-declaration>} <statement-seq> '}'
	private int analyseCompoundStatement(int fatherNo) {
		token = getToken();
		if (token == null) {
			return 0;
		}
		if (token.getType() == TokenType.LLB) {
			int no = NO++;
			// 新建块
			block = new Block(no, fatherNo);
			table.addBlock(block);
			// {<variable-declaration>}
			while (true) {
				if (analyseVariableDeclaration(no) != 1) {
					break;
				}
			}
			if (analyseStatementSeq(no) != 1) {
				Err.error(ErrEnum.FUNC_STATMENT_ERR);
				return -2;
			}
			token = getToken();
			if (token == null) {
				Err.error(ErrEnum.EOF_ERR);
				return -2;
			}
			if (token.getType() != TokenType.RLB) {
				Err.error(ErrEnum.RLB_ERR);
				return -2;
			}
			// 回退block
			block = table.getBlock(block.fatherNo);
			return 1;
		}
		reToken();
		return -1;
	}

	// <statement-seq> ::= {<statement>}
	private int analyseStatementSeq(int no) {
		while (true) {
			if (analyseStatement(no) != 1) {
				break;
			}
		}
		return 1;
	}

	// <statement> ::=
	// <compound-statement>
	// |<condition-statement>
	// |<loop-statement>
	// |<jump-statement>
	// |<print-statement>
	// |<scan-statement>
	// |<assignment-expression>';'
	// |<function-call>';'
	// |';'
	private int analyseStatement(int no) {
		// 这里存在隐性递归
		// 用预读来判断
		token = getToken();
		if (token == null) {
			return 0;
		}
		if (token.getType() == TokenType.LLB) {
			reToken();
			if (analyseCompoundStatement(no) == 1) {
				return 1;
			}
		} else {
			reToken();
			// 调试用返回
			return -1;
		}
		if (analyseConditionStatement(no) == 1) {
			return 1;
		} else if (analyseLoopStatement(no) == 1) {
			return 1;
		} else if (analyseJumpStatement(no) == 1) {
			return 1;
		} else if (analysePrintStatement(no) == 1) {
			return 1;
		} else if (analyseScanStatement(no) == 1) {
			return 1;
		}
		// 再单独判断后面是否有分号
		else if (analyseAssignmentExpression(no) == 1) {
			return 1;
		}
		// 再单独判断后面是否有分号
		else if (analyseFunctionCall(no) == 1) {
			return 1;
		} else {

		}
		return -1;
	}

	// <condition-statement> ::=
	// 'if' '(' <condition> ')' <statement> ['else' <statement>]
	private int analyseConditionStatement(int no) {
		token = getToken();
		if (token == null) {
			return 0;
		}
		// 'if'
		if (token.getType() == TokenType.IF) {
			// '('
			token = getToken();
			if (token == null) {
				Err.error(ErrEnum.EOF_ERR);
				return -2;
			}
			if (token.getType() != TokenType.LSB) {
				Err.error(ErrEnum.LSB_ERR);
				return -2;
			}
			// <condition>
			if (analyseCondition(no) != 1) {
				Err.error(ErrEnum.FUNC_STATMENT_ERR);
				return -2;
			}
			// ')'
			token = getToken();
			if (token == null) {
				Err.error(ErrEnum.EOF_ERR);
				return -2;
			}
			if (token.getType() != TokenType.RSB) {
				Err.error(ErrEnum.RSB_ERR);
				return -2;
			}
			// <statement>
			if (analyseStatement(no) != 1) {
				Err.error(ErrEnum.FUNC_STATMENT_ERR);
				return -2;
			}
			// 'else'
			token = getToken();
			if (token == null) {
				return 1;
			}
			if (token.getType() != TokenType.ELSE) {
				reToken();
				return 1;
			}
			// <statement>
			if (analyseStatement(no) != 1) {
				Err.error(ErrEnum.FUNC_STATMENT_ERR);
				return -2;
			}
			return 1;
		}
		reToken();
		return -1;
	}

	// <condition> ::= <expression>[<relational-operator><expression>]
	private int analyseCondition(int no) {
		if (analyseExpression(no) == 1) {
			token = getToken();
			if (token == null) {
				return 1;
			}
			TokenType type = token.getType();
			if (type == TokenType.L || type == TokenType.LE || type == TokenType.G || type == TokenType.GE
					|| type == TokenType.UE || type == TokenType.EE) {
				if (analyseExpression(no) != 1) {
					Err.error(ErrEnum.FUNC_STATMENT_ERR);
					return -2;
				}
				// switch (type) {
				// case L:
				// text.addCode("isub", "", "");
				// }
			} else {
				reToken();
				text.addCode("je", "", "");
				return 1;
			}
		}
		return -1;
	}

	private int analyseLoopStatement(int no) {
		return 1;
	}

	private int analyseJumpStatement(int no) {
		return 1;
	}

	private int analysePrintStatement(int no) {
		return 1;
	}

	private int analyseScanStatement(int no) {
		return 1;
	}

	private int analyseAssignmentExpression(int no) {
		return 1;
	}

	private int analyseFunctionCall(int no) {
		return 1;
	}
}
