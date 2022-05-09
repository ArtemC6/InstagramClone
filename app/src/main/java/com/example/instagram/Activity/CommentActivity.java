package com.example.instagram.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.instagram.Adapter.CommentAdapter;
import com.example.instagram.Model.Comment;
import com.example.instagram.Model.User;
import com.example.instagram.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentActivity extends AppCompatActivity {

    private RecyclerView recycler_view_comment;
    private CommentAdapter commentAdapter;
    private List<Comment> commentList;

    private EditText add_comment_edt;
    private CircleImageView image_profile_comment;
    private TextView postComment;

    private String postId;
    private String authorId;
    private FirebaseUser fUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        init();
        getComment();
    }

    //    Переменые
    private void init() {
        findViewById(R.id.backComment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Intent intent = getIntent();
        postId = intent.getStringExtra("postId");
        authorId = intent.getStringExtra("authorId");

        recycler_view_comment = findViewById(R.id.recycler_view_comment);
        recycler_view_comment.setHasFixedSize(true);
        recycler_view_comment.setLayoutManager(new LinearLayoutManager(this));

        commentList = new ArrayList<>();
        commentAdapter = new CommentAdapter(this, commentList, postId);

        recycler_view_comment.setAdapter(commentAdapter);

        add_comment_edt = findViewById(R.id.add_comment_edt);
        image_profile_comment = findViewById(R.id.image_profile_comment);
        postComment = findViewById(R.id.postComment);

        fUser = FirebaseAuth.getInstance().getCurrentUser();

        getUserImage();

        postComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(add_comment_edt.getText().toString())) {
                } else {
                    putComment();
                }
            }
        });
    }

    //    Получить комментарии
    private void getComment() {
        FirebaseDatabase.getInstance().getReference().child("Comments").child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                commentList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Comment comment = snapshot.getValue(Comment.class);
                    commentList.add(comment);
                }

                commentAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //    Отправить комментарий
    private void putComment() {
        HashMap<String, Object> map = new HashMap<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Comments").child(postId);
        // id коммента
        String id = ref.push().getKey();

        map.put("id", id);
        map.put("comment", add_comment_edt.getText().toString());
        map.put("publisher", fUser.getUid());

        ref.child(id).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(CommentActivity.this, "Комментарий добавлен", Toast.LENGTH_SHORT).show();
                    addNotification(postId, authorId);
                    add_comment_edt.setText("");
                } else {
                }
            }
        });

    }

    //    Оптроавить уведомление
    private void addNotification(String postId, String publisherId) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Notifications");
        String notificationId = databaseReference.push().getKey();
        HashMap<String, Object> map = new HashMap<>();
        map.put("userid", FirebaseAuth.getInstance().getCurrentUser().getUid());
        map.put("notifid", notificationId);
        map.put("text", "Комментарий: " + add_comment_edt.getText().toString());
        map.put("postid", postId);
        map.put("isPost", true);

        databaseReference.child(publisherId).child(notificationId).setValue(map);
    }

    //    Получить фото профиля
    private void getUserImage() {
        FirebaseDatabase.getInstance().getReference().child("Users").child(fUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user.getImageurl().equals("default")) {
                    image_profile_comment.setImageResource(R.drawable.ic_profile_user);
                } else {
                    try {
                        Glide.with(CommentActivity.this).load(user.getImageurl()).into(image_profile_comment);
                    } catch (Exception e) {

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
