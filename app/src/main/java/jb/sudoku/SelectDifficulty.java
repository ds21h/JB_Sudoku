package jb.sudoku;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Toast;

public class SelectDifficulty extends Activity {
    static final String cLevel = "Level";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_difficulty_layout);
    }

    public void hOk_Click(View pView){
        Intent lInt;
        Bundle lBundle;
        RadioButton[] lButtons = new RadioButton[5];
        int lCount;

        lButtons[0] = findViewById(R.id.rdoVeryEasy);
        lButtons[1] = findViewById(R.id.rdoEasy);
        lButtons[2] = findViewById(R.id.rdoMedium);
        lButtons[3] = findViewById(R.id.rdoHard);
        lButtons[4] = findViewById(R.id.rdoVeryHard);
        for (lCount = 0; lCount < lButtons.length; lCount++){
            if (lButtons[lCount].isChecked()){
                break;
            }
        }
        if (lCount < lButtons.length){
            lInt = new Intent();
            lBundle = new Bundle();
            lBundle.putInt(cLevel, lCount);
            lInt.putExtras(lBundle);
            setResult(RESULT_OK, lInt);
            finish();
        } else {
            Toast.makeText(this, R.string.msg_no_selection, Toast.LENGTH_SHORT).show();
        }
    }
}
