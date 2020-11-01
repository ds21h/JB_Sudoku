package jb.sudoku;

import android.content.Context;

class SaveGame implements Runnable {
    private Context mContext;
    private SudokuGameBase mGame;

    SaveGame (Context pContext, SudokuGameBase pGame){
        mContext = pContext.getApplicationContext();
        mGame = new SudokuGameBase(pGame);
    }

    @Override
    public void run() {
        Data lData;

        lData = Data.getInstance(mContext);
        synchronized(mContext){
            lData.xSaveGame(mGame);
        }
    }
}
