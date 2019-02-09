package jb.sudoku;

import java.util.List;

class GameData {
    private Cell[] mCells;
    private boolean mPencilMode;
    private boolean mSetUpMode;
    private int mSelection;
    private int mSelectionRow;
    private int mSelectionColumn;

    private int[] mDigitCount = new int[10];

    GameData() {
        int lCount;

        mCells = new Cell[81];
        for (lCount = 0; lCount < mCells.length; lCount++) {
            mCells[lCount] = new Cell();
        }
        mDigitCount[0] = 81;
        for (lCount = 1; lCount < mDigitCount.length; lCount++) {
            mDigitCount[lCount] = 0;
        }
        mPencilMode = false;
        mSetUpMode = false;
        mSelection = 0;
        mSelectionRow = 0;
        mSelectionColumn = 0;
    }

    GameData(List<DbCell> pCells, int pSelection, boolean pSetUp, boolean pPencil){
        int lCount;
        DbCell lDbCell;

        mCells = new Cell[81];
        for (lCount = 0; lCount < mCells.length; lCount++) {
            mCells[lCount] = new Cell();
        }
        for (lCount = 0; lCount < pCells.size(); lCount++){
            lDbCell = pCells.get(lCount);
            mCells[lDbCell.xCellNumber()] = lDbCell.xCell();
        }
        for (lCount = 0; lCount < mDigitCount.length; lCount++){
            mDigitCount[lCount] = 0;
        }
        for (lCount = 0; lCount < mCells.length; lCount++){
            mDigitCount[mCells[lCount].xValue()]++;
        }
        mPencilMode = pPencil;
        mSetUpMode = pSetUp;
        mSelection = pSelection;
        mSelectionRow = mSelection / 9;
        mSelectionColumn = mSelection % 9;
    }

    Cell[] xCells() {
        return mCells;
    }

    boolean xPencilMode() {
        return mPencilMode;
    }

    void xPencilFlip() {
        mPencilMode = !mPencilMode;
    }

    boolean xSetUpMode() {
        return mSetUpMode;
    }

    Cell xSelectedCell() {
        return mCells[mSelection];
    }

    Cell xCell(int pRow, int pColumn) {
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

    void xInitSetUp() {
        int lCount;

        for (lCount = 0; lCount < mCells.length; lCount++) {
            mCells[lCount].xReset();
        }
        mSetUpMode = true;
    }

    void xFinishSetup() {
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
        mSetUpMode = false;
    }

    void xSetCellValue(int pValue){
        Cell lCell;

        lCell = mCells[mSelection];
        if (lCell.xValue() == pValue){
            lCell.xValueReset();
            mDigitCount[0]++;
            mDigitCount[pValue]--;
        } else {
            if (lCell.xValue() == 0){
                mDigitCount[0]--;
            } else {
                mDigitCount[lCell.xValue()]--;
            }
            lCell.xValue(pValue);
            mDigitCount[pValue]++;
        }
    }

    void xResetConflicts(){
        int lCount;

        for (lCount = 0; lCount < mCells.length; lCount++){
            mCells[lCount].xConflict(false);
        }
    }
}
