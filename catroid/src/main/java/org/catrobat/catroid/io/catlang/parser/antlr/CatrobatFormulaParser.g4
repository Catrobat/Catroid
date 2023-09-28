parser grammar CatrobatFormulaParser;

options {
	tokenVocab = CatrobatFormulaLexer;
}

formula
   : FORMULA_NUMBER
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