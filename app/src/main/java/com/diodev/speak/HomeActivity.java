package com.diodev.speak;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.View;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.diodev.speak.adapters.GroupAdapter;

import java.util.ArrayList;
import java.util.List;


public class HomeActivity extends AppCompatActivity {

    public static final String CHAT = "CHAT";
    public static final String GENERAL_CHAT = "GENERAL_CHAT";
    public static final String GENERAL_CHAT_ID = "1";
    private TextView mGeneralTV, mAddTV;
    private RecyclerView mGroupsRV;
    private GroupAdapter mGroupAdapter;
    private List<String> mGroups;
    private String mUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        iniUserData();

        initComponents();
    }

    private void iniUserData() {
        Bundle bundle = getIntent().getExtras();
        mUsername = bundle.getString(MainActivity.USERNAME);
    }

    private void initComponents() {
        mGeneralTV = (TextView) findViewById(R.id.tv_general);
        mGeneralTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startGeneralChannel();
                startChannel(GENERAL_CHAT_ID);
            }
        });

        mAddTV = (TextView) findViewById(R.id.btn_add_group);
        mAddTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buildAlertDialog();
            }
        });

        mGroups = new ArrayList<>();
        mGroupAdapter = new GroupAdapter(mGroups, new GroupAdapter.IGroup() {
            @Override
            public void onClick(String groupName) {
                startChannel(groupName);
            }
        });
        mGroupsRV = (RecyclerView) findViewById(R.id.rv_groups);
        mGroupsRV.setLayoutManager(new LinearLayoutManager(this));
        mGroupsRV.setAdapter(mGroupAdapter);
    }

    private void startGeneralChannel() {
        Intent i = new Intent(this, ChatActivity.class);
        i.putExtra(MainActivity.USERNAME, mUsername);
        i.putExtra(GENERAL_CHAT, GENERAL_CHAT_ID);
        startActivity(i);
    }

    private void startChannel(String groupId) {
        Intent i = new Intent(this, ChatActivity.class);
        i.putExtra(MainActivity.USERNAME, mUsername);
        i.putExtra(CHAT, groupId);
        startActivity(i);
    }

    private void buildAlertDialog() {
        MaterialDialog builder = new MaterialDialog.Builder(this)
                .title("Add Group")
                .widgetColor(getResources().getColor(R.color.primary_text))
                .inputRangeRes(2, 20, R.color.colorError)
                .inputType(InputType.TYPE_CLASS_TEXT)
                .input("Add group name", "", new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {
                        mGroups.add(input.toString());
                        mGroupAdapter.notifyDataSetChanged();
                    }
                }).negativeText("Cancel")
                .show();
    }
}
