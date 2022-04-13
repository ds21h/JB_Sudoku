package jb.sudoku;

import android.app.Application;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SudokuApp extends Application {
    private static SudokuApp mSudokuApp;
    ExecutorService xExecutor;

    static SudokuApp getInstance(){
        return mSudokuApp;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mSudokuApp = this;
        xExecutor = Executors.newCachedThreadPool();
    }
}
