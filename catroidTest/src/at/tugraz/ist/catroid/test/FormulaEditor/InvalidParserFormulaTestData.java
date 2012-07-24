package at.tugraz.ist.catroid.test.FormulaEditor;

public enum InvalidParserFormulaTestData {
	EMPTY_FORMULA("", 0), OPERATOR_MISSING_1("1 1", 2), OPERATOR_MISSING_2("1+2+3 4+5+6", 6), OPERATOR_MISSING_3(
			"1*2--3 32/2*1+3", 7), OPERATOR_MISSING_4("1--1--1 1--1", 8), NUMBER_MISSING_1("-", 1), NUMBER_MISSING_2(
			"--", 1), NUMBER_MISSING_3("-1--", 4), NUMBER_MISSING_4("+", 0), NUMBER_MISSING_5("*", 0), NUMBER_MISSING_6(
			"/", 0), NUMBER_MISSING_7("+1", 0), NUMBER_MISSING_8("*1", 0), NUMBER_MISSING_9("/1", 0), NUMBER_MISSING_10(
			"1+1+1+1+1+", 10);
	;

	private String input;
	private Integer firstErrorPosition;

	InvalidParserFormulaTestData(String input, Integer firstErrorPosition) {
		this.input = input;
		this.firstErrorPosition = firstErrorPosition;
	}

	public String getInput() {
		return input;
	}

	public Integer getFirstErrorPosition() {
		return firstErrorPosition;
	}

}
