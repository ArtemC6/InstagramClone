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
import com.example.instagram.Activity.ChatActivity;
import com.example.instagram.Activity.MainActivity;
import com.example.instagram.Model.User;
import com.example.instagram.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatPeopleOnlineAdapter extends RecyclerView.Adapter<ChatPeopleOnlineAdapter.ViewHolder> {

    private Context context;
    private List<User> userList;

    public ChatPeopleOnlineAdapter(Context context, List<User> userList) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.online_peple_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = userList.get(position);

        holder.name_people_online.setText(user.getUsername());
        Glide.with(context).load(user.getImageurl()).error(R.drawable.ic_profile_user).into(holder.profile_people_online);

        // передача инфи о пользоватеи
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("useridrandom", user.getUseridraidom());
                intent.putExtra("profailPic", user.getImageurl());
                intent.putExtra("username", user.getUsername());
                intent.putExtra("name", user.getName());
                intent.putExtra("userid", user.getId());
                intent.putExtra("userstatus", user.getStatus());
                context.startActivity(intent);
                MainActivity.online();
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public CircleImageView profile_people_online;
        private TextView name_people_online;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            profile_people_online = itemView.findViewById(R.id.profile_people_online);
            name_people_online = itemView.findViewById(R.id.name_people_online);
        }

    }
}
