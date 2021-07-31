package jb.sudoku;

import android.content.Context;

class SaveGameRunnable implements Runnable {
    private final Context mContext;
    private final SudokuGameBase mGame;

    SaveGameRunnable(Context pContext, SudokuGameBase pGame){
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
