// $ANTLR 3.4 src/CalcGrammar.g 2012-07-26 14:42:36
package at.tugraz.ist.catroid.formulaeditor;

import org.antlr.runtime.CharStream;
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
			// src/CalcGrammar.g:312:11: ( LETTER ( LETTER )* )
			// src/CalcGrammar.g:312:15: LETTER ( LETTER )*
			{
				mLETTER();

				// src/CalcGrammar.g:312:21: ( LETTER )*
				loop6: do {
					int alt6 = 2;
					int LA6_0 = input.LA(1);

					if (((LA6_0 >= 'a' && LA6_0 <= 'z'))) {
						alt6 = 1;
					}

					switch (alt6) {
						case 1:
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
			// src/CalcGrammar.g:313:11: ( 'XACC_' | 'YACC_' | 'ZACC_' | 'AZIM_' | 'PITCH_' | 'ROLL_' | 'SLIDER_' )
			int alt7 = 7;
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
				case 'S': {
					alt7 = 7;
				}
					break;
				default:
					NoViableAltException nvae = new NoViableAltException("", 7, 0, input);

					throw nvae;

			}

			switch (alt7) {
				case 1:
				// src/CalcGrammar.g:313:15: 'XACC_'
				{
					match("XACC_");

				}
					break;
				case 2:
				// src/CalcGrammar.g:313:23: 'YACC_'
				{
					match("YACC_");

				}
					break;
				case 3:
				// src/CalcGrammar.g:313:31: 'ZACC_'
				{
					match("ZACC_");

				}
					break;
				case 4:
				// src/CalcGrammar.g:313:39: 'AZIM_'
				{
					match("AZIM_");

				}
					break;
				case 5:
				// src/CalcGrammar.g:313:47: 'PITCH_'
				{
					match("PITCH_");

				}
					break;
				case 6:
				// src/CalcGrammar.g:313:56: 'ROLL_'
				{
					match("ROLL_");

				}
					break;
				case 7:
				// src/CalcGrammar.g:313:64: 'SLIDER_'
				{
					match("SLIDER_");

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
			// src/CalcGrammar.g:314:11: ( UPPERCASE ( LETTER )* )
			// src/CalcGrammar.g:314:15: UPPERCASE ( LETTER )*
			{
				mUPPERCASE();

				// src/CalcGrammar.g:314:24: ( LETTER )*
				loop8: do {
					int alt8 = 2;
					int LA8_0 = input.LA(1);

					if (((LA8_0 >= 'a' && LA8_0 <= 'z'))) {
						alt8 = 1;
					}

					switch (alt8) {
						case 1:
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
		switch (input.LA(1)) {
			case '(': {
				alt9 = 1;
			}
				break;
			case ')': {
				alt9 = 2;
			}
				break;
			case ',': {
				alt9 = 3;
			}
				break;
			case '\t':
			case '\n':
			case '\f':
			case '\r':
			case ' ': {
				alt9 = 4;
			}
				break;
			case 'p': {
				int LA9_5 = input.LA(2);

				if ((LA9_5 == 'i')) {
					int LA9_24 = input.LA(3);

					if (((LA9_24 >= 'a' && LA9_24 <= 'z'))) {
						alt9 = 14;
					} else {
						alt9 = 5;
					}
				} else {
					alt9 = 14;
				}
			}
				break;
			case 'e': {
				int LA9_6 = input.LA(2);

				if (((LA9_6 >= 'a' && LA9_6 <= 'z'))) {
					alt9 = 14;
				} else {
					alt9 = 5;
				}
			}
				break;
			case '<':
			case '=':
			case '>': {
				alt9 = 6;
			}
				break;
			case '%':
			case '*':
			case '/':
			case '^': {
				alt9 = 7;
			}
				break;
			case '+': {
				alt9 = 8;
			}
				break;
			case '-': {
				alt9 = 9;
			}
				break;
			case '|': {
				alt9 = 10;
			}
				break;
			case '!': {
				alt9 = 11;
			}
				break;
			case '&': {
				alt9 = 12;
			}
				break;
			case '0':
			case '1':
			case '2':
			case '3':
			case '4':
			case '5':
			case '6':
			case '7':
			case '8':
			case '9': {
				alt9 = 13;
			}
				break;
			case 'a':
			case 'b':
			case 'c':
			case 'd':
			case 'f':
			case 'g':
			case 'h':
			case 'i':
			case 'j':
			case 'k':
			case 'l':
			case 'm':
			case 'n':
			case 'o':
			case 'q':
			case 'r':
			case 's':
			case 't':
			case 'u':
			case 'v':
			case 'w':
			case 'x':
			case 'y':
			case 'z': {
				alt9 = 14;
			}
				break;
			case 'X': {
				int LA9_16 = input.LA(2);

				if ((LA9_16 == 'A')) {
					alt9 = 15;
				} else {
					alt9 = 16;
				}
			}
				break;
			case 'Y': {
				int LA9_17 = input.LA(2);

				if ((LA9_17 == 'A')) {
					alt9 = 15;
				} else {
					alt9 = 16;
				}
			}
				break;
			case 'Z': {
				int LA9_18 = input.LA(2);

				if ((LA9_18 == 'A')) {
					alt9 = 15;
				} else {
					alt9 = 16;
				}
			}
				break;
			case 'A': {
				int LA9_19 = input.LA(2);

				if ((LA9_19 == 'Z')) {
					alt9 = 15;
				} else {
					alt9 = 16;
				}
			}
				break;
			case 'P': {
				int LA9_20 = input.LA(2);

				if ((LA9_20 == 'I')) {
					alt9 = 15;
				} else {
					alt9 = 16;
				}
			}
				break;
			case 'R': {
				int LA9_21 = input.LA(2);

				if ((LA9_21 == 'O')) {
					alt9 = 15;
				} else {
					alt9 = 16;
				}
			}
				break;
			case 'S': {
				int LA9_22 = input.LA(2);

				if ((LA9_22 == 'L')) {
					alt9 = 15;
				} else {
					alt9 = 16;
				}
			}
				break;
			case 'B':
			case 'C':
			case 'D':
			case 'E':
			case 'F':
			case 'G':
			case 'H':
			case 'I':
			case 'J':
			case 'K':
			case 'L':
			case 'M':
			case 'N':
			case 'O':
			case 'Q':
			case 'T':
			case 'U':
			case 'V':
			case 'W': {
				alt9 = 16;
			}
				break;
			default:
				NoViableAltException nvae = new NoViableAltException("", 9, 0, input);

				throw nvae;

		}

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

}