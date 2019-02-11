package jb.sudoku;

class Cell {
    private int mValue;
    private boolean mFixed;
    private boolean mConflict;
    private boolean[] mPencil = new boolean[9];

    Cell(){
        sReset();
    }

    Cell(int pValue, boolean pFixed, boolean pConflict, String pPencil){
        String lPos;
        int lCount;

        sReset();
        mValue = pValue;
        mFixed = pFixed;
        mConflict = pConflict;
        for (lCount = 0; lCount < pPencil.length(); lCount++){
            lPos = pPencil.substring(lCount, lCount + 1);
            if (lPos.charAt(0) >= '1' && lPos.charAt(0) <= 9){
                mPencil[Integer.valueOf(lPos) - 1] = true;
            }
        }
    }

    void xReset(){
        sReset();
    }

    private void sReset(){
        int lCount;

        mValue = 0;
        mFixed = false;
        mConflict = false;
        for (lCount = 0; lCount < mPencil.length; lCount++){
            mPencil[lCount] = false;
        }
    }

    int xValue(){
        return mValue;
    }

    void xValue(int pValue){
        if (pValue >= 1 && pValue <= 9){
            mValue = pValue;
        }
    }

    void xValueReset(){
        mValue = 0;
    }

    boolean xConflict(){
        return mConflict;
    }

    void xConflict(boolean pConflict){
        mConflict = pConflict;
    }

    boolean xFixed(){
        return mFixed;
    }

    void xFixed(boolean pFixed){
        mFixed = pFixed;
    }

    boolean xPencil(int pValue){
        if (pValue >= 1 && pValue <= 9){
            return mPencil[pValue - 1];
        } else {
            return false;
        }
    }

    String xPencils(){
        StringBuilder lBuilder;
        int lCount;

        lBuilder = new StringBuilder();
        for (lCount = 0; lCount < mPencil.length; lCount++){
            if (mPencil[lCount]){
                lBuilder.append(lCount + 1);
            }
        }
        return lBuilder.toString();
    }

    void xPencilFlip(int pValue){
        if (pValue >= 1 && pValue <= 9){
            mPencil[pValue - 1] = !mPencil[pValue - 1];
        }
    }
}
