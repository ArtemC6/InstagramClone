package com.example.instagram.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instagram.Activity.MainActivity;
import com.example.instagram.Adapter.StartRandomUserAdapter;
import com.example.instagram.Model.User;
import com.example.instagram.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class StartRecommendationPeopleFragment extends Fragment {

    private RecyclerView recycler_recommended_people;
    private List<User> userListRandom;
    private List<User> userListRandomFoll;
    private StartRandomUserAdapter startRandomUserAdapter;
    private Random random;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_start__recommendation, container, false);
        init(view);
        readUsers();
        return view;
    }

    //    Пременые
    public void init(View view) {
        random = new Random();
        recycler_recommended_people = view.findViewById(R.id.recycler_recommended_people);
        recycler_recommended_people.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        userListRandom = new ArrayList<>();
        userListRandomFoll = new ArrayList<>();
        startRandomUserAdapter = new StartRandomUserAdapter(getContext(), userListRandom, false);
        recycler_recommended_people.setAdapter(startRandomUserAdapter);


        view.findViewById(R.id.image_next_main).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), MainActivity.class));
            }
        });
    }

    //    Считывание пользоватлей
    private void readUsers() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userListRandom.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    if (!user.getId().equals(FirebaseAuth.getInstance().getUid())) {
                        userListRandomFoll.add(user);
                    }
                }

                if (userListRandomFoll.size() != 0) {
                    Set<User> items_id = new HashSet<>();
                    for (int i = 0; i < userListRandomFoll.size(); i++) {
                        int index = random.nextInt(userListRandomFoll.size());
                        User random = userListRandomFoll.get(index);
                        items_id.add(random);

                    }
                    for (User user : items_id) {
                        userListRandom.add(user);
                    }
                }

                startRandomUserAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}