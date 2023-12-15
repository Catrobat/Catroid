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
	  ( metadata | stage | globals | multiplayerVariables )*
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

stage
	: STAGE
	  CURLY_BRACKET_OPEN
	  stageContent? (SEPARATOR stageContent)*
	  CURLY_BRACKET_CLOSE
	;

stageContent: (landscapeMode | height | width | displayMode);
landscapeMode: LANDSCAPE_MODE COLON STRING;
height: HEIGHT COLON STRING;
width: WIDTH COLON STRING;
displayMode: DISPLAY_MODE COLON STRING;

globals
	: GLOBALS
	  CURLY_BRACKET_OPEN
	  variableOrListDeclaration? (SEPARATOR variableOrListDeclaration)*
	  CURLY_BRACKET_CLOSE
	;

multiplayerVariables
	: MULTIPLAYER_VARIABLES
	  CURLY_BRACKET_OPEN
	  variableDeclaration? (SEPARATOR variableDeclaration)*
	  CURLY_BRACKET_CLOSE
    ;

variableOrListDeclaration: variableDeclaration | LIST_REF;
variableDeclaration: VARIABLE_REF;

scene
	: SCENE
	  STRING
	  CURLY_BRACKET_OPEN
	  background
	  actor*
	  CURLY_BRACKET_CLOSE
	;

background
	: BACKGROUND
	  CURLY_BRACKET_OPEN
	  actorContent
	  CURLY_BRACKET_CLOSE
	;

actor
	: ACTOR_OR_OBJECT
	  STRING
	  OF_TYPE
	  STRING
	  CURLY_BRACKET_OPEN
	  actorContent
	  CURLY_BRACKET_CLOSE
	;

actorContent: (localVariables | looks | sounds | scripts | userDefinedScripts)+;

localVariables
	: LOCAL_VARIABLES
	  CURLY_BRACKET_OPEN
	  variableOrListDeclaration? (SEPARATOR variableOrListDeclaration)*
	  CURLY_BRACKET_CLOSE
	;

looks
	: LOOKS
	  CURLY_BRACKET_OPEN
	  looksAndSoundsContent? (SEPARATOR looksAndSoundsContent)*
	  CURLY_BRACKET_CLOSE
	;

sounds
	: SOUNDS
	  CURLY_BRACKET_OPEN
	  looksAndSoundsContent? (SEPARATOR looksAndSoundsContent)*
	  CURLY_BRACKET_CLOSE
	;

looksAndSoundsContent: STRING COLON STRING;

scripts
	: SCRIPTS
	  CURLY_BRACKET_OPEN
	  brick_with_body*
	  CURLY_BRACKET_CLOSE
	;

userDefinedScripts
	: USER_DEFINED_SCRIPTS
	  CURLY_BRACKET_OPEN
	  userDefinedScript*
	  CURLY_BRACKET_CLOSE
	;

userDefinedScript
	: UDB_DEFINE userDefinedBrick (UDB_DEFINITION_SCREEN_REFRESH | UDB_DEFINITION_NO_SCREEN_REFRESH) UDB_BODY_OPEN
	  brick_defintion*
	  CURLY_BRACKET_CLOSE
	| DISABLED_BRICK_INDICATION
	  UDB_DEFINE userDefinedBrick (UDB_DEFINITION_SCREEN_REFRESH | UDB_DEFINITION_NO_SCREEN_REFRESH) UDB_BODY_OPEN
	  brick_defintion*
	  DISABLED_BRICK_INDICATION
	  CURLY_BRACKET_CLOSE
	  ;

brick_defintion
	: DISABLED_BRICK_INDICATION? NOTE_BRICK
	| brick_invocation
	| brick_with_body
	;

userDefinedBrick
	: UDB_START
	  userDefinedBrickPart+
	  UDB_END
	;

userDefinedBrickPart
	: UDB_PARAM_START UDB_PARAM_TEXT UDB_PARAM_END
	| UDB_LABEL
	;

brick_with_body
	: BRICK_NAME
	  brick_condition?
	  BRICK_BODY_OPEN
	  brick_defintion*
	  CURLY_BRACKET_CLOSE
	| DISABLED_BRICK_INDICATION
	  BRICK_NAME
	  brick_condition?
	  BRICK_BODY_OPEN
	  brick_defintion*
	  DISABLED_BRICK_INDICATION
	  CURLY_BRACKET_CLOSE
	;

brick_invocation
	: DISABLED_BRICK_INDICATION?
	  (BRICK_NAME | userDefinedBrick)
	  brick_condition?
	  (SEMICOLON | UDB_SEMICOLON)
	;

brick_condition
	: (BRICK_MODE_BRACKET_OPEN | UDB_MODE_BRACKET_OPEN)
	  arg_list
	  PARAM_MODE_BRACKET_CLOSE
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