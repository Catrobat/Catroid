lexer grammar FormulaLexer;

fragment LETTER : LOWERCASE | UPPERCASE;
fragment UPPERCASE : 'A'..'Z';
fragment LOWERCASE : 'a'..'z';
fragment DIGIT : '0'..'9';
WS: [ \t\r\n]+ -> skip;

NUMBER
	: DIGIT+ ('.' DIGIT+)?
	;

BRACE_OPEN: '(';
BRACE_CLOSE: ')';
COMMA: ',';

VARIABLE: '"' (~["] | '\\"')* '"';
UDB_PARAMETER: '[' (~'[' | ~']' | '\\[' | '\\]')* ']';
LIST: '*' (~[*] | '\\*')* '*';
STRING: '\'' (~['] | '\\\'')* '\'';

OPERATOR_NUMERIC_ADD: '+';
OPERATOR_NUMERIC_MINUS: '-';
OPERATOR_NUMERIC_DIVIDE: '/' | '÷';
OPERATOR_NUMERIC_MULTIPLY: '×';
OPERATOR_LOGIC_AND: '&&';
OPERATOR_LOGIC_OR: '||';
OPERATOR_LOGIC_NOT: '!';
OPERATOR_LOGIC_EQUAL: '=';
OPERATOR_LOGIC_NOT_EQUAL: '!=' | '≠';
OPERATOR_LOGIC_LOWER: '<';
OPERATOR_LOGIC_GREATER: '>';
OPERATOR_LOGIC_LOWER_EQUAL: '<=' | '≤';
OPERATOR_LOGIC_GREATER_EQUAL: '>=' | '≥';

SENSOR_OR_PROPERTY_OR_METHOD: LETTER (LETTER | DIGIT | '%' | ' ' | '°')+;