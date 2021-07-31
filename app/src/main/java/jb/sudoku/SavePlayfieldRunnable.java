package jb.sudoku;

import android.content.Context;

class SavePlayfieldRunnable implements Runnable {
    private final Context mContext;
    private final PlayField mField;

    SavePlayfieldRunnable(Context pContext, PlayField pField){
        mContext = pContext.getApplicationContext();
        mField = new PlayField(pField);
    }

    @Override
    public void run() {
        Data lData;

        lData = Data.getInstance(mContext);
        synchronized(mContext){
            lData.xSavePlayField(mField);
        }
    }
}
