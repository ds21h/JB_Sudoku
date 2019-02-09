package jb.sudoku;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jan on 14-5-2017.
 */

class Data extends SQLiteOpenHelper {
    private static Data mInstance = null;

    private static final String cDBName = "Sudoku.db";
    private static final int cDBVersion = 1;

    static Data getInstance(Context pContext) {
        /*
         * use the application context as suggested by CommonsWare.
         * this will ensure that you dont accidentally leak an Activitys
         * context (see this article for more information:
         * http://developer.android.com/resources/articles/avoiding-memory-leaks.html)
         */
        if (mInstance == null) {
            mInstance = new Data(pContext.getApplicationContext());
        }
        return mInstance;
    }

    /**
     * constructor should be private to prevent direct instantiation.
     * make call to static factory method "getInstance()" instead.
     */
    private Data(Context pContext) {
        super(pContext, pContext.getExternalFilesDir(null).getAbsolutePath() + "/" + cDBName, null, cDBVersion);
    }

    @Override
    public synchronized void close() {
        super.close();
        mInstance = null;
    }

    @Override
    public void onCreate(SQLiteDatabase pDB) {
        sDefineContext(pDB);
        sDefineCell(pDB);
    }

    @Override
    public void onUpgrade(SQLiteDatabase pDB, int pOldVersion, int pNewVersion) {
        switch (pOldVersion){
            default:{
                pDB.execSQL("DROP TABLE IF EXISTS Context");
                pDB.execSQL("DROP TABLE IF EXISTS Cell");
                onCreate(pDB);
                break;
            }
        }
    }

    private void sDefineContext(SQLiteDatabase pDB){
        pDB.execSQL(
                "CREATE TABLE Context " +
                        "(ContextId Integer primary key, " +
                        "Selection Integer Not Null, " +
                        "SetUp Integer Not Null, " +
                        "Pencil Integer Not Null" +
                        ")"
        );
        pDB.execSQL(
                "INSERT INTO Context " +
                        "(ContextId, Selection, SetUp, Pencil) " +
                        "VALUES " +
                        "(0, 0, 0, 0)"
        );
    }

    private void sDefineCell(SQLiteDatabase pDB){
        pDB.execSQL(
                "CREATE TABLE Cell " +
                        "(_ID Integer primary key, " +
                        "ContextId Integer Not Null, " +
                        "CellNumber Integer Not Null, " +
                        "Value Integer Not Null, " +
                        "Fixed Integer Not Null, " +
                        "Confl Integer Not Null, " +
                        "Pencil Text Not Null" +
                        ")"
        );
    }

    GameData xGameData(){
        GameData lGame = null;
        SQLiteDatabase lDB;
        Cursor lCursor;
        String[] lColumns;
        String lSelection;
        String[] lSelectionArgs;
        int lSel;
        int lSetUp;
        int lPencil;
        List<DbCell> mCells;

        mCells = sCells();

        lColumns = new String[] {"Selection", "SetUp", "Pencil"};
        lSelection = "ContextId = ?";
        lSelectionArgs = new String[] {"0"};

        lDB = this.getReadableDatabase();

        lCursor = lDB.query("Context", lColumns, lSelection, lSelectionArgs, null, null, null);
        if (lCursor.moveToNext()){
            lSel = lCursor.getInt(0);
            lSetUp = lCursor.getInt(1);
            lPencil = lCursor.getInt(2);
            lGame = new GameData(mCells, lSel, (lSetUp == 0) ? false : true, (lPencil == 0) ? false : true);
        }
        lCursor.close();
        lDB.close();

        return lGame;
    }

    List<DbCell> sCells(){
        List<DbCell> lCells;
        DbCell lCell;
        SQLiteDatabase lDB;
        Cursor lCursor;
        String[] lColumns;
        String lSelection;
        String[] lSelectionArgs;
        String lSequence;
        int lCellNumber;
        int lValue;
        int lFixed;
        int lConflict;
        String lPencil;

        lColumns = new String[] {"CellNumber", "Value", "Fixed", "Confl", "Pencil"};
        lSelection = "ContextId = ?";
        lSelectionArgs = new String[] {"0"};
        lSequence = "CellNumber";

        lCells = new ArrayList<>();

        lDB = this.getReadableDatabase();

        lCursor = lDB.query("Cell", lColumns, lSelection, lSelectionArgs, null, null, null);
        while (lCursor.moveToNext()){
            lCellNumber = lCursor.getInt(0);
            lValue = lCursor.getInt(1);
            lFixed = lCursor.getInt(2);
            lConflict = lCursor.getInt(3);
            lPencil = lCursor.getString(4);
            lCell = new DbCell(lCellNumber, lValue, (lFixed == 0) ? false : true, (lConflict == 0) ? false : true, lPencil);
            lCells.add(lCell);
        }
        lCursor.close();
        lDB.close();

        return lCells;
    }

    void xSaveGame(GameData pGameData){
        Cell[] mCells;
        int lCount;

        sUpdateContext(pGameData.xSelection(), pGameData.xSetUpMode(), pGameData.xPencilMode());
        sDeleteCells();
        mCells = pGameData.xCells();
        for(lCount = 0; lCount < mCells.length; lCount++){
            sNewCell(lCount,  mCells[lCount]);
        }
    }

    private void sUpdateContext(int pSelection, boolean pSetUp, boolean pPencil){
        SQLiteDatabase lDB;
        ContentValues lValues;
        String lSelection;
        String[] lSelectionArgs;

        lValues = new ContentValues();
        lValues.put("Selection", pSelection);
        lValues.put("SetUp", (pSetUp) ? 1 : 0);
        lValues.put("Pencil", (pPencil) ? 1 : 0);
        lSelection = "ContextId = ?";
        lSelectionArgs = new String[] {"0"};

        lDB = this.getWritableDatabase();

        lDB.update("Context", lValues, lSelection, lSelectionArgs);

        lDB.close();
    }

    private void sDeleteCells(){
        SQLiteDatabase lDB;
        String lSelection;
        String[] lSelectionArgs;

        lSelection = "ContextId = ?";
        lSelectionArgs = new String[] {"0"};

        lDB = this.getWritableDatabase();

        lDB.delete("Cell", lSelection, lSelectionArgs);

        lDB.close();
    }

    private void sNewCell(int pCellNumber, Cell pCell){
        SQLiteDatabase lDB;
        ContentValues lValues;

        lDB = this.getWritableDatabase();

        lValues = new ContentValues();
        lValues.put("ContextId", 0);
        lValues.put("CellNumber", pCellNumber);
        lValues.put("Value", pCell.xValue());
        lValues.put("Fixed", (pCell.xFixed()) ? 1 : 0);
        lValues.put("Confl", (pCell.xConflict()) ? 1 : 0);
        lValues.put("Pencil", pCell.xPencils());

        lDB.insert("Cell", null, lValues);

        lDB.close();
    }
}
