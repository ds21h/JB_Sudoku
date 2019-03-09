package jb.sudoku;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

class GameScrambler {
    private final int[][] mPermutations = new int[][]{{0, 1, 2}, {0, 2, 1}, {1, 0, 2}, {1, 2, 0}, {2, 0, 1}, {2, 1, 0}};
    private String mGame;
    private Random mRandom = new Random();

    GameScrambler(String pGame) {
        mGame = pGame;
    }

    private Cell[] sGame() {
        Cell[] lCells = new Cell[81];
        int lCount;
        String lPos;

        for (lCount = 0; lCount < lCells.length; lCount++) {
            lPos = mGame.substring(lCount, lCount + 1);
            lCells[lCount] = new Cell();
            if (lPos.charAt(0) >= '1' && lPos.charAt(0) <= '9') {
                lCells[lCount].xValue(Integer.valueOf(lPos));
                lCells[lCount].xFixed(true);
            }
        }
        return lCells;
    }

    Cell[] xScrambleGame() {
        Cell[] lCells;

        lCells = sGame();
        sScrambleRows(lCells);
        sScrambleSegmentRows(lCells);
        sScrambleColumns(lCells);
        sScrambleSegmentColumns(lCells);
        sScrambleRotate(lCells);
        sDigitExchange(lCells);

        return lCells;
    }

    private void sScrambleRows(Cell[] pCells) {
        int lPerm;
        Cell[] lSave = new Cell[27];
        int[] lScramble;
        int lCount;
        int lRow;
        int lColumn;
        int lRowStart;
        int lScrambleStart;
        int lSegmentRow;
        int lSegmentRowStart;

        for (lSegmentRow = 0; lSegmentRow < 3; lSegmentRow++) {
            lSegmentRowStart = lSegmentRow * 27;

            for (lCount = 0; lCount < lSave.length; lCount++) {
                lSave[lCount] = pCells[lSegmentRowStart + lCount];
            }
            lPerm = mRandom.nextInt(6);
            lScramble = mPermutations[lPerm];
            for (lRow = 0; lRow < 3; lRow++) {
                lRowStart = lSegmentRowStart + (lRow * 9);
                lScrambleStart = lScramble[lRow] * 9;
                for (lColumn = 0; lColumn < 9; lColumn++) {
                    pCells[lRowStart + lColumn] = lSave[lScrambleStart + lColumn];
                }
            }
        }
    }

    private void sScrambleSegmentRows(Cell[] pCells) {
        Cell[] lSave = new Cell[81];
        int lCount;
        int lPerm;
        int[] lScramble;
        int lSegmentRow;
        int lStartResult;
        int lStartSave;
        int lRow;
        int lColumn;
        int lRowStart;
        int lScrambleStart;

        for (lCount = 0; lCount < lSave.length; lCount++) {
            lSave[lCount] = pCells[lCount];
        }
        lPerm = mRandom.nextInt(6);
        lScramble = mPermutations[lPerm];
        for (lSegmentRow = 0; lSegmentRow < 3; lSegmentRow++) {
            lStartResult = lSegmentRow * 27;
            lStartSave = lScramble[lSegmentRow] * 27;
            for (lRow = 0; lRow < 3; lRow++) {
                lRowStart = lStartResult + (lRow * 9);
                lScrambleStart = lStartSave + (lRow * 9);
                for (lColumn = 0; lColumn < 9; lColumn++) {
                    pCells[lRowStart + lColumn] = lSave[lScrambleStart + lColumn];
                }
            }
        }
    }

    private void sScrambleColumns(Cell[] pCells) {
        int lPerm;
        Cell[] lSave = new Cell[27];
        int[] lScramble;
        int lCount;
        int lRow;
        int lColumn;
        int lRowStart;
        int lColumnStart;
        int lScrambleStart;
        int lSegmentColumn;

        for (lSegmentColumn = 0; lSegmentColumn < 3; lSegmentColumn++){
            lCount = 0;
            for (lColumn = 0; lColumn < 3; lColumn++) {
                lColumnStart = (lSegmentColumn * 3) + lColumn;
                for (lRow = 0; lRow < 9; lRow++) {
                    lSave[lCount] = pCells[(lRow * 9) + lColumnStart];
                    lCount++;
                }
            }
            lPerm = mRandom.nextInt(6);
            lScramble = mPermutations[lPerm];
            for (lColumn = 0; lColumn < 3; lColumn++) {
                lColumnStart = (lSegmentColumn * 3) + lColumn;
                lScrambleStart = lScramble[lColumn] * 9;
                for (lRow = 0; lRow < 9; lRow++) {
                    lRowStart = (lRow * 9);
                    pCells[lRowStart + lColumnStart] = lSave[lScrambleStart + lRow];
                }
            }
        }
    }

    private void sScrambleSegmentColumns(Cell[] pCells) {
        Cell[] lSave = new Cell[81];
        int lCount;
        int lPerm;
        int[] lScramble;
        int lSegmentColumn;
        int lStartResultColumn;
        int lStartSaveColumn;
        int lRow;
        int lColumn;
        int lRowStart;
        int lScrambleStart;

        for (lCount = 0; lCount < lSave.length; lCount++) {
            lSave[lCount] = pCells[lCount];
        }
        lPerm = mRandom.nextInt(6);
        lScramble = mPermutations[lPerm];
        for (lSegmentColumn = 0; lSegmentColumn < 3; lSegmentColumn++) {
            lStartResultColumn = lSegmentColumn * 3;
            lStartSaveColumn = lScramble[lSegmentColumn] * 3;
            for (lRow = 0; lRow < 9; lRow++) {
                lRowStart = (lRow * 9) + lStartResultColumn;
                lScrambleStart = (lRow * 9) + lStartSaveColumn;
                for (lColumn = 0; lColumn < 3; lColumn++) {
                    pCells[lRowStart + lColumn] = lSave[lScrambleStart + lColumn];
                }
            }
        }
    }

    private void sScrambleRotate(Cell[] pCells) {
        Cell[] lSave = new Cell[81];
        int lCount;
        int lRowResult;
        int lColumnResult;
        int lRowSave;
        int lColumnSave;
        int lNumber;

        lNumber = mRandom.nextInt(4);
        while (lNumber > 0){
            for (lCount = 0; lCount < lSave.length; lCount++) {
                lSave[lCount] = pCells[lCount];
            }
            for (lRowResult = 0; lRowResult < 9; lRowResult++){
                lColumnSave = lRowResult;
                for (lColumnResult = 0; lColumnResult < 9; lColumnResult++){
                    lRowSave = 8 - lColumnResult;
                    pCells[(lRowResult * 9) + lColumnResult ] = lSave[(lRowSave * 9) + lColumnSave];
                }
            }

            lNumber--;
        }
    }

    private void sDigitExchange(Cell[] pCells){
        List<Integer> lDigits;
        int lCount;
        int[] lNewDigits = new int[10];
        int lPos;

        lDigits = new ArrayList<>();
        for (lCount = 1; lCount <= 9; lCount++){
            lDigits.add(lCount);
        }
        lNewDigits[0] = 0;
        for (lCount = 1; lCount <= 9; lCount++){
            lPos = mRandom.nextInt(lDigits.size());
            lNewDigits[lCount] = lDigits.get(lPos);
            lDigits.remove(lPos);
        }
        for (lCount = 0; lCount < pCells.length; lCount++){
            lPos = pCells[lCount].xValue();
            if (lPos > 0){
                pCells[lCount].xValue(lNewDigits[lPos]);
            }
        }
    }
}
