package lexicalAnalysis;

// 基础C0的DFA状态机状态
enum DFAState {
	INIT_STATE, // 初始
	DEC_INT_STATE, // 十进制整数
	HEX_INT_STATE, // 十六进制整数
	ID_STATE, // 标识符

	PLUS_STATE, // +
	MINUS_STATE, // -
	MUL_STATE, // *
	DIV_STATE, // /
	E_STATE, // =

	L_STATE, // <
	LE_STATE, // <=
	G_STATE, // >
	GE_STATE, // >=
	UE_STATE, // !=
	EE_STATE, // ==

	COMMA_STATE, // ,
	SEM_STATE, // ;
	LSB_STATE, // (
	RSB_STATE, // )
	LMB_STATE, // [
	RMB_STATE, // ]
	LLB_STATE, // {
	RLB_STATE, // }
};
