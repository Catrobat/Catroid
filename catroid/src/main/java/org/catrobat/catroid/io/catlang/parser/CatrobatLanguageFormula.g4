grammar CatrobatLanguageFormula;

WS : [ \t\r\n]+ -> skip;

fragment LETTER : LOWERCASE | UPPERCASE;
fragment UPPERCASE : 'A'..'Z';
fragment LOWERCASE : 'a'..'z';
fragment DIGIT : '0'..'9';

formula
	: '(' formula ')' 					 		#parenthesis
	| formula_element (OPERATORS formula)*      #operators
	;

formula_element
	: NUMBER										#number
	| STRING										#string
	| VARIABLE_REF									#variable
	| LIST_REF										#list
	| UDB_PARAMETER									#udbParameter
//	| BOOLEAN										#boolean
//	| SENSOR										#sensor
//	| CONSTANT										#constant
	| FUNCTION_NAME	'(' formula (',' formula)* ')'	#functionCall
//	| function_call						#functionCall
	;

NUMBER
	: '-'? DIGIT+ ('.' DIGIT+)?
	;

STRING
	: '\'' (~[\n\r] | STRING_ESCAPE)* '\''
	;
fragment STRING_ESCAPE
	: '\\' [nrtbf'\\]
	;

VARIABLE_REF
	: '"' (~[\n\r] | VAR_ESCAPE)* '"'
	;
fragment VAR_ESCAPE
	: '\\' [nrtbf"\\]
	;

LIST_REF
	: '*' (~[\n\r] | LIST_ESCAPE)* '*'
	;
fragment LIST_ESCAPE
	: '\\' [nrtbf*\\]
	;

UDB_PARAMETER
	: '[' (~'[' | '\\[' | ~']' | '\\]')* ']'
	;

FUNCTION_NAME: [a-zA-Z0-9 ]+;

OPERATORS
	: '/'
	| '='
	| '<'
	| '<='
	| '>'
	| '>='
	| 'and'
	| 'not'
	| 'or'
	| '-'
	| '*' | '×'
	| '+'
	| '!=' | '≠'
	;

//BOOLEAN
//	: 'true'
//	| 'false'
//	;

//CONSTANT
//	: 'pi'
//	;


//function_call
//	: one_param_function   '(' formula ')'
//	| two_param_function   '(' formula ',' formula ')'
//	| three_param_function '(' formula ',' formula ',' formula ')'
//	| functions_join
//	;

//one_param_function
//	: 'abs'
//    | 'arccos'
//    | 'arcsin'
//    | 'arctan'
//    | 'arctangent2'
//    | 'arduino analog pin'
//    | 'arduino digital pin'
//    | 'ceiling'
//    | 'cos'
//    | 'decimal logarithm'
//    | 'exp'
//    | 'flatten'
//    | 'floor'
//    | 'ID of detected object'
//    | 'index of current touch'
//    | 'length'
//    | 'ln'
//    | 'log'
//    | 'number of items'
//    | 'object with ID visible'
//    | 'raspberry pi pin'
//    | 'round'
//    | 'sine'
//    | 'square root'
//    | 'stage is touched'
//    | 'stage touch x'
//    | 'stage touch y'
//    | 'tan'
//    | 'touches color'
//	;
//
//two_param_function
//	: 'colour at x y'
//    | 'colour touches colour'
//    | 'contains'
//    | 'item'
//    | 'item\'s index'
//    | 'letter'
//    | 'maximum of'
//    | 'minimum of'
//    | 'mod'
//    | 'power'
//    | 'random value from to'
//    | 'regular expression'
//	;
//
//three_param_function
//	: 'if then else'
//	| 'colour equals colour with % tolerance'
//	;
//
//functions_join
//	: 'join' '(' formula ',' formula (',' formula)? ')'
//	;

//SENSOR: SENSORS_ALTITUDE | SENSORS_COLLIDES_WITH_EDGE | SENSORS_COLLIDES_WITH_FINGER | SENSORS_COMPASS_DIRECTION | SENSORS_DATE_DAY | SENSORS_DATE_MONTH | SENSORS_DATE_WEEKDAY | SENSORS_DATE_YEAR | SENSORS_DRONE_BATTERY_STATUS | SENSORS_DRONE_CAMERA_READY | SENSORS_DRONE_EMERGENCY_STATE |
//SENSORS_DRONE_FLYING | SENSORS_DRONE_INITIALIZED | SENSORS_DRONE_NUM_FRAMES | SENSORS_DRONE_RECORD_READY | SENSORS_DRONE_RECORDING | SENSORS_DRONE_USB_ACTIVE | SENSORS_DRONE_USB_REMAINING_TIME | SENSORS_EV3_SENSOR_1 | SENSORS_EV3_SENSOR_2 | SENSORS_EV3_SENSOR_3 | SENSORS_EV3_SENSOR_4 | SENSORS_FACE_DETECTED | SENSORS_FACE_SIZE | SENSORS_FINGER_X | SENSORS_FINGER_Y | SENSORS_GAMEPAD_A_PRESSED | SENSORS_GAMEPAD_B_PRESSED | SENSORS_GAMEPAD_DOWN_PRESSED | SENSORS_GAMEPAD_LEFT_PRESSED | SENSORS_GAMEPAD_RIGHT_PRESSED | SENSORS_GAMEPAD_UP_PRESSED | SENSORS_HEAD_TOP_X | SENSORS_HEAD_TOP_Y | SENSORS_LAST_FINGER_INDEX | SENSORS_LATITUDE | SENSORS_LEFT_ANKLE_X | SENSORS_LEFT_ANKLE_Y | SENSORS_LEFT_EAR_X | SENSORS_LEFT_EAR_Y | SENSORS_LEFT_ELBOW_X | SENSORS_LEFT_ELBOW_Y | SENSORS_LEFT_EYE_CENTER_X | SENSORS_LEFT_EYE_CENTER_Y | SENSORS_LEFT_EYE_INNER_X | SENSORS_LEFT_EYE_INNER_Y | SENSORS_LEFT_EYE_OUTER_X | SENSORS_LEFT_EYE_OUTER_Y | SENSORS_LEFT_FOOT_INDEX_X | SENSORS_LEFT_FOOT_INDEX_Y | SENSORS_LEFT_HEEL_X | SENSORS_LEFT_HEEL_Y | SENSORS_LEFT_HIP_X | SENSORS_LEFT_HIP_Y | SENSORS_LEFT_INDEX_X | SENSORS_LEFT_INDEX_Y | SENSORS_LEFT_KNEE_X | SENSORS_LEFT_KNEE_Y | SENSORS_LEFT_PINKY_X | SENSORS_LEFT_PINKY_Y | SENSORS_LEFT_SHOULDER_X | SENSORS_LEFT_SHOULDER_Y | SENSORS_LEFT_THUMB_X | SENSORS_LEFT_THUMB_Y | SENSORS_LEFT_WRIST_X | SENSORS_LEFT_WRIST_Y | SENSORS_LOCATION_ACCURACY | SENSORS_LONGITUDE | SENSORS_LOOK_DIRECTION | SENSORS_LOUDNESS | SENSORS_MOTION_DIRECTION | SENSORS_MOUTH_LEFT_CORNER_X | SENSORS_MOUTH_LEFT_CORNER_Y | SENSORS_MOUTH_RIGHT_CORNER_X | SENSORS_MOUTH_RIGHT_CORNER_Y | SENSORS_NECK_X | SENSORS_NECK_Y | SENSORS_NFC_TAG_ID | SENSORS_NFC_TAG_MESSAGE | SENSORS_NOSE_X | SENSORS_NOSE_Y | SENSORS_NUMBER_CURRENT_TOUCHES | SENSORS_NXT_SENSOR_1 | SENSORS_NXT_SENSOR_2 | SENSORS_NXT_SENSOR_3 | SENSORS_NXT_SENSOR_4 | SENSORS_OBJECT_ANGULAR_VELOCITY | SENSORS_OBJECT_BACKGROUND_NAME | SENSORS_OBJECT_BACKGROUND_NUMBER | SENSORS_OBJECT_BRIGHTNESS | SENSORS_OBJECT_COLOR | SENSORS_OBJECT_DISTANCE_TO | SENSORS_OBJECT_LAYER | SENSORS_OBJECT_LOOK_NAME | SENSORS_OBJECT_LOOK_NUMBER | SENSORS_OBJECT_NUMBER_OF_LOOKS | SENSORS_OBJECT_SIZE | SENSORS_OBJECT_TRANSPARENCY | SENSORS_OBJECT_X | SENSORS_OBJECT_Y | SENSORS_OBJECT_X_VELOCITY | SENSORS_OBJECT_Y_VELOCITY | SENSORS_PHIRO_BOTTOM_LEFT | SENSORS_PHIRO_BOTTOM_RIGHT | SENSORS_PHIRO_FRONT_LEFT | SENSORS_PHIRO_FRONT_RIGHT | SENSORS_PHIRO_SIDE_LEFT | SENSORS_PHIRO_SIDE_RIGHT | SENSORS_RIGHT_ANKLE_X | SENSORS_RIGHT_ANKLE_Y | SENSORS_RIGHT_EAR_X | SENSORS_RIGHT_EAR_Y | SENSORS_RIGHT_ELBOW_X | SENSORS_RIGHT_ELBOW_Y | SENSORS_RIGHT_EYE_CENTER_X | SENSORS_RIGHT_EYE_CENTER_Y | SENSORS_RIGHT_EYE_INNER_X | SENSORS_RIGHT_EYE_INNER_Y | SENSORS_RIGHT_EYE_OUTER_X | SENSORS_RIGHT_EYE_OUTER_Y | SENSORS_RIGHT_FOOT_INDEX_X | SENSORS_RIGHT_FOOT_INDEX_Y | SENSORS_RIGHT_HEEL_X | SENSORS_RIGHT_HEEL_Y | SENSORS_RIGHT_HIP_X | SENSORS_RIGHT_HIP_Y | SENSORS_RIGHT_INDEX_X | SENSORS_RIGHT_INDEX_Y | SENSORS_RIGHT_KNEE_X | SENSORS_RIGHT_KNEE_Y | SENSORS_RIGHT_PINKY_X | SENSORS_RIGHT_PINKY_Y | SENSORS_RIGHT_SHOULDER_X | SENSORS_RIGHT_SHOULDER_Y | SENSORS_RIGHT_THUMB_X | SENSORS_RIGHT_THUMB_Y | SENSORS_RIGHT_WRIST_X | SENSORS_RIGHT_WRIST_Y | SENSORS_SECOND_FACE_DETECTED | SENSORS_SECOND_FACE_SIZE | SENSORS_SECOND_FACE_X | SENSORS_SECOND_FACE_Y | SENSORS_SPEECH_RECOGNITION_LANGUAGE | SENSORS_STAGE_HEIGHT | SENSORS_STAGE_WIDTH | SENSORS_TEXT_BLOCK_FROM_CAMERA | SENSORS_TEXT_BLOCK_LANGUAGE_FROM_CAMERA | SENSORS_TEXT_BLOCK_SIZE | SENSORS_TEXT_BLOCK_X | SENSORS_TEXT_BLOCK_Y | SENSORS_TEXT_BLOCKS_NUMBER | SENSORS_TEXT_FROM_CAMERA | SENSORS_TIME_HOUR | SENSORS_TIME_MINUTE | SENSORS_TIME_SECOND | SENSORS_TIMER | SENSORS_USER_LANGUAGE | SENSORS_X_ACCELERATION | SENSORS_X_INCLINATION | SENSORS_Y_ACCELERATION | SENSORS_Y_INCLINATION | SENSORS_Z_ACCELERATION | SENSORS_FACE_X | SENSORS_FACE_Y | SENSORS_FINGER_TOUCHED;
//SENSORS_ALTITUDE: 'altitude';
//SENSORS_COLLIDES_WITH_EDGE: 'touches edge';
//SENSORS_COLLIDES_WITH_FINGER: 'touches finger';
//SENSORS_COMPASS_DIRECTION: 'compass direction';
//SENSORS_DATE_DAY: 'day';
//SENSORS_DATE_MONTH: 'month';
//SENSORS_DATE_WEEKDAY: 'weekday';
//SENSORS_DATE_YEAR: 'year';
//SENSORS_DRONE_BATTERY_STATUS: 'drone battery status';
//SENSORS_DRONE_CAMERA_READY: 'drone camera ready';
//SENSORS_DRONE_EMERGENCY_STATE: 'drone emergency state';
//SENSORS_DRONE_FLYING: 'drone flying';
//SENSORS_DRONE_INITIALIZED: 'drone initialized';
//SENSORS_DRONE_NUM_FRAMES: 'drone camera number of frames';
//SENSORS_DRONE_RECORD_READY: 'drone record ready';
//SENSORS_DRONE_RECORDING: 'drone camera recording';
//SENSORS_DRONE_USB_ACTIVE: 'drone usb active';
//SENSORS_DRONE_USB_REMAINING_TIME: 'drone usb remaining time';
//SENSORS_EV3_SENSOR_1: 'EV3 sensor 1';
//SENSORS_EV3_SENSOR_2: 'EV3 sensor 2';
//SENSORS_EV3_SENSOR_3: 'EV3 sensor 3';
//SENSORS_EV3_SENSOR_4: 'EV3 sensor 4';
//SENSORS_FACE_DETECTED: 'face is visible';
//SENSORS_FACE_SIZE: 'face size';
//SENSORS_FINGER_X: 'stage touch x';
//SENSORS_FINGER_Y: 'stage touch y';
//SENSORS_GAMEPAD_A_PRESSED: 'gamepad A pressed';
//SENSORS_GAMEPAD_B_PRESSED: 'gamepad B pressed';
//SENSORS_GAMEPAD_DOWN_PRESSED: 'gamepad down pressed';
//SENSORS_GAMEPAD_LEFT_PRESSED: 'gamepad left pressed';
//SENSORS_GAMEPAD_RIGHT_PRESSED: 'gamepad right pressed';
//SENSORS_GAMEPAD_UP_PRESSED: 'gamepad up pressed';
//SENSORS_HEAD_TOP_X: 'head top x';
//SENSORS_HEAD_TOP_Y: 'head top y';
//SENSORS_LAST_FINGER_INDEX: 'last stage touch index';
//SENSORS_LATITUDE: 'latitude';
//SENSORS_LEFT_ANKLE_X: 'left ankle x';
//SENSORS_LEFT_ANKLE_Y: 'left ankle y';
//SENSORS_LEFT_EAR_X: 'left ear x';
//SENSORS_LEFT_EAR_Y: 'left ear y';
//SENSORS_LEFT_ELBOW_X: 'left elbow x';
//SENSORS_LEFT_ELBOW_Y: 'left elbow y';
//SENSORS_LEFT_EYE_CENTER_X: 'left eye center x';
//SENSORS_LEFT_EYE_CENTER_Y: 'left eye center y';
//SENSORS_LEFT_EYE_INNER_X: 'left eye inner x';
//SENSORS_LEFT_EYE_INNER_Y: 'left eye inner y';
//SENSORS_LEFT_EYE_OUTER_X: 'left eye outer x';
//SENSORS_LEFT_EYE_OUTER_Y: 'left eye outer y';
//SENSORS_LEFT_FOOT_INDEX_X: 'left foot index x';
//SENSORS_LEFT_FOOT_INDEX_Y: 'left foot index y';
//SENSORS_LEFT_HEEL_X: 'left heel x';
//SENSORS_LEFT_HEEL_Y: 'left heel y';
//SENSORS_LEFT_HIP_X: 'left hip x';
//SENSORS_LEFT_HIP_Y: 'left hip y';
//SENSORS_LEFT_INDEX_X: 'left index knuckle x';
//SENSORS_LEFT_INDEX_Y: 'left index knuckle y';
//SENSORS_LEFT_KNEE_X: 'left knee x';
//SENSORS_LEFT_KNEE_Y: 'left knee y';
//SENSORS_LEFT_PINKY_X: 'left pinky knuckle x';
//SENSORS_LEFT_PINKY_Y: 'left pinky knuckle y';
//SENSORS_LEFT_SHOULDER_X: 'left shoulder x';
//SENSORS_LEFT_SHOULDER_Y: 'left shoulder y';
//SENSORS_LEFT_THUMB_X: 'left thumb knuckle x';
//SENSORS_LEFT_THUMB_Y: 'left thumb knuckle y';
//SENSORS_LEFT_WRIST_X: 'left wrist x';
//SENSORS_LEFT_WRIST_Y: 'left wrist y';
//SENSORS_LOCATION_ACCURACY: 'location accuracy';
//SENSORS_LONGITUDE: 'longitude';
//SENSORS_LOOK_DIRECTION: 'look direction';
//SENSORS_LOUDNESS: 'loudness';
//SENSORS_MOTION_DIRECTION: 'motion direction';
//SENSORS_MOUTH_LEFT_CORNER_X: 'mouth left corner x';
//SENSORS_MOUTH_LEFT_CORNER_Y: 'mouth left corner y';
//SENSORS_MOUTH_RIGHT_CORNER_X: 'mouth right corner x';
//SENSORS_MOUTH_RIGHT_CORNER_Y: 'mouth right corner y';
//SENSORS_NECK_X: 'neck x';
//SENSORS_NECK_Y: 'neck y';
//SENSORS_NFC_TAG_ID: 'nfc tag id';
//SENSORS_NFC_TAG_MESSAGE: 'nfc tag message';
//SENSORS_NOSE_X: 'nose x';
//SENSORS_NOSE_Y: 'nose y';
//SENSORS_NUMBER_CURRENT_TOUCHES: 'number of current touches';
//SENSORS_NXT_SENSOR_1: 'NXT sensor 1';
//SENSORS_NXT_SENSOR_2: 'NXT sensor 2';
//SENSORS_NXT_SENSOR_3: 'NXT sensor 3';
//SENSORS_NXT_SENSOR_4: 'NXT sensor 4';
//SENSORS_OBJECT_ANGULAR_VELOCITY: 'angular velocity';
//SENSORS_OBJECT_BACKGROUND_NAME: 'background name';
//SENSORS_OBJECT_BACKGROUND_NUMBER: 'background number';
//SENSORS_OBJECT_BRIGHTNESS: 'brightness';
//SENSORS_OBJECT_COLOR: 'colour';
//SENSORS_OBJECT_DISTANCE_TO: 'distance to touch position';
//SENSORS_OBJECT_LAYER: 'layer';
//SENSORS_OBJECT_LOOK_NAME: 'look name';
//SENSORS_OBJECT_LOOK_NUMBER: 'look number';
//SENSORS_OBJECT_NUMBER_OF_LOOKS: 'number of looks';
//SENSORS_OBJECT_SIZE: 'size';
//SENSORS_OBJECT_TRANSPARENCY: 'transparency';
//SENSORS_OBJECT_X: 'position x';
//SENSORS_OBJECT_Y: 'position y';
//SENSORS_OBJECT_X_VELOCITY: 'x velocity';
//SENSORS_OBJECT_Y_VELOCITY: 'y velocity';
//SENSORS_PHIRO_BOTTOM_LEFT: 'phiro bottom left sensor';
//SENSORS_PHIRO_BOTTOM_RIGHT: 'phiro bottom right sensor';
//SENSORS_PHIRO_FRONT_LEFT: 'phiro front left sensor';
//SENSORS_PHIRO_FRONT_RIGHT: 'phiro front right sensor';
//SENSORS_PHIRO_SIDE_LEFT: 'phiro side left sensor';
//SENSORS_PHIRO_SIDE_RIGHT: 'phiro side right sensor';
//SENSORS_RIGHT_ANKLE_X: 'right ankle x';
//SENSORS_RIGHT_ANKLE_Y: 'right ankle y';
//SENSORS_RIGHT_EAR_X: 'right ear x';
//SENSORS_RIGHT_EAR_Y: 'right ear y';
//SENSORS_RIGHT_ELBOW_X: 'right elbow x';
//SENSORS_RIGHT_ELBOW_Y: 'right elbow y';
//SENSORS_RIGHT_EYE_CENTER_X: 'right eye center x';
//SENSORS_RIGHT_EYE_CENTER_Y: 'right eye center y';
//SENSORS_RIGHT_EYE_INNER_X: 'right eye inner x';
//SENSORS_RIGHT_EYE_INNER_Y: 'right eye inner y';
//SENSORS_RIGHT_EYE_OUTER_X: 'right eye outer x';
//SENSORS_RIGHT_EYE_OUTER_Y: 'right eye outer y';
//SENSORS_RIGHT_FOOT_INDEX_X: 'right foot index x';
//SENSORS_RIGHT_FOOT_INDEX_Y: 'right foot index y';
//SENSORS_RIGHT_HEEL_X: 'right heel x';
//SENSORS_RIGHT_HEEL_Y: 'right heel y';
//SENSORS_RIGHT_HIP_X: 'right hip x';
//SENSORS_RIGHT_HIP_Y: 'right hip y';
//SENSORS_RIGHT_INDEX_X: 'right index knuckle x';
//SENSORS_RIGHT_INDEX_Y: 'right index knuckle y';
//SENSORS_RIGHT_KNEE_X: 'right knee x';
//SENSORS_RIGHT_KNEE_Y: 'right knee y';
//SENSORS_RIGHT_PINKY_X: 'right pinky knuckle x';
//SENSORS_RIGHT_PINKY_Y: 'right pinky knuckle y';
//SENSORS_RIGHT_SHOULDER_X: 'right shoulder x';
//SENSORS_RIGHT_SHOULDER_Y: 'right shoulder y';
//SENSORS_RIGHT_THUMB_X: 'right thumb knuckle x';
//SENSORS_RIGHT_THUMB_Y: 'right thumb knuckle y';
//SENSORS_RIGHT_WRIST_X: 'right wrist x';
//SENSORS_RIGHT_WRIST_Y: 'right wrist y';
//SENSORS_SECOND_FACE_DETECTED: 'second face is visible';
//SENSORS_SECOND_FACE_SIZE: 'second face size';
//SENSORS_SECOND_FACE_X: 'second face x position';
//SENSORS_SECOND_FACE_Y: 'second face y position';
//SENSORS_SPEECH_RECOGNITION_LANGUAGE: 'listening language';
//SENSORS_STAGE_HEIGHT: 'stage height';
//SENSORS_STAGE_WIDTH: 'stage width';
//SENSORS_TEXT_BLOCK_FROM_CAMERA: 'text block from camera';
//SENSORS_TEXT_BLOCK_LANGUAGE_FROM_CAMERA: 'text block language from camera';
//SENSORS_TEXT_BLOCK_SIZE: 'text block size from camera';
//SENSORS_TEXT_BLOCK_X: 'text block x from camera';
//SENSORS_TEXT_BLOCK_Y: 'text block y from camera';
//SENSORS_TEXT_BLOCKS_NUMBER: 'number of text blocks';
//SENSORS_TEXT_FROM_CAMERA: 'text from camera';
//SENSORS_TIME_HOUR: 'hour';
//SENSORS_TIME_MINUTE: 'minute';
//SENSORS_TIME_SECOND: 'second';
//SENSORS_TIMER: 'timer';
//SENSORS_USER_LANGUAGE: 'user language';
//SENSORS_X_ACCELERATION: 'acceleration x';
//SENSORS_X_INCLINATION: 'inclination x';
//SENSORS_Y_ACCELERATION: 'acceleration y';
//SENSORS_Y_INCLINATION: 'inclination y';
//SENSORS_Z_ACCELERATION: 'acceleration z';
//SENSORS_FACE_X: 'face x position';
//SENSORS_FACE_Y: 'face y position';
//SENSORS_FINGER_TOUCHED: 'stage is touched';