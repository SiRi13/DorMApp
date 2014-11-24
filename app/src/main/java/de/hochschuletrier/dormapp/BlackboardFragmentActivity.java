package de.hochschuletrier.dormapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.EditText;

import de.hochschuletrier.dormapp.common.Constants;

public class BlackboardFragmentActivity extends FragmentActivity {

    public static final String BLACKBOARD_EDIT_TEXT = "de.hstrier.livingcommunity.blackboard_edit_text";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_blackboard_edit);
        Intent editIntent = getIntent();
        final EditText editText = (EditText) findViewById(R.id.fragment_blackboard_editText);
        editText.setText(editIntent.getStringExtra(BLACKBOARD_EDIT_TEXT).toString());
        editIntent.putExtra(BLACKBOARD_EDIT_TEXT, "new text");
        setResult(Constants.ACTIVITY_RESULT_OK, editIntent);
        
    }

}
