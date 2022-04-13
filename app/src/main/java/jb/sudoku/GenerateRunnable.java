package jb.sudoku;

import android.os.Handler;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

class GenerateRunnable implements Runnable{
    static final int cGenerateEnded = 0;
    static final int cGenerateFinished = 1;

    private final int[] mMaxTry = {1, 3, 5};   // Retry numbers for Easy/Normal/Hard

    private Handler mHandler;
    private SudokuGame mGame;
    int mLevel;
    int mMaxRetry;
    private GenerateCell[] mCells;
    private Random mRandom;
    private boolean mEnd;

    GenerateRunnable(Handler pHandler, SudokuGame pGame, int pLevel){
        mHandler = pHandler;
        mGame = pGame;
        mLevel = pLevel;
        if (mLevel < 1 || mLevel > mMaxTry.length){
            mMaxRetry = mMaxTry[mMaxTry.length - 1];
        } else {
            mMaxRetry = mMaxTry[mLevel - 1];
        }
        mCells = new GenerateCell[81];
        mRandom = new Random();
        mEnd = false;
    }

    void xEnd(){
        mEnd = true;
    }

    @Override
    public void run() {
        int lResult;
        SudokuSolver lSolver = new SudokuSolver();
        boolean lGameOk;
        List<Integer> lCellList = new ArrayList<>();

        lResult = cGenerateEnded;
        mGame.xGenerateStart(mLevel);
        do {
            if (mEnd){
                break;
            }
            sInit();
            sMakeSolution();
            sInitCellList(lCellList);
            sBasicGame(lCellList);
            lGameOk = lSolver.xSingleSolution(mCells);
        } while (!lGameOk);
        if (!mEnd){
            sCreateGame(lCellList, lSolver, mMaxRetry);
            if (!mEnd){
                sFinishGame();
                mGame.xGenerateEnd(mCells);
                lResult |= cGenerateFinished;
            }
        }
        mHandler.sendEmptyMessage(lResult);
    }

    private void sInit() {
        int lCount;

        for (lCount = 0; lCount < mCells.length; lCount++) {
            mCells[lCount] = new GenerateCell();
        }
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

    private void sCreateGame(List<Integer> pCellList, SudokuSolver pSolver, int pMax){
        int lAttempt;
        int lRndCell;
        int lCellNumber;
        int lSaveValue;
        boolean lGameOK;

        lAttempt = 0;
        do{
            if (mEnd){
                break;
            }
            if (pCellList.size() > 0){
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
            } else {
                break;
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
}
