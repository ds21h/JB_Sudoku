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
    private GameData mGameData;
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

            switch (mGameData.xGameStatus()) {
                case GameData.cStatusGenerate: {
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
                case GameData.cStatusPlay: {
                    lInstant = Instant.now();
                    lNowTime = lInstant.getEpochSecond();
                    lElapsed = (int) (lNowTime - mStartTime);
                    lMinute = lElapsed / 60;
                    lSecond = lElapsed % 60;
                    setTitle(mHeader + String.format(" %02d:%02d", lMinute, lSecond));
                    mRefreshHandler.postDelayed(this, 100);
                    break;
                }
                case GameData.cStatusSolved: {
                    lElapsed = mGameData.xUsedTime();
                    lMinute = lElapsed / 60;
                    lSecond = lElapsed % 60;
                    setTitle(mHeader + String.format(" %02d:%02d", lMinute, lSecond));
                    mRefreshHandler.postDelayed(this, 500);
                    break;
                }
                case GameData.cStatusSetup: {
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
        mGameData = new GameData();
        mGame = new SudokuGame(mGameData);
        mSdkView = findViewById(R.id.svMain);
        mSdkView.setGame(mGame);
        mSdkView.setIntSudokuView(new SudokuView.intSudokuView() {
            @Override
            public void onSolved() {
                sSaveUsedTime();
                mGameData.xGameStatus(GameData.cStatusSolved);
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

        mGameData = mData.xGameData();
        mGame.xGameData(mGameData);
        sSetHeader();
        if (mGameData.xGameStatus() == GameData.cStatusPlay) {
            sSetStartTime();
            mStartTime -= mGameData.xUsedTime();
            mGameData.xResetUsedTime();
        }
        mRefreshHandler.postDelayed(mRefreshRunnable, 10);
    }

    @Override
    protected void onPause() {
        super.onPause();

        mRefreshHandler.removeCallbacks(mRefreshRunnable);
        if (mGameData.xGameStatus() == GameData.cStatusPlay) {
            sSaveUsedTime();
        }
        mData.xSaveGame(mGameData);
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

        switch (mGameData.xGameStatus()){
            case GameData.cStatusSetup:{
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
            case GameData.cStatusGenerate:{
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
            case GameData.cStatusPlay:{
                lMnuGenerate.setEnabled(true);
                lMnuNew.setEnabled(true);
                lMnuSetup.setEnabled(true);
                lMnuSetupStart.setEnabled(true);
                lMnuSetupFinish.setEnabled(false);
                if (mGameData.xLibraryMode()){
                    lMnuStore.setEnabled(false);
                } else {
                    lMnuStore.setEnabled(true);
                }
                lMnuSolve.setEnabled(true);
                lMnuPencil.setEnabled(true);
                break;
            }
            case GameData.cStatusSolved:{
                lMnuGenerate.setEnabled(true);
                lMnuNew.setEnabled(true);
                lMnuSetup.setEnabled(true);
                lMnuSetupStart.setEnabled(true);
                lMnuSetupFinish.setEnabled(false);
                if (mGameData.xLibraryMode()){
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
        mGameData.xGameStatus(GameData.cStatusSetup);
        mGame.xStartSetUp();
        mSdkView.invalidate();
    }

    public void hSetupFinish(MenuItem pItem) {
        mGame.xEndSetup();
        mGameData.xResetUsedTime();
        mGameData.xGameStatus(GameData.cStatusPlay);
        sSetHeader();
        sSetStartTime();
        mSdkView.invalidate();
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
            mGameData.xGameStatus(GameData.cStatusSolved);
            sSetHeader();
            mSdkView.invalidate();
        } else {
            mGameData.xGameStatus(GameData.cStatusNone);
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
            mGameData.xGameStatus(GameData.cStatusGenerate);
            mGameData.xDifficulty(pLevel);
            mGenerator = new GenerateGame(this).execute(pLevel);
            mGenerateCount = 0;
            mSdkView.setEnabled(false);
        }
    }

    public void hNew0(MenuItem pItem) {
        mGameData.xDifficulty(0);
        sNew();
    }

    public void hNew1(MenuItem pItem) {
        mGameData.xDifficulty(1);
        sNew();
    }

    public void hNew2(MenuItem pItem) {
        mGameData.xDifficulty(2);
        sNew();
    }

    public void hNew3(MenuItem pItem) {
        mGameData.xDifficulty(3);
        sNew();
    }

    public void hNew4(MenuItem pItem) {
        mGameData.xDifficulty(4);
        sNew();
    }

    private void sNew() {
        String lGame;

        lGame = mData.xRandomGame(mGameData.xDifficulty());
        if (lGame == null) {
            mGameData.xGameStatus(GameData.cStatusNone);
            Toast.makeText(mContext, R.string.msg_no_game, Toast.LENGTH_SHORT).show();
        } else {
            mGame.xNewGame(lGame);
            sStartGame();
        }
    }

    private void sStartGame() {
        sSetStartTime();
        mGameData.xGameStatus(GameData.cStatusPlay);
        mGameData.xResetUsedTime();
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
        mGameData.xAddUsedTime((int)(lInstant.getEpochSecond() - mStartTime));
    }

    private void sSetHeader() {
        String lHeader;
        String lPrefix;

        if (mGameData.xGameStatus() == GameData.cStatusPlay || mGameData.xGameStatus() == GameData.cStatusSolved) {
            if (mGameData.xDifficulty() < 0) {
                lPrefix = "";
            } else {
                if (mGameData.xLibraryMode()) {
                    lPrefix = "";
                } else {
                    lPrefix = "(G) ";
                }
            }
            switch (mGameData.xDifficulty()) {
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
