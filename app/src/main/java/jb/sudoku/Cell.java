package jb.sudoku;

class Cell {
    private int mValue;
    private boolean mFixed;

    Cell(){
        mValue = 0;
        mFixed = false;
    }

    Cell(int pValue, boolean pFixed){
        mValue = pValue;
        mFixed = pFixed;
    }

    Cell(Cell pCell){
        mValue = pCell.mValue;
        mFixed = pCell.mFixed;
    }

    void xInitCell(Cell pCell){
        mValue = pCell.mValue;
        mFixed = pCell.mFixed;
    }

    /**
     * Reset cell to empty
     */
    void xReset() {
        mValue = 0;
        mFixed = false;
    }

    /**
     * Gets the value of the cell
     *
     * @return int     Value
     */
    int xValue() {
        return mValue;
    }

    /**
     * Sets the value
     *
     * @param pValue 1..9    Value
     */
    void xValue(int pValue) {
        if (pValue >= 1 && pValue <= 9) {
            mValue = pValue;
        }
    }

    /**
     * Resets the value to 'no value'
     */
    void xValueReset() {
        mValue = 0;
    }

    /**
     * Gets the fixed flag
     *
     * @return boolean     The fixed flag
     */
    boolean xFixed() {
        return mFixed;
    }

    /**
     * Sets the fixed flag
     *
     * @param pFixed boolean     The value to set the fixed flag to
     */
    void xFixed(boolean pFixed) {
        mFixed = pFixed;
    }
}
