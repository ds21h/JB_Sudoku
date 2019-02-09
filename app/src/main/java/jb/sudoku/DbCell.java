package jb.sudoku;

class DbCell {
    private int mCelNumber;
    private Cell mCell;

    DbCell(int pCelNumber, int pValue, boolean pFixed, boolean pConflict, String pPencil){
        mCell = new Cell(pValue, pFixed, pConflict, pPencil);
        mCelNumber = pCelNumber;
    }

    int xCellNumber(){
        return mCelNumber;
    }

    Cell xCell(){
        return mCell;
    }
}
