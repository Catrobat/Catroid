parser grammar CatrobatParameterParser;

options {
	tokenVocab = CatrobatParameterLexer;
}

argument: expression;

expression
	: simple_expression
	| expression OPERATOR_ADD expression
	| expression OPERATOR_NUMERIC_DIVIDE expression
	| expression OPERATOR_NUMERIC_MULTIPLY expression
	| expression OPERATOR_LOGIC_AND expression
	| expression OPERATOR_LOGIC_OR expression
	| expression OPERATOR_LOGIC_NOT expression
	| expression OPERATOR_LOGIC_EQUAL expression
	| expression OPERATOR_LOGIC_NOT_EQUAL expression
	| expression OPERATOR_LOGIC_LOWER expression
	| expression OPERATOR_LOGIC_GREATER expression
	| expression OPERATOR_LOGIC_LOWER_EQUAL expression
	| expression OPERATOR_LOGIC_GREATER_EQUAL expression
	;

simple_expression
	: literal
	| sensor_reference
	| method_invoaction
	| unary_number
	| BRACE_OPEN expression BRACE_CLOSE
	;

sensor_reference: FUNCTION_OR_SENSOR;

method_invoaction: FUNCTION_OR_SENSOR parameters?;

parameters: BRACE_OPEN param_list BRACE_CLOSE;
param_list: expression (COMMA expression)*;

unary_number: (OPERATOR_ADD | OPERATOR_LOGIC_NOT) expression;

literal
	: NUMBER
	| VARIABLE
	| LIST
	| UDB_PARAMETER
	| STRING
	;

//options {
//	tokenVocab = CatrobatFormulaLexer;
//}
//
//formula
//	: BRACKET_OPEN formula BRACKET_CLOSE 		#parenthesis
//	| formula_element (OPERATOR formula)*       #operators
//	;
//
//formula_element
//	: NUMBER										    #number
//	| STRING											#string
//	| VARIABLE_REF										#variable
//	| LIST_REF											#list
//	| UDB_PARAMETER										#udbParameter
//	| FUNCTION_NAME	(FORMULA_BRACKET_OPEN FORMULA_BRACKET_CLOSE)?	#functionCall
//	;