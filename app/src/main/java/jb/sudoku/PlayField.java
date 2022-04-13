package jb.sudoku;

import java.util.List;

class PlayField {
    private int mFieldId;
    private PlayCell[] mCells;
    private boolean mPencilMode;
    private boolean mPencilAuto;
    private int mSelection;
    private int mSelectionRow;
    private int mSelectionColumn;

    private int[] mDigitCount = new int[10];

    PlayField() {
        int lCount;

        mFieldId = 0;
        mCells = new PlayCell[81];
        for (lCount = 0; lCount < mCells.length; lCount++) {
            mCells[lCount] = new PlayCell();
        }
        mDigitCount[0] = 81;
        for (lCount = 1; lCount < mDigitCount.length; lCount++) {
            mDigitCount[lCount] = 0;
        }
        mPencilMode = false;
        mPencilAuto = true;
        mSelection = 0;
        mSelectionRow = 0;
        mSelectionColumn = 0;
    }

    PlayField(int pFieldId, List<DbCell> pCells, int pSelection, boolean pPencil) {
        int lCount;
        DbCell lDbCell;

        mFieldId = pFieldId;
        mCells = new PlayCell[81];
        for (lCount = 0; lCount < mCells.length; lCount++) {
            mCells[lCount] = new PlayCell();
        }
        for (lCount = 0; lCount < pCells.size(); lCount++) {
            lDbCell = pCells.get(lCount);
            mCells[lDbCell.xCellNumber()].xInitPlayCell(lDbCell);
        }
        sDigitCount();
        mPencilMode = pPencil;
        mPencilAuto = true;
        mSelection = pSelection;
        mSelectionRow = mSelection / 9;
        mSelectionColumn = mSelection % 9;
    }

    PlayField (PlayField pField){
        sPlayFieldInit(pField.mFieldId, pField);
    }

    PlayField(int pFieldId, PlayField pField){
        sPlayFieldInit(pFieldId, pField);
    }

    private void sPlayFieldInit(int pFieldId, PlayField pField){
        int lCount;

        mFieldId = pFieldId;
        mCells = new PlayCell[81];
        for (lCount = 0; lCount < mCells.length; lCount++){
            mCells[lCount] = new PlayCell(pField.mCells[lCount]);
        }
        mPencilMode = pField.mPencilMode;
        mPencilAuto = pField.mPencilAuto;
        mSelection = pField.mSelection;
        mSelectionRow = mSelection / 9;
        mSelectionColumn = mSelection % 9;
        sDigitCount();
    }

    private void sDigitCount() {
        int lCount;

        for (lCount = 0; lCount < mDigitCount.length; lCount++) {
            mDigitCount[lCount] = 0;
        }
        for (lCount = 0; lCount < mCells.length; lCount++) {
            mDigitCount[mCells[lCount].xValue()]++;
        }
    }

    boolean xSolved() {
        return (mDigitCount[0] == 0) ? true : false;
    }

    boolean xEmptyField() {
        return (mDigitCount[0] == 81) ? true : false;
    }

    PlayCell[] xCells() {
        return mCells;
    }

    void xCells(Cell[] pCells) {
        int lCount;

        for (lCount = 0; lCount < mCells.length; lCount++) {
            mCells[lCount].xInitCell(pCells[lCount]);
        }
        mPencilMode = false;
        sDigitCount();
    }

    int xFieldId(){
        return mFieldId;
    }

    void xResetFieldId(){
        mFieldId = 0;
    }

    boolean xPencilMode() {
        return mPencilMode;
    }

    void xPencilFlip() {
        mPencilMode = !mPencilMode;
    }

    boolean xPencilAuto(){
        return mPencilAuto;
    }

    void xFlipPencilAuto(){
        mPencilAuto = !mPencilAuto;
    }

    PlayCell xSelectedCell() {
        return mCells[mSelection];
    }

    PlayCell xCell(int pRow, int pColumn) {
        return mCells[(pRow * 9) + pColumn];
    }

    int xSelection() {
        return mSelection;
    }

    int xSelectionRow() {
        return mSelectionRow;
    }

    void xSelectionRow(int pRow) {
        if (pRow >= 0 && pRow <= 8) {
            mSelectionRow = pRow;
            mSelection = (mSelectionRow * 9) + mSelectionColumn;
        }
    }

    int xSelectionColumn() {
        return mSelectionColumn;
    }

    void xSelectionColumn(int pColumn) {
        if (pColumn >= 0 && pColumn <= 8) {
            mSelectionColumn = pColumn;
            mSelection = (mSelectionRow * 9) + mSelectionColumn;
        }
    }

    int xDigitCount(int pDigit) {
        if (pDigit >= 1 && pDigit <= 9) {
            return mDigitCount[pDigit];
        } else {
            return 0;
        }
    }

    void xInitField() {
        int lCount;

        for (lCount = 0; lCount < mCells.length; lCount++) {
            mCells[lCount].xReset();
        }
        sDigitCount();
        mPencilMode = false;
    }

    void xResetField(){
        int lCount;

        xResetFieldId();
        for (lCount = 0; lCount < mCells.length; lCount++) {
            if (!mCells[lCount].xFixed()){
                mCells[lCount].xReset();
             }
        }
        sDigitCount();
        mPencilMode = false;
    }

    void xCombineField(PlayField pCombineField){
        int lCount;

        for (lCount = 0; lCount < mCells.length; lCount++){
            mCells[lCount].xCombine(pCombineField.xCells()[lCount]);
        }
        sDigitCount();
    }

    void xFixField() {
        int lCount;
        Cell lCell;

        for (lCount = 0; lCount < mCells.length; lCount++) {
            lCell = mCells[lCount];
            if (lCell.xValue() == 0) {
                lCell.xFixed(false);
            } else {
                lCell.xFixed(true);
            }
        }
        sDigitCount();
    }

    boolean xSetCellValue(int pValue) {
        PlayCell lCell;
        boolean lValueSet;

        lCell = mCells[mSelection];
        if (lCell.xValue() == pValue) {
            lCell.xValueReset();
            mDigitCount[0]++;
            mDigitCount[pValue]--;
            lValueSet = false;
        } else {
            if (lCell.xValue() == 0) {
                mDigitCount[0]--;
            } else {
                mDigitCount[lCell.xValue()]--;
            }
            lCell.xValue(pValue);
            mDigitCount[pValue]++;
            lValueSet = true;
        }
        return lValueSet;
    }


    void xResetConflicts() {
        int lCount;

        for (lCount = 0; lCount < mCells.length; lCount++) {
            mCells[lCount].xConflict(false);
        }
    }

    void xInitPencil() {
        int lCount;

        for (lCount = 0; lCount < mCells.length; lCount++) {
            mCells[lCount].xSetPencils();
        }
    }

    void xClearPencil() {
        int lCount;

        for (lCount = 0; lCount < mCells.length; lCount++) {
            mCells[lCount].xClearPencils();
        }
    }
}
