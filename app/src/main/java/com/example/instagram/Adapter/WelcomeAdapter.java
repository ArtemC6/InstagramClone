package com.example.instagram.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.instagram.Activity.MainActivity;
import com.example.instagram.Fragments.ProfileFragment;
import com.example.instagram.Model.Post;
import com.example.instagram.Model.User;
import com.example.instagram.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class WelcomeAdapter extends RecyclerView.Adapter<WelcomeAdapter.ViewHolder> {

    private Context context;
    private List<User> userList;
    private boolean isFragment;
    private FirebaseUser firebaseUser;

    public WelcomeAdapter(Context context, List<User> userList, boolean isFragment) {
        this.context = context;
        this.userList = userList;
        this.isFragment = isFragment;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.welcome_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        User user = userList.get(position);

        holder.userName_welcome.setText(user.getUsername());
        holder.name_welcome.setText(user.getName());
        if (user.getImageurl().equals("default")) {
            holder.cardViewWelcome.setImageResource(R.drawable.ic_profile_user);
        } else {
            Glide.with(context)
                    .load(user.getImageurl())
                    .into(holder.cardViewWelcome);
        }
        isFollowed(user.getId(), holder.btn_welcome);


        if (user.getId().equals(firebaseUser.getUid())) {
            holder.btn_welcome.setVisibility(View.GONE);
        }

        if (user.isPosition()) {
            holder.doneWelcome.setVisibility(View.VISIBLE);
        } else {
            holder.doneWelcome.setVisibility(View.GONE);
        }

        List<Post> photoList = new ArrayList<>();
        holder.recyclerWelcome.setLayoutManager(new LinearLayoutManager(context,RecyclerView.HORIZONTAL,false));
        PhotoAdapterWelcome photoAdapter = new PhotoAdapterWelcome(context,photoList);
        holder.recyclerWelcome.setAdapter(photoAdapter);

        FirebaseDatabase.getInstance().getReference().child("Posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                photoList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Post post = snapshot.getValue(Post.class);
                    if (post.getPublisher().equals(user.getId())) {
                        photoList.add(post);
                        if (photoList.size() <= 2)
                        photoAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        holder.btn_welcome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // если нажать пописаться
                if (holder.btn_welcome.getText().toString().equals(("Подписаться"))) {

                    // нынешний пользователь
                    FirebaseDatabase.getInstance().getReference().child("Follow").
                            child((firebaseUser.getUid())).child("following").child(user.getId()).setValue(true);

                    // подписчик
                    FirebaseDatabase.getInstance().getReference().child("Follow").
                            child(user.getId()).child("followers").child(firebaseUser.getUid()).setValue(true);

                    addNotification(user.getId());
                } else {
                    FirebaseDatabase.getInstance().getReference().child("Follow").
                            child((firebaseUser.getUid())).child("following").child(user.getId()).removeValue();

                    FirebaseDatabase.getInstance().getReference().child("Follow").
                            child(user.getId()).child("followers").child(firebaseUser.getUid()).removeValue();
                }
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFragment) {
                    context.getSharedPreferences("PROFILE", Context.MODE_PRIVATE).edit().putString("profileId", user.getId()).apply();

                    ((FragmentActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id
                            .fragment_home_container_main, new ProfileFragment()).commit();
                } else {
                    Intent intent = new Intent(context, MainActivity.class);
                    intent.putExtra("publisherId", user.getId());
                    context.startActivity(intent);
                }
            }
        });

        holder.welcomeBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeItem(position);
            }
        });


    }

    private void removeItem(int position) {
        userList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, userList.size());
    }

    private void isFollowed(final String id, final Button btnFollow) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                .child("following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(id).exists()) {
                    btnFollow.setText("Отписаться");
                    btnFollow.setBackgroundResource(R.drawable.bg_button_bio_white);
                    btnFollow.setTextColor(Color.BLACK);
                } else {
                    btnFollow.setText("Подписаться");
                    btnFollow.setBackgroundResource(R.drawable.bg_button_followers);
                    btnFollow.setTextColor(Color.WHITE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView name_welcome, userName_welcome;
        private Button btn_welcome;
        private ImageView welcomeBack, imWelcome_1, imWelcome_2, imWelcome_3, doneWelcome,cardViewWelcome;
        private RecyclerView recyclerWelcome;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            welcomeBack = itemView.findViewById(R.id.wellcomBack);
            doneWelcome = itemView.findViewById(R.id.doneWellcome);
            imWelcome_1 = itemView.findViewById(R.id.imWellcom1);
            imWelcome_2 = itemView.findViewById(R.id.imWellcom2);
            imWelcome_3 = itemView.findViewById(R.id.imWellcom3);
            cardViewWelcome = itemView.findViewById(R.id.cirdWellcime);
            userName_welcome = itemView.findViewById(R.id.userName_wellcome);
            name_welcome = itemView.findViewById(R.id.name_wellcome);
            btn_welcome = itemView.findViewById(R.id.blue_btn_Welcome);
            recyclerWelcome = itemView.findViewById(R.id.recyclerWelcome);
        }
    }

    private void addNotification(String publisherId) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Notifications");
        String notificationId = databaseReference.push().getKey();
        HashMap<String, Object> map = new HashMap<>();
        map.put("userid", FirebaseAuth.getInstance().getCurrentUser().getUid());
        map.put("notifid", notificationId);
        map.put("text", "Подписался(-ась) на ваши обновления. ");
        map.put("isPost", false);

        databaseReference.child(publisherId).child(notificationId).setValue(map);
    }
}
