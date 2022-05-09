package com.example.instagram.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.instagram.R;
import com.theartofdev.edmodo.cropper.CropImage;

/*
Работа с постом
 */
public class PostActivity extends AppCompatActivity {

    private Uri imageUri;
    private ImageView closeWindowAddPost, addPost, imageAdded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        init();
    }

    //    Переменые
    private void init() {
        closeWindowAddPost = findViewById(R.id.closeWindowAddPost);
        imageAdded = findViewById(R.id.image_added);
        addPost = findViewById(R.id.addPost);

        closeWindowAddPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PostActivity.this, MainActivity.class));
                finish();
            }
        });

        addPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PostActivity.this, PostDescriptionActivity.class);
                intent.setData(imageUri);
                startActivity(intent);
            }
        });

        CropImage.activity().start(PostActivity.this);
    }

    // открыват фото
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            // записывается id картинки
            imageUri = result.getUri();
            imageAdded.setImageURI(imageUri);
        } else {
            startActivity(new Intent(PostActivity.this, MainActivity.class));
            finish();
        }
    }
}
