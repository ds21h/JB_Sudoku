package jb.sudoku;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Toast;

public class MainSudoku extends Activity {
    private final Context mContext = this;
    private SudokuView mSdkView;
    private SudokuGame mGame;
    private Data mData;
    private int mSelectedCell = 0;
    private boolean mSetup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mGame = new SudokuGame();
        mSdkView = new SudokuView(this);
        mSdkView.setGame(mGame);
        mSdkView.setIntSudokuView(new SudokuView.intSudokuView() {
            @Override
            public void onSolved() {
                Toast.makeText(mContext, R.string.msg_solved, Toast.LENGTH_SHORT).show();
            }
        });
        mData = Data.getInstance(mContext);
        addContentView(mSdkView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    @Override
    protected void onResume() {
        GameData lGameData;

        super.onResume();

        lGameData = mData.xGameData();
        mGame.xGameData(lGameData);
    }

    @Override
    protected void onPause() {
        super.onPause();

        mData.xSaveGame(mGame.xGameData());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu pMenu) {
        super.onCreateOptionsMenu(pMenu);
        getMenuInflater().inflate(R.menu.sudoku_menu, pMenu);
        return true;
    }

    public void hSetup(MenuItem pItem){
        mGame.xStartSetUp();
        mSdkView.invalidate();
    }

    public void hStore(MenuItem pItem){
        mGame.xEndSetup();
        mSdkView.invalidate();
    }

    public void hSolve(MenuItem pItem){
        mGame.xSolve();
        mSdkView.invalidate();
    }

    public void hGenerate(MenuItem pItem){
        mGame.xGenerate();
        mSdkView.invalidate();
    }

    public void hFillPencil(MenuItem pItem){
        mGame.xFillPencil();
        mSdkView.invalidate();
    }

    public void hClearPencil(MenuItem pItem){
        mGame.xClearPencil();
        mSdkView.invalidate();
    }
}
