lexer grammar CatrobatLanguageLexer;
import CatrobatFormulaLexer;

options {
	language = Java;
}

fragment LETTER : LOWERCASE | UPPERCASE;
fragment UPPERCASE : 'A'..'Z';
fragment LOWERCASE : 'a'..'z';
fragment DIGIT : '0'..'9';
NUMBER: DIGIT+ ('.' DIGIT+)?;
WS: [ \t\r\n]+ -> skip;
CURLY_BRACKET_OPEN: '{';
CURLY_BRACKET_CLOSE: '}';

mode DEFAULT_MODE;
STRING : '\'' (~[\n\r] | STRING_ESCAPE)* '\'';
fragment STRING_ESCAPE : '\\' [nrtbf'\\];
SEPARATOR: ',';
COLON: ':';

PROGRAM_START : '#!' (' ')+ 'Catrobat Language Version' (' ')+ NUMBER;
PROGRAM: 'Program';

METADATA: 'Metadata';
DESCRIPTION: 'Description';
CATROBAT_VERSION: 'Catrobat version';
CATRPBAT_APP_VERSION: 'Catrobat app version';

SCENE: 'Scene';
BACKGROUND: 'Background';
SCRIPTS: 'Scripts';

BRICK_NAME: LETTER+ (' ' LETTER+)* -> mode(BRICK_MODE);

mode BRICK_MODE;
BRICK_MODE_WS: [ \t\r\n]+ -> skip;
BRICK_BRACKET_OPEN: '(' -> mode(PARAM_MODE);
BRICK_BRACKET_CLOSE: ')' -> mode(DEFAULT_MODE);
SEMICOLON: ';' -> mode(DEFAULT_MODE);

mode PARAM_MODE;
PARAM_MODE_WS: [ \t\r\n]+ -> skip;
PARAM_BRACKET_OPEN: '(' -> pushMode(FORMULA_MODE);
PARAM_BRACKET_CLOSE: ')' -> mode(BRICK_MODE);
PARAM_NAME: LETTER+ (' ' LETTER+)*;
PARAM_COLON: ':';



