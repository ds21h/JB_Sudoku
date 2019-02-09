package jb.sudoku;

class SudokuGame {
    private GameData mGameData;
    private Cell[] mCheck;
    private boolean mOK;

    SudokuGame(){
        mGameData = new GameData();
        mCheck = new Cell[9];
    }

    GameData xGameData(){
        return mGameData;
    }

    void xGameData(GameData pGameData){
        mGameData = pGameData;
    }

    boolean xSelectionValue(int pRow, int pColumn){
        Cell lCell;
        int lValue;
        boolean lResult;

        lResult = false;
        lValue = mGameData.xSelectedCell().xValue();
        if (lValue > 0){
            lCell = mGameData.xCell(pRow, pColumn);
            if (lCell.xValue() == lValue){
                lResult = true;
            } else {
                if (lCell.xValue() == 0){
                    if (mGameData.xPencilMode()){
                        if (lCell.xPencil(lValue)){
                            lResult = true;
                        }
                    }
                }
            }
        }
        return lResult;
    }

    boolean xSelectionRange(int pRow, int pColumn){
        if (pRow == mGameData.xSelectionRow()){
            return true;
        }
        if (pColumn == mGameData.xSelectionColumn()){
            return true;
        }
        if ((pRow / 3) == (mGameData.xSelectionRow() / 3) && (pColumn / 3) == (mGameData.xSelectionColumn() / 3)){
            return true;
        }
        return false;
    }

    void xStartSetUp(){
        mGameData.xInitSetUp();
    }

    void xEndSetup(){
        if (xCheckGame()){
            mGameData.xFinishSetup();
        }
    }

    void xProcessDigit(int pDigit){
        Cell lCell;

        if (pDigit >= 1 && pDigit <= 9){
            lCell = mGameData.xSelectedCell();
            if (mGameData.xPencilMode()){
                lCell.xPencilFlip(pDigit);
            } else {
                if (!lCell.xFixed()){
                    mGameData.xSetCellValue(pDigit);
                    xCheckGame();
                }
            }
        }
    }

    boolean xCheckGame(){
        mGameData.xResetConflicts();
        mOK = true;
        sCheckRows();
        sCheckColumns();
        sCheckSegments();
        return mOK;
    }

    private void sCheckRows(){
        int lRow;
        int lColumn;

        for (lRow = 0; lRow < 9; lRow++){
            for (lColumn = 0; lColumn < 9; lColumn++){
                mCheck[lColumn] = mGameData.xCell(lRow, lColumn);
            }
            sCheckBlock();
        }
    }

    private void sCheckColumns(){
        int lRow;
        int lColumn;

        for (lColumn = 0; lColumn < 9; lColumn++){
            for (lRow = 0; lRow < 9; lRow++){
                mCheck[lRow] = mGameData.xCell(lRow, lColumn);
            }
            sCheckBlock();
        }
    }

    private void sCheckSegments(){
        int lRow;
        int lColumn;
        int lGrid;

        for (lGrid = 0; lGrid < 9; lGrid++){
            for (lRow = 0; lRow < 3; lRow++){
                for (lColumn = 0; lColumn < 3; lColumn++){
                    mCheck[(lRow * 3) + lColumn] = mGameData.xCells()[(((lGrid / 3) * 27) + (lRow * 9)) + ((lGrid % 3) * 3) + lColumn];
                }
            }
            sCheckBlock();
        }
    }

    private void sCheckBlock(){
        int lCount1;
        int lCount2;

        for (lCount1 = 0; lCount1 < mCheck.length - 1; lCount1++){
            if (mCheck[lCount1].xValue() != 0){
                for (lCount2 = lCount1 + 1; lCount2 < mCheck.length; lCount2++){
                    if (mCheck[lCount1].xValue() == mCheck[lCount2].xValue()){
                        mCheck[lCount1].xConflict(true);
                        mCheck[lCount2].xConflict(true);
                        mOK = false;
                    }
                }
            }
        }
    }
}
