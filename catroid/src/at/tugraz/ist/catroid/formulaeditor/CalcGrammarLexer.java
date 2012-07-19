// $ANTLR 3.4 src/CalcGrammar.g 2012-07-19 16:19:07
package at.tugraz.ist.catroid.formulaeditor;

import java.util.ArrayList;
import java.util.List;

import org.antlr.runtime.BaseRecognizer;
import org.antlr.runtime.CharStream;
import org.antlr.runtime.DFA;
import org.antlr.runtime.EarlyExitException;
import org.antlr.runtime.Lexer;
import org.antlr.runtime.MismatchedSetException;
import org.antlr.runtime.NoViableAltException;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.RecognizerSharedState;
import org.antlr.runtime.Token;

@SuppressWarnings({ "all", "warnings", "unchecked" })
public class CalcGrammarLexer extends Lexer {
	public static final int EOF = -1;
	public static final int T__24 = 24;
	public static final int T__25 = 25;
	public static final int T__26 = 26;
	public static final int COMMENT = 4;
	public static final int CONSTANT = 5;
	public static final int DECINT = 6;
	public static final int DIGIT = 7;
	public static final int GT = 8;
	public static final int ID = 9;
	public static final int LAND = 10;
	public static final int LETTER = 11;
	public static final int LT = 12;
	public static final int MINUS = 13;
	public static final int MULOP = 14;
	public static final int NOT = 15;
	public static final int NUMBER = 16;
	public static final int OR = 17;
	public static final int PLUS = 18;
	public static final int RELOP = 19;
	public static final int SENSOR = 20;
	public static final int UPID = 21;
	public static final int UPPERCASE = 22;
	public static final int WS = 23;

	private int lexerErrorCount = 0;
	private List<String> lexerErrorMessages = null;

	@Override
	public void reportError(RecognitionException e) {
		lexerErrorCount++;
		if (lexerErrorMessages == null) {
			lexerErrorMessages = new ArrayList<String>();
		}

		NoViableAltException ex = (NoViableAltException) e;

		lexerErrorMessages.add("\t#" + lexerErrorCount + ": line " + this.state.tokenStartLine + ":"
				+ this.state.tokenStartCharPositionInLine + " no viable " + "alternative at character '"
				+ this.input.substring(ex.index, ex.index) + "'");
	}

	public int getLexerErrorCount() {
		return this.lexerErrorCount;
	}

	public List<String> getLexerErrorMessages() {
		return this.lexerErrorMessages;
	}

	@Override
	public Token nextToken() {
		Token token = super.nextToken();
		if (token.getText().equals("<EOF>")) {
			return Token.EOF_TOKEN;
		}
		return token;
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

	// $ANTLR start "T__24"
	public final void mT__24() throws RecognitionException {
		try {
			int _type = T__24;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// src/CalcGrammar.g:40:7: ( '(' )
			// src/CalcGrammar.g:40:9: '('
			{
				match('(');

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
			// src/CalcGrammar.g:41:7: ( ')' )
			// src/CalcGrammar.g:41:9: ')'
			{
				match(')');

			}

			state.type = _type;
			state.channel = _channel;
		} finally {
			// do for sure before leaving
		}
	}

	// $ANTLR end "T__25"

	// $ANTLR start "T__26"
	public final void mT__26() throws RecognitionException {
		try {
			int _type = T__26;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// src/CalcGrammar.g:42:7: ( ',' )
			// src/CalcGrammar.g:42:9: ','
			{
				match(',');

			}

			state.type = _type;
			state.channel = _channel;
		} finally {
			// do for sure before leaving
		}
	}

	// $ANTLR end "T__26"

	// $ANTLR start "WS"
	public final void mWS() throws RecognitionException {
		try {
			int _type = WS;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// src/CalcGrammar.g:265:5: ( ( ' ' | '\\r' | '\\t' | '\\u000C' | '\\n' ) )
			// src/CalcGrammar.g:265:7: ( ' ' | '\\r' | '\\t' | '\\u000C' | '\\n' )
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

	// $ANTLR start "COMMENT"
	public final void mCOMMENT() throws RecognitionException {
		try {
			int _type = COMMENT;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// src/CalcGrammar.g:268:5: ( '/*' ( . )* '*/' )
			// src/CalcGrammar.g:268:7: '/*' ( . )* '*/'
			{
				match("/*");

				// src/CalcGrammar.g:268:12: ( . )*
				loop1: do {
					int alt1 = 2;
					int LA1_0 = input.LA(1);

					if ((LA1_0 == '*')) {
						int LA1_1 = input.LA(2);

						if ((LA1_1 == '/')) {
							alt1 = 2;
						} else if (((LA1_1 >= '\u0000' && LA1_1 <= '.') || (LA1_1 >= '0' && LA1_1 <= '\uFFFF'))) {
							alt1 = 1;
						}

					} else if (((LA1_0 >= '\u0000' && LA1_0 <= ')') || (LA1_0 >= '+' && LA1_0 <= '\uFFFF'))) {
						alt1 = 1;
					}

					switch (alt1) {
						case 1:
						// src/CalcGrammar.g:268:12: .
						{
							matchAny();

						}
							break;

						default:
							break loop1;
					}
				} while (true);

				match("*/");

				_channel = HIDDEN;

			}

			state.type = _type;
			state.channel = _channel;
		} finally {
			// do for sure before leaving
		}
	}

	// $ANTLR end "COMMENT"

	// $ANTLR start "CONSTANT"
	public final void mCONSTANT() throws RecognitionException {
		try {
			int _type = CONSTANT;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// src/CalcGrammar.g:272:10: ( 'pi' | 'e' )
			int alt2 = 2;
			int LA2_0 = input.LA(1);

			if ((LA2_0 == 'p')) {
				alt2 = 1;
			} else if ((LA2_0 == 'e')) {
				alt2 = 2;
			} else {
				NoViableAltException nvae = new NoViableAltException("", 2, 0, input);

				throw nvae;

			}
			switch (alt2) {
				case 1:
				// src/CalcGrammar.g:272:12: 'pi'
				{
					match("pi");

				}
					break;
				case 2:
				// src/CalcGrammar.g:272:17: 'e'
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
			// src/CalcGrammar.g:275:13: ( '<' )
			// src/CalcGrammar.g:275:15: '<'
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
			// src/CalcGrammar.g:276:13: ( '>' )
			// src/CalcGrammar.g:276:15: '>'
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
			// src/CalcGrammar.g:277:10: ( ( LT | ( LT '=' ) | GT | ( GT '=' ) | ( '=' ) ) )
			// src/CalcGrammar.g:277:12: ( LT | ( LT '=' ) | GT | ( GT '=' ) | ( '=' ) )
			{
				// src/CalcGrammar.g:277:12: ( LT | ( LT '=' ) | GT | ( GT '=' ) | ( '=' ) )
				int alt3 = 5;
				switch (input.LA(1)) {
					case '<': {
						int LA3_1 = input.LA(2);

						if ((LA3_1 == '=')) {
							alt3 = 2;
						} else {
							alt3 = 1;
						}
					}
						break;
					case '>': {
						int LA3_2 = input.LA(2);

						if ((LA3_2 == '=')) {
							alt3 = 4;
						} else {
							alt3 = 3;
						}
					}
						break;
					case '=': {
						alt3 = 5;
					}
						break;
					default:
						NoViableAltException nvae = new NoViableAltException("", 3, 0, input);

						throw nvae;

				}

				switch (alt3) {
					case 1:
					// src/CalcGrammar.g:277:13: LT
					{
						mLT();

					}
						break;
					case 2:
					// src/CalcGrammar.g:277:16: ( LT '=' )
					{
						// src/CalcGrammar.g:277:16: ( LT '=' )
						// src/CalcGrammar.g:277:17: LT '='
						{
							mLT();

							match('=');

						}

					}
						break;
					case 3:
					// src/CalcGrammar.g:277:24: GT
					{
						mGT();

					}
						break;
					case 4:
					// src/CalcGrammar.g:277:27: ( GT '=' )
					{
						// src/CalcGrammar.g:277:27: ( GT '=' )
						// src/CalcGrammar.g:277:28: GT '='
						{
							mGT();

							match('=');

						}

					}
						break;
					case 5:
					// src/CalcGrammar.g:277:35: ( '=' )
					{
						// src/CalcGrammar.g:277:35: ( '=' )
						// src/CalcGrammar.g:277:36: '='
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
			// src/CalcGrammar.g:278:10: ( '*' | '/' | '%' | '^' )
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
			// src/CalcGrammar.g:279:10: ( '+' )
			// src/CalcGrammar.g:279:12: '+'
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
			// src/CalcGrammar.g:280:10: ( '-' )
			// src/CalcGrammar.g:280:12: '-'
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
			// src/CalcGrammar.g:281:10: ( '|' )
			// src/CalcGrammar.g:281:12: '|'
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
			// src/CalcGrammar.g:282:10: ( '!' )
			// src/CalcGrammar.g:282:12: '!'
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
			// src/CalcGrammar.g:283:10: ( '&' )
			// src/CalcGrammar.g:283:12: '&'
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
			// src/CalcGrammar.g:286:11: ( DECINT )
			// src/CalcGrammar.g:286:13: DECINT
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
			// src/CalcGrammar.g:287:17: ( ( DIGIT )+ ( ( '.' | ',' ) ( DIGIT )+ )? )
			// src/CalcGrammar.g:287:19: ( DIGIT )+ ( ( '.' | ',' ) ( DIGIT )+ )?
			{
				// src/CalcGrammar.g:287:19: ( DIGIT )+
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

				// src/CalcGrammar.g:287:28: ( ( '.' | ',' ) ( DIGIT )+ )?
				int alt6 = 2;
				int LA6_0 = input.LA(1);

				if ((LA6_0 == ',' || LA6_0 == '.')) {
					alt6 = 1;
				}
				switch (alt6) {
					case 1:
					// src/CalcGrammar.g:287:29: ( '.' | ',' ) ( DIGIT )+
					{
						if (input.LA(1) == ',' || input.LA(1) == '.') {
							input.consume();
						} else {
							MismatchedSetException mse = new MismatchedSetException(null, input);
							recover(mse);
							throw mse;
						}

						// src/CalcGrammar.g:287:39: ( DIGIT )+
						int cnt5 = 0;
						loop5: do {
							int alt5 = 2;
							int LA5_0 = input.LA(1);

							if (((LA5_0 >= '0' && LA5_0 <= '9'))) {
								alt5 = 1;
							}

							switch (alt5) {
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
									if (cnt5 >= 1) {
										break loop5;
									}
									EarlyExitException eee = new EarlyExitException(5, input);
									throw eee;
							}
							cnt5++;
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
			// src/CalcGrammar.g:288:19: ( '0' .. '9' )
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
			// src/CalcGrammar.g:291:11: ( LETTER ( LETTER | DIGIT | '_' )* )
			// src/CalcGrammar.g:291:15: LETTER ( LETTER | DIGIT | '_' )*
			{
				mLETTER();

				// src/CalcGrammar.g:291:21: ( LETTER | DIGIT | '_' )*
				loop7: do {
					int alt7 = 2;
					int LA7_0 = input.LA(1);

					if (((LA7_0 >= '0' && LA7_0 <= '9') || LA7_0 == '_' || (LA7_0 >= 'a' && LA7_0 <= 'z'))) {
						alt7 = 1;
					}

					switch (alt7) {
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
							break loop7;
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
			// src/CalcGrammar.g:292:11: ( 'X_Accelerometer' | 'Y_Accelerometer' | 'Z_Accelerometer' | 'Azimuth_Orientation' | 'Pitch_Orientation' | 'Roll_Orientation' )
			int alt8 = 6;
			switch (input.LA(1)) {
				case 'X': {
					alt8 = 1;
				}
					break;
				case 'Y': {
					alt8 = 2;
				}
					break;
				case 'Z': {
					alt8 = 3;
				}
					break;
				case 'A': {
					alt8 = 4;
				}
					break;
				case 'P': {
					alt8 = 5;
				}
					break;
				case 'R': {
					alt8 = 6;
				}
					break;
				default:
					NoViableAltException nvae = new NoViableAltException("", 8, 0, input);

					throw nvae;

			}

			switch (alt8) {
				case 1:
				// src/CalcGrammar.g:292:15: 'X_Accelerometer'
				{
					match("X_Accelerometer");

				}
					break;
				case 2:
				// src/CalcGrammar.g:292:33: 'Y_Accelerometer'
				{
					match("Y_Accelerometer");

				}
					break;
				case 3:
				// src/CalcGrammar.g:292:51: 'Z_Accelerometer'
				{
					match("Z_Accelerometer");

				}
					break;
				case 4:
				// src/CalcGrammar.g:292:69: 'Azimuth_Orientation'
				{
					match("Azimuth_Orientation");

				}
					break;
				case 5:
				// src/CalcGrammar.g:292:91: 'Pitch_Orientation'
				{
					match("Pitch_Orientation");

				}
					break;
				case 6:
				// src/CalcGrammar.g:292:111: 'Roll_Orientation'
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
			// src/CalcGrammar.g:293:11: ( UPPERCASE ( LETTER | DIGIT | UPPERCASE | '_' )* )
			// src/CalcGrammar.g:293:15: UPPERCASE ( LETTER | DIGIT | UPPERCASE | '_' )*
			{
				mUPPERCASE();

				// src/CalcGrammar.g:293:24: ( LETTER | DIGIT | UPPERCASE | '_' )*
				loop9: do {
					int alt9 = 2;
					int LA9_0 = input.LA(1);

					if (((LA9_0 >= '0' && LA9_0 <= '9') || (LA9_0 >= 'A' && LA9_0 <= 'Z') || LA9_0 == '_' || (LA9_0 >= 'a' && LA9_0 <= 'z'))) {
						alt9 = 1;
					}

					switch (alt9) {
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
							break loop9;
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
			// src/CalcGrammar.g:294:20: ( ( 'a' .. 'z' ) )
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
			// src/CalcGrammar.g:295:20: ( ( 'A' .. 'Z' ) )
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
		// src/CalcGrammar.g:1:8: ( T__24 | T__25 | T__26 | WS | COMMENT | CONSTANT | RELOP | MULOP | PLUS | MINUS | OR | NOT | LAND | NUMBER | ID | SENSOR | UPID )
		int alt10 = 17;
		alt10 = dfa10.predict(input);
		switch (alt10) {
			case 1:
			// src/CalcGrammar.g:1:10: T__24
			{
				mT__24();

			}
				break;
			case 2:
			// src/CalcGrammar.g:1:16: T__25
			{
				mT__25();

			}
				break;
			case 3:
			// src/CalcGrammar.g:1:22: T__26
			{
				mT__26();

			}
				break;
			case 4:
			// src/CalcGrammar.g:1:28: WS
			{
				mWS();

			}
				break;
			case 5:
			// src/CalcGrammar.g:1:31: COMMENT
			{
				mCOMMENT();

			}
				break;
			case 6:
			// src/CalcGrammar.g:1:39: CONSTANT
			{
				mCONSTANT();

			}
				break;
			case 7:
			// src/CalcGrammar.g:1:48: RELOP
			{
				mRELOP();

			}
				break;
			case 8:
			// src/CalcGrammar.g:1:54: MULOP
			{
				mMULOP();

			}
				break;
			case 9:
			// src/CalcGrammar.g:1:60: PLUS
			{
				mPLUS();

			}
				break;
			case 10:
			// src/CalcGrammar.g:1:65: MINUS
			{
				mMINUS();

			}
				break;
			case 11:
			// src/CalcGrammar.g:1:71: OR
			{
				mOR();

			}
				break;
			case 12:
			// src/CalcGrammar.g:1:74: NOT
			{
				mNOT();

			}
				break;
			case 13:
			// src/CalcGrammar.g:1:78: LAND
			{
				mLAND();

			}
				break;
			case 14:
			// src/CalcGrammar.g:1:83: NUMBER
			{
				mNUMBER();

			}
				break;
			case 15:
			// src/CalcGrammar.g:1:90: ID
			{
				mID();

			}
				break;
			case 16:
			// src/CalcGrammar.g:1:93: SENSOR
			{
				mSENSOR();

			}
				break;
			case 17:
			// src/CalcGrammar.g:1:100: UPID
			{
				mUPID();

			}
				break;

		}

	}

	protected DFA10 dfa10 = new DFA10(this);
	static final String DFA10_eotS = "\5\uffff\1\11\1\20\1\32\11\uffff\6\27\2\uffff\1\32\1\uffff\116\27"
			+ "\3\157\3\27\1\uffff\2\27\1\157\1\27\1\157\1\27\1\157";
	static final String DFA10_eofS = "\167\uffff";
	static final String DFA10_minS = "\1\11\4\uffff\1\52\1\151\1\60\11\uffff\3\137\1\172\1\151\1\157\2"
			+ "\uffff\1\60\1\uffff\3\101\1\151\1\164\1\154\3\143\1\155\1\143\1"
			+ "\154\3\143\1\165\1\150\1\137\3\145\1\164\1\137\1\117\3\154\1\150"
			+ "\1\117\1\162\3\145\1\137\1\162\1\151\3\162\1\117\1\151\1\145\3\157"
			+ "\1\162\1\145\1\156\3\155\1\151\1\156\1\164\4\145\1\164\1\141\3\164"
			+ "\1\156\1\141\1\164\3\145\2\164\1\151\3\162\1\141\1\151\1\157\3\60"
			+ "\1\164\1\157\1\156\1\uffff\1\151\1\156\1\60\1\157\1\60\1\156\1\60";
	static final String DFA10_maxS = "\1\174\4\uffff\1\52\1\151\1\172\11\uffff\3\137\1\172\1\151\1\157"
			+ "\2\uffff\1\172\1\uffff\3\101\1\151\1\164\1\154\3\143\1\155\1\143"
			+ "\1\154\3\143\1\165\1\150\1\137\3\145\1\164\1\137\1\117\3\154\1\150"
			+ "\1\117\1\162\3\145\1\137\1\162\1\151\3\162\1\117\1\151\1\145\3\157"
			+ "\1\162\1\145\1\156\3\155\1\151\1\156\1\164\4\145\1\164\1\141\3\164"
			+ "\1\156\1\141\1\164\3\145\2\164\1\151\3\162\1\141\1\151\1\157\3\172"
			+ "\1\164\1\157\1\156\1\uffff\1\151\1\156\1\172\1\157\1\172\1\156\1" + "\172";
	static final String DFA10_acceptS = "\1\uffff\1\1\1\2\1\3\1\4\3\uffff\1\7\1\10\1\11\1\12\1\13\1\14\1"
			+ "\15\1\16\1\17\6\uffff\1\21\1\5\1\uffff\1\6\124\uffff\1\20\7\uffff";
	static final String DFA10_specialS = "\167\uffff}>";
	static final String[] DFA10_transitionS = {
			"\2\4\1\uffff\2\4\22\uffff\1\4\1\15\3\uffff\1\11\1\16\1\uffff"
					+ "\1\1\1\2\1\11\1\12\1\3\1\13\1\uffff\1\5\12\17\2\uffff\3\10\2"
					+ "\uffff\1\24\16\27\1\25\1\27\1\26\5\27\1\21\1\22\1\23\3\uffff"
					+ "\1\11\2\uffff\4\20\1\7\12\20\1\6\12\20\1\uffff\1\14", "", "", "", "", "\1\30", "\1\31",
			"\12\20\45\uffff\1\20\1\uffff\32\20", "", "", "", "", "", "", "", "", "", "\1\33", "\1\34", "\1\35",
			"\1\36", "\1\37", "\1\40", "", "", "\12\20\45\uffff\1\20\1\uffff\32\20", "", "\1\41", "\1\42", "\1\43",
			"\1\44", "\1\45", "\1\46", "\1\47", "\1\50", "\1\51", "\1\52", "\1\53", "\1\54", "\1\55", "\1\56", "\1\57",
			"\1\60", "\1\61", "\1\62", "\1\63", "\1\64", "\1\65", "\1\66", "\1\67", "\1\70", "\1\71", "\1\72", "\1\73",
			"\1\74", "\1\75", "\1\76", "\1\77", "\1\100", "\1\101", "\1\102", "\1\103", "\1\104", "\1\105", "\1\106",
			"\1\107", "\1\110", "\1\111", "\1\112", "\1\113", "\1\114", "\1\115", "\1\116", "\1\117", "\1\120",
			"\1\121", "\1\122", "\1\123", "\1\124", "\1\125", "\1\126", "\1\127", "\1\130", "\1\131", "\1\132",
			"\1\133", "\1\134", "\1\135", "\1\136", "\1\137", "\1\140", "\1\141", "\1\142", "\1\143", "\1\144",
			"\1\145", "\1\146", "\1\147", "\1\150", "\1\151", "\1\152", "\1\153", "\1\154", "\1\155", "\1\156",
			"\12\27\7\uffff\32\27\4\uffff\1\27\1\uffff\32\27", "\12\27\7\uffff\32\27\4\uffff\1\27\1\uffff\32\27",
			"\12\27\7\uffff\32\27\4\uffff\1\27\1\uffff\32\27", "\1\160", "\1\161", "\1\162", "", "\1\163", "\1\164",
			"\12\27\7\uffff\32\27\4\uffff\1\27\1\uffff\32\27", "\1\165",
			"\12\27\7\uffff\32\27\4\uffff\1\27\1\uffff\32\27", "\1\166",
			"\12\27\7\uffff\32\27\4\uffff\1\27\1\uffff\32\27" };

	static final short[] DFA10_eot = DFA.unpackEncodedString(DFA10_eotS);
	static final short[] DFA10_eof = DFA.unpackEncodedString(DFA10_eofS);
	static final char[] DFA10_min = DFA.unpackEncodedStringToUnsignedChars(DFA10_minS);
	static final char[] DFA10_max = DFA.unpackEncodedStringToUnsignedChars(DFA10_maxS);
	static final short[] DFA10_accept = DFA.unpackEncodedString(DFA10_acceptS);
	static final short[] DFA10_special = DFA.unpackEncodedString(DFA10_specialS);
	static final short[][] DFA10_transition;

	static {
		int numStates = DFA10_transitionS.length;
		DFA10_transition = new short[numStates][];
		for (int i = 0; i < numStates; i++) {
			DFA10_transition[i] = DFA.unpackEncodedString(DFA10_transitionS[i]);
		}
	}

	class DFA10 extends DFA {

		public DFA10(BaseRecognizer recognizer) {
			this.recognizer = recognizer;
			this.decisionNumber = 10;
			this.eot = DFA10_eot;
			this.eof = DFA10_eof;
			this.min = DFA10_min;
			this.max = DFA10_max;
			this.accept = DFA10_accept;
			this.special = DFA10_special;
			this.transition = DFA10_transition;
		}

		public String getDescription() {
			return "1:1: Tokens : ( T__24 | T__25 | T__26 | WS | COMMENT | CONSTANT | RELOP | MULOP | PLUS | MINUS | OR | NOT | LAND | NUMBER | ID | SENSOR | UPID );";
		}
	}

}