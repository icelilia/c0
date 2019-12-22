package error;

public enum ErrEnum {

	PARA_ERR, // 参数错误
	INPUT_FILE_ERR, // 输入文件不存在
	OUTPUT_FILE_ERR, // 无法创建输出文件

	INPUT_ERR, // 输入含有非法字符或者十六进制整型字面量格式错误
	OUTPUT_ERR, // 输出错误，文件写入出错

	INT_OF_ERR, // 32位整型字面量溢出

	SP_ERR, // 特殊错误，仅调试用

	EOF_ERR, // 分析中途遇到EOF

	ID_ERR, // ID错误，缺少标识符

	UK_TYPE_ERR, // 不支持的数据类型

	CONST_DECL_ERR, // 常量声明时语法错误

	CONST_INIT_ERR, // 常量未被显示地初始化
	
	CONST_AS_ERR, // 常量无法被再次赋值

	ID_REDECL_ERR, // 常量重定义

	VAR_DECL_ERR, // 变量声明时语法错误

	VAR_REDECL_ERR, // 变量重定义

	SEM_ERR, // 缺少分号

	EXP_ERR, // 表达式中存在错误

	ID_UNDECL_ERR, // 标识符未定义

	VAR_UNINIT_ERR, // 使用了未初始化的变量

	FUNC_REDECL_ERR, // 函数重定义

	LSB_ERR, // 缺少左-小括号

	RSB_ERR, // 缺少右-小括号

	FUNC_PARA_DECL_ERR, // 函数参数声明时语法错误

	LLB_ERR, // 缺少左-大括号

	RLB_ERR, // 缺少右-大括号

	FUNC_STATMENT_ERR, // 函数语句语法错误
	
	FUNC_PARA_ERR, // 函数调用错误
	
	VOID_FUNC_CALL_ERR, // 表达式中调用返回值为空的函数

	ErrNoBegin, ErrNoEnd, ErrNeedIdentifier, ErrConstantNeedValue, ErrNoSemicolon, ErrInvalidVariableDeclaration,
	ErrIncompleteExpression, ErrNotDeclared, ErrAssignToConstant, ErrDuplicateDeclaration, ErrNotInitialized,
	ErrInvalidAssignment, ErrInvalidPrint
}
