package jb.sudoku;

import android.os.AsyncTask;

import org.jetbrains.annotations.NotNull;

class SudokuGame {
    private GameData mGameData;

    SudokuGame(GameData pGameData) {
        mGameData = pGameData;
    }

    GameData xGameData() {
        return mGameData;
    }

    void xGameData(GameData pGameData) {
        mGameData = pGameData;
    }

    boolean xSelectionValue(int pRow, int pColumn) {
        PlayCell lCell;
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
                    if (lCell.xPencil(lValue)) {
                        lResult = true;
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
        //noinspection RedundantIfStatement
        if ((pRow / 3) == (mGameData.xSelectionRow() / 3) && (pColumn / 3) == (mGameData.xSelectionColumn() / 3)) {
            return true;
        }
        return false;
    }

    void xStartSetUp() {
        mGameData.xInitSetUp();
    }

    void xEndSetup() {
        if (sCheckGame()) {
            mGameData.xFinishSetup();
        }
    }

    void xNewGame(String pGame){
        GameScrambler lScrambler;
        Cell[] lCells;

        lScrambler = new GameScrambler(pGame);
        lCells = lScrambler.xScrambleGame();
        mGameData.xCells(lCells, true);
    }

    String xGame(){
        return mGameData.xGame();
    }

    void xFillPencil() {
        mGameData.xInitPencil();
        sPencilRows();
        sPencilColumns();
        sPencilSegments();
    }

    void xClearPencil() {
        mGameData.xClearPencil();
    }

    boolean xSolve() {
        SudokuSolver lSolver;
        Cell[] lCells;
        boolean lResult;

        lSolver = new SudokuSolver();
        lCells = lSolver.xSolve(mGameData.xCells());
        if (lCells == null) {
            lResult = false;
        } else {
            mGameData.xCells(lCells);
            lResult = true;
        }
        return lResult;
    }

    void xGenerate(int pLevel, AsyncTask pTask) {
        SudokuGenerator lGenerator;
        Cell[] lCells;

        lGenerator = new SudokuGenerator();
        lCells = lGenerator.xGenerate(pLevel, pTask);
        if (!(pTask != null && pTask.isCancelled())) {
            mGameData.xCells(lCells, false );
        }
    }

    void xProcessDigit(int pDigit) {
        PlayCell lCell;

        if (pDigit >= 1 && pDigit <= 9) {
            lCell = mGameData.xSelectedCell();
            if (mGameData.xPencilMode()) {
                lCell.xPencilFlip(pDigit);
            } else {
                if (!lCell.xFixed()) {
                    mGameData.xSetCellValue(pDigit);
                    sPencilRow(mGameData.xSelectionRow());
                    sPencilColumn(mGameData.xSelectionColumn());
                    sPencilSegment(mGameData.xSelectionRow() / 3, mGameData.xSelectionColumn() / 3);
                    sCheckGame();
                }
            }
        }
    }

    private boolean sCheckGame() {
        boolean lResult;
        boolean lResStep;

        mGameData.xResetConflicts();
        lResult = sCheckRows();
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

    private boolean sCheckRows() {
        int lRow;
        boolean lResult;
        boolean lResStep;

        lResult = true;
        for (lRow = 0; lRow < 9; lRow++) {
            lResStep = sCheckRow(lRow);
            if (lResult) {
                lResult = lResStep;
            }
        }
        return lResult;
    }

    private boolean sCheckRow(int pRow) {
        boolean lResult;
        int lColumn;
        PlayCell[] lBlock = new PlayCell[9];

        for (lColumn = 0; lColumn < 9; lColumn++) {
            lBlock[lColumn] = mGameData.xCell(pRow, lColumn);
        }
        lResult = sCheckBlock(lBlock);
        return lResult;
    }

    private boolean sCheckColumns() {
        int lColumn;
        boolean lResult;
        boolean lResStep;

        lResult = true;
        for (lColumn = 0; lColumn < 9; lColumn++) {
            lResStep = sCheckColumn(lColumn);
            if (lResult) {
                lResult = lResStep;
            }
        }
        return lResult;
    }

    private boolean sCheckColumn(int pColumn) {
        int lRow;
        PlayCell[] lBlock = new PlayCell[9];
        boolean lResult;

        for (lRow = 0; lRow < 9; lRow++) {
            lBlock[lRow] = mGameData.xCell(lRow, pColumn);
        }
        lResult = sCheckBlock(lBlock);
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
                lResStep = sCheckSegment(lSegmentRow, lSegmentColumn);
                if (lResult) {
                    lResult = lResStep;
                }
            }
        }
        return lResult;
    }

    private boolean sCheckSegment(int pSegmentRow, int pSegmentColumn) {
        int lRow;
        int lColumn;
        int lCell;
        PlayCell[] lBlock = new PlayCell[9];
        boolean lResult;

        lCell = 0;
        for (lRow = 0; lRow < 3; lRow++) {
            for (lColumn = 0; lColumn < 3; lColumn++) {
                lBlock[lCell] = mGameData.xCells()[((pSegmentRow * 27) + (lRow * 9)) + (pSegmentColumn * 3) + lColumn];
                lCell++;
            }
        }
        lResult = sCheckBlock(lBlock);
        return lResult;
    }

    private boolean sCheckBlock(@NotNull PlayCell[] pBlock) {
        int lCount1;
        int lCount2;
        boolean lResult;

        lResult = true;
        for (lCount1 = 0; lCount1 < pBlock.length - 1; lCount1++) {
            if (pBlock[lCount1].xValue() != 0) {
                for (lCount2 = lCount1 + 1; lCount2 < pBlock.length; lCount2++) {
                    if (pBlock[lCount1].xValue() == pBlock[lCount2].xValue()) {
                        pBlock[lCount1].xConflict(true);
                        pBlock[lCount2].xConflict(true);
                        lResult = false;
                    }
                }
            }
        }
        return lResult;
    }

    private void sPencilRows() {
        int lRow;

        for (lRow = 0; lRow < 9; lRow++) {
            sPencilRow(lRow);
        }
    }

    private void sPencilRow(int pRow) {
        int lColumn;
        PlayCell[] lBlock = new PlayCell[9];

        for (lColumn = 0; lColumn < 9; lColumn++) {
            lBlock[lColumn] = mGameData.xCell(pRow, lColumn);
        }
        sPencilBlock(lBlock);
    }

    private void sPencilColumns() {
        int lColumn;

        for (lColumn = 0; lColumn < 9; lColumn++) {
            sPencilColumn(lColumn);
        }
    }

    private void sPencilColumn(int pColumn) {
        int lRow;
        PlayCell[] lBlock = new PlayCell[9];

        for (lRow = 0; lRow < 9; lRow++) {
            lBlock[lRow] = mGameData.xCell(lRow, pColumn);
        }
        sPencilBlock(lBlock);
    }

    private void sPencilSegments() {
        int lSegmentRow;
        int lSegmentColumn;

        for (lSegmentRow = 0; lSegmentRow < 3; lSegmentRow++) {
            for (lSegmentColumn = 0; lSegmentColumn < 3; lSegmentColumn++) {
                sPencilSegment(lSegmentRow, lSegmentColumn);
            }
        }
    }

    private void sPencilSegment(int pSegmentRow, int pSegmentColumn) {
        int lRow;
        int lColumn;
        int lCell;
        PlayCell[] lBlock = new PlayCell[9];

        lCell = 0;
        for (lRow = 0; lRow < 3; lRow++) {
            for (lColumn = 0; lColumn < 3; lColumn++) {
                lBlock[lCell] = mGameData.xCells()[((pSegmentRow * 27) + (lRow * 9)) + (pSegmentColumn * 3) + lColumn];
                lCell++;
            }
        }
        sPencilBlock(lBlock);
    }

    private void sPencilBlock(@NotNull PlayCell[] pBlock) {
        int lCount1;
        int lCount2;

        for (lCount1 = 0; lCount1 < pBlock.length; lCount1++) {
            if (pBlock[lCount1].xValue() != 0) {
                for (lCount2 = 0; lCount2 < pBlock.length; lCount2++) {
                    pBlock[lCount2].xPencilReset(pBlock[lCount1].xValue());
                }
            }
        }
    }
}
