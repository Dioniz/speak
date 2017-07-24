package com.diodev.speak;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    public static final String USERNAME = "USERNAME";

    private EditText mUsernameET;
    private Button mValidateBTN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*LinearLayout llRoot = (LinearLayout) findViewById(R.id.ll_controls);
        EmojiconEditText emojiET = (EmojiconEditText) findViewById(R.id.emojicon_edit_text);
        ImageView btn = (ImageView) findViewById(R.id.emoji_btn);

        EmojIconActions emojIcon=new EmojIconActions(this, llRoot, emojiET, btn);

        emojIcon.setUseSystemEmoji(true);
        emojIcon.ShowEmojIcon();*/

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        initComponents();
    }

    private void initComponents() {
        mUsernameET = (EditText) findViewById(R.id.et_username);
        mValidateBTN = (Button) findViewById(R.id.btn_validate);
    }

    public void validate(View v) {
        String username = mUsernameET.getText().toString();
        if (username.length() > 0 && username.length() < 25) {
            Intent i = new Intent(this, HomeActivity.class);
            i.putExtra(USERNAME, username);
            startActivity(i);
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
