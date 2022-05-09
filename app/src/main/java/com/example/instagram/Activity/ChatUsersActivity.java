package com.example.instagram.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instagram.Adapter.ChatPeopleOnlineAdapter;
import com.example.instagram.Adapter.ChatUserAdapter;
import com.example.instagram.Model.User;
import com.example.instagram.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ChatUsersActivity extends AppCompatActivity {
    private RecyclerView recycler_chat_user, recycler_view_user_online;
    private ChatUserAdapter chatAdapter;
    private EditText search_user_chat;
    private List<User> userList;
    private FirebaseUser fUser;
    private TextView nameUser_chat;

    private List<User> useOnlineList;
    private ChatPeopleOnlineAdapter chatPeopleOnlineAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chath_user);
        init();
        getUser();
        readUsersOnline();
    }

    //    Переменые
    private void init() {
        MainActivity.onlineTame();
        nameUser_chat = findViewById(R.id.nameUser_chat);
        recycler_view_user_online = findViewById(R.id.recycler_view_user_online);
        recycler_chat_user = findViewById(R.id.recycler_chat_user);
        search_user_chat = findViewById(R.id.search_user_chat);
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        recycler_chat_user.setHasFixedSize(true);
        recycler_chat_user.setLayoutManager(new LinearLayoutManager(this));

        useOnlineList = new ArrayList<>();
        recycler_view_user_online.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        recycler_view_user_online.setHasFixedSize(true);
        chatPeopleOnlineAdapter = new ChatPeopleOnlineAdapter(this, useOnlineList);
        recycler_view_user_online.setAdapter(chatPeopleOnlineAdapter);

        userList = new ArrayList<>();
        chatAdapter = new ChatUserAdapter(this, userList);
        recycler_chat_user.setAdapter(chatAdapter);

        findViewById(R.id.image_back_user_chat).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.online();
                startActivity(new Intent(ChatUsersActivity.this, MainActivity.class));
            }
        });
    }

    //    Прочитать всех пльзоватлей в сети
    private void readUsersOnline() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                useOnlineList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    if (!user.getId().equals(FirebaseAuth.getInstance().getUid())) {
                        if (user.getStatus().equals("Сейчас в сети")) {

                            useOnlineList.add(user);

                        }
                    }
                }

                if (useOnlineList.size() == 0) {
                    recycler_view_user_online.setVisibility(View.GONE);
                } else {
                    recycler_view_user_online.setVisibility(View.VISIBLE);
                }

                chatPeopleOnlineAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //    Получить информацию о пльзователи
    private void getUser() {
        FirebaseDatabase.getInstance().getReference().child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    User user = dataSnapshot.getValue(User.class);
                    user.setUseridraidom(dataSnapshot.getKey());
                    if (!user.getId().equals(FirebaseAuth.getInstance().getUid())) {
                        userList.add(user);
                    }
                }
                chatAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        nameUser_chat.setText(getSharedPreferences("thatUser", MODE_PRIVATE).getString("nameChat", ""));
        FirebaseDatabase.getInstance().getReference().child("Users").child(fUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                nameUser_chat.setText(user.getUsername());
                getSharedPreferences("thatUser", MODE_PRIVATE).edit().putString("nameChat", user.getUsername()).apply();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        search_user_chat.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchUser(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
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

    //    Поиск пользователя
    private void searchUser(String s) {
        Query query = FirebaseDatabase.getInstance().getReference().child("Users")
                .orderByChild("username").startAt(s).endAt(s + "\uf8ff");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    if (!user.getId().equals(FirebaseAuth.getInstance().getUid()))
                        userList.add(user);
                }
                chatAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        MainActivity.onlineTame();
    }

}