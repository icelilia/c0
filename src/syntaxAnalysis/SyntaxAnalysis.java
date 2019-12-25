package syntaxAnalysis;

import java.util.ArrayList;

import lexicalAnalysis.Token;
import lexicalAnalysis.TokenType;
import error.*;

// Author：Andersen
// 语法分析和语义分析，以及中间代码生成耦合
// 比较复杂

public class SyntaxAnalysis {

	private ArrayList<Token> tokenList;
	private int index = 0; // 下一个要取的Token标号
	private Token token; // 全局使用的token

	// 函数表
	private FuncTable funcTable = new FuncTable();
	// 函数返回类型
	private TokenType resType;
	// 函数参数个数
	private int paraNum;
	// 函数信息体
	private Func func = null;

	// 语句块
	private Block block = null;
	// 语句块编号
	private int NO = 1;
	// 变量表
	private Table table = new Table();

	// 栈偏移
	private int offset;

	// 调用者提供的参数个数
	private int callParaNum;

	// 指令文本
	private Text text;

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
	public FuncTable syntaxAnalysis() {
		if (analyseC0Program() == 1) {
			// 检查是否有main函数
			for (Func func : funcTable.getFuncList()) {
				if (func.name.contentEquals("main")) {
					System.out.println("success");
					return funcTable;
				}
			}
			Err.error(ErrEnum.NO_MAIN_ERR);
			return null;
		} else {
			System.out.println("fail");
			return null;
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
				Err.error(ErrEnum.NEED_ID_ERR);
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
		Err.error(ErrEnum.US_TYPE_ERR);
		return -2;
	}

	// <C0-program> ::= {<variable-declaration>}{<function-definition>}
	private int analyseC0Program() {
		// 记录全局的变量声明是否已经解析完
		boolean flag = false;
		// {<variable-declaration>}
		// 为全局常、变量新建一个Block，编号为0，父编号为-1
		block = new Block(0, -1);
		table.addBlock(block);
		// 为全局常、变量的初始化代码新建代码块
		text = new Text("全局"); // 用中文当名字，防止和后面的函数重名
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
				// 刚刚解析完全局变量声明
				if (flag == false) {
					// 为全局变量声明创建一个函数：“全局”
					func = new Func("全局", null, null);
					func.addText(text);
					funcTable.addFunc(func);
					flag = true;
				}
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
				Err.error(ErrEnum.US_TYPE_ERR);
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
			// 注意，重定义只在本块中查找，不向上递归
			// 同时不能与函数名重名
			if (block.containsID(name)) {
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
			// 同样，只在本块中查找
			if (block.containsID(name)) {
				Err.error(ErrEnum.ID_REDECL_ERR);
				return -2;
			}
			token = getToken();
			// 没有显式初始化
			if (token == null) {
				// 未初始化占位符
				text.addCode("ipush", "0", "");
				block.put(-1, name, offset++);
				return 1;
			}
			if (token.getType() != TokenType.E) {
				reToken();
				// 未初始化占位符
				text.addCode("ipush", "0", "");
				block.put(-1, name, offset++);
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
			Integer res = table.getKind(name, no);
			// 不存在
			if (res == null) {
				Err.error(ErrEnum.ID_UNDECL_ERR);
				return -2;
			}
			// 未初始化
			if (res == -1) {
				Err.error(ErrEnum.VAR_UNINIT_ERR);
				return -2;
			}
			// 获得栈偏移
			Offset off = table.getOffset(name, no);

			// level = 1
			if (off.no == 0 && no != 0) {
				text.addCode("loada", "1", off.offset.toString());
			}
			// level = 0
			else {
				text.addCode("loada", "0", off.offset.toString());
			}
			text.addCode("iload", "", "");
			return 1;
		}
		// <integer-literal>
		else if (token.getType() == TokenType.DEC_INT) {
			text.addCode("ipush", token.getValue(), "");
			return 1;
		}
		// 函数调用
		int res = analyseFunctionCall(no);
		// 表达式中使用返回值为空的函数调用
		if (res == 1) {
			Err.error(ErrEnum.VOID_FUNC_CALL_ERR);
			return -2;
		} else if (res == 2) {
			return 1;
		}
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
				if (funcTable.containsFunc(name)) {
					Err.error(ErrEnum.FUNC_REDECL_ERR);
					return -2;
				}

				// 这里将参数当作函数中最外层的一个块
				// 注意，函数名和参数，算作函数第一个大括号的作用域
				// 这个块的父块一定是全局块0
				// 栈偏移置零
				offset = 0;
				// 参数个数置零
				paraNum = 0;
				int no = NO++;
				// 由于愚蠢的错误，导致Block数据结构根本没有必要
				// 现在每个函数只会有一个Block
				block = new Block(no, 0);
				table.addBlock(block);
				// 新建代码块
				text = new Text(name);
				// 开始分析参数列表
				// 注意，参数也算作局部常、变量
				if (analyseParameterClause() == 1) {
					// 参数分析完成
					func = new Func(name, resType, paraNum);
					// text整合到func对应位置
					func.addText(text);
					funcTable.addFunc(func);
					if (analyseCompoundStatement(no) == 1) {
						// 回退block
						// 其实没必要
						block = table.getBlock(block.fatherNo);
						// 无论如何，函数需要返回语句
						if (resType == TokenType.VOID) {
							text.addCode("ret", "", "");
						} else {
							text.addCode("ipush", "0", "");
							text.addCode("iret", "", "");
						}
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
				Err.error(ErrEnum.NEED_ID_ERR);
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
				// 参数个数+1
				paraNum++;
				token = getToken();
				if (token == null) {
					Err.error(ErrEnum.EOF_ERR);
					return -2;
				}
				if (token.getType() == TokenType.ID) {
					String name = token.getValue();
					// 检查重定义
					if (block.containsID(name)) {
						Err.error(ErrEnum.ID_REDECL_ERR);
						return -2;
					}
					// 传入参数需要仔细考虑
					block.put(0, name, offset++);
					return 1;
				}
				// 缺少ID
				Err.error(ErrEnum.NEED_ID_ERR);
				return -2;
			}
			// 不支持的数据类型
			Err.error(ErrEnum.US_TYPE_ERR);
			return -2;
		}
		// 非CONST参数
		else if (token.getType() == TokenType.INT) {
			// 参数个数+1
			paraNum++;
			token = getToken();
			if (token == null) {
				Err.error(ErrEnum.EOF_ERR);
				return -2;
			}
			if (token.getType() == TokenType.ID) {
				String name = token.getValue();
				// 检查重定义
				if (block.containsID(name)) {
					Err.error(ErrEnum.ID_REDECL_ERR);
					return -2;
				}
				// 传入参数需要仔细考虑
				// 这里参数一定是会被初始化的
				block.put(1, name, offset++);
				return 1;
			}
			// 缺少ID
			Err.error(ErrEnum.NEED_ID_ERR);
			return -2;
		}
		// 暂定不支持的数据（参数）类型
		Err.error(ErrEnum.US_TYPE_ERR);
		return -2;
	}

	// <compound-statement> ::= '{' {<variable-declaration>} <statement-seq> '}'
	private int analyseCompoundStatement(int fatherNo) {
		token = getToken();
		if (token == null) {
			return 0;
		}
		if (token.getType() == TokenType.LLB) {
			// {<variable-declaration>}
			// 直接获取当前block的no就行
			int no = block.no;
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
			return 1;
		} else {
			reToken();
			return -1;
		}
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
	// '{' <statement-seq> '}'
	// |<condition-statement>
	// |<loop-statement>
	// |<jump-statement>
	// |<print-statement>
	// |<scan-statement>
	// |<assignment-expression>';'
	// |<function-call>';'
	// |';''
	private int analyseStatement(int no) {
		token = getToken();
		if (token == null) {
			return 0;
		}
		if (token.getType() == TokenType.LLB) {
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
			return 1;
		}
		reToken();
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
		// 赋值语句和函数调用头符号集相同
		// 这里token不可能为空
		token = getToken();
		if (token.getType() == TokenType.ID) {
			String name = token.getValue();
			reToken();
			// 如果是函数
			if (funcTable.containsFunc(name)) {
				int res = analyseFunctionCall(no);
				if (res == 1 || res == 2) {
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
				Err.error(ErrEnum.FUNC_STATMENT_ERR);
				return -2;
			}
			// 其余均用变量来解释
			else {
				if (analyseAssignmentExpression(no) == 1) {
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
				Err.error(ErrEnum.FUNC_STATMENT_ERR);
				return -2;
			}
		}
		// ;
		reToken();
		token = getToken();
		if (token == null) {
			return 0;
		}
		if (token.getType() != TokenType.SEM) {
			reToken();
			return -1;
		}
		return 1;

	}

	// <condition-statement> ::=
	// 'if' '(' <condition> ')' <statement> ['else' <statement>]
	private int analyseConditionStatement(int no) {
		JumpOffset JMP = new JumpOffset(0), LABEL1 = new JumpOffset(0), LABEL2 = new JumpOffset(0);
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
			if (analyseCondition(no, JMP) != 1) {
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
			// <statement> stm1
			if (analyseStatement(no) != 1) {
				Err.error(ErrEnum.FUNC_STATMENT_ERR);
				return -2;
			}
			token = getToken();
			if (token == null) {
				// 这里求label1的位置
				LABEL1.label = text.getIndex() + 1;
				// 回填jcond指令
				text.reWrite(JMP.label, "", new Integer(LABEL1.label).toString(), "");
				return 1;
			}
			if (token.getType() != TokenType.ELSE) {
				reToken();
				// 这里求label1的位置
				LABEL1.label = text.getIndex() + 1;
				// 回填jcond指令
				text.reWrite(JMP.label, "", new Integer(LABEL1.label).toString(), "");
				return 1;
			}
			// 'else'
			// 先加一条无条件跳转指令
			// LABEL2先置空
			text.addCode("jmp", "", "");
			// 再求label1的位置
			LABEL1.label = text.getIndex() + 1;
			// 回填第一个jcond指令
			text.reWrite(JMP.label, "", new Integer(LABEL1.label).toString(), "");
			// 记录第二个jmp指令的位置
			JMP.label = text.getIndex();
			// <statement> stm2
			if (analyseStatement(no) != 1) {
				Err.error(ErrEnum.FUNC_STATMENT_ERR);
				return -2;
			}
			// 求label2的位置
			LABEL2.label = text.getIndex() + 1;
			// 回填第二个jmp指令
			text.reWrite(JMP.label, "", new Integer(LABEL2.label).toString(), "");
			return 1;
		}
		reToken();
		return -1;
	}

	// <condition> ::= <expression>[<relational-operator><expression>]
	private int analyseCondition(int no, JumpOffset JMP) {
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
				text.addCode("isub", "", "");
				switch (type) {
				case L:
					text.addCode("jge", "", "");
					break;
				case LE:
					text.addCode("jg", "", "");
					break;
				case G:
					text.addCode("jle", "", "");
					break;
				case GE:
					text.addCode("jl", "", "");
					break;
				case UE:
					text.addCode("je", "", "");
					break;
				case EE:
					text.addCode("jne", "", "");
					break;
				default:
					Err.error(ErrEnum.SP_ERR);
					break;
				}
				JMP.label = text.getIndex();
				return 1;
			} else {
				reToken();
				// <condition> ::= <expression>
				// 现在栈顶即是<expression>
				// 跳转至stm2
				// 暂时先不写label
				text.addCode("je", "", "");
				// 记录jmp语句的位置，随后回填
				JMP.label = text.getIndex();
				return 1;
			}
		}
		return -1;
	}

	// <loop-statement> ::= 'while' '(' <condition> ')' <statement>
	private int analyseLoopStatement(int no) {
		JumpOffset JMP = new JumpOffset(0), LABEL1 = new JumpOffset(0), LABEL2 = new JumpOffset(0);
		token = getToken();
		if (token == null) {
			return 0;
		}
		// while
		if (token.getType() == TokenType.WHILE) {
			// (
			token = getToken();
			if (token == null) {
				Err.error(ErrEnum.EOF_ERR);
				return -2;
			}
			if (token.getType() != TokenType.LSB) {
				Err.error(ErrEnum.LSB_ERR);
				return -2;
			}
			// 记录label2固定跳转
			LABEL2.label = text.getIndex() + 1;
			if (analyseCondition(no, JMP) != 1) {
				Err.error(ErrEnum.FUNC_STATMENT_ERR);
				return -2;
			}
			// )
			token = getToken();
			if (token == null) {
				Err.error(ErrEnum.EOF_ERR);
				return -2;
			}
			if (token.getType() != TokenType.RSB) {
				Err.error(ErrEnum.RSB_ERR);
				return -2;
			}
			if (analyseStatement(no) != 1) {
				Err.error(ErrEnum.FUNC_STATMENT_ERR);
				return -2;
			}
			text.addCode("jmp", new Integer(LABEL2.label).toString(), "");
			LABEL1.label = text.getIndex() + 1;
			text.reWrite(JMP.label, "", new Integer(LABEL1.label).toString(), "");
			return 1;
		}
		reToken();
		return -1;
	}

	// <jump-statement> ::= <return-statement>
	// <return-statement> ::= 'return' [<expression>] ';'
	// <jump-statement> ::= 'return' [<expression>] ';'
	private int analyseJumpStatement(int no) {
		token = getToken();
		if (token == null) {
			return 0;
		}
		// return
		if (token.getType() == TokenType.RETURN) {
			if (func.resType == TokenType.VOID) {
				token = getToken();
				if (token == null) {
					Err.error(ErrEnum.EOF_ERR);
					return -2;
				}
				if (token.getType() != TokenType.SEM) {
					Err.error(ErrEnum.SEM_ERR);
					return -2;
				}
				text.addCode("ret", "", "");
				return 1;
			} else if (func.resType == TokenType.INT) {
				if (analyseExpression(no) != 1) {
					Err.error(ErrEnum.EXP_ERR);
					return -2;
				}
				// ;
				token = getToken();
				if (token == null) {
					Err.error(ErrEnum.EOF_ERR);
					return -2;
				}
				if (token.getType() != TokenType.SEM) {
					Err.error(ErrEnum.SEM_ERR);
					return -2;
				}
				text.addCode("iret", "", "");
				return 1;
			} else {
				Err.error(ErrEnum.SP_ERR);
				return -2;
			}

		}
		reToken();
		return -1;
	}

	// <print-statement> ::= 'print' '(' [<printable-list>] ')' ';'
	private int analysePrintStatement(int no) {
		token = getToken();
		if (token == null) {
			return 0;
		}
		if (token.getType() != TokenType.PRINT) {
			reToken();
			return -1;
		}
		token = getToken();
		if (token == null) {
			Err.error(ErrEnum.EOF_ERR);
			return -2;
		}
		if (token.getType() != TokenType.LSB) {
			Err.error(ErrEnum.LSB_ERR);
			return -2;
		}
		// 预读
		token = getToken();
		if (token == null) {
			Err.error(ErrEnum.EOF_ERR);
			return -2;
		}
		if (token.getType() == TokenType.RSB) {
			return 1;
		}
		// 含有表达式
		reToken();
		if (analysePrintableList(no) != 1) {
			Err.error(ErrEnum.FUNC_STATMENT_ERR);
			return -2;
		}
		text.addCode("printl", "", "");
		token = getToken();
		if (token == null) {
			Err.error(ErrEnum.EOF_ERR);
			return -2;
		}
		if (token.getType() != TokenType.RSB) {
			Err.error(ErrEnum.RSB_ERR);
			return -2;
		}
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

	// <printable-list> ::= <printable> {',' <printable>}
	private int analysePrintableList(int no) {
		if (analysePrintable(no) == 1) {
			while (true) {
				token = getToken();
				if (token == null) {
					return 1;
				}
				if (token.getType() != TokenType.COMMA) {
					reToken();
					return 1;
				}
				// bipush 32 + cprint
				text.addCode("bipush", "32", "");
				text.addCode("cprint", "", "");
				if (analysePrintable(no) != 1) {
					Err.error(ErrEnum.FUNC_STATMENT_ERR);
					return -2;
				}
			}
		}
		return -1;
	}

	// <printable> ::= <expression>
	private int analysePrintable(int no) {
		if (analyseExpression(no) != 1) {
			return -1;
		}
		text.addCode("iprint", "", "");
		return 1;
	}

	// <scan-statement> ::= 'scan' '(' <identifier> ')' ';'
	private int analyseScanStatement(int no) {
		// scan
		token = getToken();
		if (token == null) {
			return 0;
		}
		if (token.getType() != TokenType.SCAN) {
			reToken();
			return -1;
		}
		// (
		token = getToken();
		if (token == null) {
			Err.error(ErrEnum.EOF_ERR);
			return -2;
		}
		if (token.getType() != TokenType.LSB) {
			Err.error(ErrEnum.LSB_ERR);
			return -2;
		}
		// id
		token = getToken();
		if (token == null) {
			Err.error(ErrEnum.EOF_ERR);
			return -2;
		}
		if (token.getType() != TokenType.ID) {
			Err.error(ErrEnum.NEED_ID_ERR);
			return -2;
		}
		String name = token.getValue();
		// )
		token = getToken();
		if (token == null) {
			Err.error(ErrEnum.EOF_ERR);
			return -2;
		}
		if (token.getType() != TokenType.RSB) {
			Err.error(ErrEnum.RSB_ERR);
			return -2;
		}
		// ;
		token = getToken();
		if (token == null) {
			Err.error(ErrEnum.EOF_ERR);
			return -2;
		}
		if (token.getType() != TokenType.SEM) {
			Err.error(ErrEnum.SEM_ERR);
			return -2;
		}
		// 先求地址，栈偏移
		Integer res = table.getKind(name, no);
		// 不存在
		if (res == null) {
			Err.error(ErrEnum.ID_UNDECL_ERR);
			return -2;
		}
		// 常量
		if (res == 0) {
			Err.error(ErrEnum.CONST_AS_ERR);
			return -2;
		}
		// 获得栈偏移
		Offset off = table.getOffset(name, no);
		// level = 1
		if (off.no == 0 && no != 0) {
			text.addCode("loada", "1", off.offset.toString());
		}
		// level = 0
		else {
			text.addCode("loada", "0", off.offset.toString());
		}
		// 再压栈值
		text.addCode("iscan", "", "");
		// 最后store
		text.addCode("istore", "", "");
		return 1;
	}

	// <assignment-expression> ::= <identifier>'='<expression>
	private int analyseAssignmentExpression(int no) {
		token = getToken();
		if (token == null) {
			return 0;
		}
		if (token.getType() != TokenType.ID) {
			reToken();
			return -1;
		}
		// 加载地址
		String name = token.getValue();
		Integer res = table.getKind(name, no);
		// 不存在
		if (res == null) {
			Err.error(ErrEnum.ID_UNDECL_ERR);
			return -2;
		}
		// 常量
		if (res == 0) {
			Err.error(ErrEnum.CONST_AS_ERR);
			return -2;
		}
		// 获得栈偏移
		Offset off = table.getOffset(name, no);
		// level = 1
		if (off.no == 0 && no != 0) {
			text.addCode("loada", "1", off.offset.toString());
		}
		// level = 0
		else {
			text.addCode("loada", "0", off.offset.toString());
		}
		// =
		token = getToken();
		if (token == null) {
			Err.error(ErrEnum.EOF_ERR);
			return -2;
		}
		if (token.getType() != TokenType.E) {
			Err.error(ErrEnum.SP_ERR);
			return -2;
		}
		// <expression>
		if (analyseExpression(no) != 1) {
			Err.error(ErrEnum.FUNC_STATMENT_ERR);
			return -2;
		}
		// 解析完表达式，值已经被压栈了
		text.addCode("istore", "", "");
		// 如果是未初始化的变量，则更改状态
		if (res == -1) {
			table.getBlock(off.no).change(name);
		}
		return 1;
	}

	// <function-call> ::=
	// <identifier> '(' [<expression-list>] ')'
	// 这里比较特殊，涉及到返回值类型的问题
	// VOID 返回1
	// INT 返回2
	private int analyseFunctionCall(int no) {
		token = getToken();
		if (token == null) {
			return 0;
		}
		if (token.getType() == TokenType.ID) {
			String name = token.getValue();
			// 查找是否有该函数
			if (!funcTable.containsFunc(name)) {
				Err.error(ErrEnum.ID_UNDECL_ERR);
				return -2;
			}
			// 获得该函数的引用
			Func temp = funcTable.getFunc(name);
			token = getToken();
			if (token == null) {
				Err.error(ErrEnum.EOF_ERR);
				return -2;
			}
			if (token.getType() != TokenType.LSB) {
				Err.error(ErrEnum.LSB_ERR);
				return -2;
			}
			// 预读判断是否有参数列表
			token = getToken();
			if (token == null) {
				Err.error(ErrEnum.EOF_ERR);
				return -2;
			}
			// 没有参数列表
			if (token.getType() == TokenType.RSB) {
				reToken();
				// 检查参数个数是否对应
				if (temp.paraNum != 0) {
					Err.error(ErrEnum.FUNC_PARA_ERR);
					return -2;
				}
				// 获得函数表上的位置
				Integer funcIndex = funcTable.getIndex(name);
				// 由于将全局变量声明的代码也视作一个函数，所以这里funcIndex会比正常多1
				// 添加调用语句
				text.addCode("call", new Integer(funcIndex - 1).toString(), "");
			}
			// 有参数列表
			else {
				reToken();
				// 调用者提供的参数置零
				callParaNum = 0;
				if (analyseExpressionList(no) != 1) {
					Err.error(ErrEnum.FUNC_STATMENT_ERR);
					return -2;
				}
				// 检查参数个数是否对应
				if (temp.paraNum != callParaNum) {
					Err.error(ErrEnum.FUNC_PARA_ERR);
					return -2;
				}
				// 获得函数表上的位置
				Integer funcIndex = funcTable.getIndex(name);
				// 这里同上
				// 添加调用语句
				text.addCode("call", new Integer(funcIndex - 1).toString(), "");
			}
			token = getToken();
			if (token == null) {
				Err.error(ErrEnum.EOF_ERR);
				return -2;
			}
			if (token.getType() != TokenType.RSB) {
				Err.error(ErrEnum.RSB_ERR);
				return -2;
			}
			if (temp.resType == TokenType.VOID) {
				return 1;
			} else if (temp.resType == TokenType.INT) {
				return 2;
			}
		}
		reToken();
		return -1;
	}

	// <expression-list> ::=
	// <expression>{','<expression>}
	private int analyseExpressionList(int no) {
		if (analyseExpression(no) == 1) {
			callParaNum++;
			while (true) {
				token = getToken();
				if (token == null) {
					return 1;
				}
				if (token.getType() != TokenType.COMMA) {
					reToken();
					return 1;
				}
				if (analyseExpression(no) != 1) {
					Err.error(ErrEnum.FUNC_STATMENT_ERR);
					return -2;
				}
				callParaNum++;
			}
		}
		return -1;
	}
}
