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
    private static final int cDBVersion = 4;
    private static String mExternalFilesDir;

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
        sDefineGame(pDB);
        sInitGame(pDB);
    }

    @Override
    public void onUpgrade(SQLiteDatabase pDB, int pOldVersion, int pNewVersion) {
        switch (pOldVersion){
            case 1:{
                pDB.execSQL("DROP TABLE IF EXISTS Context");
                pDB.execSQL("DROP TABLE IF EXISTS GameContext");
                pDB.execSQL("DROP TABLE IF EXISTS FieldContext");
                pDB.execSQL("DROP TABLE IF EXISTS Cell");
                sDefineGameContext(pDB);
                sInitGameContext(pDB);
                sDefineFieldContext(pDB);
                sDefineCell(pDB);
                sDefineGame(pDB);
                sInitGame(pDB);
                break;
            }
            case 2:
            case 3:{
                pDB.execSQL("DROP TABLE IF EXISTS Context");
                pDB.execSQL("DROP TABLE IF EXISTS GameContext");
                pDB.execSQL("DROP TABLE IF EXISTS FieldContext");
                pDB.execSQL("DROP TABLE IF EXISTS Cell");
                sDefineGameContext(pDB);
                sInitGameContext(pDB);
                sDefineFieldContext(pDB);
                sDefineCell(pDB);
                break;
            }
            default:{
                pDB.execSQL("DROP TABLE IF EXISTS Context");
                pDB.execSQL("DROP TABLE IF EXISTS GameContext");
                pDB.execSQL("DROP TABLE IF EXISTS FieldContext");
                pDB.execSQL("DROP TABLE IF EXISTS Cell");
                pDB.execSQL("DROP TABLE IF EXISTS Game");
                onCreate(pDB);
                break;
            }
        }
    }

    private void sDefineGameContext(SQLiteDatabase pDB) {
        pDB.execSQL(
                "CREATE TABLE GameContext " +
                        "(ContextId Text primary key, " +
                        "SetUp Integer Not Null, " +
                        "Lib Integer Not Null, " +
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
                        "(ContextId, SetUp, Lib, Difficulty, SelectedField, UsedTime) " +
                        "VALUES " +
                        "('Game', 0, 0, -1, 0, 0)"
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

    private void sDefineGame(SQLiteDatabase pDB){
        pDB.execSQL(
                "CREATE TABLE Game " +
                        "(_ID Integer primary key, " +
                        "Level Integer Not Null, " +
                        "Game Text Not Null" +
                        ")"
        );
    }

    private void sInitGame(SQLiteDatabase pDB){
        pDB.execSQL(
                "INSERT INTO Game " +
                        "(Level, Game) " +
                        "VALUES " +
                        "(0, '502870300036004790009602045600190504254060009070420038165003080490701200000086451'), " +
                        "(1, '004000800060907010300020005003000600900070003050604080807301406030000020601402507'), " +
                        "(2, '001200094080700006206080700705800030040002089600400100000000000007090300060007040'), " +
                        "(3, '007000016030700200006008040000100005090002000700000009000060401200004070070050900'), " +
                        "(4, '000508070000040005000001230602900000100020050080060409004007002010450008508000700')"
        );
    }

    SudokuGame xCurrentGame(){
        List<PlayField> lFields;
        SudokuGame lGame;
        SQLiteDatabase lDB;
        Cursor lCursor;
        String[] lColumns;
        String lSelection;
        String[] lSelectionArgs;
        int lSetUp = 0;
        int lLib = 0;
        int lDifficulty = -1;
        int lSelectedField = 0;
        int lUsedTime = 0;

        lFields = sGetFields();

        lDB = this.getReadableDatabase();

        lColumns = new String[] {"SetUp", "Lib", "Difficulty", "SelectedField", "UsedTime"};
        lSelection = "ContextId = ?";
        lSelectionArgs = new String[] {"Game"};

        try{
            lCursor = lDB.query("GameContext", lColumns, lSelection, lSelectionArgs, null, null, null);
            if (lCursor.moveToNext()){
                lSetUp = lCursor.getInt(0);
                lLib = lCursor.getInt(1);
                lDifficulty = lCursor.getInt(2);
                lSelectedField = lCursor.getInt(3);
                lUsedTime = lCursor.getInt(4);
            }
            lCursor.close();
        } catch (Exception pExc){
            int A = 1;
        }
        lDB.close();

        lGame = new SudokuGame(lFields, (lSetUp == 0) ? false : true, (lLib == 0) ? false : true, lDifficulty, lSelectedField, lUsedTime);
        return lGame;
    }

    private List<PlayField> sGetFields(){
        List<PlayField> lFields;
        PlayField lField;
        List<DbCell> lCells;
        SQLiteDatabase lDB;
        Cursor lCursor;
        String[] lColumns;
        String lSequence;
        int lFieldId;
        int lSel;
        int lPencil;

        lFields = new ArrayList<>();

        lDB = this.getReadableDatabase();

        lColumns = new String[] {"FieldId", "Selection", "Pencil"};
        lSequence = "FieldId";

        lCursor = lDB.query("FieldContext", lColumns, null, null, null, null, lSequence);
        while (lCursor.moveToNext()){
            lFieldId = lCursor.getInt(0);
            lSel = lCursor.getInt(1);
            lPencil = lCursor.getInt(2);
            lCells = sCells(lFieldId);
            //noinspection RedundantConditionalExpression
            lField = new PlayField(lFieldId, lCells, lSel, (lPencil == 0) ? false : true);
            lFields.add(lField);
        }
        lCursor.close();

        return lFields;
    }

    private List<DbCell> sCells(int pFieldId){
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
        lSelection = "FieldId = ?";
        lSelectionArgs = new String[1];
        lSelectionArgs[0] = String.valueOf(pFieldId);
        lSequence = "CellNumber";

        lCells = new ArrayList<>();

        lDB = this.getReadableDatabase();

        lCursor = lDB.query("Cell", lColumns, lSelection, lSelectionArgs, null, null, lSequence);
        while (lCursor.moveToNext()){
            lCellNumber = lCursor.getInt(0);
            lValue = lCursor.getInt(1);
            lFixed = lCursor.getInt(2);
            lConflict = lCursor.getInt(3);
            lPencil = lCursor.getString(4);
            //noinspection RedundantConditionalExpression
            lCell = new DbCell(lCellNumber, lValue, (lFixed == 0) ? false : true, (lConflict == 0) ? false : true, lPencil);
            lCells.add(lCell);
        }
        lCursor.close();
        lDB.close();

        return lCells;
    }

    void xSaveGameO(SudokuGame pGame){
        PlayCell[] lCells;
        int lCount;
        List<PlayField> lFields;

        sUpdateGameContext(pGame);
        xDeleteSave();
        lFields = pGame.xPlayFields();
        for(PlayField lField : lFields){
            sNewFieldContext(lField);
            lCells = lField.xCells();
            for(lCount = 0; lCount < lCells.length; lCount++){
                sNewCell(lField.xFieldId(), lCount,  lCells[lCount]);
            }
        }
    }

    void xSaveGame(SudokuGameBase pGame){
        sUpdateGameContext(pGame);
        xSavePlayField(pGame.xPlayField());
    }

    void xSavePlayField(PlayField pField){
        PlayCell[] lCells;
        int lCount;

        xDeletePlayField(pField.xFieldId());
        sNewFieldContext(pField);
        lCells = pField.xCells();
        for(lCount = 0; lCount < lCells.length; lCount++){
            sNewCell(pField.xFieldId(), lCount,  lCells[lCount]);
        }
    }

    void xDeletePlayField(int pPlayFieldId){
        SQLiteDatabase lDB;
        String lSelection;
        String[] lSelectionArgs;

        lSelection = "FieldId = ?";
        lSelectionArgs = new String[1];
        lSelectionArgs[0] = String.valueOf(pPlayFieldId);

        lDB = this.getWritableDatabase();

        lDB.delete("FieldContext", lSelection, lSelectionArgs);

        lDB.delete("Cell", lSelection, lSelectionArgs);

        lDB.close();

    }

    void xDeleteSave(){
        SQLiteDatabase lDB;

        lDB = this.getWritableDatabase();

        lDB.delete("FieldContext", null, null);

        lDB.delete("Cell", null, null);

        lDB.close();
    }

    private void sNewFieldContext(PlayField pField){
        SQLiteDatabase lDB;
        ContentValues lValues;

        lDB = this.getWritableDatabase();

        lValues = new ContentValues();
        lValues.put("FieldId", pField.xFieldId());
        lValues.put("Selection", pField.xSelection());
        lValues.put("Pencil", (pField.xPencilMode()) ? 1 : 0);

        lDB.insert("FieldContext", null, lValues);

        lDB.close();
    }

    private void sUpdateGameContext(SudokuGameBase pGame){
        SQLiteDatabase lDB;
        ContentValues lValues;
        String lSelection;
        String[] lSelectionArgs;

        lDB = this.getWritableDatabase();

        lValues = new ContentValues();
        lValues.put("SetUp", (pGame.xGameStatus() == SudokuGame.cStatusSetup) ? 1 : 0);
        lValues.put("Lib", (pGame.xLibraryMode()) ? 1 : 0);
        lValues.put("Difficulty", pGame.xDifficulty());
        lValues.put("SelectedField", pGame.xPlayField().xFieldId());
        lValues.put("UsedTime", pGame.xUsedTime());
        lSelection = "ContextId = ?";
        lSelectionArgs = new String[] {"Game"};

        lDB.update("GameContext", lValues, lSelection, lSelectionArgs);
        lDB.close();
    }

    private void sNewCell(int pFieldId, int pCellNumber, PlayCell pCell){
        SQLiteDatabase lDB;
        ContentValues lValues;

        lDB = this.getWritableDatabase();

        lValues = new ContentValues();
        lValues.put("FieldId", pFieldId);
        lValues.put("CellNumber", pCellNumber);
        lValues.put("Value", pCell.xValue());
        lValues.put("Fixed", (pCell.xFixed()) ? 1 : 0);
        lValues.put("Confl", (pCell.xConflict()) ? 1 : 0);
        lValues.put("Pencil", pCell.xPencils());

        lDB.insert("Cell", null, lValues);

        lDB.close();
    }

    String xRandomGame(int pLevel){
        String lGame = null;
        SQLiteDatabase lDB;
        Cursor lCursor;
        String[] lColumns;
        String lSelection;
        String[] lSelectionArgs;
        String lSequence;
        String lLimit;

        lColumns = new String[] {"Game"};
        lSelection = "Level = ?";
        lSelectionArgs = new String[] {String.valueOf(pLevel)};
        lSequence = "RANDOM()";
        lLimit = "1";

        lDB = this.getReadableDatabase();

        lCursor = lDB.query("Game", lColumns, lSelection, lSelectionArgs, null, null, lSequence, lLimit);
        if (lCursor.moveToNext()){
            lGame = lCursor.getString(0);
        }
        lCursor.close();
        lDB.close();

        return lGame;
    }

    void xNewGame(int pLevel, String pGame){
        SQLiteDatabase lDB;
        ContentValues lValues;

        lDB = this.getWritableDatabase();

        lValues = new ContentValues();
        lValues.put("Level", pLevel);
        lValues.put("Game", pGame);

        lDB.insert("Game", null, lValues);

        lDB.close();
    }
}
