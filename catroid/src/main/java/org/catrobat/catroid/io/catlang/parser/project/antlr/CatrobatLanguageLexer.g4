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
SCRIPTS: 'Scripts' -> mode(SCRIPTS_MODE);
USER_DEFINED_SCRIPTS: 'User Defined Bricks' -> mode(USER_DEFINED_SCRIPTS_MODE);

mode SCRIPTS_MODE;
SCRIPTS_WS: [ \t\r\n]+ -> skip;
SCRIPT_DISABLED_INDICATOR: '//';
SCRIPTS_START: '{';
SCRIPT_NAME: LETTER+ (' '? (LETTER+ | NUMBER | ',' | '.'))* -> pushMode(BRICK_MODE);
SCRIPTS_END: '}' -> mode(DEFAULT_MODE);

mode USER_DEFINED_SCRIPTS_MODE;
USER_DEFINED_SCRIPTS_WS: [ \t\r\n]+ -> skip;
USER_DEFINED_SCRIPTS_START: '{';
UDB_DEFINE: 'Define';
USER_DEFINED_SCRIPT_UDB_START: '`' -> pushMode(UDB_MODE);
USER_DEFINED_SCRIPTS_END: '}' -> mode(DEFAULT_MODE);

mode BRICK_LIST_MODE;
BRICK_LIST_WS: [ \t]+ -> skip;
BRICKLIST_NEWLINE: '\r'? '\n';
BRICK_NAME: LETTER+ (' '? (LETTER+ | NUMBER | ',' | '.'))* -> pushMode(BRICK_MODE);
UDB_START: '`' -> pushMode(UDB_MODE);
BRICK_LIST_DISABLED_INDICATOR: '//';
NOTE_BRICK: '#' (~[\n\r] | NOTE_BRICK_ESCAPE)*;
fragment NOTE_BRICK_ESCAPE : '\\' [nrtbf\\];
BRICK_LIST_END_ELSE: '}' (' ' | '\t')+ 'Else' -> mode(BRICK_MODE);
BRICK_LIST_END: '}' -> popMode;

mode BRICK_MODE;
BRICK_MODE_WS: [ \t]+ -> skip;
BRICK_MODE_BRACKET_OPEN: '(' -> pushMode(PARAM_MODE);
SEMICOLON: ';' -> popMode;
BRICK_BODY_OPEN: '{' -> mode(BRICK_LIST_MODE);

mode UDB_MODE;
UDB_MODE_WS: [\t\r\n]+ -> skip;
UDB_PARAM_START: '[' -> pushMode(UDB_PARAM_MODE);
UDB_LABEL: ~('[' | '`' | ']')+;
UDB_END: '`' -> mode(UDB_AFTER_MODE);

mode UDB_PARAM_MODE;
UDB_PARAM_MODE_WS: [\t]+ -> skip;
UDB_PARAM_TEXT: (~(']') | '\\]')+;
UDB_PARAM_END: ']' -> popMode;

mode UDB_AFTER_MODE;
UDB_AFTER_MODE_WS: [ \t]+ -> skip;
UDB_MODE_BRACKET_OPEN: '(' -> pushMode(PARAM_MODE);
UDB_SEMICOLON: ';' -> popMode;
UDB_DEFINITION_SCREEN_REFRESH: 'with screen refresh as';
UDB_DEFINITION_NO_SCREEN_REFRESH: 'without screen refresh as';
UDB_BODY_OPEN: '{' -> mode(BRICK_LIST_MODE);

mode PARAM_MODE;
PARAM_MODE_WS: [ \t]+ -> skip;
PARAM_MODE_BRACKET_OPEN: '(' -> pushMode(FORMULA_MODE);
PARAM_MODE_BRACKET_CLOSE: ')' -> popMode;
PARAM_MODE_NAME: LETTER+ (' ' | LETTER  | NUMBER | '²' | '/')*;
PARAM_MODE_UDB_NAME: '[' (~(']') | '\\]')+ ']';
PARAM_MODE_COLON: ':';
PARAM_SEPARATOR: ',';

mode FORMULA_MODE;
FORMULA_MODE_WS: [\t]+ -> skip;
FORMULA_MODE_BRACKET_CLOSE: ')' -> popMode;

// >ignore everything< except for the brackets
FORMULA_MODE_BRACKET_OPEN: '(' -> pushMode(FORMULA_MODE);
FORMULA_MODE_ANYTHING: ~('\'' | '"' | '[' | ']' | '(' | ')' | '*')+;
FORMULA_MODE_STRING_BEGIN: '\'' -> pushMode(FORMULA_STRING_MODE);
FORMULA_MODE_VARIABLE_BEGIN: '"' -> pushMode(FORMULA_VARIABLE_MODE);
FORMULA_MODE_UDB_PARAM_BEGIN: '[' -> pushMode(FORMULA_UDB_PARAM_MODE);
FORMULA_LIST_MODE_BEGIN: '*' -> pushMode(FORMULA_LIST_MODE);

mode FORMULA_STRING_MODE;
FORMULA_STRING_MODE_ANYTHING: (~('\'') | '\\\'')+;
FORMULA_STRING_MODE_END: '\'' -> popMode;

mode FORMULA_VARIABLE_MODE;
FORMULA_VARIABLE_MODE_ANYTHING: (~('"') | '\\"')+;
FORMULA_VARIABLE_MODE_END: '"' -> popMode;

mode FORMULA_UDB_PARAM_MODE;
FORMULA_UDB_PARAM_MODE_ANYTHING: (~(']') | '\\]')+;
FORMULA_UDB_PARAM_MODE_END: ']' -> popMode;

mode FORMULA_LIST_MODE;
FORMULA_LIST_MODE_ANYTHING: (~('*') | '\\*')+;
FORMULA_LIST_MODE_END: '*' -> popMode;
