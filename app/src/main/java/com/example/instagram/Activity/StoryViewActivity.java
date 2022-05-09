package com.example.instagram.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instagram.Adapter.UserAdapter;
import com.example.instagram.Model.User;
import com.example.instagram.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class StoryViewActivity extends AppCompatActivity {

    private RecyclerView recyclerStoryUser;
    private UserAdapter userAdapter;

    private ImageView closeStoryView;
    private TextView story_count_View;
    private String id, title;
    private List<String> idList;
    private List<User> mUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story_view);
        init();
        getViews();
    }

    //    Переменые
    private void init() {
        recyclerStoryUser = findViewById(R.id.recyclerStoryUser);
        closeStoryView = findViewById(R.id.closeStoryView);
        story_count_View = findViewById(R.id.story_count_View);

        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        title = intent.getStringExtra("title");

        recyclerStoryUser.setHasFixedSize(true);
        recyclerStoryUser.setLayoutManager(new LinearLayoutManager(this));
        mUsers = new ArrayList<>();
        userAdapter = new UserAdapter(this, mUsers, false);
        recyclerStoryUser.setAdapter(userAdapter);

        idList = new ArrayList<>();
        story_count_View.setText(String.valueOf(getIntent().getIntExtra("viewCon", 0)));

        closeStoryView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    //    Кто посмотрел
    private void getViews() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Story").child(id).
                child(getIntent().getStringExtra("storyId")).child("views");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                idList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    idList.add(dataSnapshot.getKey());

                }
                showUsers();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //    Покозать пользователей
    private void showUsers() {
        FirebaseDatabase.getInstance().getReference().child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUsers.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    for (String id : idList) {
                        if (user.getId().equals(id)) {
                            if (!user.getId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                mUsers.add(user);
                            }
                        }
                    }
                }
                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}