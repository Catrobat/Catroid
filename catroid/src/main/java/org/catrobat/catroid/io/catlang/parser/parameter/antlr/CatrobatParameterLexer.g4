lexer grammar CatrobatParameterLexer;

fragment LETTER : LOWERCASE | UPPERCASE;
fragment UPPERCASE : 'A'..'Z';
fragment LOWERCASE : 'a'..'z';
fragment DIGIT : '0'..'9';
WS: [ \t\r\n]+ -> skip;

//mode FORMULA_MODE;
//FORMULA_BRACKET_CLOSE: ')' -> popMode;

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
//OPERATOR_LOGIC_AND: 'and';
//OPERATOR_LOGIC_OR: 'or';
//OPERATOR_LOGIC_NOT: 'not';
OPERATOR_LOGIC_EQUAL: '=';
OPERATOR_LOGIC_NOT_EQUAL: '!=' | '≠';
OPERATOR_LOGIC_LOWER: '<';
OPERATOR_LOGIC_GREATER: '>';
OPERATOR_LOGIC_LOWER_EQUAL: '<=' | '≤';
OPERATOR_LOGIC_GREATER_EQUAL: '>=' | '≥';

FUNCTION_OR_SENSOR: LETTER (LETTER | DIGIT | '%' | ' ')+;

//mode VARIABLE_MODE;
//VARIABLE_MODE_ANYTHING: ~('"') -> skip;
//VARIABLE_MODE_END : '"' -> popMode;
//
//mode UDB_PARAM_MODE;
//UDB_PARAM_MODE_ANYTHING: ~(']') -> skip;
//UDB_PARAM_MODE_END : ']' -> popMode;
//
//mode LIST_MODE;
//LIST_MODE_ANYTHING: ~('*') -> skip;
//LIST_MODE_END : '*' -> popMode;
//
//mode STRING_MODE;
//STRING_MODE_ESCAPED_QUOTE: '\\\'' -> skip;
//STRING_MODE_ANYTHING: ~('\'') -> skip;
//STRING_MODE_END: '\'' -> popMode;