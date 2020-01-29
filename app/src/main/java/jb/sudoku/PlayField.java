package jb.sudoku;

import java.util.List;

class PlayField {
    private int mFieldId;
    private PlayCell[] mCells;
    private boolean mPencilMode;
    //    private boolean mSetUpMode;
//    private boolean mLibraryMode;
//    private boolean mSolved;
    private int mSelection;
    private int mSelectionRow;
    private int mSelectionColumn;
//    private int mGameStatus;
//    static final int cStatusNone = 0;
//    static final int cStatusSetup = 1;
//    static final int cStatusGenerate = 2;
//    static final int cStatusPlay = 3;
//    static final int cStatusSolved = 4;
//    private int mDifficulty;
//    private int mUsedTime;

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
/*        mGameStatus = cStatusNone;
        mSetUpMode = false;
        mLibraryMode = false;
        mSolved = false;
        mDifficulty = -1;
        mUsedTime = 0; */
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
//        mSetUpMode = pSetUp;
//        mLibraryMode = pLib;
        mSelection = pSelection;
        mSelectionRow = mSelection / 9;
        mSelectionColumn = mSelection % 9;
/*        if (pCells.size() > 0){
            if (mSetUpMode) {
                mGameStatus = cStatusSetup;
            } else {
                if (mSolved) {
                    mGameStatus = cStatusSolved;
                } else {
                    mGameStatus = cStatusPlay;
                }
            }
        } else {
            mGameStatus = cStatusNone;
        }
        mDifficulty = pDifficulty;
        mUsedTime = pUsedTime; */
    }

    private void sDigitCount() {
        int lCount;

        for (lCount = 0; lCount < mDigitCount.length; lCount++) {
            mDigitCount[lCount] = 0;
        }
        for (lCount = 0; lCount < mCells.length; lCount++) {
            mDigitCount[mCells[lCount].xValue()]++;
        }
/*        //noinspection RedundantIfStatement
        if (mDigitCount[0] > 0) {
            mSolved = false;
        } else {
            mSolved = true;
        } */
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

/*    void xCells(Cell[] pCells, boolean pLibrary) {
        int lCount;

        for (lCount = 0; lCount < mCells.length; lCount++) {
            mCells[lCount].xInitCell(pCells[lCount]);
        }
        mSetUpMode = false;
        mPencilMode = false;
        mLibraryMode = pLibrary;
        sDigitCount();
    } */

    boolean xPencilMode() {
        return mPencilMode;
    }

    void xPencilFlip() {
        mPencilMode = !mPencilMode;
    }

/*    boolean xSetUpMode() {
        return mSetUpMode;
    } */

/*    boolean xLibraryMode() {
        return mLibraryMode;
    } */

/*    int xGameStatus() {
        return mGameStatus;
    } */

/*    void xGameStatus(int pStatus) {
        mGameStatus = pStatus;
    } */

/*    int xDifficulty(){
        return mDifficulty;
    } */

/*    void xDifficulty(int pDifficulty){
        mDifficulty = pDifficulty;
    } */

/*    int xUsedTime(){
        return mUsedTime;
    } */

/*    void xResetUsedTime(){
        mUsedTime = 0;
    } */

/*    void xAddUsedTime(int pCorr){
        mUsedTime += pCorr;
    } */

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

/*    void xInitSetUp() {
        int lCount;

        if (!mSetUpMode) {
            for (lCount = 0; lCount < mCells.length; lCount++) {
                mCells[lCount].xReset();
            }
            sDigitCount();
            mSetUpMode = true;
            mPencilMode = false;
            mLibraryMode = false;
            mDifficulty = -1;
        }
    } */

    void xResetField() {
        int lCount;

        for (lCount = 0; lCount < mCells.length; lCount++) {
            mCells[lCount].xReset();
        }
        sDigitCount();
        mPencilMode = false;
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

/*    void xFinishSetup() {
        int lCount;
        Cell lCell;

        if (mSetUpMode) {
            for (lCount = 0; lCount < mCells.length; lCount++) {
                lCell = mCells[lCount];
                if (lCell.xValue() == 0) {
                    lCell.xFixed(false);
                } else {
                    lCell.xFixed(true);
                }
            }
            sDigitCount();
            mSetUpMode = false;
        }
    } */

    String xGame() {
        StringBuilder lBuilder;
        int lCount;

        lBuilder = new StringBuilder();
        for (lCount = 0; lCount < mCells.length; lCount++) {
            if (mCells[lCount].xFixed()) {
                lBuilder.append(mCells[lCount].xValue());
            } else {
                lBuilder.append("0");
            }
        }
        return lBuilder.toString();
    }

    void xSetCellValue(int pValue) {
        PlayCell lCell;

        lCell = mCells[mSelection];
        if (lCell.xValue() == pValue) {
            lCell.xValueReset();
            mDigitCount[0]++;
            mDigitCount[pValue]--;
        } else {
            if (lCell.xValue() == 0) {
                mDigitCount[0]--;
            } else {
                mDigitCount[lCell.xValue()]--;
            }
            lCell.xValue(pValue);
            mDigitCount[pValue]++;
        }
/*        //noinspection RedundantIfStatement
        if (mDigitCount[0] > 0) {
            mSolved = false;
        } else {
            mSolved = true;
        } */
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
