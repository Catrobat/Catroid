package at.tugraz.ist.catroid.test.FormulaEditor;

public enum ParserFormulaTestData {
	UNARY_MINUS_1("-1", -1.0), UNARY_MINUS_2("-1--1", 0.0), UNARY_MINUS_MULT("-1*-1", 1.0), UNARY_MINUS_DIVIDE("-1/-1",
			1.0), OPERATOR_PRIORITY_1("1-2*2", -3.0), OPERATOR_PRIORITY_2("1+2*2", 5.0), OPERATOR_PRIORITY_3("1-2/2",
			0.0), OPERATOR_PRIORITY_4("1+2/2", 2.0), OPERATOR_PRIORITY_LEFT_BINDING_1("5-4-1", 0.0), OPERATOR_PRIORITY_LEFT_BINDING_2(
			"100/10/10", 1.0), OPERATOR_PRIORITY_LONG_1("2*2*2*2 + 3*3*3*3", 97.0), OPERATOR_PRIORITY_LONG_2(
			"16/2/2/2 + 81/3/3/3", 5.0);

	private String input;
	private Double output;

	ParserFormulaTestData(String input, Double output) {
		this.input = input;
		this.output = output;
	}

	public String getInput() {
		return input;
	}

	public Double getOutput() {
		return output;
	}

}
