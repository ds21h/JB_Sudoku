package jb.sudoku;

class Cell {
    private int mValue;
    private boolean mFixed;
    private boolean mConflict;
    private boolean[] mPencil = new boolean[9];

    /**
     * Constructor for creating an empty cell
     */
    Cell() {
        sReset();
    }

    /**
     * Constructor for recreating an cell
     *
     * @param pValue    0..9    Value of the ce
     * @param pFixed    boolean Flag indicating if value can be changed
     * @param pConflict boolean Flag indicating that the value of this cell is conflicting wih another cell
     * @param pPencil   String  Pencil marks for this cell
     */
    Cell(int pValue, boolean pFixed, boolean pConflict, String pPencil) {
        String lPos;
        int lCount;

        sReset();
        mValue = pValue;
        mFixed = pFixed;
        mConflict = pConflict;
        for (lCount = 0; lCount < pPencil.length(); lCount++) {
            lPos = pPencil.substring(lCount, lCount + 1);
            if (lPos.charAt(0) >= '1' && lPos.charAt(0) <= '9') {
                mPencil[Integer.valueOf(lPos) - 1] = true;
            }
        }
    }

    /**
     * Reset cell to empty
     */
    void xReset() {
        sReset();
    }

    /**
     * Reset cell to empty
     */
    private void sReset() {
        int lCount;

        mValue = 0;
        mFixed = false;
        mConflict = false;
        for (lCount = 0; lCount < mPencil.length; lCount++) {
            mPencil[lCount] = false;
        }
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
     * Gets the conflict flag
     *
     * @return boolean     The conflict flag
     */
    boolean xConflict() {
        return mConflict;
    }

    /**
     * Sets the conflict flag
     *
     * @param pConflict boolean     The value to set the flag to
     */
    void xConflict(boolean pConflict) {
        mConflict = pConflict;
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

    /**
     * Gets the pencil flag for the given digit
     *
     * @param pValue 1..9    The digit
     * @return boolean The pencil flag
     */
    boolean xPencil(int pValue) {
        if (pValue >= 1 && pValue <= 9) {
            return mPencil[pValue - 1];
        } else {
            return false;
        }
    }

    /**
     * Gets the pencil flags as string
     *
     * @return String      The flagged digits
     */
    String xPencils() {
        StringBuilder lBuilder;
        int lCount;

        lBuilder = new StringBuilder();
        for (lCount = 0; lCount < mPencil.length; lCount++) {
            if (mPencil[lCount]) {
                lBuilder.append(lCount + 1);
            }
        }
        return lBuilder.toString();
    }

    /**
     * Flips (inverts) the pencil flag for the specified digit
     *
     * @param pValue 1..9    The digit
     */
    void xPencilFlip(int pValue) {
        if (pValue >= 1 && pValue <= 9) {
            mPencil[pValue - 1] = !mPencil[pValue - 1];
        }
    }

    /**
     * Sets the pencil flag for the specified digit to false
     *
     * @param pPencil 1..9    The digit
     */
    void xPencilReset(int pPencil){
        if (pPencil >= 1 && pPencil <= 9) {
            mPencil[pPencil - 1] = false;
        }
    }
    /**
     * Sets all the pencil flags to true
     */
    void xSetPencils() {
        sSetPencils(true);
    }

    /**
     * Sets all the pencil flags to false
     */
    void xClearPencils() {
        sSetPencils(false);
    }

    /**
     * Sets all pencil flags to the specified value
     * @param pPencil   boolean
     */
    private void sSetPencils(boolean pPencil) {
        int lCount;

        for (lCount = 0; lCount < mPencil.length; lCount++) {
            mPencil[lCount] = pPencil;
        }
    }
}
