package jb.sudoku;

import java.util.List;

class GameData {
    private Cell[] mCells;
    private boolean mPencilMode;
    private boolean mSetUpMode;
    private boolean mSolved;
    private int mSelection;
    private int mSelectionRow;
    private int mSelectionColumn;

    private int[] mDigitCount = new int[10];

    /**
     * Constructor for creating an empty game.
     */
    GameData() {
        int lCount;

        mCells = new Cell[81];
        for (lCount = 0; lCount < mCells.length; lCount++) {
            mCells[lCount] = new Cell();
        }
        mDigitCount[0] = 81;
        for (lCount = 1; lCount < mDigitCount.length; lCount++) {
            mDigitCount[lCount] = 0;
        }
        mPencilMode = false;
        mSetUpMode = false;
        mSolved = false;
        mSelection = 0;
        mSelectionRow = 0;
        mSelectionColumn = 0;
    }

    /**
     * Constructor for recreating a game.
     *
     * @param pCells        List<DbCell>  List of the cells (missing cells are created empty)
     * @param pSelection    int         Cellnumber of the selected cell
     * @param pSetUp        boolean     Status of the setupmode
     * @param pPencil       boolean     Status of the pencilmode
     */
    GameData(List<DbCell> pCells, int pSelection, boolean pSetUp, boolean pPencil){
        int lCount;
        DbCell lDbCell;

        mCells = new Cell[81];
        for (lCount = 0; lCount < mCells.length; lCount++) {
            mCells[lCount] = new Cell();
        }
        for (lCount = 0; lCount < pCells.size(); lCount++){
            lDbCell = pCells.get(lCount);
            mCells[lDbCell.xCellNumber()] = lDbCell.xCell();
        }
        sDigitCount();
        mPencilMode = pPencil;
        mSetUpMode = pSetUp;
        mSelection = pSelection;
        mSelectionRow = mSelection / 9;
        mSelectionColumn = mSelection % 9;
    }

    /**
     * Recount all the digits
     */
    void sDigitCount(){
        int lCount;

        for (lCount = 0; lCount < mDigitCount.length; lCount++){
            mDigitCount[lCount] = 0;
        }
        for (lCount = 0; lCount < mCells.length; lCount++){
            mDigitCount[mCells[lCount].xValue()]++;
        }
        if (mDigitCount[0] > 0){
            mSolved = false;
        } else {
            mSolved = true;
        }
    }

    /**
     * Recount all the digits
     */
    void xDigitCount(){
        sDigitCount();
    }

    /**
     * Gets the array of all the cells.
     *
     * @return          Cell[]  Array of all the cells
     */
    Cell[] xCells() {
        return mCells;
    }

    boolean xSolved(){
        return mSolved;
    }

    /**
     * Gets the status of the pencilmode.
     *
     * @return          boolean Status of the pencilmode.
     */
    boolean xPencilMode() {
        return mPencilMode;
    }

    /**
     * Flips (inverts) the status of the pencilmode.
     */
    void xPencilFlip() {
        mPencilMode = !mPencilMode;
    }

    /**
     * Gets the status of the setupmode
     *
     * @return          boolean Status of setup mode
     */
    boolean xSetUpMode() {
        return mSetUpMode;
    }

    /**
     * Gets the selected cell
     *
     * @return          Cell    The cell
     */
    Cell xSelectedCell() {
        return mCells[mSelection];
    }

    /**
     * Gets the cell at the specified cellnumber
     *
     * @param pCell     0..80   The cellnumber
     * @return          Cell    The cell
     */
    Cell xCell(int pCell){
        return mCells[pCell];
    }

    /**
     * Gets the cell at the specified row and column.
     *
     * @param pRow      0..8    The row
     * @param pColumn   0..8    The column
     * @return          Cell    The cell
     */
    Cell xCell(int pRow, int pColumn) {
        return mCells[(pRow * 9) + pColumn];
    }

    /**
     * Gets the cellnumber of the selected cell.
     *
     * @return      int     The cellnumber
     */
    int xSelection() {
        return mSelection;
    }

    /**
     * Gets the row of the selected cell.
     *
     * @return      int     The row
     */
    int xSelectionRow() {
        return mSelectionRow;
    }

    /**
     * Set the row of the selected cell.
     *
     * @param pRow  0..8    The row.
     */
    void xSelectionRow(int pRow) {
        if (pRow >= 0 && pRow <= 8) {
            mSelectionRow = pRow;
            mSelection = (mSelectionRow * 9) + mSelectionColumn;
        }
    }

    /**
     * Gets the column of the selected cell.
     *
     * @return      int     The column.
     */
    int xSelectionColumn() {
        return mSelectionColumn;
    }

    /**
     * Sets the column of the selected cell.
     *
     * @param pColumn 0..8  The column.
     */
    void xSelectionColumn(int pColumn) {
        if (pColumn >= 0 && pColumn <= 8) {
            mSelectionColumn = pColumn;
            mSelection = (mSelectionRow * 9) + mSelectionColumn;
        }
    }

    /**
     * Gets the count for the specified digit.
     *
     * @param pDigit    1..9    The digit to count.
     * @return          int     The count for the digit.
     */
    int xDigitCount(int pDigit) {
        if (pDigit >= 1 && pDigit <= 9) {
            return mDigitCount[pDigit];
        } else {
            return 0;
        }
    }

    /**
     * Starts setup mode. Resets all cells.
     */
    void xInitSetUp() {
        int lCount;

        for (lCount = 0; lCount < mCells.length; lCount++) {
            mCells[lCount].xReset();
        }
        sDigitCount();
        mSetUpMode = true;
    }

    /**
     * End setup mode. Flags all filled cells as fixed.
     */
    void xFinishSetup() {
        int lCount;
        Cell lCell;

        for (lCount = 0; lCount < mCells.length; lCount++) {
            lCell = mCells[lCount];
            if (lCell.xValue() == 0) {
                lCell.xFixed(false);
            } else {
                lCell.xFixed(true);
            }
        }
        sDigitCount();
        mSetUpMode = false;
    }

    /**
     * Sets the value of the selected cell.
     *
     * @param pValue    0..9    The value to set the cell to
     */
    void xSetCellValue(int pValue){
        Cell lCell;

        lCell = mCells[mSelection];
        if (lCell.xValue() == pValue){
            lCell.xValueReset();
            mDigitCount[0]++;
            mDigitCount[pValue]--;
        } else {
            if (lCell.xValue() == 0){
                mDigitCount[0]--;
            } else {
                mDigitCount[lCell.xValue()]--;
            }
            lCell.xValue(pValue);
            mDigitCount[pValue]++;
        }
        if (mDigitCount[0] > 0){
            mSolved = false;
        } else {
            mSolved = true;
        }
    }


    /**
     * Resets the conflict flag for all cells
     */
    void xResetConflicts(){
        int lCount;

        for (lCount = 0; lCount < mCells.length; lCount++){
            mCells[lCount].xConflict(false);
        }
    }

    /**
     * Sets all pencilflags in all cells
     */
    void xInitPencil(){
        int lCount;

        for (lCount = 0; lCount < mCells.length; lCount++){
            mCells[lCount].xSetPencils();
        }
    }

    /**
     * Resets all pencilflags in all cells
     */
    void xClearPencil(){
        int lCount;

        for (lCount = 0; lCount < mCells.length; lCount++){
            mCells[lCount].xClearPencils();
        }
    }
}
