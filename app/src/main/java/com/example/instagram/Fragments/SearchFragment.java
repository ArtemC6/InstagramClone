package com.example.instagram.Fragments;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instagram.Adapter.PhotoAdapterSearch;
import com.example.instagram.Model.Post;
import com.example.instagram.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {

    private RecyclerView recycler_view_UserImage;
    private List<Post> postList;
    private PhotoAdapterSearch photoAdapterSearch;
    private EditText search_bar_click;
    private RelativeLayout bar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        init(view);
        return view;
    }

    //    Переменые
    public void init(View view) {
        search_bar_click = view.findViewById(R.id.search_bar_click);
        bar = view.findViewById(R.id.bar);
        recycler_view_UserImage = view.findViewById(R.id.recycler_view_UserImage);
        recycler_view_UserImage.setHasFixedSize(true);
        recycler_view_UserImage.setLayoutManager(new GridLayoutManager(getContext(), 3));

        postList = new ArrayList<>();
        photoAdapterSearch = new PhotoAdapterSearch(getContext(), postList);
        recycler_view_UserImage.setAdapter(photoAdapterSearch);
        recycler_view_UserImage.setVisibility(View.INVISIBLE);
        photoAdapterSearch.setData(postList);

        search_bar_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((FragmentActivity) getContext()).getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frameSearch, new SearchUserFragment()).commit();
                bar.setVisibility(View.GONE);
                recycler_view_UserImage.setVisibility(View.GONE);
            }
        });

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                recycler_view_UserImage.setVisibility(View.VISIBLE);
            }
        }, 300);

        readUserImage();
    }

    //    Считываем фото пользователей
    private void readUserImage() {
        FirebaseDatabase.getInstance().getReference().child("Posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Post post = dataSnapshot.getValue(Post.class);
                    postList.add(post);
                }
                photoAdapterSearch.setData(postList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
