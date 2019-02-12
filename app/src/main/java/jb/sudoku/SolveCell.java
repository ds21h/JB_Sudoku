package jb.sudoku;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Cellwrapper for use during solving of the game.
 * Add a basis flag to indicate whether or not the solver is autorised to change the cell.
 * Also adds the memory which values haven't been used yet.
 */
class SolveCell {
    private Cell mCell;
    private boolean mBasis;
    private List<Integer> mFree;

    /**
     * Constructor for creating instance 'around' the given cell
     *
     * @param pCell     Cell    The given cell
     */
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
     * Gets the basis flag
     *
     * @return      boolean     The basis flag
     */
    boolean xBasis(){
        return mBasis;
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
            mCell.xValue(mFree.get(lPos));
            mFree.remove(lPos);
            lResult = true;
        } else {
            mCell.xReset();
            lResult = false;
        }
        return lResult;
    }

    /**
     * Reset this wrapper and the cell to empty
     */
    void xReset(){
        mCell.xReset();
        sResetFree();
    }
}
