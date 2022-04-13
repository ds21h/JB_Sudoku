package jb.sudoku;

import org.jetbrains.annotations.NotNull;

class SudokuSolver {
    private final Cell[] mCells;

    SudokuSolver(){
        int lCount;

        mCells = new Cell[81];
        for (lCount = 0; lCount < mCells.length; lCount++){
            mCells[lCount] = new Cell();
        }
    }

    Cell[] xSolve(Cell[] pCells){
        boolean lResultOK;

        sCopyGame(pCells);
        lResultOK = sSolve();
        if (lResultOK){
            return mCells;
        } else {
            return null;
        }
    }

    boolean xSingleSolution(Cell[] pCells){
        int lResult;

        sCopyGame(pCells);
        lResult = sCountPos(0, 0);
        //noinspection RedundantIfStatement
        if (lResult == 1){
            return true;
        } else {
            return false;
        }
    }

    private void sCopyGame(Cell[] pCells){
        int lCount;

        for (lCount = 0; lCount < mCells.length; lCount++){
            mCells[lCount].xInitCell(pCells[lCount]);
        }
    }

    private boolean sSolve(){
        return sNextPos(0);
    }

    private boolean sNextPos(int pStart){
        int lValue;
        boolean lResultOK;

        if (pStart < 81) {
            if (mCells[pStart].xValue() != 0){
                lResultOK = sNextPos(pStart + 1);
            } else {
                lResultOK = false;
                for (lValue = 1; lValue <= 9; lValue++){
                    mCells[pStart].xValue(lValue);
                    if (xCheckCell(pStart, mCells)){
                        lResultOK = sNextPos(pStart + 1);
                        if (lResultOK){
                            break;
                        }
                    }
                }
                if (!lResultOK){
                    mCells[pStart].xReset();
                }
            }
        } else {
            lResultOK = true;
        }
        return lResultOK;
    }

    private int sCountPos(int pStart, int pCount){
        int lValue;
        int lCount;

        if (pStart >= 81){
            return pCount + 1;
        }
        if (mCells[pStart].xValue() != 0){
            return sCountPos(pStart + 1, pCount);
        }
        lCount = pCount;
        for (lValue = 1; lValue <= 9 && lCount < 2; lValue++){
            mCells[pStart].xValue(lValue);
            if (xCheckCell(pStart, mCells)){
                lCount = sCountPos(pStart + 1, lCount);
            }
        }
        mCells[pStart].xReset();
        return lCount;
    }

    static boolean xCheckCell(int pCellNumber, Cell[] pCells) {
        Cell[] lBlock = new Cell[9];
        int lRow;
        int lColumn;
        int lSegmentRow;
        int lSegmentColumn;
        int lCell;
        boolean lResult;

        lRow = pCellNumber / 9;
        lSegmentRow = lRow / 3;
        for (lColumn = 0; lColumn < 9; lColumn++) {
            lBlock[lColumn] = pCells[(lRow * 9) + lColumn];
        }
        lResult = sCheckBlock(lBlock);
        if (lResult) {
            lColumn = pCellNumber % 9;
            lSegmentColumn = lColumn / 3;
            for (lRow = 0; lRow < 9; lRow++) {
                lBlock[lRow] = pCells[(lRow * 9) + lColumn];
            }
            lResult = sCheckBlock(lBlock);
            if (lResult) {
                lCell = 0;
                for (lRow = 0; lRow < 3; lRow++) {
                    for (lColumn = 0; lColumn < 3; lColumn++) {
                        lBlock[lCell] = pCells[((lSegmentRow * 27) + (lRow * 9)) + (lSegmentColumn * 3) + lColumn];
                        lCell++;
                    }
                }
                lResult = sCheckBlock(lBlock);
            }
        }

        return lResult;
    }

    private static boolean sCheckBlock(@NotNull Cell[] pBlock) {
        int lCount1;
        int lCount2;
        boolean lResult;

        lResult = true;
        for (lCount1 = 0; lCount1 < pBlock.length - 1; lCount1++) {
            if (pBlock[lCount1].xValue() != 0) {
                for (lCount2 = lCount1 + 1; lCount2 < pBlock.length; lCount2++) {
                    if (pBlock[lCount1].xValue() == pBlock[lCount2].xValue()) {
                        lResult = false;
                    }
                }
            }
        }
        return lResult;
    }
}
