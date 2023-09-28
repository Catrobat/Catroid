lexer grammar CatrobatFormulaLexer;

fragment LETTER : LOWERCASE | UPPERCASE;
fragment UPPERCASE : 'A'..'Z';
fragment LOWERCASE : 'a'..'z';
fragment DIGIT : '0'..'9';

mode FORMULA_MODE;
FORMULA_BRACKET_CLOSE: ')' -> popMode;

FORMULA_NUMBER
	: '-'? DIGIT+ ('.' DIGIT+)?
	;

//STRING
//	: '\'' (~[\n\r] | STRING_ESCAPE)* '\''
//	;
//fragment STRING_ESCAPE
//	: '\\' [nrtbf'\\]
//	;
//
//VARIABLE_REF
//	: '"' (~[\n\r] | VAR_ESCAPE)* '"'
//	;
//fragment VAR_ESCAPE
//	: '\\' [nrtbf"\\]
//	;
//
//LIST_REF
//	: '*' (~[\n\r] | LIST_ESCAPE)* '*'
//	;
//fragment LIST_ESCAPE
//	: '\\' [nrtbf*\\]
//	;
//
//UDB_PARAMETER
//	: '[' (~'[' | '\\[' | ~']' | '\\]')* ']'
//	;
//
//OPERATOR
//	: '/'
//	| '='
//	| '<'
//	| '<='
//	| '>'
//	| '>='
//	| 'and'
//	| 'not'
//	| 'or'
//	| '-'
//	| '*' | '×'
//	| '+'
//	| '!=' | '≠'
//	;
//
//BRACKET_OPEN
//	: '('
//	;
//
//BRACKET_CLOSE
//	: ')'
//	;
//
//FUNCTION_NAME
//	: [a-zA-Z0-9 ]+ -> mode(FORMULA_ELEMENT_MODE)
//	;
//
//WS: [ \t\r\n]+ -> skip;
//
//
//mode FORMULA_ELEMENT_MODE;
//FORMULA_BRACKET_OPEN: '(';
//FORMULA_BRACKET_CLOSE: ')' -> mode(DEFAULT_MODE);
