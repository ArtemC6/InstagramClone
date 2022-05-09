package com.example.instagram.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.instagram.Activity.StoryWindowActivity;
import com.example.instagram.Model.Stories;
import com.example.instagram.Model.User;
import com.example.instagram.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.ViewHolder> {

    private Context context;
    private List<Stories> storiesList;

    public StoryAdapter(Context context, List<Stories> storiesList) {
        this.context = context;
        this.storiesList = storiesList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.storis_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Stories stories = storiesList.get(position);
        userInfo(holder, stories);
        seenStory(holder, stories.getPublisher());
    }

    private void userInfo(@NonNull ViewHolder holder, Stories stories) {
        FirebaseDatabase.getInstance().getReference().child("Users").child(stories.getPublisher())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        try {
                            Glide.with(context).load(user.getImageurl()).into(holder.story_photo);
                        } catch (Exception e) {
                        }
                        if (user.getImageurl().equals("default")) {
                            holder.story_photo.setImageResource(R.drawable.ic_profile_user);
                        } else {
                            try {
                                Glide.with(context).load(user.getImageurl()).into(holder.story_photo_seen);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        holder.user_name_story.setText(user.getUsername());

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(context, StoryWindowActivity.class);
                                intent.putExtra("username", user.getUsername());
                                intent.putExtra("imageurl", user.getImageurl());
                                intent.putExtra("storiesurl", stories.getImageurl());
                                intent.putExtra("userId", stories.getPublisher());
                                context.startActivity(intent);
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void seenStory(ViewHolder viewHolder, String userId) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Story").child(userId);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int i = 0;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    if (!dataSnapshot.child("views").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).exists()) {
                        i++;
                    }
                }

                if (i > 0) {
                    viewHolder.story_photo.setVisibility(View.VISIBLE);
                    viewHolder.story_photo_seen.setVisibility(View.GONE);
                } else {
                    viewHolder.story_photo.setVisibility(View.GONE);
                    viewHolder.story_photo_seen.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return storiesList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public CircleImageView story_photo, story_photo_seen, add_story_photo;
        private TextView user_name_story, add_story_name;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            story_photo = itemView.findViewById(R.id.story_photo);
            story_photo_seen = itemView.findViewById(R.id.story_photo_seen);
            user_name_story = itemView.findViewById(R.id.user_name_story);
        }
    }
}
