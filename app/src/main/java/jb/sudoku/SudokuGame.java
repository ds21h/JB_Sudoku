package jb.sudoku;

import java.util.Random;

class SudokuGame {
    private GameData mGameData;

    SudokuGame() {
        mGameData = new GameData();
    }

    GameData xGameData() {
        return mGameData;
    }

    void xGameData(GameData pGameData) {
        mGameData = pGameData;
    }

    boolean xSelectionValue(int pRow, int pColumn) {
        Cell lCell;
        int lValue;
        boolean lResult;

        lResult = false;
        lValue = mGameData.xSelectedCell().xValue();
        if (lValue > 0) {
            lCell = mGameData.xCell(pRow, pColumn);
            if (lCell.xValue() == lValue) {
                lResult = true;
            } else {
                if (lCell.xValue() == 0) {
                    if (mGameData.xPencilMode()) {
                        if (lCell.xPencil(lValue)) {
                            lResult = true;
                        }
                    }
                }
            }
        }
        return lResult;
    }

    boolean xSelectionRange(int pRow, int pColumn) {
        if (pRow == mGameData.xSelectionRow()) {
            return true;
        }
        if (pColumn == mGameData.xSelectionColumn()) {
            return true;
        }
        if ((pRow / 3) == (mGameData.xSelectionRow() / 3) && (pColumn / 3) == (mGameData.xSelectionColumn() / 3)) {
            return true;
        }
        return false;
    }

    void xStartSetUp() {
        mGameData.xInitSetUp();
    }

    void xEndSetup() {
        if (xCheckGame()) {
            mGameData.xFinishSetup();
        }
    }

    boolean xSolve() {
        SolveCell[] lCells;
        int lCount;
        Random lRandom;
        int lCellNumber;
        boolean lSolved;

        lCells = new SolveCell[81];
        for (lCount = 0; lCount < lCells.length; lCount++) {
            lCells[lCount] = new SolveCell(mGameData.xCells()[lCount]);
        }

        lCellNumber = 0;
        lSolved = true;
        lRandom = new Random();
        while (lCellNumber < 81) {
            if (lCells[lCellNumber].xBasis()) {
                lCellNumber++;
            } else {
                if (lCells[lCellNumber].xNextValue(lRandom)) {
                    if (sCheckCell(lCellNumber)) {
                        lCellNumber++;
                    }
                } else {
                    lCells[lCellNumber].xReset();
                    do {
                        lCellNumber--;
                        if (lCellNumber < 0) {
                            lSolved = false;
                            break;
                        }
                    } while (lCells[lCellNumber].xBasis());
                    if (lCellNumber < 0) {
                        break;
                    }
                }
            }
        }
        mGameData.xDigitCount();
        return lSolved;
    }

    void xProcessDigit(int pDigit) {
        Cell lCell;

        if (pDigit >= 1 && pDigit <= 9) {
            lCell = mGameData.xSelectedCell();
            if (mGameData.xPencilMode()) {
                lCell.xPencilFlip(pDigit);
            } else {
                if (!lCell.xFixed()) {
                    mGameData.xSetCellValue(pDigit);
                    xCheckGame();
                }
            }
        }
    }

    boolean xCheckGame() {
        boolean lResult;
        boolean lResStep;

        lResult = true;
        mGameData.xResetConflicts();
        lResStep = sCheckRows();
        if (lResult) {
            lResult = lResStep;
        }
        lResStep = sCheckColumns();
        if (lResult) {
            lResult = lResStep;
        }
        lResStep = sCheckSegments();
        if (lResult) {
            lResult = lResStep;
        }
        return lResult;
    }

    private boolean sCheckCell(int pCellNumber) {
        int lRow;
        int lColumn;
        boolean lResult;

        lRow = pCellNumber / 9;
        lResult = sCheckRow(lRow, false);
        if (lResult) {
            lColumn = pCellNumber % 9;
            lResult = sCheckColumn(lColumn, false);
            if (lResult) {
                lResult = sCheckSegment(lRow /3, lColumn / 3, false);
            }
        }

        return lResult;
    }

    private boolean sCheckRows() {
        int lRow;
        boolean lResult;
        boolean lResStep;

        lResult = true;
        for (lRow = 0; lRow < 9; lRow++) {
            lResStep = sCheckRow(lRow, true);
            if (lResult) {
                lResult = lResStep;
            }
        }
        return lResult;
    }

    private boolean sCheckRow(int pRow, boolean pMark) {
        boolean lResult;
        int lColumn;
        Cell[] lBlock = new Cell[9];

        for (lColumn = 0; lColumn < 9; lColumn++) {
            lBlock[lColumn] = mGameData.xCell(pRow, lColumn);
        }
        lResult = sCheckBlock(lBlock, pMark);
        return lResult;
    }

    private boolean sCheckColumns() {
        int lColumn;
        boolean lResult;
        boolean lResStep;

        lResult = true;
        for (lColumn = 0; lColumn < 9; lColumn++) {
            lResStep = sCheckColumn(lColumn, true);
            if (lResult) {
                lResult = lResStep;
            }
        }
        return lResult;
    }

    private boolean sCheckColumn(int pColumn, boolean pMark) {
        int lRow;
        Cell[] lBlock = new Cell[9];
        boolean lResult;

        for (lRow = 0; lRow < 9; lRow++) {
            lBlock[lRow] = mGameData.xCell(lRow, pColumn);
        }
        lResult = sCheckBlock(lBlock, pMark);
        return lResult;
    }

    private boolean sCheckSegments() {
        int lSegmentRow;
        int lSegmentColumn;
        boolean lResult;
        boolean lResStep;

        lResult = true;
        for (lSegmentRow = 0; lSegmentRow < 3; lSegmentRow++) {
            for (lSegmentColumn = 0; lSegmentColumn < 3; lSegmentColumn++) {
                lResStep = sCheckSegment(lSegmentRow, lSegmentColumn, true);
                if (lResult) {
                    lResult = lResStep;
                }
            }
        }
        return lResult;
    }

    private boolean sCheckSegment(int pSegmentRow, int pSegmentColumn, boolean pMark) {
        int lRow;
        int lColumn;
        int lCell;
        Cell[] lBlock = new Cell[9];
        boolean lResult;

        lCell = 0;
        for (lRow = 0; lRow < 3; lRow++) {
            for (lColumn = 0; lColumn < 3; lColumn++) {
                lBlock[lCell] = mGameData.xCells()[((pSegmentRow * 27) + (lRow * 9)) + (pSegmentColumn * 3) + lColumn];
                lCell++;
            }
        }
        lResult = sCheckBlock(lBlock, pMark);
        return lResult;
    }

    private boolean sCheckBlock(Cell[] pBlock, boolean pMark) {
        int lCount1;
        int lCount2;
        boolean lResult;

        lResult = true;
        for (lCount1 = 0; lCount1 < pBlock.length - 1; lCount1++) {
            if (pBlock[lCount1].xValue() != 0) {
                for (lCount2 = lCount1 + 1; lCount2 < pBlock.length; lCount2++) {
                    if (pBlock[lCount1].xValue() == pBlock[lCount2].xValue()) {
                        if (pMark) {
                            pBlock[lCount1].xConflict(true);
                            pBlock[lCount2].xConflict(true);
                        }
                        lResult = false;
                    }
                }
            }
        }
        return lResult;
    }
}
