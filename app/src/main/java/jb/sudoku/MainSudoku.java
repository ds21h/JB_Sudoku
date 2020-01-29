package jb.sudoku;

//*
//*  Uses the JSR-310 backport for Android (java.time.* package in Java 8)
//*
//*  See https://github.com/JakeWharton/ThreeTenABP
//*

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.jakewharton.threetenabp.AndroidThreeTen;

import org.threeten.bp.Instant;

import java.lang.ref.WeakReference;

public class MainSudoku extends Activity {
    private final Context mContext = this;
    private SudokuView mSdkView;
    private SudokuGame mGame;
//    private GameData mGameData;
    private Data mData;
    private AsyncTask mGenerator;
    private int mGenerateCount;
    private long mStartTime;
    private String mHeader;

    Handler mRefreshHandler = new Handler();
    Runnable mRefreshRunnable = new Runnable() {
        @SuppressLint("DefaultLocale")
        @Override
        public void run() {
            StringBuilder lBuilder;
            int lCount;
            Instant lInstant;
            long lNowTime;
            int lElapsed;
            int lMinute;
            int lSecond;

            switch (mGame.xGameStatus()) {
                case SudokuGame.cStatusGenerate: {
                    lBuilder = new StringBuilder();
                    mGenerateCount++;
                    if (mGenerateCount > 5) {
                        mGenerateCount = 1;
                    }
                    lBuilder.append(getString(R.string.mnu_generate));
                    for (lCount = 0; lCount < mGenerateCount; lCount++) {
                        lBuilder.append(".");
                    }
                    setTitle(lBuilder.toString());
                    mRefreshHandler.postDelayed(this, 1000);
                    break;
                }
                case SudokuGame.cStatusPlay: {
                    lInstant = Instant.now();
                    lNowTime = lInstant.getEpochSecond();
                    lElapsed = (int) (lNowTime - mStartTime);
                    lMinute = lElapsed / 60;
                    lSecond = lElapsed % 60;
                    setTitle(mHeader + String.format(" %02d:%02d", lMinute, lSecond));
                    mRefreshHandler.postDelayed(this, 100);
                    break;
                }
                case SudokuGame.cStatusSolved: {
                    lElapsed = mGame.xUsedTime();
                    lMinute = lElapsed / 60;
                    lSecond = lElapsed % 60;
                    setTitle(mHeader + String.format(" %02d:%02d", lMinute, lSecond));
                    mRefreshHandler.postDelayed(this, 500);
                    break;
                }
                case SudokuGame.cStatusSetup: {
                    setTitle(getString(R.string.app_name) + " - " + getString(R.string.mnu_setup));
                    mRefreshHandler.postDelayed(this, 500);
                    break;
                }
                default: {
                    setTitle(R.string.app_name);
                    mRefreshHandler.postDelayed(this, 500);
                    break;
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidThreeTen.init(this);
        setContentView(R.layout.mainsudoku_layout);

        mHeader = "";
        mGenerator = null;
//        mGameData = new GameData();
        mGame = new SudokuGame();
        mSdkView = findViewById(R.id.svMain);
        mSdkView.setGame(mGame);
        mSdkView.setIntSudokuView(new SudokuView.intSudokuView() {
            @Override
            public void onSolved() {
                sSaveUsedTime();
                Toast.makeText(mContext, R.string.msg_solved, Toast.LENGTH_SHORT).show();
            }
        });
        mData = Data.getInstance(mContext);
        mGenerateCount = -1;
        mStartTime = 0;
    }

    @Override
    protected void onResume() {
        super.onResume();

        mGame = mData.xCurrentGame();
        mSdkView.setGame(mGame);
//        mGameData = mGame.xPlayField();
        sSetHeader();
        if (mGame.xGameStatus() == SudokuGame.cStatusPlay) {
            sSetStartTime();
            mStartTime -= mGame.xUsedTime();
            mGame.xResetUsedTime();
        }
        mRefreshHandler.postDelayed(mRefreshRunnable, 10);
    }

    @Override
    protected void onPause() {
        super.onPause();

        mRefreshHandler.removeCallbacks(mRefreshRunnable);
        if (mGame.xGameStatus() == SudokuGame.cStatusPlay) {
            sSaveUsedTime();
        }
        mData.xSaveGame(mGame);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu pMenu) {
        super.onCreateOptionsMenu(pMenu);
        getMenuInflater().inflate(R.menu.sudoku_menu, pMenu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu pMenu) {
        super.onPrepareOptionsMenu(pMenu);

        MenuItem lMnuGenerate;
        MenuItem lMnuNew;
        MenuItem lMnuSetup;
        MenuItem lMnuSetupStart;
        MenuItem lMnuSetupFinish;
        MenuItem lMnuStore;
        MenuItem lMnuSolve;
        MenuItem lMnuPencil;

        lMnuGenerate = pMenu.findItem(R.id.mnuGenerate);
        lMnuNew = pMenu.findItem(R.id.mnuNew);
        lMnuSetup = pMenu.findItem(R.id.mnuSetup);
        lMnuSetupStart = pMenu.findItem(R.id.mnuSetupStart);
        lMnuSetupFinish = pMenu.findItem(R.id.mnuSetupFinish);
        lMnuStore = pMenu.findItem(R.id.mnuStore);
        lMnuSolve = pMenu.findItem(R.id.mnuSolve);
        lMnuPencil = pMenu.findItem(R.id.mnuPencil);

        switch (mGame.xGameStatus()){
            case SudokuGame.cStatusSetup:{
                lMnuGenerate.setEnabled(false);
                lMnuNew.setEnabled(false);
                lMnuSetup.setEnabled(true);
                lMnuSetupStart.setEnabled(false);
                lMnuSetupFinish.setEnabled(true);
                lMnuStore.setEnabled(false);
                lMnuSolve.setEnabled(false);
                lMnuPencil.setEnabled(false);
                break;
            }
            case SudokuGame.cStatusGenerate:{
                lMnuGenerate.setEnabled(false);
                lMnuNew.setEnabled(false);
                lMnuSetup.setEnabled(false);
                lMnuSetupStart.setEnabled(false);
                lMnuSetupFinish.setEnabled(false);
                lMnuStore.setEnabled(false);
                lMnuSolve.setEnabled(false);
                lMnuPencil.setEnabled(false);
                break;
            }
            case SudokuGame.cStatusPlay:{
                lMnuGenerate.setEnabled(true);
                lMnuNew.setEnabled(true);
                lMnuSetup.setEnabled(true);
                lMnuSetupStart.setEnabled(true);
                lMnuSetupFinish.setEnabled(false);
                if (mGame.xLibraryMode()){
                    lMnuStore.setEnabled(false);
                } else {
                    lMnuStore.setEnabled(true);
                }
                lMnuSolve.setEnabled(true);
                lMnuPencil.setEnabled(true);
                break;
            }
            case SudokuGame.cStatusSolved:{
                lMnuGenerate.setEnabled(true);
                lMnuNew.setEnabled(true);
                lMnuSetup.setEnabled(true);
                lMnuSetupStart.setEnabled(true);
                lMnuSetupFinish.setEnabled(false);
                if (mGame.xLibraryMode()){
                    lMnuStore.setEnabled(false);
                } else {
                    lMnuStore.setEnabled(true);
                }
                lMnuSolve.setEnabled(false);
                lMnuPencil.setEnabled(false);
                break;
            }
            default:{
                lMnuGenerate.setEnabled(true);
                lMnuNew.setEnabled(true);
                lMnuSetup.setEnabled(true);
                lMnuSetupStart.setEnabled(true);
                lMnuSetupFinish.setEnabled(false);
                lMnuStore.setEnabled(false);
                lMnuSolve.setEnabled(false);
                lMnuPencil.setEnabled(false);
                break;
            }
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mGenerator != null) {
            mGenerator.cancel(true);
        }
    }

    @Override
    protected void onActivityResult(int pRequest, int pResult, Intent pInt) {
        Bundle lBundle;
        int lDifficulty;

        if (pRequest == 1) {
            if (pResult == RESULT_OK) {
                lBundle = pInt.getExtras();
                if (lBundle != null) {
                    lDifficulty = lBundle.getInt(SelectDifficulty.cLevel);
                    sStore(lDifficulty);
                }
            }
        }
    }

    public void hSetupStart(MenuItem pItem) {
        mGame.xStartSetUp();
        mSdkView.invalidate();
    }

    public void hSetupFinish(MenuItem pItem) {
        if (mGame.xEndSetup()){
            sStartGame();
        }
    }

    public void hStore(MenuItem pItem) {
        Intent lInt;

        lInt = new Intent();
        lInt.setClass(this, SelectDifficulty.class);
        startActivityForResult(lInt, 1);
    }

    private void sStore(int pLevel) {
        String lGame;

        lGame = mGame.xGame();
        mData.xNewGame(pLevel, lGame);
        Toast.makeText(mContext, R.string.msg_game_stored, Toast.LENGTH_SHORT).show();
    }

    public void hSolve(MenuItem pItem) {
        boolean lResult;

        lResult = mGame.xSolve();
        sSaveUsedTime();
        if (lResult) {
            sSetHeader();
            mSdkView.invalidate();
        } else {
            Toast.makeText(mContext, R.string.msg_unsolvable, Toast.LENGTH_SHORT).show();
        }
    }

    public void hGenerate1(MenuItem pItem) {
        sGenerate(1);
    }

    public void hGenerate2(MenuItem pItem) {
        sGenerate(2);
    }

    public void hGenerate3(MenuItem pItem) {
        sGenerate(3);
    }

    private void sGenerate(int pLevel) {
        if (mGenerator != null) {
            if (mGenerator.getStatus() == AsyncTask.Status.FINISHED) {
                mGenerator = null;
            }
        }
        if (mGenerator == null) {
            mGenerator = new GenerateGame(this).execute(pLevel);
            mGenerateCount = 0;
            mSdkView.setEnabled(false);
        }
    }

    public void hNew0(MenuItem pItem) {
        mGame.xDifficulty(0);
        sNew();
    }

    public void hNew1(MenuItem pItem) {
        mGame.xDifficulty(1);
        sNew();
    }

    public void hNew2(MenuItem pItem) {
        mGame.xDifficulty(2);
        sNew();
    }

    public void hNew3(MenuItem pItem) {
        mGame.xDifficulty(3);
        sNew();
    }

    public void hNew4(MenuItem pItem) {
        mGame.xDifficulty(4);
        sNew();
    }

    private void sNew() {
        String lGame;

        lGame = mData.xRandomGame(mGame.xDifficulty());
        if (lGame == null) {
            Toast.makeText(mContext, R.string.msg_no_game, Toast.LENGTH_SHORT).show();
        } else {
            mGame.xNewGame(lGame);
            sStartGame();
        }
    }

    private void sStartGame() {
        sSetStartTime();
        mGame.xStartGame();
        sSetHeader();
        mSdkView.setEnabled(true);
        mSdkView.invalidate();
    }

    public void hFillPencil(MenuItem pItem) {
        mGame.xFillPencil();
        mSdkView.invalidate();
    }

    public void hClearPencil(MenuItem pItem) {
        mGame.xClearPencil();
        mSdkView.invalidate();
    }

    private void sSetStartTime() {
        Instant lInstant;

        lInstant = Instant.now();
        mStartTime = lInstant.getEpochSecond();
    }

    private void sSaveUsedTime(){
        Instant lInstant;

        lInstant = Instant.now();
        mGame.xAddUsedTime((int)(lInstant.getEpochSecond() - mStartTime));
    }

    private void sSetHeader() {
        String lHeader;
        String lPrefix;

        if (mGame.xGameStatus() == SudokuGame.cStatusPlay || mGame.xGameStatus() == SudokuGame.cStatusSolved) {
            if (mGame.xDifficulty() < 0) {
                lPrefix = "";
            } else {
                if (mGame.xLibraryMode()) {
                    lPrefix = "";
                } else {
                    lPrefix = "(G) ";
                }
            }
            switch (mGame.xDifficulty()) {
                case 0: {
                    lHeader = getString(R.string.mnu_level_very_easy);
                    break;
                }
                case 1: {
                    lHeader = getString(R.string.mnu_level_easy);
                    break;
                }
                case 2: {
                    lHeader = getString(R.string.mnu_level_medium);
                    break;
                }
                case 3: {
                    lHeader = getString(R.string.mnu_level_hard);
                    break;
                }
                case 4: {
                    lHeader = getString(R.string.mnu_level_very_hard);
                    break;
                }
                default: {
                    lHeader = getString(R.string.app_name);
                    break;
                }
            }
            mHeader = lPrefix + lHeader;
        } else {
            mHeader = getString(R.string.app_name);
        }
    }

    static class GenerateGame extends AsyncTask<Integer, Void, Void> {
        WeakReference<MainSudoku> mRefMain;

        private GenerateGame(MainSudoku pMain) {
            mRefMain = new WeakReference<>(pMain);
        }

        @Override
        protected Void doInBackground(Integer... pLevel) {
            MainSudoku lMain;
            int lLevel;

            lMain = mRefMain.get();
            if (lMain != null) {
                lLevel = pLevel[0];
                lMain.mGame.xGenerate(lLevel, this);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            MainSudoku lMain;

            lMain = mRefMain.get();
            if (lMain != null) {
                lMain.sStartGame();
            }
        }
    }
}
