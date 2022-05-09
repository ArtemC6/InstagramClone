package com.example.instagram.Activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.instagram.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;

public class AddStoryActivity extends AppCompatActivity {

    private Uri imageUri;
    private String imageUrl;
    private ImageView image_photo, image_Next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_story);
        init();
    }

    //    Переменые
    private void init() {
        image_photo = findViewById(R.id.image_photo);
        image_Next = findViewById(R.id.image_Next);
        CropImage.activity().start(AddStoryActivity.this);

        findViewById(R.id.arrowBackStoryAdd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        image_Next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upload();
            }
        });
    }

    // Загружаем сториз
    private void upload() {
        Dialog pd = new Dialog(this);
        pd.setContentView(R.layout.loading_window_add_post);
        pd.setCancelable(false);
        pd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pd.create();
        pd.show();
        // если есть картинки
        if (imageUri != null) {
            final StorageReference filePath = FirebaseStorage.getInstance().
                    getReference("Stories").child(System.currentTimeMillis() + "." + getFileExtension(imageUri));

            StorageTask uploadTask = filePath.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    return filePath.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    Uri downloadUri = task.getResult();
                    imageUrl = downloadUri.toString();

                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Story");
                    String storyId = ref.push().getKey();
                    long time = System.currentTimeMillis() + 86400000;

                    HashMap<String, Object> map = new HashMap<>();
                    map.put("storiesid", storyId);
                    map.put("imageurl", imageUrl);
                    map.put("timestart", ServerValue.TIMESTAMP);
                    map.put("timeend", time);
                    map.put("publisher", FirebaseAuth.getInstance().getCurrentUser().getUid());

                    ref.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(storyId).setValue(map);

                    pd.dismiss();
                    if (task.isSuccessful()) {
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                startActivity(new Intent(AddStoryActivity.this, MainActivity.class));
                                finish();
                            }
                        }, 600);

                    }
                }
            });
        }
    }

    // Конвент
    private String getFileExtension(Uri uri) {
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(this.getContentResolver().getType(uri));
    }

    // Получить uri
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            // записывается id картинки
            imageUri = result.getUri();
            image_photo.setImageURI(imageUri);
        } else {
            startActivity(new Intent(AddStoryActivity.this, MainActivity.class));
            finish();
        }
    }
}