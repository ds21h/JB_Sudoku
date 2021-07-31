package jb.sudoku;

import java.util.ArrayList;
import java.util.List;

class SudokuGame extends SudokuGameBase {
    private final List<PlayField> mPlayFields;

    SudokuGame() {
        super();

        mPlayFields = new ArrayList<>();
        mPlayFields.add(xPlayField());
    }

    SudokuGame(SudokuGame pGame){
        super(pGame);
        mPlayFields = null;
    }

    SudokuGame(List<PlayField> pFields, boolean pSetUp, int pDifficulty, int pSelectedField, int pUsedTime) {
        super();

        PlayField lPlayField;

        mPlayFields = pFields;
        if (mPlayFields.isEmpty()) {
            lPlayField = new PlayField();
            mPlayFields.add(lPlayField);
        } else {
            lPlayField = pFields.get(0);
            if (lPlayField.xFieldId() != pSelectedField){
                for (PlayField lField : mPlayFields) {
                    if (lField.xFieldId() == pSelectedField){
                        lPlayField = lField;
                        break;
                    }
                }
            }
        }
        super.xSudokuGameInit(lPlayField, pSetUp, pDifficulty, pUsedTime);
    }

    List<PlayField> xPlayFields(){
        return mPlayFields;
    }

    int xFieldCount(){
        return mPlayFields.size();
    }

    private void sInitPlayFields(){
        PlayField lPlayField;

        lPlayField = mPlayFields.get(0);
        lPlayField.xResetFieldId();
        mPlayFields.clear();
        mPlayFields.add(lPlayField);
        xPlayField(lPlayField);
    }

    void xPlayFieldCopy(){
        int lNewId;
        PlayField lField;

        lNewId = mPlayFields.get(mPlayFields.size() - 1).xFieldId() + 1;
        lField = new PlayField(lNewId, xPlayField());
        mPlayFields.add(lField);
        xPlayField(lField);
    }

    void xSwitchPlayField(int pNewId){
        for (PlayField lField : mPlayFields){
            if (lField.xFieldId() == pNewId){
                xPlayField(lField);
                break;
            }
        }
    }

    void xDeleteCurrentPlayField(){
        if (xPlayField().xFieldId() != 0){
            if (mPlayFields.size() > 1){
                mPlayFields.remove(xPlayField());
                xPlayField(mPlayFields.get(mPlayFields.size() - 1));
            }
        }
    }

    void xStartSetUp() {
        if (xGameStatus() != cStatusSetup) {
            sInitPlayFields();
            super.xStartSetUp();
        }
    }

    @Override
    void xGenerateEnd(Cell[] pCells){
        sInitPlayFields();
        super.xGenerateEnd(pCells);
    }
}
