package com.example.instagram.Login;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.instagram.Activity.AddPhotoToProfileActivity;
import com.example.instagram.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegistrationActivity extends AppCompatActivity {

    private EditText ed_email_registration, ed_password_registration, ed_username_registration, ed_usernameChange;
    private Button bt_registration_final, bt_next_start, bt_next_registration, bt_next_name_change;
    private DatabaseReference mRootRef;
    private TextView text_email, changeName_, text_bot, text_bot_main, textPasswordIncorrect;
    private LinearLayout layout_registration_start, layout_registration_user, layout_finis,
            layout_change, linearBottomText, linearMainNext;
    private FirebaseAuth auth;
    private Dialog pd;
    private View view;
    private CheckBox checkbox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        init();
    }

    // работа с кантенерами,отоброжения
    private void containers(Editable s) {
        if (s.length() >= 6) {
            textPasswordIncorrect.setVisibility(View.GONE);
            ed_password_registration.setBackground(getResources().getDrawable(R.drawable.ed_text_bagraund));
            bt_next_registration.setBackgroundResource(R.drawable.bg_button_blue);
            bt_next_registration.setTextColor(getResources().getColor(R.color.white));

            bt_next_registration.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    linearBottomText.setVisibility(View.VISIBLE);
                    linearMainNext.setVisibility(View.GONE);
                    text_email.setText(ed_email_registration.getText().toString() + "?");
                    layout_registration_user.setVisibility(View.GONE);
                    layout_finis.setVisibility(View.VISIBLE);

                    changeName_.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            layout_finis.setVisibility(View.GONE);
                            layout_change.setVisibility(View.VISIBLE);
                            ed_usernameChange.setText(ed_email_registration.getText().toString());
                        }
                    });
                }
            });
        } else {
            ed_password_registration.setBackground(getResources().getDrawable(R.drawable.ed_password_incorrect));
            textPasswordIncorrect.setVisibility(View.VISIBLE);
            bt_next_registration.setBackgroundResource(R.drawable.bg_button_transparent_blue);
            bt_next_registration.setTextColor(getResources().getColor(R.color.grey_66));
        }
    }

    // переменые
    private void init() {
        pd = new Dialog(this);
        pd.setContentView(R.layout.loading_window_registration);
        pd.setCancelable(true);
        pd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        ed_email_registration = findViewById(R.id.ed_email_registration);
        view = findViewById(R.id.view);
        textPasswordIncorrect = findViewById(R.id.textPasswordIncorrect);
        linearBottomText = findViewById(R.id.linearBottomText);
        text_bot = findViewById(R.id.text_bot);
        linearMainNext = findViewById(R.id.linearMainNext);
        checkbox = findViewById(R.id.checkbox);
        text_bot_main = findViewById(R.id.text_bot_main);
        ed_username_registration = findViewById(R.id.ed_username_registration);
        ed_usernameChange = findViewById(R.id.ed_usernameChange);
        bt_next_start = findViewById(R.id.bt_next_start);
        bt_registration_final = findViewById(R.id.bt_registration_final);
        text_email = findViewById(R.id.text_email);
        bt_next_registration = findViewById(R.id.bt_next_registration);
        layout_registration_start = findViewById(R.id.layout_registration_start);
        layout_registration_user = findViewById(R.id.layout_registration_user);
        layout_change = findViewById(R.id.layout_change);
        changeName_ = findViewById(R.id.changeName_);
        bt_next_name_change = findViewById(R.id.bt_next_name_change);
        layout_finis = findViewById(R.id.layout_finis);
        ed_password_registration = findViewById(R.id.ed_password_registration);
        mRootRef = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance();
        checkbox.setButtonTintList(ColorStateList.valueOf(getColor(R.color.blue_92)));
        checkbox.setChecked(true);

//      Вернутся
        findViewById(R.id.liner_perexot_nazad).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Регистрация
        bt_registration_final.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = ed_email_registration.getText().toString();
                String username = ed_username_registration.getText().toString();
                String password = ed_password_registration.getText().toString();
                String name = ed_usernameChange.getText().toString();

                if (!TextUtils.isEmpty(email) || !TextUtils.isEmpty(password) || !TextUtils.isEmpty(username)) {
                    registrationUser(email, password, name, username);
                }
            }
        });

        ed_password_registration.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                containers(s);
            }
        });

        ed_email_registration.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                nextStart(s);
            }
        });

//      Изменить имя
        bt_next_name_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout_finis.setVisibility(View.VISIBLE);
                layout_change.setVisibility(View.GONE);

                text_email.setText(ed_usernameChange.getText().toString() + "?");
            }
        });
    }

    //  Продолжить
    private void nextStart(Editable s) {
        if (s.length() == 0) {
            bt_next_start.setBackgroundResource(R.drawable.bg_button_transparent_blue);
            bt_next_start.setTextColor(getResources().getColor(R.color.grey_66));
        } else {
            bt_next_start.setBackgroundResource(R.drawable.bg_button_blue);
            bt_next_start.setTextColor(getResources().getColor(R.color.white));
            if (s.length() >= 6)
                bt_next_start.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        view.setVisibility(View.GONE);
                        text_bot_main.setTextColor(getResources().getColor(R.color.text));
                        text_bot_main.setText("Ваши контакты будут ругулярно синхранизироваться и хронится на сервирах Instagram.Благодаря этому" +
                                "вам и другим пользователям будет проще находить друзей,а мы сможем улутшать качество своих услуг.Чтобы удалить контакты," +
                                " отмените их синхранизацию в настройках.");

                        layout_registration_start.setVisibility(View.GONE);
                        layout_registration_user.setVisibility(View.VISIBLE);
                    }
                });
        }
    }

    //  Регистрация аккаунта
    private void registrationUser(String email, String password, String name, String username) {
        pd.show();
        auth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                HashMap<String, Object> map = new HashMap<>();

                if (!TextUtils.isEmpty(name)) {
                    map.put("name", name);
                } else {
                    map.put("name", email);
                }

                map.put("email", email);
                map.put("username", username);
                map.put("id", auth.getCurrentUser().getUid());
                map.put("bio", "");
                map.put("imageurl", "default");
                map.put("status", "Сейчас в сети");
                map.put("position", false);

                mRootRef.child("Users").child(auth.getCurrentUser().getUid()).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            pd.dismiss();
                            startActivity(new Intent(RegistrationActivity.this, AddPhotoToProfileActivity.class));
                            finish();
                        } else {
                            pd.dismiss();
                        }
                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

    }
}