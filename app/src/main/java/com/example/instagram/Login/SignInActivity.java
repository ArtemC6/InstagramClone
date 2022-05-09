package com.example.instagram.Login;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.instagram.Activity.MainActivity;
import com.example.instagram.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignInActivity extends AppCompatActivity {

    private LinearLayout bt_sinIn;
    private EditText ed_password_singIn, ed_email_singIn;
    private FirebaseAuth auth;
    private TextView tv_singIn;
    private ProgressBar pb_singIn;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singin);
        init();
    }

    // работа с кантенерами,отоброжения
    private void examination(Editable s) {
        if (s.length() >= 6) {
            bt_sinIn.setBackgroundResource(R.drawable.bg_button_blue);
            bt_sinIn.setClickable(true);
            tv_singIn.setTextColor(getResources().getColor(R.color.white));

            bt_sinIn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pb_singIn.setVisibility(View.VISIBLE);
                    tv_singIn.setVisibility(View.GONE);
                    String emil = ed_email_singIn.getText().toString();
                    String password = ed_password_singIn.getText().toString();

                    if (!TextUtils.isEmpty(emil) || !TextUtils.isEmpty(password)) {
                        singInUser(emil, password);
                    }
                }
            });

        } else {
            bt_sinIn.setBackgroundResource(R.drawable.bg_button_transparent_blue);
            tv_singIn.setTextColor(getResources().getColor(R.color.grey_66));
            pb_singIn.setVisibility(View.GONE);
            tv_singIn.setVisibility(View.VISIBLE);
        }
    }

    // метод для входа в аккаунт
    private void singInUser(String emil, String password) {
        auth.signInWithEmailAndPassword(emil, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    startActivity(new Intent(SignInActivity.this, MainActivity.class));
                    finish();
                } else {
                    pb_singIn.setVisibility(View.GONE);
                    tv_singIn.setVisibility(View.VISIBLE);
                    Toast.makeText(SignInActivity.this, "Произошла ошибка", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //   Переменые
    private void init() {
        ed_email_singIn = findViewById(R.id.ed_email_singIn);
        ed_password_singIn = findViewById(R.id.ed_password_singIn);
        bt_sinIn = findViewById(R.id.bt_sinIn);
        pb_singIn = findViewById(R.id.pb_singIn);
        tv_singIn = findViewById(R.id.tv_singIn);
        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        findViewById(R.id.liner_perexot).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignInActivity.this, RegistrationActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });

        ed_email_singIn.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                examination(s);
            }

        });
    }

    //  Переход в main есть есть аккаунт
    @Override
    protected void onStart() {
        super.onStart();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            startActivity(new Intent(SignInActivity.this, MainActivity.class));
            finish();
        }
    }

}