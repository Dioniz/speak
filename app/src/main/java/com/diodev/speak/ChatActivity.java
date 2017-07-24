package com.diodev.speak;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.diodev.speak.adapters.ChatAdapter;
import com.diodev.speak.models.Message;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;


public class ChatActivity extends AppCompatActivity {

    private static final String ENDPOINT = "http://10.246.64.123:3000";
    private Socket mSocket;
    private Boolean isConnected = false;
    private String mUsername, mGroupId;
    private RecyclerView mChatRV;
    private ChatAdapter mChatAdapter;
    private List<Message> mMessages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        
        initGroupRoom();
        initComponents();
        initListeners();
    }
    private void initGroupRoom() {
        Bundle bundle = getIntent().getExtras();
        mUsername = bundle.getString(MainActivity.USERNAME);
        mGroupId = bundle.getString(HomeActivity.CHAT);
        initChatConnection();
    }

    private void initChatConnection() {
        try {
            mSocket = IO.socket(ENDPOINT);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        mSocket.on(Socket.EVENT_CONNECT, onConnect);
        mSocket.on(Socket.EVENT_DISCONNECT, onDisconnect);
        mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
        mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
        mSocket.on("new message", onNewMessage);
        //mSocket.on("user joined", onUserJoined);
        //mSocket.on("join room", onJoinRoom);
        //mSocket.on("user left", onUserLeft);
        mSocket.on("typing", onTyping);
        mSocket.on("stop typing", onStopTyping);

        mSocket.connect();
    }

    private void initComponents() {
        mMessages = new ArrayList<>();
        mMessages.add(new Message("1", "fran", "hola que hace", new Date(), Message.TYPE_MESSAGE));
        mMessages.add(new Message("1", "fran", "Hola? que aaaaceeee", new Date(), Message.TYPE_MESSAGE));
        mChatAdapter = new ChatAdapter(mMessages);
        mChatRV = (RecyclerView) findViewById(R.id.rv_chat_activity);
        mChatRV.setLayoutManager(new LinearLayoutManager(this));
        mChatRV.setAdapter(mChatAdapter);
    }

    private void initListeners() {
        final EditText emojiEditText = (EditText) findViewById(R.id.emojicon_edit_text);
        TextView sendTV = (TextView) findViewById(R.id.tv_send);
        sendTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attempSend(emojiEditText);
            }
        });
        emojiEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int id, KeyEvent event) {
                if (id == R.id.send || id == EditorInfo.IME_NULL) {
                    attempSend(emojiEditText);
                    return true;
                }
                return false;
            }
        });
        emojiEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (null == mUsername) return;
                if (!mSocket.connected()) return;

                mSocket.emit("typing", mGroupId);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }


    private void attempSend(EditText emojiEditText){
        String m = emojiEditText.getText().toString().trim();
        Message message = new Message(mGroupId, mUsername, m, new Date(), Message.TYPE_MESSAGE_OWN);
        emojiEditText.setText("");
        mSocket.emit("new message", message.getMessage(), mGroupId);
        mMessages.add(message);
        mChatAdapter.notifyItemInserted(mMessages.size() - 1);
        scrollToBottom();
    }

    private void scrollToBottom() {
        mChatRV.scrollToPosition(mChatAdapter.getItemCount() - 1);
    }

    // SOCKET.IO EVENTS
    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(!isConnected) {
                        Toast.makeText(getApplicationContext(), "Conected", Toast.LENGTH_LONG).show();
                        mSocket.emit("add user", mUsername, mGroupId);
                        mSocket.emit("join room", mGroupId);
                        isConnected = true;
                    }
                }
            });
        }
    };

    private Emitter.Listener onDisconnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.i("SOCKET.IO EVENT", "diconnected");
                    isConnected = false;
                    Toast.makeText(getApplicationContext(), "Disconnected", Toast.LENGTH_LONG).show();
                }
            });
        }
    };

    private Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.e("SOCKET.IO EVENT", "Error connecting" + args[0].toString());
                    Toast.makeText(getApplicationContext(), "Failed to connect", Toast.LENGTH_LONG).show();
                }
            });
        }
    };

    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String username;
                    String m;
                    try {
                        username = data.getString("username");
                        m = data.getString("message");

                        Message message = new Message(mGroupId, username, m, new Date(), Message.TYPE_MESSAGE);
                        mMessages.add(message);
                        mChatAdapter.notifyItemInserted(mMessages.size() - 1);
                        scrollToBottom();
                    } catch (JSONException e) {
                        Log.e("SOCKET.IO EVENT", e.getMessage());
                        return;
                    }

                    /*removeTyping(username);
                    addMessage(username, message);*/
                }
            });
        }
    };

    /*private Emitter.Listener onUserJoined = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String username;
                    int numUsers;
                    try {
                        username = data.getString("username");
                        numUsers = data.getInt("numUsers");
                    } catch (JSONException e) {
                        Log.e(TAG, e.getMessage());
                        return;
                    }

                    addLog(getResources().getString(R.string.message_user_joined, username));
                    addParticipantsLog(numUsers);
                }
            });
        }
    };*/

    /*private Emitter.Listener onUserLeft = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String username;
                    int numUsers;
                    try {
                        username = data.getString("username");
                        numUsers = data.getInt("numUsers");
                    } catch (JSONException e) {
                        Log.e(TAG, e.getMessage());
                        return;
                    }

                    addLog(getResources().getString(R.string.message_user_left, username));
                    addParticipantsLog(numUsers);
                    removeTyping(username);
                }
            });
        }
    };*/

    private Emitter.Listener onTyping = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String username;
                    try {
                        username = data.getString("username");
                    } catch (JSONException e) {
                        Log.e("SOCKET.IO EVENT", e.getMessage());
                        return;
                    }
                    addTyping(username);
                }
            });
        }
    };

    private Emitter.Listener onStopTyping = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String username;
                    try {
                        username = data.getString("username");
                    } catch (JSONException e) {
                        Log.e("SOCKET.IO EVENT", e.getMessage());
                        return;
                    }
                    removeTyping(username);
                }
            });
        }
    };

    private Runnable onTypingTimeout = new Runnable() {
        @Override
        public void run() {
            /*if (!mTyping) return;

            mTyping = false;
            mSocket.emit("stop typing", mGroupId);*/
        }
    };

    private void addTyping(String username) {
        /*mMessages.add(new Message(mGroupId, username, "escribiendo...", new Date(), Message.TYPE_ACTION));
        mChatAdapter.notifyItemInserted(mMessages.size() - 1);
        scrollToBottom();*/
        TextView actionTV = (TextView) findViewById(R.id.tv_actions);
        actionTV.setText(username + " escribiendo...");

    }

    private void removeTyping(String username) {
        /*for (int i = mMessages.size() - 1; i >= 0; i--) {
            Message message = mMessages.get(i);
            if (message.getType() == Message.TYPE_ACTION && message.getUsername().equals(username)) {
                mMessages.remove(i);
                mChatAdapter.notifyItemRemoved(i);
            }
        }*/
        TextView actionTV = (TextView) findViewById(R.id.tv_actions);
        actionTV.setText("");

    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        mSocket.emit("user left", mGroupId);

        mSocket.disconnect();

        mSocket.off(Socket.EVENT_CONNECT, onConnect);
        mSocket.off(Socket.EVENT_DISCONNECT, onDisconnect);
        mSocket.off(Socket.EVENT_CONNECT_ERROR, onConnectError);
        mSocket.off(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
        mSocket.off("new message", onNewMessage);
        //mSocket.off("user joined", onUserJoined);
        //mSocket.off("user left", onUserLeft);
        mSocket.off("typing", onTyping);
        mSocket.off("stop typing", onStopTyping);
    }
}
