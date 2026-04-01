package org.catrobat.catroid.content.bricks;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.UserVariable;
public class ShowFaceDataBrick extends UserVariableBrickWithFormula {

    private static final long serialVersionUID = 1L;

    public ShowFaceDataBrick() {
        addAllowedBrickField(BrickField.ASK_QUESTION, R.id.brick_ask_question_edit_text);
    }

    public ShowFaceDataBrick(String questionText) {
        this(new Formula(questionText));
    }

    public ShowFaceDataBrick(Formula questionFormula, UserVariable answerVariable) {
        this(questionFormula);
        userVariable = answerVariable;
    }

    public ShowFaceDataBrick(Formula questionFormula) {
        this();
        setFormulaWithBrickField(BrickField.ASK_QUESTION, questionFormula);
    }

    @Override
    public int getViewResource() {
        return R.layout.brick_show_sensor_data;
    }

    @Override
    protected int getSpinnerId() {
        return R.id.brick_ask_spinner;
    }

    @Override
    public void addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
        sequence.addAction(sprite.getActionFactory()
                .createSensorDataResponseAction(sprite, sequence, getFormulaWithBrickField(BrickField.ASK_QUESTION),
                        userVariable));
    }
}

