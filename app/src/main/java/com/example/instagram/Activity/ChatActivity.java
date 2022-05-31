package com.example.instagram.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.instagram.Adapter.ChatAdapter;
import com.example.instagram.Model.ModelMessage;
import com.example.instagram.Model.Post;
import com.example.instagram.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    private TextView username_that, text_send, statusTextThat, thatUserName,
            thatNameUser, thatPostCount, thatFollowers, thatUserFollowing;
    private RecyclerView recycler_view_that;
    private ImageView image_camera, image_search, user_profile_chat_my, thatUserProfileImage, thatDotOnline;

    private ChatAdapter catheAdapter;
    private ArrayList<ModelMessage> messageModelsList;
    private EditText chat_massage_edit;
    private LinearLayout liner_massage, ThatLinerL;
    private FirebaseDatabase database;
    private Button visitProfileUserBtn;
    private FirebaseAuth auth;
    private String recieveid, profailPic, username, name, userstatus, userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        init();
    }

    //    Отправить сообщение
    private void sendMessage(String senderid, String senderRoom, String receverRoom) {
        text_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String massage = chat_massage_edit.getText().toString();
                // передается id зарегина пользовтеля и сообзение
                if (!TextUtils.isEmpty(chat_massage_edit.getText().toString())) {
                    final ModelMessage model = new ModelMessage(senderid, massage);
                    // передается время
                    model.setTimestamp(new Date().getTime());
                    chat_massage_edit.setText("");

                    database.getReference().child("chats")
                            .child(senderRoom)
                            .push()
                            .setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            database.getReference().child("chats")
                                    .child(receverRoom)
                                    .push() // разное
                                    .setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                }
                            });
                        }
                    });
                }
            }
        });
    }

    //    Переменые
    private void init() {
        user_profile_chat_my = findViewById(R.id.user_profile_chat_my);
        thatDotOnline = findViewById(R.id.thatDotOnline);
        thatFollowers = findViewById(R.id.thatFollowers);
        thatPostCount = findViewById(R.id.thatPostCount);
        visitProfileUserBtn = findViewById(R.id.visitProfileUserBtn);
        ThatLinerL = findViewById(R.id.ThatLinerL);
        thatUserProfileImage = findViewById(R.id.thatUserProfileImage);
        statusTextThat = findViewById(R.id.statusTextThat);
        image_search = findViewById(R.id.image_search);
        image_camera = findViewById(R.id.image_camera);
        thatUserFollowing = findViewById(R.id.thatUserFollowing);
        username_that = findViewById(R.id.username_that);
        chat_massage_edit = findViewById(R.id.chat_massage_edit);
        liner_massage = findViewById(R.id.liner_massage);
        text_send = findViewById(R.id.text_send);
        thatUserName = findViewById(R.id.thatUserName);
        thatNameUser = findViewById(R.id.thatNameUser);
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();

        recieveid = getIntent().getStringExtra("useridrandom");
        profailPic = getIntent().getStringExtra("profailPic");
        username = getIntent().getStringExtra("username");
        name = getIntent().getStringExtra("name");
        userstatus = getIntent().getStringExtra("userstatus");
        userId = getIntent().getStringExtra("userid");

        if (userstatus.equals("Сейчас в сети")) {
            thatDotOnline.setVisibility(View.VISIBLE);
        } else {
            thatDotOnline.setVisibility(View.GONE);
        }

        statusTextThat.setText(userstatus);
        thatNameUser.setText(name);
        thatUserName.setText(" " + username);

        if (profailPic.equals("default")) {
            user_profile_chat_my.setImageResource(R.drawable.ic_profile_user);
            thatUserProfileImage.setImageResource(R.drawable.ic_profile_user);
        } else {
            Glide.with(this).load(profailPic).into(user_profile_chat_my);
            Glide.with(this).load(profailPic).into(thatUserProfileImage);
        }

        recycler_view_that = findViewById(R.id.recycler_view_that);
        recycler_view_that.setHasFixedSize(true);
        recycler_view_that.setLayoutManager(new LinearLayoutManager(this));
        messageModelsList = new ArrayList<>();
        catheAdapter = new ChatAdapter(messageModelsList, this, recieveid);
        recycler_view_that.setAdapter(catheAdapter);
        username_that.setText(name);

        MainActivity.onlineTame();

        final String senderid = auth.getUid();
        final String senderRoom = senderid + recieveid;
        final String receverRoom = recieveid + senderid;

        database.getReference().child("chats")
                .child(senderRoom).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messageModelsList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    ModelMessage model = dataSnapshot.getValue(ModelMessage.class);
                    if (model != null) {
                        model.setMassageId(snapshot.getKey()); // под вопросом
                    }
                    messageModelsList.add(model);
                }
                catheAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        getUserInfo();
        sendMessage(senderid, senderRoom, receverRoom);

        findViewById(R.id.arrow_chat_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, Object> map = new HashMap<>();
                MainActivity.online();
                startActivity(new Intent(getApplicationContext(), ChatUsersActivity.class));
            }
        });

        visitProfileUserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ChatActivity.this, MainActivity.class).putExtra("publisherId", userId));
            }
        });

        user_profile_chat_my.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ChatActivity.this, MainActivity.class).putExtra("publisherId", userId));
            }
        });

        chat_massage_edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                getMassage(s);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
            }
        });
    }

    //    Получить информацию о пользователи
    private void getUserInfo() {
        // количисто постов
        FirebaseDatabase.getInstance().getReference().child("Posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int counter = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Post post = snapshot.getValue(Post.class);

                    if (post.getPublisher().equals(userId)) counter++;
                }
                thatPostCount.setText(String.valueOf(counter));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

//      Количетво Попписчиков
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Follow").child(userId);
        ref.child("followers").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                thatFollowers.setText("" + dataSnapshot.getChildrenCount() + " ");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // переход на страницу пользователя
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Follow").
                child(FirebaseAuth.getInstance().getUid())
                .child("following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(userId).exists())
                    thatUserFollowing.setText("Вы подписаны на друг друга в Instagram");
                else
                    thatUserFollowing.setText("Вы не подписаны на друг друга в Instagram");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        MainActivity.onlineTame();
    }

    @Override
    protected void onStop() {
        super.onStop();
        MainActivity.offline();
    }

    @Override
    protected void onStart() {
        super.onStart();
        MainActivity.online();
    }

    //    Рабоата отображением кнопок
    private void getMassage(Editable s) {
        if (s.length() == 0) {
            text_send.setVisibility(View.GONE);
            liner_massage.setVisibility(View.VISIBLE);
            image_camera.setVisibility(View.VISIBLE);
            image_search.setVisibility(View.GONE);
        } else {
            liner_massage.setVisibility(View.GONE);
            text_send.setVisibility(View.VISIBLE);
            image_camera.setVisibility(View.GONE);
            image_search.setVisibility(View.VISIBLE);
        }
    }
}