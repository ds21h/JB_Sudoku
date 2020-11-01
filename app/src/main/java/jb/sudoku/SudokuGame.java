package jb.sudoku;

import android.os.AsyncTask;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

class SudokuGame extends SudokuGameBase {
    private List<PlayField> mPlayFields;

    SudokuGame() {
        super();

        mPlayFields = new ArrayList<>();
        mPlayFields.add(xPlayField());
    }

    SudokuGame(SudokuGame pGame){

    }

    SudokuGame(List<PlayField> pFields, boolean pSetUp, boolean pLib, int pDifficulty, int pSelectedField, int pUsedTime) {
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
        super.xSudokuGameInit(lPlayField, pSetUp, pLib, pDifficulty, pSelectedField, pUsedTime);
    }

    List<PlayField> xPlayFields(){
        return mPlayFields;
    }

    int xSelectedField() {
        return xPlayField().xFieldId();
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

    void xPlayFieldClone(){
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

    void xGenerate(int pLevel, AsyncTask pTask) {
        SudokuGenerator lGenerator;
        Cell[] lCells;

        xGenerateStart(pLevel );

        lGenerator = new SudokuGenerator();
        lCells = lGenerator.xGenerate(pLevel, pTask);
        if (!(pTask != null && pTask.isCancelled())) {
            sInitPlayFields();
            xGenerateEnd(lCells);
        }
    }
}
