package jb.sudoku;

//*
//*  Uses the JSR-310 backport for Android (java.time.* package in Java 8)
//*
//*  See https://github.com/JakeWharton/ThreeTenABP
//*

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.jakewharton.threetenabp.AndroidThreeTen;
import org.threeten.bp.Instant;
import java.util.List;

public class MainSudoku extends Activity {
    private final Context mContext = this;
    private SudokuView mSdkView;
    private SudokuGame mGame;
    private Data mData;
    private GenerateRunnable mGenerate;
    private int mGenerateCount;
    private long mStartTime;
    private String mHeader;

    Handler mGenerateHandler = new Handler(Looper.getMainLooper(), new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message pMessage) {
            if (pMessage.what == GenerateRunnable.cGenerateFinished){
                sStartGame();
            }
            mGenerate = null;
            return true;
        }
    });
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
                    lBuilder.append(getString(R.string.mnu_new));
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
        mGenerate = null;
        mGame = new SudokuGame();
        mSdkView = findViewById(R.id.svMain);
        mSdkView.setGame(mGame);
        mSdkView.setIntSudokuView(() -> {
            sSaveUsedTime();
            Toast.makeText(mContext, R.string.msg_solved, Toast.LENGTH_SHORT).show();
        });
        mData = Data.getInstance(mContext);
        mGenerateCount = -1;
        mStartTime = 0;
    }

    @Override
    protected void onStart(){
        super.onStart();

        mGame = mData.xCurrentGame();
    }

    @Override
    protected void onResume() {
        super.onResume();

        mSdkView.setGame(mGame);
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
    }

    @Override
    protected void onStop(){
        SaveGameRunnable lSaveGameRunnable;

        mGenerateHandler.removeCallbacksAndMessages(null);
        lSaveGameRunnable = new SaveGameRunnable(mContext, mGame);
        SudokuApp.getInstance().xExecutor.execute(lSaveGameRunnable);

        super.onStop();
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
        MenuItem lMnuSetup;
        MenuItem lMnuSetupStart;
        MenuItem lMnuSetupFinish;
        MenuItem lMnuSolve;
        MenuItem lMnuPencil;
        MenuItem lMnuPencilAuto;
        MenuItem lMnuFields;
        MenuItem lMnuSwitch;
        MenuItem lMnuDelete;

        lMnuGenerate = pMenu.findItem(R.id.mnuNew);
        lMnuSetup = pMenu.findItem(R.id.mnuSetup);
        lMnuSetupStart = pMenu.findItem(R.id.mnuSetupStart);
        lMnuSetupFinish = pMenu.findItem(R.id.mnuSetupFinish);
        lMnuSolve = pMenu.findItem(R.id.mnuSolve);
        lMnuPencil = pMenu.findItem(R.id.mnuPencil);
        lMnuPencilAuto = pMenu.findItem(R.id.mnuPencilAuto);
        lMnuFields = pMenu.findItem(R.id.mnuFields);
        lMnuSwitch = pMenu.findItem(R.id.mnuSwitch);
        lMnuDelete = pMenu.findItem(R.id.mnuDelete);

        switch (mGame.xGameStatus()){
            case SudokuGame.cStatusSetup:{
                lMnuGenerate.setEnabled(false);
                lMnuSetup.setEnabled(true);
                lMnuSetupStart.setEnabled(false);
                lMnuSetupFinish.setEnabled(true);
                lMnuSolve.setEnabled(false);
                lMnuPencil.setEnabled(false);
                lMnuFields.setEnabled(false);
                break;
            }
            case SudokuGame.cStatusGenerate:{
                lMnuGenerate.setEnabled(false);
                lMnuSetup.setEnabled(false);
                lMnuSetupStart.setEnabled(false);
                lMnuSetupFinish.setEnabled(false);
                lMnuSolve.setEnabled(false);
                lMnuPencil.setEnabled(false);
                lMnuFields.setEnabled(false);
                break;
            }
            case SudokuGame.cStatusPlay:{
                lMnuGenerate.setEnabled(true);
                lMnuSetup.setEnabled(true);
                lMnuSetupStart.setEnabled(true);
                lMnuSetupFinish.setEnabled(false);
                lMnuSolve.setEnabled(true);
                lMnuPencil.setEnabled(true);
                lMnuPencilAuto.setChecked(mGame.xPencilAuto());
                lMnuFields.setEnabled(true);
                if (mGame.xFieldCount() > 1){
                    lMnuSwitch.setEnabled(true);
                    lMnuDelete.setEnabled(mGame.xPlayField().xFieldId() != 0);
                } else {
                    lMnuSwitch.setEnabled(false);
                    lMnuDelete.setEnabled(false);
                }
                break;
            }
            case SudokuGame.cStatusSolved:{
                lMnuGenerate.setEnabled(true);
                lMnuSetup.setEnabled(true);
                lMnuSetupStart.setEnabled(true);
                lMnuSetupFinish.setEnabled(false);
                lMnuSolve.setEnabled(false);
                lMnuPencil.setEnabled(false);
                lMnuFields.setEnabled(false);
                break;
            }
            default:{
                lMnuGenerate.setEnabled(true);
                lMnuSetup.setEnabled(true);
                lMnuSetupStart.setEnabled(true);
                lMnuSetupFinish.setEnabled(false);
                lMnuSolve.setEnabled(false);
                lMnuPencil.setEnabled(false);
                lMnuFields.setEnabled(false);
                break;
            }
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mGenerate != null){
            mGenerate.xEnd();
            mGenerate = null;
        }
    }

    public void hSetupStart(MenuItem pItem) {
        mData.xDeleteSave();
        mGame.xStartSetUp();
        mSdkView.invalidate();
    }

    public void hSetupFinish(MenuItem pItem) {
        if (mGame.xEndSetup()){
            sStartGame();
        }
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

    public void hGenerate(MenuItem pItem) {
        int lItem;
        int lLevel;

        lItem = pItem.getItemId();
        if (lItem == R.id.mnuGenEasy){
            lLevel = SudokuGameBase.cDiffEasy;
        } else {
            if (lItem == R.id.mnuGenMed){
                lLevel = SudokuGameBase.cDiffMedium;
            } else {
                if (lItem == R.id.mnuGenHard){
                    lLevel = SudokuGameBase.cDiffHard;
                } else {
                    lLevel = 0;
                }
            }
        }
        if (lLevel > 0){
            mData.xDeleteSave();
            if (mGenerate == null){
                mGenerate = new GenerateRunnable(mGenerateHandler, mGame, lLevel);
                SudokuApp.getInstance().xExecutor.execute(mGenerate);
            }
        }
    }

    private void sStartGame() {
        sSetStartTime();
        mGame.xStartGame();
        sSetHeader();
        mSdkView.setEnabled(true);
        mSdkView.invalidate();
    }

    public void hAutoPencil(MenuItem pItem) {
        mGame.xFlipPencilAuto();
    }

    public void hFillPencil(MenuItem pItem) {
        mGame.xFillPencil();
        mSdkView.invalidate();
    }

    public void hClearPencil(MenuItem pItem) {
        mGame.xClearPencil();
        mSdkView.invalidate();
    }

    public void hFieldCopy(MenuItem pItem) {
        SavePlayfieldRunnable lSavePlayfieldRunnable;

        lSavePlayfieldRunnable = new SavePlayfieldRunnable(mContext, mGame.xPlayField());
        SudokuApp.getInstance().xExecutor.execute(lSavePlayfieldRunnable);
        mGame.xPlayFieldCopy();
        mSdkView.invalidate();
    }

    public void hFieldDelete(MenuItem pItem){
        mData.xDeletePlayField(mGame.xPlayField().xFieldId());
        mGame.xDeleteCurrentPlayField();
        mSdkView.invalidate();
    }

    public void hFieldSwitch(MenuItem pItem){
        AlertDialog lDialog;
        AlertDialog.Builder lBuilder;
        final String[] lItems;
        List<PlayField> lFields;
        int lCountIn;
        int lCountOut;
        int lId;

        lFields = mGame.xPlayFields();
        lItems = new String[lFields.size() - 1];

        lCountOut = 0;
        for (lCountIn = 0; lCountIn < lFields.size(); lCountIn++){
            lId = lFields.get(lCountIn).xFieldId();
            if (lId != mGame.xPlayField().xFieldId()){
                lItems[lCountOut] = String.valueOf(lId);
                lCountOut++;
            }
        }
        if (lItems.length > 1){
            lBuilder = new AlertDialog.Builder(this);
            lBuilder.setItems(lItems, (dialog, pChoice) -> sSwitchPlayField(Integer.parseInt(lItems[pChoice])));
            lDialog = lBuilder.create();
            lDialog.show();
        } else {
            sSwitchPlayField(Integer.parseInt(lItems[0]));
        }
    }

    private void sSwitchPlayField(int pNewId){
        SavePlayfieldRunnable lSavePlayfieldRunnable;

        lSavePlayfieldRunnable = new SavePlayfieldRunnable(mContext, mGame.xPlayField());
        SudokuApp.getInstance().xExecutor.execute(lSavePlayfieldRunnable);
        mGame.xSwitchPlayField(pNewId);
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

        if (mGame.xGameStatus() == SudokuGame.cStatusPlay || mGame.xGameStatus() == SudokuGame.cStatusSolved) {
            switch (mGame.xDifficulty()) {
                case SudokuGameBase.cDiffEasy: {
                    lHeader = getString(R.string.mnu_level_easy);
                    break;
                }
                case SudokuGameBase.cDiffMedium: {
                    lHeader = getString(R.string.mnu_level_medium);
                    break;
                }
                case SudokuGameBase.cDiffHard: {
                    lHeader = getString(R.string.mnu_level_hard);
                    break;
                }
                default: {
                    lHeader = getString(R.string.app_name);
                    break;
                }
            }
            mHeader = lHeader;
        } else {
            mHeader = getString(R.string.app_name);
        }
    }
}
