package jb.sudoku;

import android.os.AsyncTask;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

class SudokuGenerator {
    private GenerateCell[] mCells;
    private Random mRandom;

    SudokuGenerator() {
        mCells = new GenerateCell[81];
        mRandom = new Random();
        sInit();
    }

    private void sInit() {
        int lCount;

        for (lCount = 0; lCount < mCells.length; lCount++) {
            mCells[lCount] = new GenerateCell();
        }
    }

    Cell[] xGenerate(int pLevel, AsyncTask pTask) {
        SudokuSolver lSolver = new SudokuSolver();
        boolean lGameOk;
        List<Integer> lCellList = new ArrayList<>();

        do {
            if (pTask != null && pTask.isCancelled()){
                break;
            }
            sInit();
            sMakeSolution();
            sInitCellList(lCellList);
            sBasicGame(lCellList);
            lGameOk = lSolver.xSingleSolution(mCells);
        } while (!lGameOk);
        sCreateGame(pTask, lCellList, lSolver, pLevel);
        sFinishGame();
        return mCells;
    }

    private void sMakeSolution() {
        sNextValue(0);
    }

    private boolean sNextValue(int pStart) {
        boolean lResultOK;

        lResultOK = false;
        if (pStart < mCells.length) {
            while (mCells[pStart].xNextValue(mRandom)) {
                if (SudokuSolver.xCheckCell(pStart, mCells)) {
                    lResultOK = sNextValue(pStart + 1);
                    if (lResultOK) {
                        break;
                    }
                }
            }
            if (!lResultOK) {
                mCells[pStart].xReset();
            }
        } else {
            lResultOK = true;
        }

        return lResultOK;
    }

    private void sInitCellList(List<Integer> pCellList){
        int lCount;

        pCellList.clear();
        for (lCount = 0; lCount < 81; lCount++) {
            pCellList.add(lCount);
        }
    }

    private void sBasicGame(List<Integer> pCellList) {
        int lCount;
        int lRndCell;
        int lCellNumber;

        for (lCount = 0; lCount < 40; lCount++) {
            lRndCell = mRandom.nextInt(pCellList.size());
            lCellNumber = pCellList.get(lRndCell);
            pCellList.remove(lRndCell);
            mCells[lCellNumber].xReset();
        }
    }

    private void sCreateGame(@Nullable AsyncTask pTask, List<Integer> pCellList, SudokuSolver pSolver, int pMax){
        int lAttempt;
        int lRndCell;
        int lCellNumber;
        int lSaveValue;
        boolean lGameOK;

        lAttempt = 0;
        do{
            if (pTask != null && pTask.isCancelled()){
                break;
            }
            lRndCell = mRandom.nextInt(pCellList.size());
            lCellNumber = pCellList.get(lRndCell);
            pCellList.remove(lRndCell);
            lSaveValue = mCells[lCellNumber].xValue();
            mCells[lCellNumber].xReset();
            lGameOK = pSolver.xSingleSolution(mCells);
            if (lGameOK){
                lAttempt = 0;
            } else {
                mCells[lCellNumber].xValue(lSaveValue);
                lAttempt++;
                if (lAttempt > pMax){
                    break;
                }
            }
        } while (true);
    }

    private void sFinishGame(){
        int lCount;

        for (lCount = 0; lCount < mCells.length; lCount++){
            if (mCells[lCount].xValue() > 0){
                mCells[lCount].xFixed(true);
            }
        }
    }

    /*    private void sCreateGame(){
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
            if (mTask != null && mTask.isCancelled()){
                break;
            }
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
        mGameData.xFinishSetup();

    }
    private boolean sCheckCell(int pCellNumber) {
        Cell[] lBlock = new Cell[9];
        int lRow;
        int lColumn;
        int lCell;
        boolean lResult;

        lRow = pCellNumber / 9;
        for (lColumn = 0; lColumn < 9; lColumn++) {
            lBlock[lColumn] = mCells[(lRow * 9) + lColumn];
        }
        lResult = sCheckBlock(lBlock);
        if (lResult) {
            lColumn = pCellNumber % 9;
            for (lRow = 0; lRow < 9; lRow++) {
                lBlock[lRow] = mCells[(lRow * 9) + lColumn];
            }
            lResult = sCheckBlock(lBlock);
            if (lResult) {
                lCell = 0;
                for (lRow = 0; lRow < 3; lRow++) {
                    for (lColumn = 0; lColumn < 3; lColumn++) {
                        lBlock[lCell] = mCells[(((lRow / 3) * 27) + (lRow * 9)) + ((lColumn / 3) * 3) + lColumn];
                        lCell++;
                    }
                }
                lResult = sCheckBlock(lBlock);
            }
        }

        return lResult;
    }

    private boolean sCheckBlock(@NotNull Cell[] pBlock) {
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
    } */
}
