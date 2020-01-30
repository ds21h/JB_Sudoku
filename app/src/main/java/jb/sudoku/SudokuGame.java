package jb.sudoku;

import android.os.AsyncTask;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

class SudokuGame {
    private boolean mLibraryMode;
    private int mGameStatus;
    static final int cStatusNone = 0;
    static final int cStatusSetup = 1;
    static final int cStatusGenerate = 2;
    static final int cStatusPlay = 3;
    static final int cStatusSolved = 4;
    private int mDifficulty;
    private int mUsedTime;

    private List<PlayField> mPlayFields;
    private PlayField mPlayField;

    SudokuGame() {
        mGameStatus = cStatusNone;
        mLibraryMode = false;
        mDifficulty = -1;
        mUsedTime = 0;

        mPlayField = new PlayField();
        mPlayFields = new ArrayList<>();
        mPlayFields.add(mPlayField);
    }

    SudokuGame(List<PlayField> pFields, boolean pSetUp, boolean pLib, int pDifficulty, int pSelectedField, int pUsedTime) {
        mPlayFields = pFields;
        if (mPlayFields.isEmpty()) {
            mPlayField = new PlayField();
            mPlayFields.add(mPlayField);
        } else {
            mPlayField = pFields.get(0);
            if (mPlayField.xFieldId() != pSelectedField){
                for (PlayField lField : mPlayFields) {
                    if (lField.xFieldId() == pSelectedField){
                        mPlayField = lField;
                        break;
                    }
                }
            }
        }
        mLibraryMode = pLib;
        if (mPlayField.xEmptyField()) {
            mGameStatus = cStatusNone;
        } else {
            if (pSetUp) {
                mGameStatus = cStatusSetup;
            } else {
                if (mPlayField.xSolved()) {
                    mGameStatus = cStatusSolved;
                } else {
                    mGameStatus = cStatusPlay;
                }
            }
        }
        mDifficulty = pDifficulty;
        mUsedTime = pUsedTime;
    }

    PlayField xPlayField() {
        return mPlayField;
    }

    List<PlayField> xPlayFields(){
        return mPlayFields;
    }

    int xSelectedField() {
        return mPlayField.xFieldId();
    }

    int xGameStatus() {
        return mGameStatus;
    }

    int xUsedTime() {
        return mUsedTime;
    }

    void xAddUsedTime(int pCorr) {
        mUsedTime += pCorr;
    }

    void xResetUsedTime() {
        mUsedTime = 0;
    }

    boolean xLibraryMode() {
        return mLibraryMode;
    }

    int xDifficulty() {
        return mDifficulty;
    }

    void xDifficulty(int pDifficulty) {
        mDifficulty = pDifficulty;
    }

    int xFieldCount(){
        return mPlayFields.size();
    }

    private void sInitPlayFields(){
        mPlayField = mPlayFields.get(0);
        mPlayFields.clear();
        mPlayFields.add(mPlayField);
    }

    void xPlayFieldClone(){
        int lNewId;
        PlayField lField;

        lNewId = mPlayFields.get(mPlayFields.size() - 1).xFieldId() + 1;
        lField = new PlayField(lNewId, mPlayField);
        mPlayFields.add(lField);
        mPlayField = lField;
    }

    void xSwitchPlayField(int pNewId){
        for (PlayField lField : mPlayFields){
            if (lField.xFieldId() == pNewId){
                mPlayField = lField;
                break;
            }
        }
    }

    void xDeleteCurrentPlayField(){
        if (mPlayField.xFieldId() != 0){
            if (mPlayFields.size() > 1){
                mPlayFields.remove(mPlayField);
                mPlayField = mPlayFields.get(0);
            }
        }
    }

    boolean xSelectionValue(int pRow, int pColumn) {
        PlayCell lCell;
        int lValue;
        boolean lResult;

        lResult = false;
        lValue = mPlayField.xSelectedCell().xValue();
        if (lValue > 0) {
            lCell = mPlayField.xCell(pRow, pColumn);
            if (lCell.xValue() == lValue) {
                lResult = true;
            } else {
                if (lCell.xValue() == 0) {
                    if (lCell.xPencil(lValue)) {
                        lResult = true;
                    }
                }
            }
        }
        return lResult;
    }

    boolean xSelectionRange(int pRow, int pColumn) {
        if (pRow == mPlayField.xSelectionRow()) {
            return true;
        }
        if (pColumn == mPlayField.xSelectionColumn()) {
            return true;
        }
        //noinspection RedundantIfStatement
        if ((pRow / 3) == (mPlayField.xSelectionRow() / 3) && (pColumn / 3) == (mPlayField.xSelectionColumn() / 3)) {
            return true;
        }
        return false;
    }

    void xStartSetUp() {
        if (mGameStatus != cStatusSetup) {
            mGameStatus = cStatusSetup;
            sInitPlayFields();
            mPlayField.xResetField();
            mLibraryMode = false;
            mDifficulty = -1;
        }
    }

    boolean xEndSetup() {
        boolean lResult;

        lResult = true;
        if (mGameStatus == cStatusSetup) {
            lResult = sCheckGame();
            if (lResult) {
                mPlayField.xFixField();
            }
        }
        return lResult;
    }

    void xStartGame() {
        mGameStatus = cStatusPlay;
        xResetUsedTime();
    }

    void xNewGame(String pGame) {
        GameScrambler lScrambler;
        Cell[] lCells;

        lScrambler = new GameScrambler(pGame);
        lCells = lScrambler.xScrambleGame();
        sInitPlayFields();
        mPlayField.xCells(lCells);
        mLibraryMode = true;
    }

    String xGame() {
        return mPlayField.xGame();
    }

    void xFillPencil() {
        mPlayField.xInitPencil();
        sPencilRows();
        sPencilColumns();
        sPencilSegments();
    }

    void xClearPencil() {
        mPlayField.xClearPencil();
    }

    boolean xSolve() {
        SudokuSolver lSolver;
        Cell[] lCells;
        boolean lResult;

        lSolver = new SudokuSolver();
        lCells = lSolver.xSolve(mPlayField.xCells());
        if (lCells == null) {
            mGameStatus = cStatusNone;
            lResult = false;
        } else {
            mPlayField.xCells(lCells);
            mGameStatus = cStatusSolved;
            lResult = true;
        }
        return lResult;
    }

    void xGenerate(int pLevel, AsyncTask pTask) {
        SudokuGenerator lGenerator;
        Cell[] lCells;

        mGameStatus = cStatusGenerate;
        mDifficulty = pLevel;

        lGenerator = new SudokuGenerator();
        lCells = lGenerator.xGenerate(pLevel, pTask);
        if (!(pTask != null && pTask.isCancelled())) {
            sInitPlayFields();
            mPlayField.xCells(lCells);
            mLibraryMode = false;
        }
    }

    void xProcessDigit(int pDigit) {
        PlayCell lCell;

        if (pDigit >= 1 && pDigit <= 9) {
            lCell = mPlayField.xSelectedCell();
            if (mPlayField.xPencilMode()) {
                lCell.xPencilFlip(pDigit);
            } else {
                if (!lCell.xFixed()) {
                    mPlayField.xSetCellValue(pDigit);
                    if (mPlayField.xSolved()) {
                        mGameStatus = cStatusSolved;
                    }
                    sPencilRow(mPlayField.xSelectionRow());
                    sPencilColumn(mPlayField.xSelectionColumn());
                    sPencilSegment(mPlayField.xSelectionRow() / 3, mPlayField.xSelectionColumn() / 3);
                    sCheckGame();
                }
            }
        }
    }

    private boolean sCheckGame() {
        boolean lResult;
        boolean lResStep;

        mPlayField.xResetConflicts();
        lResult = sCheckRows();
        lResStep = sCheckColumns();
        if (lResult) {
            lResult = lResStep;
        }
        lResStep = sCheckSegments();
        if (lResult) {
            lResult = lResStep;
        }
        return lResult;
    }

    private boolean sCheckRows() {
        int lRow;
        boolean lResult;
        boolean lResStep;

        lResult = true;
        for (lRow = 0; lRow < 9; lRow++) {
            lResStep = sCheckRow(lRow);
            if (lResult) {
                lResult = lResStep;
            }
        }
        return lResult;
    }

    private boolean sCheckRow(int pRow) {
        boolean lResult;
        int lColumn;
        PlayCell[] lBlock = new PlayCell[9];

        for (lColumn = 0; lColumn < 9; lColumn++) {
            lBlock[lColumn] = mPlayField.xCell(pRow, lColumn);
        }
        lResult = sCheckBlock(lBlock);
        return lResult;
    }

    private boolean sCheckColumns() {
        int lColumn;
        boolean lResult;
        boolean lResStep;

        lResult = true;
        for (lColumn = 0; lColumn < 9; lColumn++) {
            lResStep = sCheckColumn(lColumn);
            if (lResult) {
                lResult = lResStep;
            }
        }
        return lResult;
    }

    private boolean sCheckColumn(int pColumn) {
        int lRow;
        PlayCell[] lBlock = new PlayCell[9];
        boolean lResult;

        for (lRow = 0; lRow < 9; lRow++) {
            lBlock[lRow] = mPlayField.xCell(lRow, pColumn);
        }
        lResult = sCheckBlock(lBlock);
        return lResult;
    }

    private boolean sCheckSegments() {
        int lSegmentRow;
        int lSegmentColumn;
        boolean lResult;
        boolean lResStep;

        lResult = true;
        for (lSegmentRow = 0; lSegmentRow < 3; lSegmentRow++) {
            for (lSegmentColumn = 0; lSegmentColumn < 3; lSegmentColumn++) {
                lResStep = sCheckSegment(lSegmentRow, lSegmentColumn);
                if (lResult) {
                    lResult = lResStep;
                }
            }
        }
        return lResult;
    }

    private boolean sCheckSegment(int pSegmentRow, int pSegmentColumn) {
        int lRow;
        int lColumn;
        int lCell;
        PlayCell[] lBlock = new PlayCell[9];
        boolean lResult;

        lCell = 0;
        for (lRow = 0; lRow < 3; lRow++) {
            for (lColumn = 0; lColumn < 3; lColumn++) {
                lBlock[lCell] = mPlayField.xCells()[((pSegmentRow * 27) + (lRow * 9)) + (pSegmentColumn * 3) + lColumn];
                lCell++;
            }
        }
        lResult = sCheckBlock(lBlock);
        return lResult;
    }

    private boolean sCheckBlock(@NotNull PlayCell[] pBlock) {
        int lCount1;
        int lCount2;
        boolean lResult;

        lResult = true;
        for (lCount1 = 0; lCount1 < pBlock.length - 1; lCount1++) {
            if (pBlock[lCount1].xValue() != 0) {
                for (lCount2 = lCount1 + 1; lCount2 < pBlock.length; lCount2++) {
                    if (pBlock[lCount1].xValue() == pBlock[lCount2].xValue()) {
                        pBlock[lCount1].xConflict(true);
                        pBlock[lCount2].xConflict(true);
                        lResult = false;
                    }
                }
            }
        }
        return lResult;
    }

    private void sPencilRows() {
        int lRow;

        for (lRow = 0; lRow < 9; lRow++) {
            sPencilRow(lRow);
        }
    }

    private void sPencilRow(int pRow) {
        int lColumn;
        PlayCell[] lBlock = new PlayCell[9];

        for (lColumn = 0; lColumn < 9; lColumn++) {
            lBlock[lColumn] = mPlayField.xCell(pRow, lColumn);
        }
        sPencilBlock(lBlock);
    }

    private void sPencilColumns() {
        int lColumn;

        for (lColumn = 0; lColumn < 9; lColumn++) {
            sPencilColumn(lColumn);
        }
    }

    private void sPencilColumn(int pColumn) {
        int lRow;
        PlayCell[] lBlock = new PlayCell[9];

        for (lRow = 0; lRow < 9; lRow++) {
            lBlock[lRow] = mPlayField.xCell(lRow, pColumn);
        }
        sPencilBlock(lBlock);
    }

    private void sPencilSegments() {
        int lSegmentRow;
        int lSegmentColumn;

        for (lSegmentRow = 0; lSegmentRow < 3; lSegmentRow++) {
            for (lSegmentColumn = 0; lSegmentColumn < 3; lSegmentColumn++) {
                sPencilSegment(lSegmentRow, lSegmentColumn);
            }
        }
    }

    private void sPencilSegment(int pSegmentRow, int pSegmentColumn) {
        int lRow;
        int lColumn;
        int lCell;
        PlayCell[] lBlock = new PlayCell[9];

        lCell = 0;
        for (lRow = 0; lRow < 3; lRow++) {
            for (lColumn = 0; lColumn < 3; lColumn++) {
                lBlock[lCell] = mPlayField.xCells()[((pSegmentRow * 27) + (lRow * 9)) + (pSegmentColumn * 3) + lColumn];
                lCell++;
            }
        }
        sPencilBlock(lBlock);
    }

    private void sPencilBlock(@NotNull PlayCell[] pBlock) {
        int lCount1;
        int lCount2;

        for (lCount1 = 0; lCount1 < pBlock.length; lCount1++) {
            if (pBlock[lCount1].xValue() != 0) {
                for (lCount2 = 0; lCount2 < pBlock.length; lCount2++) {
                    pBlock[lCount2].xPencilReset(pBlock[lCount1].xValue());
                }
            }
        }
    }
}
