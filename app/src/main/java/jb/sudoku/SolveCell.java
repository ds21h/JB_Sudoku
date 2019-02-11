package jb.sudoku;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

class SolveCell {
    private Cell mCell;
    private boolean mBasis;
    private List<Integer> mFree;

    SolveCell(Cell pCell){
        mFree = new ArrayList<>();
        mCell = pCell;
        if (mCell.xValue() > 0){
            mBasis = true;
        } else {
            mBasis = false;
            sResetFree();
        }
    }

    private void sResetFree(){
        int lCount;

        mFree.clear();
        for (lCount = 1; lCount <= 9; lCount++){
            mFree.add(lCount);
        }
    }

    boolean xBasis(){
        return mBasis;
    }

    boolean xNextValue(Random pRandom){
        int lPos;
        boolean lResult;

        if (mFree.size() > 0){
            lPos = pRandom.nextInt(mFree.size());
            mCell.xValue(mFree.get(lPos));
            mFree.remove(lPos);
            lResult = true;
        } else {
            mCell.xReset();
            lResult = false;
        }
        return lResult;
    }

    void xReset(){
        mCell.xReset();
        sResetFree();
    }
}
