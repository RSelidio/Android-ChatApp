package com.example.itmanagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.text.method.ScrollingMovementMethod;
import android.util.AttributeSet;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.itmanagement.adapters.AdapterChat;
import com.example.itmanagement.models.ModelChat;
import com.example.itmanagement.models.ModelUser;
import com.example.itmanagement.notificationpackage.APIServices;
import com.example.itmanagement.notificationpackage.Data;
import com.example.itmanagement.notificationpackage.Response;
import com.example.itmanagement.notificationpackage.Sender;
import com.example.itmanagement.notificationpackage.Token;
import com.example.itmanagement.notificationpackage.Userretrofit;
import com.google.android.gms.common.api.Api;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;

public class ChatActivity extends AppCompatActivity {

    Toolbar toolbar;
    RecyclerView recyclerView;
    ImageView profileIv;
    TextView nameTv, userStatusTv;
    EditText messageEt;
    ImageButton sendBtn;

    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference userDBref;

    ValueEventListener seenListener;
    DatabaseReference userRefForSeen;

    List<ModelChat> chatList;
    AdapterChat adapterChat;

    String hisUID;
    String myUid;
    String hisImage;

    APIServices apiServices;
    boolean notify = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("");
        recyclerView = findViewById(R.id.chat_recycleView);
        profileIv = findViewById(R.id.profileIv);
        nameTv = findViewById(R.id.nameTv);
        userStatusTv = findViewById(R.id.userStatusTv);
        messageEt = findViewById(R.id.messageET);
        sendBtn = findViewById(R.id.sendBtn);


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        apiServices = Userretrofit.getRetrofit("https://fcm.googleapis.com/").create(APIServices.class);


        Intent intent = getIntent();
        hisUID = intent.getStringExtra("hisUID");

        firebaseAuth = FirebaseAuth.getInstance();

        firebaseDatabase = FirebaseDatabase.getInstance();
        userDBref = firebaseDatabase.getReference("Users");

        Query userQuery = userDBref.orderByChild("uid").equalTo(hisUID);
        userQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()){
                    String firstName = ""+ ds.child("firstName").getValue();
                    hisImage = ""+ ds.child("image").getValue();
                    String typingStatus = ""+ ds.child("typingTo").getValue();

                    if (typingStatus.equals(myUid)){
                        userStatusTv.setText("Typing...");
                    }else {
                        String onlineStatus = ""+ ds.child("onlineStatus").getValue();

                        if (onlineStatus.equals("online")){
                            userStatusTv.setText(onlineStatus);
                        }
                        else {
                            Calendar cal = Calendar.getInstance(Locale.ENGLISH);
                            cal.setTimeInMillis(Long.parseLong(onlineStatus));
                            String dateTime = DateFormat.format("dd/MM/yyyy hh:mm aa", cal).toString();
                            userStatusTv.setText("Last seen at: " + dateTime);
                        }
                        nameTv.setText(firstName);

                        try{
                            Picasso.get().load(hisImage).placeholder(R.drawable.ic_default_profile_white).into(profileIv);
                        }catch (Exception e){
                            Picasso.get().load(R.drawable.ic_default_profile_white).into(profileIv);
                        }
                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notify = true;

                String message = messageEt.getText().toString().trim();
                if(TextUtils.isEmpty(message)){
                    Toast.makeText(ChatActivity.this, "Cannot Send Empty Message...", Toast.LENGTH_SHORT);
                }else {
                    sendMessage(message);
                }
                messageEt.setText("");
            }
        });

        messageEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() ==0 ){
                    checkTypingStatus("noOne");

                }else {
                    checkTypingStatus(hisUID);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        readMessages();

        seenMessages();
    }

    private void seenMessages() {
        userRefForSeen = FirebaseDatabase.getInstance().getReference("Chats");
        seenListener = userRefForSeen.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    ModelChat chat = dataSnapshot.getValue(ModelChat.class);
                    if(chat.getReceiver().equals(myUid) && chat.getSender().equals(hisUID)){
                        HashMap<String, Object> hasSeenHashMap = new HashMap<>();
                        hasSeenHashMap.put("isSeen",true);
                        dataSnapshot.getRef().updateChildren(hasSeenHashMap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readMessages() {
        chatList = new ArrayList<>();
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Chats");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatList.clear();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    ModelChat chat = dataSnapshot.getValue(ModelChat.class);
                    if(chat.getReceiver().equals(myUid) && chat.getSender().equals(hisUID) ||
                            chat.getReceiver().equals(hisUID) && chat.getSender().equals(myUid)){
                        chatList.add(chat);
                    }
                    adapterChat = new AdapterChat(ChatActivity.this, chatList,hisImage);
                    adapterChat.notifyDataSetChanged();
                   // recyclerView.smoothScrollToPosition(recyclerView.getBottom());
                    recyclerView.setAdapter(adapterChat);


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void sendMessage(String message) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        String timestamp = String.valueOf(System.currentTimeMillis());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", myUid);
        hashMap.put("receiver", hisUID);
        hashMap.put("message", message);
        hashMap.put("timestamp", timestamp);
        hashMap.put("isSeen", false);
        databaseReference.child("Chats").push().setValue(hashMap);


        DatabaseReference database = FirebaseDatabase.getInstance().getReference("Users").child(myUid);
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ModelUser user = snapshot.getValue(ModelUser.class);

                if(notify){
                    sendNotification(hisUID, user.getFirstName(),message);
                }
                notify = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void sendNotification(String hisUID, String firstName, String message) {
        DatabaseReference allTokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = allTokens.orderByKey().equalTo(hisUID);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    Token token = dataSnapshot.getValue(Token.class);
                    Data data = new Data(myUid, firstName+":"+message, "New Message",hisUID, R.drawable.ic_default_messeage);

                    Sender sender = new Sender(data, token.getToken());
                    apiServices.sendNotification(sender)
                            .enqueue(new Callback<Response>() {
                                @Override
                                public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                                    Toast.makeText(ChatActivity.this, ""+response.message(),Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onFailure(Call<Response> call, Throwable t) {

                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void checkUserStatus(){
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user != null) {

            myUid = user.getUid();
        }else {
                startActivity(new Intent(this, Login.class));
                finish();
            }
        }

    private void checkOnlineStatus(String status){
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Users").child(myUid);
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("onlineStatus", status);

        dbRef.updateChildren(hashMap);
    }

    private void checkTypingStatus(String typing){
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Users").child(myUid);
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("typingTo", typing);

        dbRef.updateChildren(hashMap);
    }

    @Override
    protected void onStart() {
        checkUserStatus();
        checkOnlineStatus("online");
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
        String timestamp = String.valueOf(System.currentTimeMillis());
        checkOnlineStatus(timestamp);
        checkTypingStatus("noOne");
        userRefForSeen.removeEventListener(seenListener);
    }

    @Override
    protected void onResume() {
        checkOnlineStatus("online");
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        menu.findItem(R.id.action_search).setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_logout){
            firebaseAuth.signOut();
            checkUserStatus();
        }
        return super.onOptionsItemSelected(item);
    }
}
