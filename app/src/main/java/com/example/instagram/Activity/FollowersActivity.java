package com.example.instagram.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.example.instagram.Adapter.FragmentFollowersAdapter;
import com.example.instagram.Adapter.UserAdapter;
import com.example.instagram.Model.Profil;
import com.example.instagram.Model.User;
import com.example.instagram.R;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FollowersActivity extends AppCompatActivity {

    private String id, title;
    private int followingCont, followersCont;
    private List<String> idList;
    private List<User> mUsers;
    private TextView textUserName;

    private RecyclerView recycler_view_like;
    private UserAdapter userAdapter;
    private LinearLayout linearFollowersMain;

    private FragmentPagerAdapter fragmentPagerAdapter;
    private TabLayout tabLayoutFollowers;
    private ViewPager viewPagerFollowers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_followers);
        init();
    }

    //    Переменые
    private void init() {
        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        title = intent.getStringExtra("title");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tabLayoutFollowers = findViewById(R.id.tabLayoutFollowers);
        linearFollowersMain = findViewById(R.id.linearFollowersMain);
        textUserName = findViewById(R.id.textUserName);
        viewPagerFollowers = findViewById(R.id.viewPagerFollowers);

        recycler_view_like = findViewById(R.id.recycler_view_like);
        recycler_view_like.setHasFixedSize(true);
        recycler_view_like.setLayoutManager(new LinearLayoutManager(this));
        mUsers = new ArrayList<>();
        idList = new ArrayList<>();
        userAdapter = new UserAdapter(this, mUsers, false);
        recycler_view_like.setAdapter(userAdapter);
        userInfo();

        switch (title) {
            case "Подписчики":
                break;

            case "Подписки":
                break;

            case "Отметки\"Нравится\"":
                linearFollowersMain.setVisibility(View.GONE);
                recycler_view_like.setVisibility(View.VISIBLE);
                toolbar.setVisibility(View.VISIBLE);
                getLikes();
            case "Просмотры":
                break;
        }

        findViewById(R.id.backFollowers).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        getFollowers();
    }

    //    Получить подписчиков
    private void getFollowers() {
        FirebaseDatabase.getInstance().getReference().child("Follow").child(id).child("followers").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                followersCont = (int) dataSnapshot.getChildrenCount();
                getFollowings();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //    Получить подписки
    private void getFollowings() {
        FirebaseDatabase.getInstance().getReference().child("Follow").child(id).child("following").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                followingCont = (int) dataSnapshot.getChildrenCount();

                fragmentPagerAdapter = new FragmentFollowersAdapter(getSupportFragmentManager(), id, followingCont, followersCont);
                viewPagerFollowers.setAdapter(fragmentPagerAdapter);
                tabLayoutFollowers.setupWithViewPager(viewPagerFollowers);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //    Получить лаки
    private void getLikes() {
        FirebaseDatabase.getInstance().getReference().child("Likes").child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                idList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    idList.add((snapshot.getKey()));
                }
                showUsers();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //    Покозать пользователя
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

    //    Получить информафию о пользователи
    private void userInfo() {
        FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                textUserName.setText(user.getUsername());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
