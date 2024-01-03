parser grammar CatrobatParameterParser;

options {
	tokenVocab = CatrobatParameterLexer;
}

argument: expression EOF;

expression
	: additiveExpression
	;

additiveExpression
    :   multiplicativeExpression (additiveOperator multiplicativeExpression)*
    ;

multiplicativeExpression
    :   comparisonExpression (multiplicativeOperator comparisonExpression)*
    ;

comparisonExpression
	: simpleExpression (comparisonOperator simpleExpression)*
	;

additiveOperator: OPERATOR_NUMERIC_ADD | OPERATOR_NUMERIC_MINUS | OPERATOR_LOGIC_OR;

multiplicativeOperator: OPERATOR_NUMERIC_MULTIPLY | OPERATOR_NUMERIC_DIVIDE | OPERATOR_LOGIC_AND;

comparisonOperator: OPERATOR_LOGIC_EQUAL | OPERATOR_LOGIC_NOT_EQUAL | OPERATOR_LOGIC_LOWER | OPERATOR_LOGIC_GREATER | OPERATOR_LOGIC_LOWER_EQUAL | OPERATOR_LOGIC_GREATER_EQUAL;

simpleExpression
	: literal
	| sensorPropertyOrMethodInvocation
	| unaryExpression
	| BRACE_OPEN expression BRACE_CLOSE
	;

sensorPropertyOrMethodInvocation: SENSOR_OR_PROPERTY_OR_METHOD methodParameters?;


methodParameters: BRACE_OPEN parameterList BRACE_CLOSE;
parameterList: expression (COMMA expression)*;

unaryExpression: (OPERATOR_NUMERIC_ADD | OPERATOR_NUMERIC_MINUS | OPERATOR_LOGIC_NOT) simpleExpression;

literal
	: NUMBER
	| VARIABLE
	| LIST
	| UDB_PARAMETER
	| STRING
	;