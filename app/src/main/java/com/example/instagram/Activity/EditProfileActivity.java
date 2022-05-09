package com.example.instagram.Activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.instagram.Model.User;
import com.example.instagram.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

public class EditProfileActivity extends AppCompatActivity {

    private ImageView closeEditProfile, saveEditProfile, imageProfile;
    private TextView changePhoto;
    private MaterialEditText fullNameEditProfile, userNameEditProfile, bioEditProfile;

    private FirebaseUser fUser;
    private Uri mImageUri;
    private StorageTask uploadTask;
    private StorageReference storageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        init();
        dataCaching();
    }

    //    Переменые
    private void init() {
        closeEditProfile = findViewById(R.id.closeEditProfile);
        imageProfile = findViewById(R.id.image_profile);
        saveEditProfile = findViewById(R.id.saveEditProfile);
        changePhoto = findViewById(R.id.change_photo);
        fullNameEditProfile = findViewById(R.id.fullNameEditProfile);
        userNameEditProfile = findViewById(R.id.userNameEditProfile);
        bioEditProfile = findViewById(R.id.bioEditProfile);
        MainActivity.onlineTame();

        fUser = FirebaseAuth.getInstance().getCurrentUser();
        storageRef = FirebaseStorage.getInstance().getReference().child("Uploads");
        String cs = Context.CONNECTIVITY_SERVICE;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(cs);
        if (cm.getActiveNetworkInfo() == null) {

        } else {
            FirebaseDatabase.getInstance().getReference().child("Users").child(fUser.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);

                    fullNameEditProfile.setText(user.getName());
                    userNameEditProfile.setText(user.getUsername());
                    bioEditProfile.setText(user.getBio());

                    getSharedPreferences("profileUser", MODE_PRIVATE).edit().putString("name", user.getName()).apply();
                    getSharedPreferences("profileUser", MODE_PRIVATE).edit().putString("username", user.getUsername()).apply();
                    getSharedPreferences("profileUser", MODE_PRIVATE).edit().putString("bio", user.getBio()).apply();

                    if (user.getImageurl().equals("default")) {
                        imageProfile.setImageResource(R.drawable.ic_profile_user);
                    } else {
                        Glide.with(getApplicationContext())
                                .load(user.getImageurl())
                                .into(imageProfile);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        closeEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        saveEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, Object> map = new HashMap<>();
                map.put("name", fullNameEditProfile.getText().toString());
                map.put("username", userNameEditProfile.getText().toString());
                map.put("bio", bioEditProfile.getText().toString());
                FirebaseDatabase.getInstance().getReference().child("Users").child(fUser.getUid()).updateChildren(map);
                finish();
            }
        });

        changePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity().setCropShape(CropImageView.CropShape.OVAL).start(EditProfileActivity.this);
            }
        });

        imageProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity().setCropShape(CropImageView.CropShape.OVAL).start(EditProfileActivity.this);
            }
        });
    }

    //    Кэширование данных пльзоватлея
    private void dataCaching() {
        fullNameEditProfile.setText(getSharedPreferences("profileUser", MODE_PRIVATE).getString("name", ""));
        userNameEditProfile.setText(getSharedPreferences("profileUser", MODE_PRIVATE).getString("username", ""));
        bioEditProfile.setText(getSharedPreferences("profileUser", MODE_PRIVATE).getString("bio", ""));
        imageProfile.setImageResource(R.drawable.ic_profile_user);
    }

    //    Загрузить фото
    private void uploadImage() {
        final Dialog pd = new Dialog(this);
        pd.setContentView(R.layout.loading_window_add_post);
        pd.setCancelable(false);
        pd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pd.create();
        pd.show();

        if (mImageUri != null) {
            final StorageReference fileRef = storageRef.child(System.currentTimeMillis() + ".jpeg");

            uploadTask = fileRef.putFile(mImageUri);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    return fileRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        String url = downloadUri.toString();
                        FirebaseDatabase.getInstance().getReference().child("Users").child(fUser.getUid()).child("imageurl").setValue(url);
                        pd.dismiss();
                    } else {
                    }
                }
            });
        } else {
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        MainActivity.offline();
    }

    @Override
    protected void onStart() {
        super.onStart();
        MainActivity.online();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            mImageUri = result.getUri();

            uploadImage();
        } else {
        }
    }
}
