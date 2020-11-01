package jb.sudoku;

import android.content.Context;

class SavePlayfield implements Runnable {
    private Context mContext;
    private PlayField mField;

    SavePlayfield(Context pContext, PlayField pField){
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
