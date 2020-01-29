package jb.sudoku;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class SudokuView extends View {
    interface intSudokuView {
        void onSolved();
    }

    private final int cMargin = 10;
    private final int cButtonMargin = 20;
    private Paint mPaint = new Paint();
    private final int cColorBackNorm = Color.argb(255, 255, 255, 245);
    private final int cColorBackFixed = Color.argb(255, 230, 230, 230);
    private final int cColorBackRange = Color.argb(255, 255, 255, 150);
    private final int cColorBackValue = Color.argb(255, 150, 255, 255);
    private final int cColorForeNorm = Color.BLACK;
    @SuppressWarnings("FieldCanBeLocal")
    private final int cColorForeConflict = Color.RED;
    private final int cColorButtonNorm = Color.argb(255, 255, 255, 245);
    private final int cColorButtonFull = Color.argb(255, 190, 190, 190);
    private final int cColorButtonPencil = Color.argb(255, 200, 255, 200);
    private final int cAlphaNorm = 255;
    private final int cAlphaDisabled = 100;
    private final int cStrokeNone = 0;
    private final int cStrokeNarrow = 3;
    @SuppressWarnings("FieldCanBeLocal")
    private final int cStrokeWide = 8;
    @SuppressWarnings("FieldCanBeLocal")
    private final int cStrokeSelection = 12;
    private float mCellSize;
    private float mButtonSize;

    private SudokuGame mGame = null;
    private RectF[] mButton = new RectF[10];
    private boolean mButtonsEnabled;
    private boolean mEnabled;

    private intSudokuView mIntView = null;

    public SudokuView(Context pContext) {
        super(pContext);
        sInit();
    }

    public SudokuView(Context pContext, AttributeSet pAttrSet) {
        super(pContext, pAttrSet);
        sInit();
    }

    private void sInit() {
        mEnabled = true;
        mCellSize = 0;
        mButtonsEnabled = false;

        mPaint.setTextAlign(Paint.Align.CENTER);
    }

    public void setGame(SudokuGame pGame) {
        mGame = pGame;
    }

    /**
     * Set listener for onSolved
     *
     * @param pIntView intSudokuView   Listener
     */
    public void setIntSudokuView(intSudokuView pIntView) {
        mIntView = pIntView;
    }

    @Override
    public void setEnabled(boolean pEnabled) {
        super.setEnabled(pEnabled);

        mEnabled = pEnabled;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent pEvent) {
        float lDispl;
        int lColumn;
        int lRow;
        boolean lCellSelect;
        RectF lRect;
        int lCount;

        if (mGame != null && mEnabled){
            if (pEvent.getAction() == MotionEvent.ACTION_DOWN && mCellSize > 0) {
                lCellSelect = false;
                lDispl = pEvent.getX() - cMargin;
                if (lDispl >= 0) {
                    lColumn = (int) (lDispl / mCellSize);
                    if (lColumn < 9) {
                        lDispl = pEvent.getY() - cMargin;
                        if (lDispl >= 0) {
                            lRow = (int) (lDispl / mCellSize);
                            if (lRow < 9) {
                                lCellSelect = true;
                                mGame.xPlayField().xSelectionRow(lRow);
                                mGame.xPlayField().xSelectionColumn(lColumn);
                                invalidate();
                            }
                        }
                    }
                }
                if (!lCellSelect) {
                    if (mButtonsEnabled){
                        lRect = new RectF(pEvent.getX(), pEvent.getY(), pEvent.getX(), pEvent.getY());
                        if (mButton[0].contains(lRect)) {
                            if (!(mGame.xGameStatus() == SudokuGame.cStatusSetup)) {
                                mGame.xPlayField().xPencilFlip();
                                invalidate();
                            }
                        } else {
                            for (lCount = 1; lCount <= 9; lCount++) {
                                if (mButton[lCount].contains(lRect)) {
                                    mGame.xProcessDigit(lCount);
                                    if (mGame.xGameStatus() == SudokuGame.cStatusSolved) {
                                        if (mIntView != null) {
                                            mIntView.onSolved();
                                        }
                                    }
                                    invalidate();
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
        return true;
    }

    @Override
    public void onDraw(Canvas pCanvas) {
        super.onDraw(pCanvas);

        if (mGame != null){
            mCellSize = (getWidth() - (2 * cMargin)) / 9F;
            mButtonSize = (getWidth() / 6F) - (2 * cButtonMargin);
            sDrawPlayField(pCanvas);
            if (mGame.xGameStatus() == SudokuGame.cStatusPlay || mGame.xGameStatus() == SudokuGame.cStatusSetup){
                sDrawButtons(pCanvas);
                mButtonsEnabled = true;
            } else {
                mButtonsEnabled = false;
            }
        }
    }

    private void sDrawPlayField(Canvas pCanvas) {
        int lRow;
        int lColumn;
        int lPencilRow;
        int lPencilColumn;
        float lRowMargin;
        float lColumnMargin;
        RectF lRect;
        PlayCell lCell;
        float lPencilCellSize;
        int lPencil;
        int lAlpha;

        if (mEnabled){
            lAlpha = cAlphaNorm;
        } else {
            lAlpha = cAlphaDisabled;
        }
        lPencilCellSize = mCellSize / 3;
        for (lRow = 0; lRow < 9; lRow++) {
            lRowMargin = (lRow * mCellSize) + cMargin;
            for (lColumn = 0; lColumn < 9; lColumn++) {
                lColumnMargin = (lColumn * mCellSize) + cMargin;
                lCell = mGame.xPlayField().xCells()[(lRow * 9) + lColumn];
                lRect = new RectF(lColumnMargin, lRowMargin, lColumnMargin + mCellSize, lRowMargin + mCellSize);

                //      Background
                mPaint.setStrokeWidth(cStrokeNone);
                mPaint.setStyle(Paint.Style.FILL);
                if (mGame.xSelectionValue(lRow, lColumn)) {
                    mPaint.setColor(cColorBackValue);
                } else {
                    if (lCell.xFixed()) {
                        mPaint.setColor(cColorBackFixed);
                    } else {
                        if (mGame.xSelectionRange(lRow, lColumn)) {
                            mPaint.setColor(cColorBackRange);
                        } else {
                            mPaint.setColor(cColorBackNorm);
                        }
                    }
                }
                mPaint.setAlpha(lAlpha);
                pCanvas.drawRect(lRect, mPaint);

                //      Cell outline
                mPaint.setStrokeWidth(cStrokeNarrow);
                mPaint.setStyle(Paint.Style.STROKE);
                mPaint.setColor(cColorForeNorm);
                mPaint.setAlpha(lAlpha);
                pCanvas.drawRect(lRect, mPaint);

                //      Cell value
                mPaint.setStyle(Paint.Style.FILL);
                if (lCell.xValue() > 0) {
                    //  Normal
                    mPaint.setTextSize(mCellSize * 0.8F);
                    if (lCell.xConflict()) {
                        mPaint.setColor(cColorForeConflict);
                    } else {
                        mPaint.setColor(cColorForeNorm);
                    }
                    mPaint.setAlpha(lAlpha);
                    pCanvas.drawText(String.valueOf(lCell.xValue()), lRect.centerX(), lRect.bottom - mPaint.getFontMetrics().descent, mPaint);
                } else {
                    // Pencil
                    mPaint.setTextSize(lPencilCellSize);
                    mPaint.setColor(cColorForeNorm);
                    mPaint.setAlpha(lAlpha);
                    for (lPencilRow = 0; lPencilRow < 3; lPencilRow++) {
                        for (lPencilColumn = 0; lPencilColumn < 3; lPencilColumn++) {
                            lPencil = (lPencilRow * 3) + lPencilColumn + 1;
                            if (lCell.xPencil(lPencil)) {
                                pCanvas.drawText(String.valueOf(lPencil), lRect.left + (lPencilColumn * lPencilCellSize) + (lPencilCellSize / 2), lRect.top + ((lPencilRow + 1) * lPencilCellSize) - (mPaint.getFontMetrics().descent / 2), mPaint);
                            }
                        }
                    }
                }
            }
        }
        //      Thicker lines around segments
        mPaint.setStrokeWidth(cStrokeWide);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(cColorForeNorm);
        mPaint.setAlpha(lAlpha);
        pCanvas.drawLine(cMargin + (mCellSize * 3), cMargin, cMargin + (mCellSize * 3), (mCellSize * 9) + cMargin, mPaint);
        pCanvas.drawLine(cMargin + (mCellSize * 6), cMargin, cMargin + (mCellSize * 6), (mCellSize * 9) + cMargin, mPaint);
        pCanvas.drawLine(cMargin, cMargin + (mCellSize * 3), (mCellSize * 9) + cMargin, cMargin + (mCellSize * 3), mPaint);
        pCanvas.drawLine(cMargin, cMargin + (mCellSize * 6), (mCellSize * 9) + cMargin, cMargin + (mCellSize * 6), mPaint);

        mPaint.setStrokeWidth(cStrokeSelection);
        pCanvas.drawRect((mGame.xPlayField().xSelectionColumn() * mCellSize) + cMargin, (mGame.xPlayField().xSelectionRow() * mCellSize) + cMargin, ((mGame.xPlayField().xSelectionColumn() + 1) * mCellSize) + cMargin, ((mGame.xPlayField().xSelectionRow() + 1) * mCellSize) + cMargin, mPaint);
    }

    private void sDrawButtons(Canvas pCanvas) {
        RectF lRectF;
        int lCount;
        float lCountSize;
        int lAlpha;

        if (mEnabled){
            lAlpha = cAlphaNorm;
        } else {
            lAlpha = cAlphaDisabled;
        }

        lCountSize = mCellSize / 3;
        for (lCount = 1; lCount <= 9; lCount++) {
            lRectF = sRectButton(lCount);
            mButton[lCount] = lRectF;
            mPaint.setStrokeWidth(cStrokeNone);
            mPaint.setStyle(Paint.Style.FILL);
            if (mGame.xPlayField().xDigitCount(lCount) < 9) {
                mPaint.setColor(cColorButtonNorm);
            } else {
                mPaint.setColor(cColorButtonFull);
            }
            mPaint.setAlpha(lAlpha);
            pCanvas.drawRect(lRectF, mPaint);

            mPaint.setColor(cColorForeNorm);
            mPaint.setStrokeWidth(cStrokeNarrow);
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setAlpha(lAlpha);
            pCanvas.drawRect(lRectF, mPaint);

            mPaint.setColor(cColorForeNorm);
            mPaint.setTextSize(mCellSize);
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setAlpha(lAlpha);
            pCanvas.drawText(String.valueOf(lCount), lRectF.centerX(), lRectF.bottom - mPaint.getFontMetrics().descent, mPaint);

            mPaint.setTextSize(lCountSize);
            pCanvas.drawText(String.valueOf(mGame.xPlayField().xDigitCount(lCount)), lRectF.right - (lCountSize / 2), lRectF.top + lCountSize, mPaint);
        }
        lRectF = sRectButton(99);
        mButton[0] = lRectF;
        if (!(mGame.xGameStatus() == SudokuGame.cStatusSetup)) {
            mPaint.setStrokeWidth(cStrokeNone);
            mPaint.setStyle(Paint.Style.FILL);
            if (mGame.xPlayField().xPencilMode()) {
                mPaint.setColor(cColorButtonPencil);
            } else {
                mPaint.setColor(cColorButtonNorm);
            }
            mPaint.setAlpha(lAlpha);
            pCanvas.drawRect(lRectF, mPaint);

            mPaint.setColor(cColorForeNorm);
            mPaint.setStrokeWidth(cStrokeNarrow);
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setAlpha(lAlpha);
            pCanvas.drawRect(lRectF, mPaint);

            mPaint.setColor(cColorForeNorm);
            mPaint.setTextSize(mCellSize);
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setAlpha(lAlpha);
            pCanvas.drawText("P", lRectF.centerX(), lRectF.bottom - mPaint.getFontMetrics().descent, mPaint);
        }
    }

    private RectF sRectButton(int pButton) {
        RectF lRect;

        lRect = new RectF();
        if (pButton > 0 && pButton <= 5) {
            lRect.left = cButtonMargin + ((pButton - 1) * (mButtonSize + (2 * cButtonMargin)));
            lRect.top = getHeight() - (3 * cButtonMargin) - (2 * mButtonSize);
            lRect.right = lRect.left + mButtonSize;
            lRect.bottom = lRect.top + mButtonSize;
        } else {
            if (pButton <= 9) {
                lRect.left = cButtonMargin + ((pButton - 6) * (mButtonSize + (2 * cButtonMargin)));
                lRect.top = getHeight() - cButtonMargin - (mButtonSize);
                lRect.right = lRect.left + mButtonSize;
                lRect.bottom = lRect.top + mButtonSize;
            } else {
                if (pButton == 99) {
                    lRect.left = getWidth() - cButtonMargin - mButtonSize;
                    lRect.top = getHeight() - cButtonMargin - mButtonSize;
                    lRect.right = lRect.left + mButtonSize;
                    lRect.bottom = lRect.top + mButtonSize;
                }
            }
        }
        return lRect;
    }
}
