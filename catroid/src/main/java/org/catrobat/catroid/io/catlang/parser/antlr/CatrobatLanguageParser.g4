parser grammar CatrobatLanguageParser;

options {
	tokenVocab = CatrobatLanguageLexer;
}

program: programHeader programBody;
programHeader: PROGRAM_START;
programBody
	: PROGRAM
	  STRING
	  CURLY_BRACKET_OPEN
	  metadata
	  scene+
	  CURLY_BRACKET_CLOSE
	;

metadata
	: METADATA
      CURLY_BRACKET_OPEN
      metadataContent? (SEPARATOR metadataContent)*
      CURLY_BRACKET_CLOSE
	;

metadataContent: (description | catrobatVersion | catrobatAppVersion);
description: DESCRIPTION COLON STRING;
catrobatVersion: CATROBAT_VERSION COLON STRING;
catrobatAppVersion: CATRPBAT_APP_VERSION COLON STRING;

scene
	: SCENE
	  STRING
	  CURLY_BRACKET_OPEN
	  background
//	  actor*
	  CURLY_BRACKET_CLOSE
	;

background
	: BACKGROUND
	  CURLY_BRACKET_OPEN
	  actorContent
	  CURLY_BRACKET_CLOSE
	;

//actorContent: (looks | localVariables | sounds | scripts)+;
actorContent: scripts*;
scripts: SCRIPTS CURLY_BRACKET_OPEN script* CURLY_BRACKET_CLOSE;
script: BRICK_NAME (SEMICOLON | conditional_brick);

conditional_brick
	: BRICK_MODE_BRACKET_OPEN
	  arg_list
	  PARAM_MODE_BRACKET_CLOSE
	  SEMICOLON
	;

arg_list: argument (PARAM_SEPARATOR argument)* ;
argument
	: PARAM_MODE_NAME
	  PARAM_MODE_COLON
	  PARAM_MODE_BRACKET_OPEN
	  formula
	  FORMULA_MODE_BRACKET_CLOSE
	;

formula
	: (FORMULA_MODE_BRACKET_OPEN
	| FORMULA_MODE_BRACKET_CLOSE
	| FORMULA_MODE_ANYTHING
	| FORMULA_MODE_APOSTROPHE | ESCAPE_MODE_APOSTROPHE_CHAR
	| FORMULA_MODE_QUOTE | ESCAPE_MODE_QUOTE_CHAR
	| FORMULA_MODE_UDB_PARAM | ESCAPE_UDB_PARAM_MODE_CHAR)*
	;