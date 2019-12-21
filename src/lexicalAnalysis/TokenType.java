package lexicalAnalysis;

public enum TokenType {
	CONST, // 基础
	VOID, // 基础
	INT, // 基础
	
	CHAR,
	DOUBLE,
	
	STRUCT,
	IF,
	ELSE,
	SWITCH,
	CASE,
	DEFAULT,
	WHILE, 
	FOR, 
	DO, 
	RETURN,
	BREAK,
	CONTINUE,
	PRINT,
	SCAN,
	
	ID, // 只有字母和数字
	DEC_INT,
	HEX_INT,
	
	PLUS,
	MINUS,
	MUL,
	DIV,
	E,
	
	L,
	LE,
	G,
	GE,
	UE,
	EE,
	
	COMMA,
	SEM,
	LSB,
	RSB,
	LMB,
	RMB,
	LLB,
	RLB, 
}
