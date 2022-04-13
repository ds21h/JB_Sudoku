package jb.sudoku;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jan on 9-2-2019.
 */

class Data extends SQLiteOpenHelper {
    private static Data mInstance = null;

    private static final String cDBName = "Sudoku.db";
    private static final int cDBVersion = 5;
    private static String mExternalFilesDir;

    private SQLiteDatabase mDB;

    static Data getInstance(Context pContext) {
        Context lContext;
        File lExternalFilesDir;
        /*
         * use the application context as suggested by CommonsWare.
         * this will ensure that you dont accidentally leak an Activitys
         * context (see this article for more information:
         * http://developer.android.com/resources/articles/avoiding-memory-leaks.html)
         *
         * use double-check locking for thread-safe initialization.
         * see https://www.geeksforgeeks.org/java-singleton-design-pattern-practices-examples/
         */
        if (mInstance == null) {
            synchronized(Data.class){
                if (mInstance == null){
                    lContext = pContext.getApplicationContext();
                    lExternalFilesDir = lContext.getExternalFilesDir(null);
                    if (lExternalFilesDir == null) {
                        mExternalFilesDir = "";
                    } else {
                        mExternalFilesDir = lExternalFilesDir.getAbsolutePath();
                    }
                    mInstance = new Data(lContext);
                }
            }
        }
        return mInstance;
    }

    /**
     * constructor should be private to prevent direct instantiation.
     * make call to static factory method "getInstance()" instead.
     */
    private Data(Context pContext) {
        super(pContext, mExternalFilesDir + "/" + cDBName, null, cDBVersion);
        mDB = this.getWritableDatabase();
    }

    @Override
    public synchronized void close() {
        super.close();
        mInstance = null;
    }

    @Override
    public void onCreate(SQLiteDatabase pDB) {
        sDefineGameContext(pDB);
        sInitGameContext(pDB);
        sDefineFieldContext(pDB);
        sDefineCell(pDB);
    }

    @Override
    public void onUpgrade(SQLiteDatabase pDB, int pOldVersion, int pNewVersion) {
                pDB.execSQL("DROP TABLE IF EXISTS Context");
                pDB.execSQL("DROP TABLE IF EXISTS GameContext");
                pDB.execSQL("DROP TABLE IF EXISTS FieldContext");
                pDB.execSQL("DROP TABLE IF EXISTS Cell");
                pDB.execSQL("DROP TABLE IF EXISTS Game");
                onCreate(pDB);
    }

    private void sDefineGameContext(SQLiteDatabase pDB) {
        pDB.execSQL(
                "CREATE TABLE GameContext " +
                        "(ContextId Text primary key, " +
                        "SetUp Integer Not Null, " +
                        "Difficulty Integer Not Null, " +
                        "SelectedField Integer Not Null, " +
                        "UsedTime Integer Not Null" +
                        ")"
        );
    }


    private void sDefineFieldContext(SQLiteDatabase pDB) {
        pDB.execSQL(
                "CREATE TABLE FieldContext " +
                        "(FieldId Integer primary key, " +
                        "Selection Integer Not Null, " +
                        "Pencil Integer Not Null" +
                        ")"
        );
    }

    private void sInitGameContext(SQLiteDatabase pDB){
        pDB.execSQL(
                "INSERT INTO GameContext " +
                        "(ContextId, SetUp, Difficulty, SelectedField, UsedTime) " +
                        "VALUES " +
                        "('Game', 0, 0, 0, 0)"
        );
    }

    private void sDefineCell(SQLiteDatabase pDB){
        pDB.execSQL(
                "CREATE TABLE Cell " +
                        "(_ID Integer primary key, " +
                        "FieldId Integer Not Null, " +
                        "CellNumber Integer Not Null, " +
                        "Value Integer Not Null, " +
                        "Fixed Integer Not Null, " +
                        "Confl Integer Not Null, " +
                        "Pencil Text Not Null" +
                        ")"
        );
    }

    SudokuGame xCurrentGame(){
        List<PlayField> lFields;
        SudokuGame lGame;
        Cursor lCursor;
        String[] lColumns;
        String lSelection;
        String[] lSelectionArgs;
        int lSetUp = 0;
        int lDifficulty = -1;
        int lSelectedField = 0;
        int lUsedTime = 0;

        lFields = sGetFields();

        lColumns = new String[] {"SetUp", "Difficulty", "SelectedField", "UsedTime"};
        lSelection = "ContextId = ?";
        lSelectionArgs = new String[] {"Game"};

        try{
            lCursor = mDB.query("GameContext", lColumns, lSelection, lSelectionArgs, null, null, null);
            if (lCursor.moveToNext()){
                lSetUp = lCursor.getInt(0);
                lDifficulty = lCursor.getInt(1);
                lSelectedField = lCursor.getInt(2);
                lUsedTime = lCursor.getInt(3);
            }
            lCursor.close();
        } catch (Exception ignored){
        }

        lGame = new SudokuGame(lFields, (lSetUp == 0) ? false : true, lDifficulty, lSelectedField, lUsedTime);
        return lGame;
    }

    private List<PlayField> sGetFields(){
        List<PlayField> lFields;
        PlayField lField;
        List<DbCell> lCells;
        Cursor lCursor;
        String[] lColumns;
        String lSequence;
        int lFieldId;
        int lSel;
        int lPencil;

        lFields = new ArrayList<>();

        lColumns = new String[] {"FieldId", "Selection", "Pencil"};
        lSequence = "FieldId";

        lCursor = mDB.query("FieldContext", lColumns, null, null, null, null, lSequence);
        while (lCursor.moveToNext()){
            lFieldId = lCursor.getInt(0);
            lSel = lCursor.getInt(1);
            lPencil = lCursor.getInt(2);
            lCells = sCells(lFieldId);
            lField = new PlayField(lFieldId, lCells, lSel, (lPencil == 0) ? false : true);
            lFields.add(lField);
        }
        lCursor.close();

        return lFields;
    }

    private List<DbCell> sCells(int pFieldId){
        List<DbCell> lCells;
        DbCell lCell;
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
        lSelection = "FieldId = ?";
        lSelectionArgs = new String[1];
        lSelectionArgs[0] = String.valueOf(pFieldId);
        lSequence = "CellNumber";

        lCells = new ArrayList<>();

        lCursor = mDB.query("Cell", lColumns, lSelection, lSelectionArgs, null, null, lSequence);
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

        return lCells;
    }

    void xSaveGame(SudokuGameBase pGame){
        mDB.beginTransaction();
        sUpdateGameContext(pGame);
        sSavePlayField(pGame.xPlayField());
        mDB.setTransactionSuccessful();
        mDB.endTransaction();
    }

    void xSavePlayField(PlayField pField){
        mDB.beginTransaction();
        sSavePlayField(pField);
        mDB.setTransactionSuccessful();
        mDB.endTransaction();
    }

    private void sSavePlayField(PlayField pField){
        PlayCell[] lCells;
        int lCount;

        sDeletePlayField(pField.xFieldId());
        sNewFieldContext(pField);
        lCells = pField.xCells();
        for(lCount = 0; lCount < lCells.length; lCount++){
            sNewCell(pField.xFieldId(), lCount,  lCells[lCount]);
        }
    }

    void xDeletePlayField(int pPlayFieldId){
        mDB.beginTransaction();
        sDeletePlayField(pPlayFieldId);
        mDB.setTransactionSuccessful();
        mDB.endTransaction();
    }

    private void sDeletePlayField(int pPlayFieldId){
        String lSelection;
        String[] lSelectionArgs;

        lSelection = "FieldId = ?";
        lSelectionArgs = new String[1];
        lSelectionArgs[0] = String.valueOf(pPlayFieldId);

        mDB.delete("FieldContext", lSelection, lSelectionArgs);

        mDB.delete("Cell", lSelection, lSelectionArgs);
    }

    void xDeleteSave(){
        mDB.beginTransaction();
        mDB.delete("FieldContext", null, null);
        mDB.delete("Cell", null, null);
        mDB.setTransactionSuccessful();
        mDB.endTransaction();
    }

    private void sNewFieldContext(PlayField pField){
        ContentValues lValues;

        lValues = new ContentValues();
        lValues.put("FieldId", pField.xFieldId());
        lValues.put("Selection", pField.xSelection());
        lValues.put("Pencil", (pField.xPencilMode()) ? 1 : 0);

        mDB.insert("FieldContext", null, lValues);
    }

    private void sUpdateGameContext(SudokuGameBase pGame){
        ContentValues lValues;
        String lSelection;
        String[] lSelectionArgs;

        lValues = new ContentValues();
        lValues.put("SetUp", (pGame.xGameStatus() == SudokuGame.cStatusSetup) ? 1 : 0);
        lValues.put("Difficulty", pGame.xDifficulty());
        lValues.put("SelectedField", pGame.xPlayField().xFieldId());
        lValues.put("UsedTime", pGame.xUsedTime());
        lSelection = "ContextId = ?";
        lSelectionArgs = new String[] {"Game"};

        mDB.update("GameContext", lValues, lSelection, lSelectionArgs);
    }

    private void sNewCell(int pFieldId, int pCellNumber, PlayCell pCell){
        ContentValues lValues;

        lValues = new ContentValues();
        lValues.put("FieldId", pFieldId);
        lValues.put("CellNumber", pCellNumber);
        lValues.put("Value", pCell.xValue());
        lValues.put("Fixed", (pCell.xFixed()) ? 1 : 0);
        lValues.put("Confl", (pCell.xConflict()) ? 1 : 0);
        lValues.put("Pencil", pCell.xPencils());

        mDB.insert("Cell", null, lValues);
    }
}
