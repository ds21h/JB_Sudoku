package jb.sudoku;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

class SudokuGame {
    private GameData mGameData;

    /**
     * Constructor for an empty game
     */
    SudokuGame() {
        mGameData = new GameData();
    }

    /**
     * Gets the gamedata from this game (for safekeeping)
     *
     * @return      GameData
     */
    GameData xGameData() {
        return mGameData;
    }

    /**
     * Sets the gamadata for this game (restoring a game)
     *
     * @param pGameData     GameData
     */
    void xGameData(GameData pGameData) {
        mGameData = pGameData;
    }

    /**
     * Determines whether the given position (row, column) has the same value as the selected cell
     *
     * @param pRow      0..8    Row
     * @param pColumn   0..8    Column
     * @return          boolean Result
     */
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
                    if (lCell.xPencil(lValue)) {
                        lResult = true;
                    }
                }
            }
        }
        return lResult;
    }

    /**
     * Determines whether the given position (row, column) is in the range of the selected cell
     * (same row, column and/or segment)
     *
     * @param pRow      0..8    Row
     * @param pColumn   0..8    Column
     * @return          boolean  Result
     */
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

    /**
     * Start setup mode
     */
    void xStartSetUp() {
        mGameData.xInitSetUp();
    }

    /**
     * End setup mode
     */
    void xEndSetup() {
        if (xCheckGame()) {
            mGameData.xFinishSetup();
        }
    }

    /**
     * Fills all possible pencilmarkings and switches to pencilmode
     */
    void xFillPencil(){
      mGameData.xInitPencil();
      sPencilRows();
      sPencilColumns();
      sPencilSegments();
    }

    /**
     * Clears all pencilmarkings
     */
    void xClearPencil(){
        mGameData.xClearPencil();
    }

    /**
     * Solves the game
     *
     * @return      boolean     True if successfull, false if failed (unsolvable)
     */
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

    /**
     * Generate a new game
     */
    void xGenerate(){
        int lCount;
        Random lRandom;
        int lRndCell;
        List<Integer> lCellList;
        int lCellNumber;
        int lNumber;
        int lSaveValue;
        int lAttempt;

        lCellList = new ArrayList<>();
        lRandom = new Random();
        do {
            mGameData.xInitSetUp();
            xSolve();
            lCellList.clear();
            for (lCount = 0; lCount < 81; lCount++){
                lCellList.add(lCount);
            }
            for (lCount = 0; lCount < 40; lCount++){
                lRndCell = lRandom.nextInt(lCellList.size());
                lCellNumber = lCellList.get(lRndCell);
                lCellList.remove(lRndCell);
                mGameData.xCell(lCellNumber).xReset();
            }
            lNumber = sNumberSolutions(2);
        } while (lNumber != 1);

        lAttempt = 0;
        do{
            lRndCell = lRandom.nextInt(lCellList.size());
            lCellNumber = lCellList.get(lRndCell);
            lCellList.remove(lRndCell);
            lSaveValue = mGameData.xCell(lCellNumber).xValue();
            mGameData.xCell(lCellNumber).xReset();
            lNumber = sNumberSolutions(2);
            if (lNumber == 1){
                lAttempt = 0;
            } else {
                mGameData.xCell(lCellNumber).xValue(lSaveValue);
                lAttempt++;
                if (lAttempt > 3){
                    break;
                }
            }
        } while (true);
        mGameData.xFinishSetup();
    }

    /**
     * Determine the number of solutions for the existing game. Stops when the given maximum is reached.
     *
     * @param pMax      int     The maximum
     * @return          int     Number of solutions
     */
    private int sNumberSolutions(int pMax){
        return sNumberSolutions(0,0, pMax);
    }

    /**
     * Recursive function to determine the number of solutions for the existing game
     *
     * @param pStart    0..80   Start cellnumber
     * @param pCount    int     Number of solutions so far
     * @param pMax      int     Maximum to count
     * @return          int     Number of solutions
     */
    private int sNumberSolutions(int pStart, int pCount, int pMax){
        int lValue;
        int lCount;
        int lResult;

        if (pStart >= 81){
            return pCount + 1;
        }
        if (mGameData.xCell(pStart).xValue() != 0){
            return sNumberSolutions(pStart + 1, pCount, pMax);
        }
        lCount = pCount;
        for (lValue = 1; lValue <= 9 && lCount < pMax; lValue++){
            mGameData.xCell(pStart).xValue(lValue);
            if (sCheckCell(pStart)){
                lCount = sNumberSolutions(pStart + 1, lCount, pMax);
            }
        }
        mGameData.xCell(pStart).xReset();
        return lCount;
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
                    sPencilRow(mGameData.xSelectionRow());
                    sPencilColumn(mGameData.xSelectionColumn());
                    sPencilSegment(mGameData.xSelectionRow() / 3, mGameData.xSelectionColumn() / 3);
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

    private boolean sCheckBlock(@NotNull Cell[] pBlock, boolean pMark) {
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

    /**
     * Resets the pencil flags according to the values in the rows
     */
    private void sPencilRows() {
        int lRow;

        for (lRow = 0; lRow < 9; lRow++) {
            sPencilRow(lRow);
        }
    }

    /**
     * Resets the pencil flags according to the values in the row
     *
     * @param pRow      0..8
     */
    private void sPencilRow(int pRow) {
        int lColumn;
        Cell[] lBlock = new Cell[9];

        for (lColumn = 0; lColumn < 9; lColumn++) {
            lBlock[lColumn] = mGameData.xCell(pRow, lColumn);
        }
        sPencilBlock(lBlock);
    }

    /**
     * Resets the pencil flags according to the values in the columns
     */
    private void sPencilColumns() {
        int lColumn;

        for (lColumn = 0; lColumn < 9; lColumn++) {
            sPencilColumn(lColumn);
        }
    }

    /**
     * Resets the pencil flags according to the values in the column
     *
     * @param pColumn       0..8
     */
    private void sPencilColumn(int pColumn) {
        int lRow;
        Cell[] lBlock = new Cell[9];

        for (lRow = 0; lRow < 9; lRow++) {
            lBlock[lRow] = mGameData.xCell(lRow, pColumn);
        }
        sPencilBlock(lBlock);
    }

    /**
     * Resets the pencil flags according to the values in the segments
     */
    private void sPencilSegments() {
        int lSegmentRow;
        int lSegmentColumn;

        for (lSegmentRow = 0; lSegmentRow < 3; lSegmentRow++) {
            for (lSegmentColumn = 0; lSegmentColumn < 3; lSegmentColumn++) {
                sPencilSegment(lSegmentRow, lSegmentColumn);
            }
        }
    }

    /**
     * Resets the pencil flags according to the values in the segment (segmentrow, segmentcolumn)
     * @param pSegmentRow       0..2
     * @param pSegmentColumn    0..2
     */
    private void sPencilSegment(int pSegmentRow, int pSegmentColumn) {
        int lRow;
        int lColumn;
        int lCell;
        Cell[] lBlock = new Cell[9];

        lCell = 0;
        for (lRow = 0; lRow < 3; lRow++) {
            for (lColumn = 0; lColumn < 3; lColumn++) {
                lBlock[lCell] = mGameData.xCells()[((pSegmentRow * 27) + (lRow * 9)) + (pSegmentColumn * 3) + lColumn];
                lCell++;
            }
        }
        sPencilBlock(lBlock);
    }

    /**
     * Resets the pencil flags according to the values in the block
     * @param pBlock    Cell[9]
     */
    private void sPencilBlock(@NotNull Cell[] pBlock) {
        int lCount1;
        int lCount2;
        boolean lResult;

        lResult = true;
        for (lCount1 = 0; lCount1 < pBlock.length; lCount1++) {
            if (pBlock[lCount1].xValue() != 0) {
                for (lCount2 = 0; lCount2 < pBlock.length; lCount2++) {
                    pBlock[lCount2].xPencilReset(pBlock[lCount1].xValue());
                }
            }
        }
    }
}
