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
    private static final int cDBVersion = 2;
    private static String mExternalFilesDir;

    static Data getInstance(Context pContext) {
        Context lContext;
        File lExternalFilesDir;
        /*
         * use the application context as suggested by CommonsWare.
         * this will ensure that you dont accidentally leak an Activitys
         * context (see this article for more information:
         * http://developer.android.com/resources/articles/avoiding-memory-leaks.html)
         */
        if (mInstance == null) {
            lContext = pContext.getApplicationContext();
            lExternalFilesDir = lContext.getExternalFilesDir(null);
            if (lExternalFilesDir == null){
                mExternalFilesDir = "";
            } else {
                mExternalFilesDir = lExternalFilesDir.getAbsolutePath();
            }
            mInstance = new Data(lContext);
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
        sDefineContext(pDB);
        sInitContext(pDB);
        sDefineCell(pDB);
        sDefineGame(pDB);
        sInitGame(pDB);
    }

    @Override
    public void onUpgrade(SQLiteDatabase pDB, int pOldVersion, int pNewVersion) {
        switch (pOldVersion){
            case 1:{
                sUpgradeContext_1(pDB);
                sDefineGame(pDB);
                sInitGame(pDB);
                break;
            }
            default:{
                pDB.execSQL("DROP TABLE IF EXISTS Context");
                pDB.execSQL("DROP TABLE IF EXISTS Cell");
                pDB.execSQL("DROP TABLE IF EXISTS Game");
                onCreate(pDB);
                break;
            }
        }
    }

    private void sDefineContext(SQLiteDatabase pDB) {
        pDB.execSQL(
                "CREATE TABLE Context " +
                        "(ContextId Integer primary key, " +
                        "Selection Integer Not Null, " +
                        "SetUp Integer Not Null, " +
                        "Pencil Integer Not Null, " +
                        "Lib Integer Not Null Default (0), " +
                        "Difficulty Integer Not Null Default (-1), " +
                        "UsedTime Integer Not Null Default (0)" +
                        ")"
        );
    }

    private void sInitContext(SQLiteDatabase pDB){
        pDB.execSQL(
                "INSERT INTO Context " +
                        "(ContextId, Selection, SetUp, Pencil, Lib, Difficulty, UsedTime) " +
                        "VALUES " +
                        "(0, 0, 0, 0, 0, -1, 0)"
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

    private void sUpgradeContext_1(SQLiteDatabase pDB){
        pDB.execSQL(
                "CREATE TABLE temp_context AS " +
                        "SELECT * FROM Context"
        );

        pDB.execSQL(
                "DROP TABLE Context"
        );

        sDefineContext(pDB);

        pDB.execSQL(
                "INSERT INTO Context " +
                        "(ContextId, Selection, SetUp, Pencil) " +
                        "SELECT ContextId, Selection, SetUp, Pencil " +
                        "FROM temp_context"
        );

        pDB.execSQL(
                "DROP TABLE temp_context"
        );
    }

    GameData xGameData(){
        GameData lGame = new GameData();
        SQLiteDatabase lDB;
        Cursor lCursor;
        String[] lColumns;
        String lSelection;
        String[] lSelectionArgs;
        int lSel;
        int lSetUp;
        int lPencil;
        int lLib;
        int lDifficulty;
        int lUsedTime;
        List<DbCell> mCells;

        mCells = sCells();

        lColumns = new String[] {"Selection", "SetUp", "Pencil", "Lib", "Difficulty", "UsedTime"};
        lSelection = "ContextId = ?";
        lSelectionArgs = new String[] {"0"};

        lDB = this.getReadableDatabase();

        lCursor = lDB.query("Context", lColumns, lSelection, lSelectionArgs, null, null, null);
        if (lCursor.moveToNext()){
            lSel = lCursor.getInt(0);
            lSetUp = lCursor.getInt(1);
            lPencil = lCursor.getInt(2);
            lLib = lCursor.getInt(3);
            lDifficulty = lCursor.getInt(4);
            lUsedTime = lCursor.getInt(5);
            //noinspection RedundantConditionalExpression
            lGame = new GameData(mCells, lSel, (lSetUp == 0) ? false : true, (lPencil == 0) ? false : true, (lLib == 0) ? false : true, lDifficulty, lUsedTime);
        }
        lCursor.close();
        lDB.close();

        return lGame;
    }

    private List<DbCell> sCells(){
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

    void xSaveGame(GameData pGameData){
        PlayCell[] mCells;
        int lCount;

        sUpdateContext(pGameData);
        sDeleteCells();
        mCells = pGameData.xCells();
        for(lCount = 0; lCount < mCells.length; lCount++){
            sNewCell(lCount,  mCells[lCount]);
        }
    }

    private void sUpdateContext(GameData pGameData){
        SQLiteDatabase lDB;
        ContentValues lValues;
        String lSelection;
        String[] lSelectionArgs;

        lValues = new ContentValues();
        lValues.put("Selection", pGameData.xSelection());
        lValues.put("SetUp", (pGameData.xSetUpMode()) ? 1 : 0);
        lValues.put("Pencil", (pGameData.xPencilMode()) ? 1 : 0);
        lValues.put("Lib", (pGameData.xLibraryMode()) ? 1 : 0);
        lValues.put("Difficulty", pGameData.xDifficulty());
        lValues.put("UsedTime", pGameData.xUsedTime());
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

    private void sNewCell(int pCellNumber, PlayCell pCell){
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
