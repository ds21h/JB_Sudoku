package jb.sudoku;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

class GenerateCell extends Cell {
    private List<Integer> mFree;

    GenerateCell(){
        super();
        mFree = new ArrayList<>();
        sResetFree();
    }

    /**
     * Resets the memory of used values
     */
    private void sResetFree(){
        int lCount;

        mFree.clear();
        for (lCount = 1; lCount <= 9; lCount++){
            mFree.add(lCount);
        }
    }

    /**
     * Sets this cell to a next (not yet used) value
     *
     * @param pRandom   Random  The random generator to use
     * @return          boolean Result: true = next value entered, false = all values already used
     */
    boolean xNextValue(Random pRandom){
        int lPos;
        boolean lResult;

        if (mFree.size() > 0){
            lPos = pRandom.nextInt(mFree.size());
            xValue(mFree.get(lPos));
            mFree.remove(lPos);
            lResult = true;
        } else {
            xReset();
            lResult = false;
        }
        return lResult;
    }

    @Override
    void xReset(){
        super.xReset();
        sResetFree();
    }
}
