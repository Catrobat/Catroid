parser grammar CatrobatParameterParser;

options {
	tokenVocab = CatrobatParameterLexer;
}

argument: expression EOF;

expression
	: additiveExpression
	| expression OPERATOR_LOGIC_EQUAL expression
	| expression OPERATOR_LOGIC_NOT_EQUAL expression
	| expression OPERATOR_LOGIC_LOWER expression
	| expression OPERATOR_LOGIC_GREATER expression
	| expression OPERATOR_LOGIC_LOWER_EQUAL expression
	| expression OPERATOR_LOGIC_GREATER_EQUAL expression
	;

additiveExpression
    :   multiplicativeExpression (additiveOperator multiplicativeExpression)*
    ;

multiplicativeExpression
    :   simpleExpression (multiplicativeOperator simpleExpression)*
    ;

additiveOperator: OPERATOR_NUMERIC_ADD | OPERATOR_NUMERIC_MINUS | OPERATOR_LOGIC_OR;

multiplicativeOperator: OPERATOR_NUMERIC_MULTIPLY | OPERATOR_NUMERIC_DIVIDE | OPERATOR_LOGIC_AND;

simpleExpression
	: literal
	| sensorPropertyOrMethodInvocation
	| unaryExpression
	| BRACE_OPEN expression BRACE_CLOSE
	;

sensorPropertyOrMethodInvocation: SENSOR_OR_PROPERTY_OR_METHOD methodParameters?;


methodParameters: BRACE_OPEN parameterList BRACE_CLOSE;
parameterList: expression (COMMA expression)*;

unaryExpression: (OPERATOR_NUMERIC_ADD | OPERATOR_NUMERIC_MINUS | OPERATOR_LOGIC_NOT) expression;

literal
	: NUMBER
	| VARIABLE
	| LIST
	| UDB_PARAMETER
	| STRING
	;