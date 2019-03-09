package jb.sudoku;

class DbCell extends PlayCell {
    private int mCelNumber;

    DbCell(int pCelNumber, int pValue, boolean pFixed, boolean pConflict, String pPencil){
        super(pValue, pFixed, pConflict, pPencil);
        mCelNumber = pCelNumber;
    }

    int xCellNumber(){
        return mCelNumber;
    }
}
