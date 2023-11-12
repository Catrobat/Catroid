grammar CatrobatLanguage;
import CatrobatLanguageFormula;

WS : [ \t\r\n]+ -> skip;

fragment LETTER : LOWERCASE | UPPERCASE;
fragment UPPERCASE : 'A'..'Z';
fragment LOWERCASE : 'a'..'z';
fragment DIGIT : '0'..'9';

ASSIGN : '=';
RELOP : '<' | '>' | '<=' | '>=' | '==' | '!=';
MULOP : '*' | '/' | '%';
AND : '&&';
OR : '||';
ADDOP : '+' | '-';
NOT : '!';
DOTOP : '.';
QUESTION : '?';
COLON : ':';

program: programHeader programBody;
programHeader: '#!' ' Catrobat Language Version ' NUMBER;
programBody: 'Program' STRING '{' metadata stage globals multiplayerVariables? scene+ '}';

metadata: 'Metadata' '{' metadataContent? (',' metadataContent)* '}';
metadataContent: (description | catrobatVersion | catrobatAppVersion);
description: 'Description:' STRING;
catrobatVersion: 'Catrobat version:' STRING;
catrobatAppVersion: 'Catrobat app version:' STRING;

stage: 'Stage' '{' stageContent? (',' stageContent)* '}';
stageContent: (landscapeMode | height | width | displayMode);
landscapeMode: 'Landscape mode:' STRING;
height: 'Height:' STRING;
width: 'Width:' STRING;
displayMode: 'Display mode:' STRING;

globals: 'Globals' '{' (variableDeclaration? (',' variableDeclaration)*)? '}';
multiplayerVariables: 'Multiplayer variables' '{' (variableDeclaration? (',' variableDeclaration)*)? '}';
variableDeclaration: VARIABLE_REF | LIST_REF;

STRING : '\'' (~[\n\r'] | STRING_ESCAPE)* '\'';
fragment STRING_ESCAPE : '\\' [nrtbf'\\];

VARIABLE_REF: '"' (~[\n\r"] | VAR_ESCAPE)* '"';
fragment VAR_ESCAPE : '\\' [nrtbf"\\];

LIST_REF: '*' (~[\n\r*] | LIST_ESCAPE)* '*';
fragment LIST_ESCAPE : '\\' [nrtbf*\\];
LIST_OR_VARIABLE_NAME: VARIABLE_REF | LIST_REF;

UDB_PARAMETER: '[' (~'[' | '\\[' | ~']' | '\\]')* ']';
BRICK_NAME: UPPERCASE LETTER* (' ' LETTER+)*;
// TODO: ignore formula content somehow
//FORMULA_PARMETER: '(' (~'(' | '\\(' | ~')' | '\\)')* ')';

NUMBER: '-'? DIGIT+ ('.' DIGIT+)?;
//BOOLEAN: 'true' | 'false';

scene: 'Scene' STRING '{' background actor* '}';
background: 'Background' '{' looks? '}';
looks: 'Looks' '{' looksAndSoundsContent? '}';
sounds: 'Sounds' '{' looksAndSoundsContent? '}';
looksAndSoundsContent: STRING ':' STRING (',' STRING ':' STRING)*;

actor: 'Actor or object' STRING 'of type' STRING '{' actorContent '}';
actorContent: (looks | localVariables | sounds | scripts)+;

localVariables: 'Locals' '{' variableDeclaration? '}';

scripts: 'Scripts' '{' brick_with_body* '}';
brick_with_body: BRICK_NAME condition? '{' (brick_invocation | brick_with_body)* '}';
condition: '(' arg_list ')';
brick_invocation: BRICK_NAME condition? ';';
arg_list: argument (',' argument)* ;

argument: PARAMETER_NAME ':' '(' formula? ')';
PARAMETER_NAME: LOWERCASE+ (' ' LOWERCASE+)*;

//formula
//	: formula_element
////	| internal_function
//	| '(' formula ')'
//	| formula ADDOP formula
//	| formula MULOP formula
//	;

//formula_element
//	: NUMBER										    #number
//	| STRING											#string
//	| VARIABLE_REF										#variable
//	| LIST_REF											#list
//	| UDB_PARAMETER										#udbParameter
//	| BOOLEAN
//	| INTERNAL_VALUE
//	| FUNCTION_NAME	('(' formula (',' formula)* ')')?	#functionCall
//	;

//FUNCTION_NAME: [a-zA-Z0-9 ]+;

//internal_function : 'join' '(' INTERNAL_VALUE ',' STRING ',' INTERNAL_VALUE ')';