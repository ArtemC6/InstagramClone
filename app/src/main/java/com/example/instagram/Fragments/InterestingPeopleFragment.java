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


public class InterestingPeopleFragment extends Fragment {
    private RecyclerView recyclerIntersPeople;
    private List<User> userListRandom,fullListRandom;
    private StartRandomUserAdapter userAdapterRandom;
    private Random randomGenerator;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_interesting_people, container, false);
        init(view);
        return view;
    }

    //    Переменые
    public void init(View view) {
        userListRandom = new ArrayList<>();
        fullListRandom = new ArrayList<>();
        randomGenerator = new Random();

        recyclerIntersPeople = view.findViewById(R.id.recyclerIntersPeople);
        recyclerIntersPeople.setLayoutManager(new LinearLayoutManager(getContext()));

        userAdapterRandom = new StartRandomUserAdapter(getContext(), userListRandom, true);
        recyclerIntersPeople.setAdapter(userAdapterRandom);

        view.findViewById(R.id.interesPeople).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), MainActivity.class));
            }
        });

        readUsers();
    }

    //    Считывает рандомных пльзователей
    private void readUsers() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userListRandom.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    if (!user.getId().equals(FirebaseAuth.getInstance().getUid())) {
                        fullListRandom.add(user);
                    }
                }

                if (fullListRandom.size() != 0) {
                    Set<User> items_id = new HashSet<>();
                    for (int i = 0; i < fullListRandom.size(); i++) {
                        int index = randomGenerator.nextInt(fullListRandom.size());
                        User random = fullListRandom.get(index);
                        items_id.add(random);

                    }
                    for (User user : items_id) {
                        userListRandom.add(user);
                    }
                }

                userAdapterRandom.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}