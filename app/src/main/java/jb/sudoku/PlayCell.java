package jb.sudoku;

class PlayCell extends Cell {
    private boolean mConflict;
    private boolean[] mPencil = new boolean[9];


    /**
     * Constructor for creating an empty cell
     */
    PlayCell() {
        super();
        sReset();
    }

    /**
     * Constructor for recreating an cell
     *
     * @param pValue    0..9    Value of the ce
     * @param pFixed    boolean Flag indicating if value can be changed
     * @param pConflict boolean Flag indicating that the value of this cell is conflicting with another cell
     * @param pPencil   String  Pencil marks for this cell
     */
    PlayCell(int pValue, boolean pFixed, boolean pConflict, String pPencil) {
        super(pValue, pFixed);

        String lPos;
        int lCount;

        sReset();
        mConflict = pConflict;
        for (lCount = 0; lCount < pPencil.length(); lCount++) {
            lPos = pPencil.substring(lCount, lCount + 1);
            if (lPos.charAt(0) >= '1' && lPos.charAt(0) <= '9') {
                mPencil[Integer.valueOf(lPos) - 1] = true;
            }
        }
    }

    @Override
    void xInitCell(Cell pCell) {
        super.xInitCell(pCell);
        sReset();
    }

    void xInitPlayCell(PlayCell pCell){
        int lCount;

        xInitCell(pCell);
        mConflict = pCell.mConflict;
        for (lCount = 0; lCount < mPencil.length; lCount++){
            mPencil[lCount] = pCell.mPencil[lCount];
        }
    }

    /**
     * Reset cell to empty
     */
    @Override
    void xReset() {
        super.xReset();
        sReset();
    }

    /**
     * Reset cell to empty
     */
    private void sReset() {
        int lCount;

        mConflict = false;
        for (lCount = 0; lCount < mPencil.length; lCount++) {
            mPencil[lCount] = false;
        }
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
