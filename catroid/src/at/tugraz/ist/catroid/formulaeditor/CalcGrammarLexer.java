// $ANTLR 3.4 src/CalcGrammar.g 2012-07-24 20:54:18
package at.tugraz.ist.catroid.formulaeditor;

import org.antlr.runtime.BaseRecognizer;
import org.antlr.runtime.CharStream;
import org.antlr.runtime.DFA;
import org.antlr.runtime.EarlyExitException;
import org.antlr.runtime.Lexer;
import org.antlr.runtime.MismatchedSetException;
import org.antlr.runtime.NoViableAltException;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.RecognizerSharedState;

@SuppressWarnings({ "all", "warnings", "unchecked" })
public class CalcGrammarLexer extends Lexer {
	public static final int EOF = -1;
	public static final int T__23 = 23;
	public static final int T__24 = 24;
	public static final int T__25 = 25;
	public static final int CONSTANT = 4;
	public static final int DECINT = 5;
	public static final int DIGIT = 6;
	public static final int GT = 7;
	public static final int ID = 8;
	public static final int LAND = 9;
	public static final int LETTER = 10;
	public static final int LT = 11;
	public static final int MINUS = 12;
	public static final int MULOP = 13;
	public static final int NOT = 14;
	public static final int NUMBER = 15;
	public static final int OR = 16;
	public static final int PLUS = 17;
	public static final int RELOP = 18;
	public static final int SENSOR = 19;
	public static final int UPID = 20;
	public static final int UPPERCASE = 21;
	public static final int WS = 22;

	private int lexerError = -1;

	@Override
	public void reportError(RecognitionException e) {
		if (lexerError != -1) {
			return;
		}

		lexerError = e.charPositionInLine;
		throw new RuntimeException(e);
	}

	public int getLexerError() {
		return lexerError;
	}

	// delegates
	// delegators
	public Lexer[] getDelegates() {
		return new Lexer[] {};
	}

	public CalcGrammarLexer() {
	}

	public CalcGrammarLexer(CharStream input) {
		this(input, new RecognizerSharedState());
	}

	public CalcGrammarLexer(CharStream input, RecognizerSharedState state) {
		super(input, state);
	}

	public String getGrammarFileName() {
		return "src/CalcGrammar.g";
	}

	// $ANTLR start "T__23"
	public final void mT__23() throws RecognitionException {
		try {
			int _type = T__23;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// src/CalcGrammar.g:21:7: ( '(' )
			// src/CalcGrammar.g:21:9: '('
			{
				match('(');

			}

			state.type = _type;
			state.channel = _channel;
		} finally {
			// do for sure before leaving
		}
	}

	// $ANTLR end "T__23"

	// $ANTLR start "T__24"
	public final void mT__24() throws RecognitionException {
		try {
			int _type = T__24;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// src/CalcGrammar.g:22:7: ( ')' )
			// src/CalcGrammar.g:22:9: ')'
			{
				match(')');

			}

			state.type = _type;
			state.channel = _channel;
		} finally {
			// do for sure before leaving
		}
	}

	// $ANTLR end "T__24"

	// $ANTLR start "T__25"
	public final void mT__25() throws RecognitionException {
		try {
			int _type = T__25;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// src/CalcGrammar.g:23:7: ( ',' )
			// src/CalcGrammar.g:23:9: ','
			{
				match(',');

			}

			state.type = _type;
			state.channel = _channel;
		} finally {
			// do for sure before leaving
		}
	}

	// $ANTLR end "T__25"

	// $ANTLR start "WS"
	public final void mWS() throws RecognitionException {
		try {
			int _type = WS;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// src/CalcGrammar.g:286:5: ( ( ' ' | '\\r' | '\\t' | '\\u000C' | '\\n' ) )
			// src/CalcGrammar.g:286:7: ( ' ' | '\\r' | '\\t' | '\\u000C' | '\\n' )
			{
				if ((input.LA(1) >= '\t' && input.LA(1) <= '\n') || (input.LA(1) >= '\f' && input.LA(1) <= '\r')
						|| input.LA(1) == ' ') {
					input.consume();
				} else {
					MismatchedSetException mse = new MismatchedSetException(null, input);
					recover(mse);
					throw mse;
				}

				_channel = HIDDEN;

			}

			state.type = _type;
			state.channel = _channel;
		} finally {
			// do for sure before leaving
		}
	}

	// $ANTLR end "WS"

	// $ANTLR start "CONSTANT"
	public final void mCONSTANT() throws RecognitionException {
		try {
			int _type = CONSTANT;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// src/CalcGrammar.g:293:10: ( 'pi' | 'e' )
			int alt1 = 2;
			int LA1_0 = input.LA(1);

			if ((LA1_0 == 'p')) {
				alt1 = 1;
			} else if ((LA1_0 == 'e')) {
				alt1 = 2;
			} else {
				NoViableAltException nvae = new NoViableAltException("", 1, 0, input);

				throw nvae;

			}
			switch (alt1) {
				case 1:
				// src/CalcGrammar.g:293:12: 'pi'
				{
					match("pi");

				}
					break;
				case 2:
				// src/CalcGrammar.g:293:17: 'e'
				{
					match('e');

				}
					break;

			}
			state.type = _type;
			state.channel = _channel;
		} finally {
			// do for sure before leaving
		}
	}

	// $ANTLR end "CONSTANT"

	// $ANTLR start "LT"
	public final void mLT() throws RecognitionException {
		try {
			// src/CalcGrammar.g:296:13: ( '<' )
			// src/CalcGrammar.g:296:15: '<'
			{
				match('<');

			}

		} finally {
			// do for sure before leaving
		}
	}

	// $ANTLR end "LT"

	// $ANTLR start "GT"
	public final void mGT() throws RecognitionException {
		try {
			// src/CalcGrammar.g:297:13: ( '>' )
			// src/CalcGrammar.g:297:15: '>'
			{
				match('>');

			}

		} finally {
			// do for sure before leaving
		}
	}

	// $ANTLR end "GT"

	// $ANTLR start "RELOP"
	public final void mRELOP() throws RecognitionException {
		try {
			int _type = RELOP;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// src/CalcGrammar.g:298:10: ( ( LT | ( LT '=' ) | GT | ( GT '=' ) | ( '=' ) ) )
			// src/CalcGrammar.g:298:12: ( LT | ( LT '=' ) | GT | ( GT '=' ) | ( '=' ) )
			{
				// src/CalcGrammar.g:298:12: ( LT | ( LT '=' ) | GT | ( GT '=' ) | ( '=' ) )
				int alt2 = 5;
				switch (input.LA(1)) {
					case '<': {
						int LA2_1 = input.LA(2);

						if ((LA2_1 == '=')) {
							alt2 = 2;
						} else {
							alt2 = 1;
						}
					}
						break;
					case '>': {
						int LA2_2 = input.LA(2);

						if ((LA2_2 == '=')) {
							alt2 = 4;
						} else {
							alt2 = 3;
						}
					}
						break;
					case '=': {
						alt2 = 5;
					}
						break;
					default:
						NoViableAltException nvae = new NoViableAltException("", 2, 0, input);

						throw nvae;

				}

				switch (alt2) {
					case 1:
					// src/CalcGrammar.g:298:13: LT
					{
						mLT();

					}
						break;
					case 2:
					// src/CalcGrammar.g:298:16: ( LT '=' )
					{
						// src/CalcGrammar.g:298:16: ( LT '=' )
						// src/CalcGrammar.g:298:17: LT '='
						{
							mLT();

							match('=');

						}

					}
						break;
					case 3:
					// src/CalcGrammar.g:298:24: GT
					{
						mGT();

					}
						break;
					case 4:
					// src/CalcGrammar.g:298:27: ( GT '=' )
					{
						// src/CalcGrammar.g:298:27: ( GT '=' )
						// src/CalcGrammar.g:298:28: GT '='
						{
							mGT();

							match('=');

						}

					}
						break;
					case 5:
					// src/CalcGrammar.g:298:35: ( '=' )
					{
						// src/CalcGrammar.g:298:35: ( '=' )
						// src/CalcGrammar.g:298:36: '='
						{
							match('=');

						}

					}
						break;

				}

			}

			state.type = _type;
			state.channel = _channel;
		} finally {
			// do for sure before leaving
		}
	}

	// $ANTLR end "RELOP"

	// $ANTLR start "MULOP"
	public final void mMULOP() throws RecognitionException {
		try {
			int _type = MULOP;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// src/CalcGrammar.g:299:10: ( '*' | '/' | '%' | '^' )
			// src/CalcGrammar.g:
			{
				if (input.LA(1) == '%' || input.LA(1) == '*' || input.LA(1) == '/' || input.LA(1) == '^') {
					input.consume();
				} else {
					MismatchedSetException mse = new MismatchedSetException(null, input);
					recover(mse);
					throw mse;
				}

			}

			state.type = _type;
			state.channel = _channel;
		} finally {
			// do for sure before leaving
		}
	}

	// $ANTLR end "MULOP"

	// $ANTLR start "PLUS"
	public final void mPLUS() throws RecognitionException {
		try {
			int _type = PLUS;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// src/CalcGrammar.g:300:10: ( '+' )
			// src/CalcGrammar.g:300:12: '+'
			{
				match('+');

			}

			state.type = _type;
			state.channel = _channel;
		} finally {
			// do for sure before leaving
		}
	}

	// $ANTLR end "PLUS"

	// $ANTLR start "MINUS"
	public final void mMINUS() throws RecognitionException {
		try {
			int _type = MINUS;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// src/CalcGrammar.g:301:10: ( '-' )
			// src/CalcGrammar.g:301:12: '-'
			{
				match('-');

			}

			state.type = _type;
			state.channel = _channel;
		} finally {
			// do for sure before leaving
		}
	}

	// $ANTLR end "MINUS"

	// $ANTLR start "OR"
	public final void mOR() throws RecognitionException {
		try {
			int _type = OR;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// src/CalcGrammar.g:302:10: ( '|' )
			// src/CalcGrammar.g:302:12: '|'
			{
				match('|');

			}

			state.type = _type;
			state.channel = _channel;
		} finally {
			// do for sure before leaving
		}
	}

	// $ANTLR end "OR"

	// $ANTLR start "NOT"
	public final void mNOT() throws RecognitionException {
		try {
			int _type = NOT;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// src/CalcGrammar.g:303:10: ( '!' )
			// src/CalcGrammar.g:303:12: '!'
			{
				match('!');

			}

			state.type = _type;
			state.channel = _channel;
		} finally {
			// do for sure before leaving
		}
	}

	// $ANTLR end "NOT"

	// $ANTLR start "LAND"
	public final void mLAND() throws RecognitionException {
		try {
			int _type = LAND;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// src/CalcGrammar.g:304:10: ( '&' )
			// src/CalcGrammar.g:304:12: '&'
			{
				match('&');

			}

			state.type = _type;
			state.channel = _channel;
		} finally {
			// do for sure before leaving
		}
	}

	// $ANTLR end "LAND"

	// $ANTLR start "NUMBER"
	public final void mNUMBER() throws RecognitionException {
		try {
			int _type = NUMBER;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// src/CalcGrammar.g:307:11: ( DECINT )
			// src/CalcGrammar.g:307:13: DECINT
			{
				mDECINT();

			}

			state.type = _type;
			state.channel = _channel;
		} finally {
			// do for sure before leaving
		}
	}

	// $ANTLR end "NUMBER"

	// $ANTLR start "DECINT"
	public final void mDECINT() throws RecognitionException {
		try {
			// src/CalcGrammar.g:308:17: ( ( DIGIT )+ ( '.' ( DIGIT )+ )? )
			// src/CalcGrammar.g:308:19: ( DIGIT )+ ( '.' ( DIGIT )+ )?
			{
				// src/CalcGrammar.g:308:19: ( DIGIT )+
				int cnt3 = 0;
				loop3: do {
					int alt3 = 2;
					int LA3_0 = input.LA(1);

					if (((LA3_0 >= '0' && LA3_0 <= '9'))) {
						alt3 = 1;
					}

					switch (alt3) {
						case 1:
						// src/CalcGrammar.g:
						{
							if ((input.LA(1) >= '0' && input.LA(1) <= '9')) {
								input.consume();
							} else {
								MismatchedSetException mse = new MismatchedSetException(null, input);
								recover(mse);
								throw mse;
							}

						}
							break;

						default:
							if (cnt3 >= 1) {
								break loop3;
							}
							EarlyExitException eee = new EarlyExitException(3, input);
							throw eee;
					}
					cnt3++;
				} while (true);

				// src/CalcGrammar.g:308:28: ( '.' ( DIGIT )+ )?
				int alt5 = 2;
				int LA5_0 = input.LA(1);

				if ((LA5_0 == '.')) {
					alt5 = 1;
				}
				switch (alt5) {
					case 1:
					// src/CalcGrammar.g:308:29: '.' ( DIGIT )+
					{
						match('.');

						// src/CalcGrammar.g:308:33: ( DIGIT )+
						int cnt4 = 0;
						loop4: do {
							int alt4 = 2;
							int LA4_0 = input.LA(1);

							if (((LA4_0 >= '0' && LA4_0 <= '9'))) {
								alt4 = 1;
							}

							switch (alt4) {
								case 1:
								// src/CalcGrammar.g:
								{
									if ((input.LA(1) >= '0' && input.LA(1) <= '9')) {
										input.consume();
									} else {
										MismatchedSetException mse = new MismatchedSetException(null, input);
										recover(mse);
										throw mse;
									}

								}
									break;

								default:
									if (cnt4 >= 1) {
										break loop4;
									}
									EarlyExitException eee = new EarlyExitException(4, input);
									throw eee;
							}
							cnt4++;
						} while (true);

					}
						break;

				}

			}

		} finally {
			// do for sure before leaving
		}
	}

	// $ANTLR end "DECINT"

	// $ANTLR start "DIGIT"
	public final void mDIGIT() throws RecognitionException {
		try {
			// src/CalcGrammar.g:309:19: ( '0' .. '9' )
			// src/CalcGrammar.g:
			{
				if ((input.LA(1) >= '0' && input.LA(1) <= '9')) {
					input.consume();
				} else {
					MismatchedSetException mse = new MismatchedSetException(null, input);
					recover(mse);
					throw mse;
				}

			}

		} finally {
			// do for sure before leaving
		}
	}

	// $ANTLR end "DIGIT"

	// $ANTLR start "ID"
	public final void mID() throws RecognitionException {
		try {
			int _type = ID;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// src/CalcGrammar.g:312:11: ( LETTER ( LETTER | DIGIT | '_' )* )
			// src/CalcGrammar.g:312:15: LETTER ( LETTER | DIGIT | '_' )*
			{
				mLETTER();

				// src/CalcGrammar.g:312:21: ( LETTER | DIGIT | '_' )*
				loop6: do {
					int alt6 = 2;
					int LA6_0 = input.LA(1);

					if (((LA6_0 >= '0' && LA6_0 <= '9') || LA6_0 == '_' || (LA6_0 >= 'a' && LA6_0 <= 'z'))) {
						alt6 = 1;
					}

					switch (alt6) {
						case 1:
						// src/CalcGrammar.g:
						{
							if ((input.LA(1) >= '0' && input.LA(1) <= '9') || input.LA(1) == '_'
									|| (input.LA(1) >= 'a' && input.LA(1) <= 'z')) {
								input.consume();
							} else {
								MismatchedSetException mse = new MismatchedSetException(null, input);
								recover(mse);
								throw mse;
							}

						}
							break;

						default:
							break loop6;
					}
				} while (true);

			}

			state.type = _type;
			state.channel = _channel;
		} finally {
			// do for sure before leaving
		}
	}

	// $ANTLR end "ID"

	// $ANTLR start "SENSOR"
	public final void mSENSOR() throws RecognitionException {
		try {
			int _type = SENSOR;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// src/CalcGrammar.g:313:11: ( 'X_Accelerometer' | 'Y_Accelerometer' | 'Z_Accelerometer' | 'Azimuth_Orientation' | 'Pitch_Orientation' | 'Roll_Orientation' )
			int alt7 = 6;
			switch (input.LA(1)) {
				case 'X': {
					alt7 = 1;
				}
					break;
				case 'Y': {
					alt7 = 2;
				}
					break;
				case 'Z': {
					alt7 = 3;
				}
					break;
				case 'A': {
					alt7 = 4;
				}
					break;
				case 'P': {
					alt7 = 5;
				}
					break;
				case 'R': {
					alt7 = 6;
				}
					break;
				default:
					NoViableAltException nvae = new NoViableAltException("", 7, 0, input);

					throw nvae;

			}

			switch (alt7) {
				case 1:
				// src/CalcGrammar.g:313:15: 'X_Accelerometer'
				{
					match("X_Accelerometer");

				}
					break;
				case 2:
				// src/CalcGrammar.g:313:33: 'Y_Accelerometer'
				{
					match("Y_Accelerometer");

				}
					break;
				case 3:
				// src/CalcGrammar.g:313:51: 'Z_Accelerometer'
				{
					match("Z_Accelerometer");

				}
					break;
				case 4:
				// src/CalcGrammar.g:313:69: 'Azimuth_Orientation'
				{
					match("Azimuth_Orientation");

				}
					break;
				case 5:
				// src/CalcGrammar.g:313:91: 'Pitch_Orientation'
				{
					match("Pitch_Orientation");

				}
					break;
				case 6:
				// src/CalcGrammar.g:313:111: 'Roll_Orientation'
				{
					match("Roll_Orientation");

				}
					break;

			}
			state.type = _type;
			state.channel = _channel;
		} finally {
			// do for sure before leaving
		}
	}

	// $ANTLR end "SENSOR"

	// $ANTLR start "UPID"
	public final void mUPID() throws RecognitionException {
		try {
			int _type = UPID;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// src/CalcGrammar.g:314:11: ( UPPERCASE ( LETTER | DIGIT | UPPERCASE | '_' )* )
			// src/CalcGrammar.g:314:15: UPPERCASE ( LETTER | DIGIT | UPPERCASE | '_' )*
			{
				mUPPERCASE();

				// src/CalcGrammar.g:314:24: ( LETTER | DIGIT | UPPERCASE | '_' )*
				loop8: do {
					int alt8 = 2;
					int LA8_0 = input.LA(1);

					if (((LA8_0 >= '0' && LA8_0 <= '9') || (LA8_0 >= 'A' && LA8_0 <= 'Z') || LA8_0 == '_' || (LA8_0 >= 'a' && LA8_0 <= 'z'))) {
						alt8 = 1;
					}

					switch (alt8) {
						case 1:
						// src/CalcGrammar.g:
						{
							if ((input.LA(1) >= '0' && input.LA(1) <= '9')
									|| (input.LA(1) >= 'A' && input.LA(1) <= 'Z') || input.LA(1) == '_'
									|| (input.LA(1) >= 'a' && input.LA(1) <= 'z')) {
								input.consume();
							} else {
								MismatchedSetException mse = new MismatchedSetException(null, input);
								recover(mse);
								throw mse;
							}

						}
							break;

						default:
							break loop8;
					}
				} while (true);

			}

			state.type = _type;
			state.channel = _channel;
		} finally {
			// do for sure before leaving
		}
	}

	// $ANTLR end "UPID"

	// $ANTLR start "LETTER"
	public final void mLETTER() throws RecognitionException {
		try {
			// src/CalcGrammar.g:315:20: ( ( 'a' .. 'z' ) )
			// src/CalcGrammar.g:
			{
				if ((input.LA(1) >= 'a' && input.LA(1) <= 'z')) {
					input.consume();
				} else {
					MismatchedSetException mse = new MismatchedSetException(null, input);
					recover(mse);
					throw mse;
				}

			}

		} finally {
			// do for sure before leaving
		}
	}

	// $ANTLR end "LETTER"

	// $ANTLR start "UPPERCASE"
	public final void mUPPERCASE() throws RecognitionException {
		try {
			// src/CalcGrammar.g:316:20: ( ( 'A' .. 'Z' ) )
			// src/CalcGrammar.g:
			{
				if ((input.LA(1) >= 'A' && input.LA(1) <= 'Z')) {
					input.consume();
				} else {
					MismatchedSetException mse = new MismatchedSetException(null, input);
					recover(mse);
					throw mse;
				}

			}

		} finally {
			// do for sure before leaving
		}
	}

	// $ANTLR end "UPPERCASE"

	public void mTokens() throws RecognitionException {
		// src/CalcGrammar.g:1:8: ( T__23 | T__24 | T__25 | WS | CONSTANT | RELOP | MULOP | PLUS | MINUS | OR | NOT | LAND | NUMBER | ID | SENSOR | UPID )
		int alt9 = 16;
		alt9 = dfa9.predict(input);
		switch (alt9) {
			case 1:
			// src/CalcGrammar.g:1:10: T__23
			{
				mT__23();

			}
				break;
			case 2:
			// src/CalcGrammar.g:1:16: T__24
			{
				mT__24();

			}
				break;
			case 3:
			// src/CalcGrammar.g:1:22: T__25
			{
				mT__25();

			}
				break;
			case 4:
			// src/CalcGrammar.g:1:28: WS
			{
				mWS();

			}
				break;
			case 5:
			// src/CalcGrammar.g:1:31: CONSTANT
			{
				mCONSTANT();

			}
				break;
			case 6:
			// src/CalcGrammar.g:1:40: RELOP
			{
				mRELOP();

			}
				break;
			case 7:
			// src/CalcGrammar.g:1:46: MULOP
			{
				mMULOP();

			}
				break;
			case 8:
			// src/CalcGrammar.g:1:52: PLUS
			{
				mPLUS();

			}
				break;
			case 9:
			// src/CalcGrammar.g:1:57: MINUS
			{
				mMINUS();

			}
				break;
			case 10:
			// src/CalcGrammar.g:1:63: OR
			{
				mOR();

			}
				break;
			case 11:
			// src/CalcGrammar.g:1:66: NOT
			{
				mNOT();

			}
				break;
			case 12:
			// src/CalcGrammar.g:1:70: LAND
			{
				mLAND();

			}
				break;
			case 13:
			// src/CalcGrammar.g:1:75: NUMBER
			{
				mNUMBER();

			}
				break;
			case 14:
			// src/CalcGrammar.g:1:82: ID
			{
				mID();

			}
				break;
			case 15:
			// src/CalcGrammar.g:1:85: SENSOR
			{
				mSENSOR();

			}
				break;
			case 16:
			// src/CalcGrammar.g:1:92: UPID
			{
				mUPID();

			}
				break;

		}

	}

	protected DFA9 dfa9 = new DFA9(this);
	static final String DFA9_eotS = "\5\uffff\1\17\1\30\11\uffff\6\26\1\uffff\1\30\1\uffff\116\26\3\155"
			+ "\3\26\1\uffff\2\26\1\155\1\26\1\155\1\26\1\155";
	static final String DFA9_eofS = "\165\uffff";
	static final String DFA9_minS = "\1\11\4\uffff\1\151\1\60\11\uffff\3\137\1\172\1\151\1\157\1\uffff"
			+ "\1\60\1\uffff\3\101\1\151\1\164\1\154\3\143\1\155\1\143\1\154\3"
			+ "\143\1\165\1\150\1\137\3\145\1\164\1\137\1\117\3\154\1\150\1\117"
			+ "\1\162\3\145\1\137\1\162\1\151\3\162\1\117\1\151\1\145\3\157\1\162"
			+ "\1\145\1\156\3\155\1\151\1\156\1\164\4\145\1\164\1\141\3\164\1\156"
			+ "\1\141\1\164\3\145\2\164\1\151\3\162\1\141\1\151\1\157\3\60\1\164"
			+ "\1\157\1\156\1\uffff\1\151\1\156\1\60\1\157\1\60\1\156\1\60";
	static final String DFA9_maxS = "\1\174\4\uffff\1\151\1\172\11\uffff\3\137\1\172\1\151\1\157\1\uffff"
			+ "\1\172\1\uffff\3\101\1\151\1\164\1\154\3\143\1\155\1\143\1\154\3"
			+ "\143\1\165\1\150\1\137\3\145\1\164\1\137\1\117\3\154\1\150\1\117"
			+ "\1\162\3\145\1\137\1\162\1\151\3\162\1\117\1\151\1\145\3\157\1\162"
			+ "\1\145\1\156\3\155\1\151\1\156\1\164\4\145\1\164\1\141\3\164\1\156"
			+ "\1\141\1\164\3\145\2\164\1\151\3\162\1\141\1\151\1\157\3\172\1\164"
			+ "\1\157\1\156\1\uffff\1\151\1\156\1\172\1\157\1\172\1\156\1\172";
	static final String DFA9_acceptS = "\1\uffff\1\1\1\2\1\3\1\4\2\uffff\1\6\1\7\1\10\1\11\1\12\1\13\1\14"
			+ "\1\15\1\16\6\uffff\1\20\1\uffff\1\5\124\uffff\1\17\7\uffff";
	static final String DFA9_specialS = "\165\uffff}>";
	static final String[] DFA9_transitionS = {
			"\2\4\1\uffff\2\4\22\uffff\1\4\1\14\3\uffff\1\10\1\15\1\uffff"
					+ "\1\1\1\2\1\10\1\11\1\3\1\12\1\uffff\1\10\12\16\2\uffff\3\7\2"
					+ "\uffff\1\23\16\26\1\24\1\26\1\25\5\26\1\20\1\21\1\22\3\uffff"
					+ "\1\10\2\uffff\4\17\1\6\12\17\1\5\12\17\1\uffff\1\13", "", "", "", "", "\1\27",
			"\12\17\45\uffff\1\17\1\uffff\32\17", "", "", "", "", "", "", "", "", "", "\1\31", "\1\32", "\1\33",
			"\1\34", "\1\35", "\1\36", "", "\12\17\45\uffff\1\17\1\uffff\32\17", "", "\1\37", "\1\40", "\1\41",
			"\1\42", "\1\43", "\1\44", "\1\45", "\1\46", "\1\47", "\1\50", "\1\51", "\1\52", "\1\53", "\1\54", "\1\55",
			"\1\56", "\1\57", "\1\60", "\1\61", "\1\62", "\1\63", "\1\64", "\1\65", "\1\66", "\1\67", "\1\70", "\1\71",
			"\1\72", "\1\73", "\1\74", "\1\75", "\1\76", "\1\77", "\1\100", "\1\101", "\1\102", "\1\103", "\1\104",
			"\1\105", "\1\106", "\1\107", "\1\110", "\1\111", "\1\112", "\1\113", "\1\114", "\1\115", "\1\116",
			"\1\117", "\1\120", "\1\121", "\1\122", "\1\123", "\1\124", "\1\125", "\1\126", "\1\127", "\1\130",
			"\1\131", "\1\132", "\1\133", "\1\134", "\1\135", "\1\136", "\1\137", "\1\140", "\1\141", "\1\142",
			"\1\143", "\1\144", "\1\145", "\1\146", "\1\147", "\1\150", "\1\151", "\1\152", "\1\153", "\1\154",
			"\12\26\7\uffff\32\26\4\uffff\1\26\1\uffff\32\26", "\12\26\7\uffff\32\26\4\uffff\1\26\1\uffff\32\26",
			"\12\26\7\uffff\32\26\4\uffff\1\26\1\uffff\32\26", "\1\156", "\1\157", "\1\160", "", "\1\161", "\1\162",
			"\12\26\7\uffff\32\26\4\uffff\1\26\1\uffff\32\26", "\1\163",
			"\12\26\7\uffff\32\26\4\uffff\1\26\1\uffff\32\26", "\1\164",
			"\12\26\7\uffff\32\26\4\uffff\1\26\1\uffff\32\26" };

	static final short[] DFA9_eot = DFA.unpackEncodedString(DFA9_eotS);
	static final short[] DFA9_eof = DFA.unpackEncodedString(DFA9_eofS);
	static final char[] DFA9_min = DFA.unpackEncodedStringToUnsignedChars(DFA9_minS);
	static final char[] DFA9_max = DFA.unpackEncodedStringToUnsignedChars(DFA9_maxS);
	static final short[] DFA9_accept = DFA.unpackEncodedString(DFA9_acceptS);
	static final short[] DFA9_special = DFA.unpackEncodedString(DFA9_specialS);
	static final short[][] DFA9_transition;

	static {
		int numStates = DFA9_transitionS.length;
		DFA9_transition = new short[numStates][];
		for (int i = 0; i < numStates; i++) {
			DFA9_transition[i] = DFA.unpackEncodedString(DFA9_transitionS[i]);
		}
	}

	class DFA9 extends DFA {

		public DFA9(BaseRecognizer recognizer) {
			this.recognizer = recognizer;
			this.decisionNumber = 9;
			this.eot = DFA9_eot;
			this.eof = DFA9_eof;
			this.min = DFA9_min;
			this.max = DFA9_max;
			this.accept = DFA9_accept;
			this.special = DFA9_special;
			this.transition = DFA9_transition;
		}

		public String getDescription() {
			return "1:1: Tokens : ( T__23 | T__24 | T__25 | WS | CONSTANT | RELOP | MULOP | PLUS | MINUS | OR | NOT | LAND | NUMBER | ID | SENSOR | UPID );";
		}
	}

}