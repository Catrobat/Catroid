lexer grammar CatrobatLanguageLexer;

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
//STRING : '\'' (~[\n\r] | STRING_ESCAPE)* '\'';
//fragment STRING_ESCAPE : '\\' [nrtbf'\\];
STRING: '\'' ( ~['] | '\\\'')* '\'';
SEPARATOR: ',';
COLON: ':';
VARIABLE_REF: '"' (~[\n\r"] | VAR_ESCAPE)* '"';
fragment VAR_ESCAPE : '\\' [nrtbf"\\];
LIST_REF: '*' (~[\n\r*] | LIST_ESCAPE)* '*';
fragment LIST_ESCAPE : '\\' [nrtbf*\\];
//VARIABLE_NAME_OR_CONTENT: '\'' ( ~['] | '\\\'' | '\\' . )* '\'';

PROGRAM_START : '#!' (' ')+ 'Catrobat Language Version' (' ')+ NUMBER;
PROGRAM: 'Program';

METADATA: 'Metadata';
DESCRIPTION: 'Description';
CATROBAT_VERSION: 'Catrobat version';
CATRPBAT_APP_VERSION: 'Catrobat app version';
STAGE: 'Stage';
LANDSCAPE_MODE: 'Landscape mode';
HEIGHT: 'Height';
WIDTH: 'Width';
DISPLAY_MODE: 'Display mode';
GLOBALS: 'Globals';
MULTIPLAYER_VARIABLES: 'Multiplayer variables';
LOCAL_VARIABLES: 'Locals';
LOOKS: 'Looks';
SOUNDS: 'Sounds';
ACTOR_OR_OBJECT: 'Actor or object';
OF_TYPE: 'of type';
SCENE: 'Scene';
BACKGROUND: 'Background';
SCRIPTS: 'Scripts';
USER_DEFINED_SCRIPTS: 'User Defined Bricks';
UDB_DEFINE: 'Define';

NOTE_BRICK: '#' (~[\n\r] | NOTE_BRICK_ESCAPE)*;
fragment NOTE_BRICK_ESCAPE : '\\' [nrtbf\\];
DISABLED_BRICK_INDICATION: '//';
BRICK_NAME: LETTER+ (' ' LETTER+)* -> mode(BRICK_MODE);
//USER_DEFINED_BRICK: '`' ~('(' | '{')+ -> mode(USER_DEFINED_BRICK_MODE);
UDB_START: '`' -> mode(UDB_MODE);

mode BRICK_MODE;
BRICK_MODE_WS: [ \t\r\n]+ -> skip;
BRICK_MODE_BRACKET_OPEN: '(' -> pushMode(PARAM_MODE);
SEMICOLON: ';' -> mode(DEFAULT_MODE);
BRICK_BODY_OPEN: '{' -> mode(DEFAULT_MODE);

mode UDB_MODE;
UDB_MODE_WS: [\t\r\n]+ -> skip;
UDB_PARAM_START: '[' -> pushMode(UDB_PARAM_MODE);
UDB_LABEL: ~('[' | '`' | ']')+;
UDB_END: '`' -> mode(UDB_AFTER_MODE);

mode UDB_PARAM_MODE;
UDB_PARAM_MODE_WS: [\t\r\n]+ -> skip;
UDB_PARAM_TEXT: ~(']')+;
UDB_PARAM_END: ']' -> popMode;

mode UDB_AFTER_MODE;
UDB_AFTER_MODE_WS: [ \t\r\n]+ -> skip;
UDB_MODE_BRACKET_OPEN: '(' -> pushMode(PARAM_MODE);
UDB_SEMICOLON: ';' -> mode(DEFAULT_MODE);
UDB_DEFINITION_SCREEN_REFRESH: 'with screen refresh as';
UDB_DEFINITION_NO_SCREEN_REFRESH: 'without screen refresh as';
UDB_BODY_OPEN: '{' -> mode(DEFAULT_MODE);

mode PARAM_MODE;
PARAM_MODE_WS: [ \t\r\n]+ -> skip;
PARAM_MODE_BRACKET_OPEN: '(' -> pushMode(FORMULA_MODE);
PARAM_MODE_BRACKET_CLOSE: ')' -> popMode;// mode(BRICK_MODE);
PARAM_MODE_NAME: '[' LETTER+ (' ' LETTER+)* ']' | LETTER+ (' ' LETTER+)*;
PARAM_MODE_COLON: ':';
PARAM_SEPARATOR: ',';

mode FORMULA_MODE;
FORMULA_MODE_WS: [ \t\r\n]+ -> skip;
FORMULA_MODE_BRACKET_CLOSE: ')' -> popMode;

// >ignore everything< except for the brackets
FORMULA_MODE_BRACKET_OPEN: '(' -> pushMode(FORMULA_MODE);
FORMULA_MODE_ANYTHING: ~('\'' | '"' | '[' | ']' | '(' | ')')+ -> skip;
FORMULA_MODE_APOSTROPHE: '\'' -> pushMode(ESCAPE_MODE_APOSTROPHE);
FORMULA_MODE_QUOTE: '"' -> pushMode(ESCAPE_MODE_QUOTE);
FORMULA_MODE_UDB_PARAM: '[' -> pushMode(ESCAPE_UDB_PARAM_MODE);

mode ESCAPE_MODE_APOSTROPHE;
ESCAPE_MODE_APOSTROPHE_ANYTHING: ~('\'')+ -> skip;
ESCAPE_MODE_APOSTROPHE_CHAR: '\'' -> popMode;

mode ESCAPE_MODE_QUOTE;
ESCAPE_MODE_QUOTE_ANYTHING: ~('"')+ -> skip;
ESCAPE_MODE_QUOTE_CHAR: '"' -> popMode;

mode ESCAPE_UDB_PARAM_MODE;
ESCAPE_UDB_PARAM_MODE_ANYTHING: ~(']')+ -> skip;
ESCAPE_UDB_PARAM_MODE_CHAR: ']' -> popMode;
// end of >ignore everything<

//mode USER_DEFINED_BRICK_PARAM_MODE;
//USER_DEFINED_BRICK_PARAM_MODE_WS: [ \t\r\n]+ -> skip;
//USER_DEFINED_BRICK_TEXT: ~[`]+;
//USER_DEFINED_BRICK_PARAM_START: '`' -> pushMode(PARAM_MODE);
//USER_DEFINED_BRICK_SEMICOLON: ';' -> mode(DEFAULT_MODE);