package com.example.instagram.Adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.instagram.Activity.CommentActivity;
import com.example.instagram.Activity.FollowersActivity;
import com.example.instagram.Fragments.PostDetailFragment;
import com.example.instagram.Fragments.ProfileFragment;
import com.example.instagram.Model.Post;
import com.example.instagram.Model.User;
import com.example.instagram.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    private Context mContext;
    private List<Post> mPosts;

    int number_of_clicks = 0;
    boolean thread_started = false;
    final int DELAY_BETWEEN_CLICKS_IN_MILLISECONDS = 350;
    private FirebaseUser firebaseUser;

    public PostAdapter(Context mContext, List<Post> mPosts) {
        this.mContext = mContext;
        this.mPosts = mPosts;
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.post_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final Post post = mPosts.get(position);
        try {
            Glide.with(mContext)
                    .load(post.getImageurl())
                    .into(holder.postImage);
        } catch (Exception e) {

        }

        if (post.getDescription().equals("")) {
            holder.description.setVisibility(View.GONE);
        } else {
            holder.description.setVisibility(View.VISIBLE);
            holder.description.setText(post.getDescription());
        }

        FirebaseDatabase.getInstance().getReference().child("Users").child(post.getPublisher())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);

                        if (user.getImageurl().equals("default")) {
                            holder.image_profile_post.setImageResource(R.drawable.ic_profile_user);
                        } else {
                            try {
                                Glide.with(mContext)
                                        .load(user.getImageurl())
                                        .into(holder.image_profile_post);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        if (!post.getPublisher().equals(firebaseUser.getUid())) {
                            holder.textMy.setVisibility(View.VISIBLE);
                            holder.textMy.setText("Потому что вы подписаны на " + user.getUsername());

                        }
                        holder.author.setText(user.getUsername());
                        holder.username.setText(user.getName());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        Date date = new Date(post.getTame());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-HH:mm");
        String strDate = simpleDateFormat.format(date);
        holder.textDate.setText(strDate.toString());

        isLiked(post.getPostid(), holder.like);
        likes(post, holder.number_of_likes, holder.linearPeopleLike, holder.profile_people_like, holder.name_people_like);
        getComments(post.getPostid(), holder.noOfComments);
        isSaved(post.getPostid(), holder.save);

        holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.like.getTag().equals("like")) {
                    // 1 Автор поста 2 текущий пользователь
                    FirebaseDatabase.getInstance().getReference().child("Likes")
                            .child(post.getPostid()).child(firebaseUser.getUid()).setValue(true);

                    addNotification(post.getPostid(), post.getPublisher());
                } else {
                    FirebaseDatabase.getInstance().getReference().child("Likes")
                            .child(post.getPostid()).child(firebaseUser.getUid()).removeValue();
                }
            }
        });

        // Коменты
        holder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, CommentActivity.class);
                intent.putExtra("postId", post.getPostid());
                intent.putExtra("authorId", post.getPublisher());
                mContext.startActivity(intent);
            }
        });

        // Коменты
        holder.noOfComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, CommentActivity.class);
                intent.putExtra("postId", post.getPostid());
                intent.putExtra("authorId", post.getPublisher());
                mContext.startActivity(intent);
            }
        });

        // сохронёные
        holder.save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.save.getTag().equals("save")) {
                    FirebaseDatabase.getInstance().getReference().child("Saves")
                            .child(firebaseUser.getUid()).child(post.getPostid()).setValue(true);
                } else {
                    FirebaseDatabase.getInstance().getReference().child("Saves")
                            .child(firebaseUser.getUid()).child(post.getPostid()).removeValue();
                }
            }
        });

        holder.image_profile_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.getSharedPreferences("PROFILE", Context.MODE_PRIVATE)
                        .edit().putString("profileId", post.getPublisher()).apply();

                ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_home_container_main, new ProfileFragment()).commit();
            }
        });

        // переход в профиль
        holder.username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.getSharedPreferences("PROFILE", Context.MODE_PRIVATE)
                        .edit().putString("profileId", post.getPublisher()).apply();

                ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_home_container_main, new ProfileFragment()).commit();
            }
        });

        // переход в профиль
        holder.author.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.getSharedPreferences("PROFILE", Context.MODE_PRIVATE)
                        .edit().putString("profileId", post.getPublisher()).apply();

                ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_home_container_main, new ProfileFragment()).commit();
            }
        });

        holder.morePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (post.getPublisher().equals(FirebaseAuth.getInstance().getUid())) {
                    BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(mContext);
                    bottomSheetDialog.setContentView(R.layout.window_setings);
                    bottomSheetDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    bottomSheetDialog.setCanceledOnTouchOutside(true);
                    TextView deletePosText = bottomSheetDialog.findViewById(R.id.deletPosttext);
                    deletePosText.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            bottomSheetDialog.dismiss();
                            Dialog dialog = new Dialog(mContext);
                            dialog.setContentView(R.layout.delete_post);
                            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            TextView deleteText = dialog.findViewById(R.id.deleteText);
                            TextView deletePostBack = dialog.findViewById(R.id.deletepostback);

                            deletePostBack.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                }
                            });
                            deleteText.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
//                                    FirebaseDatabase.getInstance().getReference().child("Posts").child(post.getPostid()).removeValue();
//                                    Toast.makeText(mContext, "Публикация удалена", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                }
                            });
                            dialog.show();
                        }
                    });
                    bottomSheetDialog.show();
                } else {
                    BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(mContext);
                    bottomSheetDialog.setContentView(R.layout.window_setings_passerby);
                    bottomSheetDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    bottomSheetDialog.setCanceledOnTouchOutside(true);
                    TextView passerbyhideText = bottomSheetDialog.findViewById(R.id.passerbyhideText);
                    passerbyhideText.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            bottomSheetDialog.dismiss();
                            deletePost(position);
                        }
                    });
                    bottomSheetDialog.show();
                }
            }
        });

        // установка последнего сообщение
        FirebaseDatabase.getInstance().
                getReference().
                child("Comments").child(post.getPostid()).orderByChild("timestamp").limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChildren()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        holder.commentLast.setText(dataSnapshot.child("comment").getValue().toString());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        holder.postImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ++number_of_clicks;
                setAnimation(holder.heartAnimation, number_of_clicks);
                if (!thread_started) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            thread_started = true;
                            try {
                                Thread.sleep(DELAY_BETWEEN_CLICKS_IN_MILLISECONDS);
                                if (number_of_clicks == 1) {
                                    mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit().putString("postid", post.getPostid()).apply();
                                    ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction()
                                            .replace(R.id.fragment_home_container_main, new PostDetailFragment()).commit();

                                } else if (number_of_clicks == 2) {
                                    if (holder.like.getTag().equals("like")) {
                                        FirebaseDatabase.getInstance().getReference().child("Likes")
                                                .child(post.getPostid()).child(firebaseUser.getUid()).setValue(true);

                                        addNotification(post.getPostid(), post.getPublisher());

                                    } else {
                                        FirebaseDatabase.getInstance().getReference().child("Likes")
                                                .child(post.getPostid()).child(firebaseUser.getUid()).removeValue();
                                    }
                                }
                                number_of_clicks = 0;
                                thread_started = false;
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }

                    }).start();
                }
            }
        });

        holder.number_of_likes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, FollowersActivity.class);
                intent.putExtra("id", post.getPostid());
                intent.putExtra("title", "Отметки\"Нравится\"");
                mContext.startActivity(intent);
            }
        });
    }

    private void deletePost(int position) {
        mPosts.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mPosts.size());
    }

    private void setAnimation(ImageView heartAnimation, int number_of_clicks) {
        Animation animHide = AnimationUtils.loadAnimation(mContext, R.anim.button_anim_hide);
        Animation animShow = AnimationUtils.loadAnimation(mContext, R.anim.button_anim_show);
        if (number_of_clicks == 2) {
            heartAnimation.setVisibility(View.VISIBLE);
            heartAnimation.setAnimation(animShow);
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    heartAnimation.setVisibility(View.GONE);
                    heartAnimation.setAnimation(animHide);
                }
            }, 500);
        }
    }

    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView image_profile_post,
                save, comment, like, postImage, morePost, heartAnimation, profile_people_like;

        public TextView username, textDate, number_of_likes,
                deletePosText, author, commentLast, textMy, noOfComments, description, name_people_like;

        public LinearLayout linearPeopleLike;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image_profile_post = itemView.findViewById(R.id.image_profile_post);
            name_people_like = itemView.findViewById(R.id.name_people_like);
            linearPeopleLike = itemView.findViewById(R.id.linearPeopleLike);
            profile_people_like = itemView.findViewById(R.id.profile_people_like);
            textMy = itemView.findViewById(R.id.textMy);
            commentLast = itemView.findViewById(R.id.commentLast);
            postImage = itemView.findViewById(R.id.post_image);
            like = itemView.findViewById(R.id.like);
            comment = itemView.findViewById(R.id.comment);
            save = itemView.findViewById(R.id.save);
            deletePosText = itemView.findViewById(R.id.deletPosttext);
            morePost = itemView.findViewById(R.id.morePost);
            textDate = itemView.findViewById(R.id.textDate);
            heartAnimation = itemView.findViewById(R.id.heartAnimation);
            username = itemView.findViewById(R.id.username);
            number_of_likes = itemView.findViewById(R.id.number_of_likes);
            author = itemView.findViewById(R.id.author);
            noOfComments = itemView.findViewById(R.id.no_of_comments);
            description = itemView.findViewById(R.id.description);

        }
    }

    private void isSaved(final String postId, final ImageView image) {
        FirebaseDatabase.getInstance().getReference().child("Saves").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(postId).exists()) {
                    // если у меня есть то сохроннёные
                    image.setImageResource(R.drawable.ic_save_back);
                    image.setTag("saved");
                } else {
                    image.setImageResource(R.drawable.ic_save);
                    image.setTag("save");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void isLiked(String postId, final ImageView imageView) {
        FirebaseDatabase.getInstance().getReference().child("Likes").child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(firebaseUser.getUid()).exists()) {
                    imageView.setImageResource(R.drawable.ic_heart_red);
                    imageView.setTag("liked");
                } else {
                    imageView.setImageResource(R.drawable.ic_heart_black);
                    imageView.setTag("like");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void likes(Post post, TextView number_of_likes, LinearLayout linearPeopleLike, ImageView profile_people_like, TextView name_people_like) {
        LinkedList<String> idList = new LinkedList<>();
        FirebaseDatabase.getInstance().getReference().child("Likes").child(post.getPostid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    idList.add((snapshot.getKey()));
                }

                if (idList.size() >= 1) {
                    linearPeopleLike.setVisibility(View.VISIBLE);
                    number_of_likes.setText("еще " + dataSnapshot.getChildrenCount());

                    FirebaseDatabase.getInstance().getReference().child("Users").child(idList.getLast()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String idUser = dataSnapshot.child("id").getValue().toString();
                            if (FirebaseAuth.getInstance().getCurrentUser().getUid() != idUser) {
                                Glide.with(mContext).load(dataSnapshot.child("imageurl").getValue().toString()).into(profile_people_like);
                                name_people_like.setText(dataSnapshot.child("username").getValue().toString());
                            } else {
                                linearPeopleLike.setVisibility(View.GONE);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                } else {
                    linearPeopleLike.setVisibility(View.GONE);
                    number_of_likes.setText("Нравится: " + dataSnapshot.getChildrenCount());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void getComments(String postId, final TextView text) {
        FirebaseDatabase.getInstance().getReference().child("Comments").child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                text.setText("Смотреть все комментирии (" + dataSnapshot.getChildrenCount() + ")");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void addNotification(String postId, String publisherId) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Notifications");
        String notificationId = databaseReference.push().getKey();
        HashMap<String, Object> map = new HashMap<>();
        map.put("userid", FirebaseAuth.getInstance().getCurrentUser().getUid());
        map.put("notifid", notificationId);
        map.put("text", "Понравилось ваше фото.");
        map.put("postid", postId);
        map.put("isPost", true);
        databaseReference.child(publisherId).child(notificationId).setValue(map);
    }

}
