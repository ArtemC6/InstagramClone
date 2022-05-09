package com.example.instagram.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.instagram.Fragments.HomeFragment;
import com.example.instagram.Fragments.NotificationFragment;
import com.example.instagram.Fragments.ProfileFragment;
import com.example.instagram.Fragments.SearchFragment;
import com.example.instagram.Login.SignInActivity;
import com.example.instagram.Model.Notification;
import com.example.instagram.Model.User;
import com.example.instagram.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import static android.telephony.AvailableNetworkInfo.PRIORITY_HIGH;

public class MainActivity extends AppCompatActivity {

    private Fragment selectorFragment;
    private ImageView homeMain, searchMain, addMain, heartMani, profileMain;
    private TextView dotNotifications;
    private FirebaseUser fUser;
    private User userName;

    private NotificationManager notificationManager;
    private static final int NOTIFY_ID = 101;
    private static final String CHANNEL_ID = "CHANNEL_ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    //  Переменые
    private void init() {
        userName = new User();
        searchMain = findViewById(R.id.searchMain);
        dotNotifications = findViewById(R.id.dotNotifications);
        homeMain = findViewById(R.id.homeMain);
        addMain = findViewById(R.id.addMain);
        heartMani = findViewById(R.id.heartMani);
        profileMain = findViewById(R.id.profileMain);

        notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        fUser = FirebaseAuth.getInstance().getCurrentUser();

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            try {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        readUser();
                        readNotifications();
                    }
                }).start();

                onlineTame();
                bottomMenu();
            } catch (Exception e) {
                FirebaseAuth.getInstance().signOut();
                finish();
            }
        } else {
            startActivity(new Intent(MainActivity.this, SignInActivity.class));
        }
    }

    //  Нижняя панель
    private void bottomMenu() {
        homeMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                online();
                selectorFragment = new HomeFragment();
                if (selectorFragment != null) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_home_container_main, selectorFragment).commit();
                }
            }
        });

        searchMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                online();
                selectorFragment = new SearchFragment();
                if (selectorFragment != null) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_home_container_main, selectorFragment).commit();
                }
            }
        });

        addMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                online();
                startActivity(new Intent(MainActivity.this, PostActivity.class));
            }
        });

        heartMani.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                online();
                dotNotifications.setVisibility(View.INVISIBLE);
                selectorFragment = new NotificationFragment();
                if (selectorFragment != null) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_home_container_main, selectorFragment).commit();
                }
            }
        });

        profileMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                online();
                selectorFragment = new ProfileFragment();
                if (selectorFragment != null) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_home_container_main, selectorFragment).commit();
                }
            }
        });

        Bundle intent = getIntent().getExtras();
        if (intent != null) {
            String profileId = intent.getString("publisherId");
            getSharedPreferences("PROFILE", MODE_PRIVATE).edit().putString("profileId", profileId).apply();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_home_container_main, new ProfileFragment()).commit();
        } else {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_home_container_main, new HomeFragment()).commit();
        }
    }

    //    Запись онлайн в базу с задержкой
    public static void onlineTame() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                online();
            }
        }, 1000);
    }

    //    Запись онлайн в базу
    public static void online() {
        Map<String, Object> map = new HashMap<>();
        map.put("status", "Сейчас в сети");
        FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getUid()).updateChildren(map);
    }

    //    Запись в базу офлаин
    public static void offline() {
        Map<String, Object> map = new HashMap<>();
        map.put("status", "Был недавно ");
        map.put("statustame", new Date().getTime());
        FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getUid()).updateChildren(map);
    }

    //    Считывание данных пользователь
    private void readUser() {
        FirebaseDatabase.getInstance().getReference().child("Users").child(fUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                try {
                    if (user.getImageurl().equals("default")) {
                        profileMain.setImageResource(R.drawable.ic_profile_user);
                    } else {
                        if (getApplicationContext() != null)
                            Glide.with(getApplicationContext())
                                    .load(user.getImageurl())
                                    .into(profileMain);
                    }
                } catch (Exception e) {
                    FirebaseAuth.getInstance().signOut();
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //    При остановлении
    @Override
    protected void onStop() {
        super.onStop();
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(MainActivity.this, SignInActivity.class));
        } else {
            offline();
        }
    }

    //    При запуски
    @Override
    protected void onStart() {
        super.onStart();
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivity(new Intent(MainActivity.this, SignInActivity.class));
        } else {
            online();
        }
    }

    //    Считывание уведомлений
    private void readNotifications() {
        LinkedList<Notification> notificationList = new LinkedList<>();
        FirebaseDatabase.getInstance().getReference().child("Notifications").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    Notification notification = dataSnapshot1.getValue(Notification.class);
                    notificationList.add(notification);
                }

                int num = (int) dataSnapshot.getChildrenCount();

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (num != 0) {
                            dotNotifications.setVisibility(View.VISIBLE);
                            phone_notification(notificationList.getLast());
                        } else {
                            dotNotifications.setVisibility(View.INVISIBLE);

                        }
                    }
                }, 500);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void phone_notification(Notification notification) {
        getName(notification);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this,
                0, notificationIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        // Установка большой картинки
//        BitmapFactory.Options options = new BitmapFactory.Options();
//        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.arrowicon, options);

//        Toast.makeText(this, getName(notification), Toast.LENGTH_SHORT).show();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                NotificationCompat.Builder notificationBuilder =
                        new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                                .setAutoCancel(false)
                                .setSmallIcon(R.drawable.ic_instagram) /* Установка значка уведлений*/
                                .setWhen(System.currentTimeMillis()) /* Время кведолмения*/
                                .setContentIntent(contentIntent) /* При нажати перехот в активность*/
                                .setContentTitle("Instagram") /*Заголовок*/
                                .setContentText(userName.getUsername() + " " + notification.getText())
//                                .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(bitmap)) /* Установка большой картинки */
                                .setSound(defaultSoundUri) /* Установка звука*/
                                .setAutoCancel(true) /* Авто закрытие после нажатия*/
                                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_profile_user)) /* Установка значка уведомления*/
//                        .addAction(R.drawable.pfofaleicon,"Запуск",contentIntent) /* Нижняя надпись*/
//                        .setStyle(new NotificationCompat.InboxStyle()
//                                .addLine("Line 1")
//                                .addLine("Line 2")
//                                .addLine("Line 3")
//                                .setBigContentTitle("Extended title")
//                                .setSummaryText("+5 more")) /* Сообощения в столбик*/
                                .setPriority(PRIORITY_HIGH); /* Устанока приоретета*/

                createChannelIfNeeded(notificationManager);
                notificationManager.notify(NOTIFY_ID, notificationBuilder.build());


//        // Установка большой картинки
//        BitmapFactory.Options options = new BitmapFactory.Options();
//        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.arrowicon, options);
//
//        NotificationCompat.Builder notificationBuilder =
//                new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
//                        .setAutoCancel(false)
//                        .setSmallIcon(R.drawable.iconinst) /* Установка значка уведлений*/
//                        .setWhen(System.currentTimeMillis()) /* Время кведолмения*/
//                        .setContentIntent(contentIntent) /* При нажати перехот в активность*/
//                        .setContentTitle("Title") /*Заголовок*/
//                        .setContentText("text") /* Текст*/
//                        .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(bitmap)) /* Установка большой картинки */
//                        .setSound(defaultSoundUri) /* Установка звука*/
//                        .setAutoCancel(true) /* Авто закрытие после нажатия*/
////                        .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.profilo)) /* Установка значка уведомления*/
////                        .addAction(R.drawable.pfofaleicon,"Запуск",contentIntent) /* Нижняя надпись*/
////                        .setStyle(new NotificationCompat.InboxStyle()
////                                .addLine("Line 1")
////                                .addLine("Line 2")
////                                .addLine("Line 3")
////                                .setBigContentTitle("Extended title")
////                                .setSummaryText("+5 more")) /* Сообощения в столбик*/
//                        .setPriority(PRIORITY_HIGH); /* Устанока приоретета*/

//        createChannelIfNeeded(notificationManager);
//        notificationManager.notify(NOTIFY_ID, notificationBuilder.build());

            }
        }, 200);

    }

    public void getName(Notification notification) {
        FirebaseDatabase.getInstance().getReference().child("Users").child(notification.getUserid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String nam = snapshot.child("username").getValue().toString();
                    userName.setUsername(nam);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public static void createChannelIfNeeded(NotificationManager manager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_ID, NotificationManager.IMPORTANCE_DEFAULT);
            manager.createNotificationChannel(notificationChannel);
        }
    }
}