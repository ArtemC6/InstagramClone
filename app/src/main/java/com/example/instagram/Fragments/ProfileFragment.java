package com.example.instagram.Fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.instagram.Activity.AddStoryActivity;
import com.example.instagram.Activity.ChatActivity;
import com.example.instagram.Activity.EditProfileActivity;
import com.example.instagram.Activity.FollowersActivity;
import com.example.instagram.Activity.MainActivity;
import com.example.instagram.Activity.OptionsActivity;
import com.example.instagram.Adapter.PhotoAdapter;
import com.example.instagram.Adapter.ProfileFillAdapter;
import com.example.instagram.Adapter.UserRandomAdapterProfile;
import com.example.instagram.Model.Post;
import com.example.instagram.Model.Profil;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class ProfileFragment extends Fragment {

    private RecyclerView recycler_view_my_saved;
    private PhotoAdapter postAdapterSaves;
    private List<Post> mySavedPostsList;

    private RecyclerView recycler_fill_account;
    private ProfileFillAdapter profileAdapter;
    private List<Profil> fillProfileList;

    private RecyclerView recyclerPeopleRandom;
    private UserRandomAdapterProfile userRandomAdapterProfile;
    private List<User> userPeopleRandomList;

    private RecyclerView recycler_view_my_photo;
    private PhotoAdapter photoAdapter;
    private List<Post> myPhotoList;
    private User userMain = null;

    private boolean prof = false;
    private boolean biog = false;
    private boolean foll = false;

    private View view1, view2;
    private Dialog dialog;
    private Toolbar toolbarMyAccount, toolbarUserAccount;

    private TextView myPostsCount, countFillAccountText, nameProfileUser, fullNameUser,
            text_all_random_profile, bioUser, followersCount, myFollowingCount, userNameProfile;

    private ImageView myPictures, bottomArrowBack, doneIconImageMyOfficial, backProfileArrow, doneIconImageUserOfficial,
            savedPictures, imageProfileAddRandomUser, UserRandomAdd, optionsImage, image_profile_my;

    private Button edit_profile_my, followingUserBtn, ChatUserBtn;

    private FirebaseUser fUser;
    private SharedPreferences sharedPref;
    private LinearLayout linearProfileEmpty, layoutPeopleRandom, layoutFillAccount,
            linearProfileMyAccount, linearNoPublicationsMyAccount, linearFollowingUser, linearFollowing, linearFollowers;

    private String profileId;
    private ProgressBar progressBarProfile;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        init(view);
        setRandomContent(view);
        setProfileInfo(view);
        getSavedPosts();
        userInfo();
        getFollowersAndFollowingCount();
        getPostCount();
        myPhotos();
        btnFollowing();
        return view;
    }

    //   Если это аккаунт пользователя
    public void userAccount() {
        followingUserBtn.setVisibility(View.VISIBLE);
        ChatUserBtn.setVisibility(View.VISIBLE);
        linearProfileMyAccount.setVisibility(View.GONE);
        layoutFillAccount.setVisibility(View.GONE);
        layoutPeopleRandom.setVisibility(View.GONE);
        linearProfileEmpty.setVisibility(View.GONE);
        bottomArrowBack.setVisibility(View.GONE);
        linearFollowingUser.setVisibility(View.VISIBLE);
        toolbarMyAccount.setVisibility(View.GONE);
        toolbarUserAccount.setVisibility(View.VISIBLE);
    }

    //   Если это мой аккаунт
    public void myAccount() {
        linearProfileMyAccount.setVisibility(View.VISIBLE);
        edit_profile_my.setVisibility(View.VISIBLE);
        followingUserBtn.setVisibility(View.GONE);
        ChatUserBtn.setVisibility(View.GONE);
        bottomArrowBack.setVisibility(View.VISIBLE);
        linearFollowingUser.setVisibility(View.GONE);
        toolbarMyAccount.setVisibility(View.VISIBLE);
        toolbarUserAccount.setVisibility(View.GONE);
        edit_profile_my.setText("Редактировать профиль");
        clickProfile();
    }

    //    При подписки на пользователя
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

    //    Стиль кнопки подписаться
    private void btnFollowing() {
        followingUserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // если нажать пописаться
                if (followingUserBtn.getText().toString().equals(("Подписаться"))) {
                    // нынешний пользователь
                    FirebaseDatabase.getInstance().getReference().child("Follow").
                            child((FirebaseAuth.getInstance().getCurrentUser().getUid())).child("following").child(profileId).setValue(true);
                    // подписчик
                    FirebaseDatabase.getInstance().getReference().child("Follow").
                            child(profileId).child("followers").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(true);
                    followingUserBtn.setBackgroundResource(R.drawable.bg_button_bio_white);
                    followingUserBtn.setTextColor(getResources().getColor(R.color.black));

                    addNotification(profileId);
                } else {
                    FirebaseDatabase.getInstance().getReference().child("Follow").
                            child((FirebaseAuth.getInstance().getUid())).child("following").child(profileId).removeValue();

                    FirebaseDatabase.getInstance().getReference().child("Follow").
                            child(profileId).child("followers").child(FirebaseAuth.getInstance().getUid()).removeValue();
                    followingUserBtn.setBackgroundResource(R.drawable.bg_button_bio_blue);
                    followingUserBtn.setTextColor(getResources().getColor(R.color.white));
                }

                if (followingUserBtn.getText().toString().equals("Отписаться")) {
                    followingUserBtn.setBackgroundResource(R.drawable.bg_button_bio_white);
                    followingUserBtn.setTextColor(getResources().getColor(R.color.black));
                } else {
                    followingUserBtn.setBackgroundResource(R.drawable.bg_button_bio_blue);
                    followingUserBtn.setTextColor(getResources().getColor(R.color.white));
                }
            }
        });

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Follow").child(FirebaseAuth.getInstance().getUid())
                .child("following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    if (dataSnapshot.child(profileId).exists()) {
                        followingUserBtn.setText("Отписаться");
                        followingUserBtn.setBackgroundResource(R.drawable.bg_button_bio_white);
                        followingUserBtn.setTextColor(getResources().getColor(R.color.black));
                    } else {
                        followingUserBtn.setText("Подписаться");
                        followingUserBtn.setBackgroundResource(R.drawable.bg_button_bio_blue);
                        followingUserBtn.setTextColor(getResources().getColor(R.color.white));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //    Переменые
    private void init(View view) {
        toolbarMyAccount = view.findViewById(R.id.toolbarMyAccount);
        toolbarUserAccount = view.findViewById(R.id.toolbarUserAccount);
        fUser = FirebaseAuth.getInstance().getCurrentUser();

        image_profile_my = view.findViewById(R.id.image_profile_my);
        progressBarProfile = view.findViewById(R.id.progressBarProfile);
        layoutPeopleRandom = view.findViewById(R.id.layoutPeopleRandom);
        recycler_view_my_photo = view.findViewById(R.id.recycler_view_my_photo);
        linearNoPublicationsMyAccount = view.findViewById(R.id.linearNoPublicationsMyAccount);
        view1 = view.findViewById(R.id.view1);
        view2 = view.findViewById(R.id.view2);
        optionsImage = view.findViewById(R.id.optionsImage);
        linearFollowing = view.findViewById(R.id.linearFollowing);
        linearFollowers = view.findViewById(R.id.linearFollowers);
        imageProfileAddRandomUser = view.findViewById(R.id.imageProfileAddRandomUser);
        UserRandomAdd = view.findViewById(R.id.UserRandomAdd);
        linearProfileMyAccount = view.findViewById(R.id.linearProfileMyAccount);
        layoutFillAccount = view.findViewById(R.id.layoutFillAccount);
        countFillAccountText = view.findViewById(R.id.countFillAccountText);
        followersCount = view.findViewById(R.id.followersCount);
        linearFollowingUser = view.findViewById(R.id.linearFollowingUser);
        myFollowingCount = view.findViewById(R.id.myFollowingCount);
        myPostsCount = view.findViewById(R.id.myPostsCount);
        fullNameUser = view.findViewById(R.id.fullNameUser);
        linearProfileEmpty = view.findViewById(R.id.linearProfileEmpty);
        bioUser = view.findViewById(R.id.bioUser);
        doneIconImageMyOfficial = view.findViewById(R.id.doneIconImageMyOfficial);
        doneIconImageUserOfficial = view.findViewById(R.id.doneIconImageUserOfficial);
        backProfileArrow = view.findViewById(R.id.backProfileArrow);
        userNameProfile = view.findViewById(R.id.userNameProfile);
        myPictures = view.findViewById(R.id.my_pictures);
        savedPictures = view.findViewById(R.id.saved_pictures);
        edit_profile_my = view.findViewById(R.id.edit_profile_my);
        followingUserBtn = view.findViewById(R.id.followingUserBtn);
        ChatUserBtn = view.findViewById(R.id.ChatUserBtn);
        nameProfileUser = view.findViewById(R.id.nameProfileUser);
        recycler_view_my_saved = view.findViewById(R.id.recycler_view_my_saved);
        bottomArrowBack = view.findViewById(R.id.bottomArrowBack);
        myPhotoList = new ArrayList<>();
        recycler_view_my_photo.setVisibility(View.VISIBLE);
        recycler_view_my_saved.setVisibility(View.GONE);
        btnShowRandomPeople();

        String data = getContext().getSharedPreferences("PROFILE", Context.MODE_PRIVATE).getString("profileId", "none");
        if (data.equals("none")) {
            // мой акаунт
            profileId = fUser.getUid();
        } else {
            // аккаунт пользователя
            profileId = data;
            getContext().getSharedPreferences("PROFILE", Context.MODE_PRIVATE).edit().clear().apply();
        }

        sharedPref = this.getActivity().getSharedPreferences("Prof", Context.MODE_PRIVATE);

        if (profileId.equals(fUser.getUid())) {
            myAccount();
        } else {
            userAccount();
        }

        edit_profile_my.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edit_profile_my.getText().toString().equals("Редактировать профиль")) {
                    startActivity(new Intent(getContext(), EditProfileActivity.class));
                }
            }
        });

        myPictures.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view1.setBackground(getResources().getDrawable(R.color.black));
                view2.setBackground(getResources().getDrawable(R.color.white_92_transparent));
                recycler_view_my_photo.setVisibility(View.VISIBLE);
                recycler_view_my_saved.setVisibility(View.GONE);
            }
        });

        savedPictures.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view1.setBackground(getResources().getDrawable(R.color.white_92_transparent));
                view2.setBackground(getResources().getDrawable(R.color.black));
                linearProfileEmpty.setVisibility(View.GONE);
                recycler_view_my_photo.setVisibility(View.GONE);
                recycler_view_my_saved.setVisibility(View.VISIBLE);
            }
        });

        linearFollowers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), FollowersActivity.class);
                intent.putExtra("id", profileId);
                intent.putExtra("title", "Подписчики");
                startActivity(intent);
            }
        });

        linearFollowing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), FollowersActivity.class);
                intent.putExtra("id", profileId);
                intent.putExtra("title", "Подписки");
                startActivity(intent);
            }
        });

        optionsImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext());
                bottomSheetDialog.setContentView(R.layout.window_setings_profile);
                bottomSheetDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                bottomSheetDialog.setCanceledOnTouchOutside(true);
                TextView settingsProfile = bottomSheetDialog.findViewById(R.id.settingsProfile);
                TextView interestingUserText = bottomSheetDialog.findViewById(R.id.interestingUserText);

                settingsProfile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(getContext(), OptionsActivity.class));
                        bottomSheetDialog.dismiss();
                    }
                });

                interestingUserText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((FragmentActivity) getActivity()).getSupportFragmentManager().beginTransaction
                                ().replace(R.id.fragment_home_container_main, new InterestingPeopleFragment()).commit();
                        bottomSheetDialog.dismiss();
                    }
                });
                bottomSheetDialog.show();
            }
        });
    }

    //    Кнопка для показа рандомных людей
    private void btnShowRandomPeople() {
        imageProfileAddRandomUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (layoutPeopleRandom.getVisibility() == View.GONE) {
                    imageProfileAddRandomUser.setImageResource(R.drawable.ic_add_user_cancel);
                    layoutPeopleRandom.setVisibility(View.VISIBLE);
                } else {
                    imageProfileAddRandomUser.setImageResource(R.drawable.ic_add_user);
                    layoutPeopleRandom.setVisibility(View.GONE);
                }
            }
        });

        UserRandomAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (layoutPeopleRandom.getVisibility() == View.GONE) {
                    UserRandomAdd.setImageResource(R.drawable.ic_add_user_cancel);
                    layoutPeopleRandom.setVisibility(View.VISIBLE);
                } else {
                    UserRandomAdd.setImageResource(R.drawable.ic_add_user);
                    layoutPeopleRandom.setVisibility(View.GONE);
                }
            }
        });

        backProfileArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), MainActivity.class));
            }
        });
    }

    //    Добовление рандомныз людей
    private void setRandomContent(View view) {
        Random random = new Random();
        List<User> userListRanFoll = new ArrayList<>();
        layoutPeopleRandom = view.findViewById(R.id.layoutPeopleRandom);
        layoutPeopleRandom.setVisibility(View.GONE);
        text_all_random_profile = view.findViewById(R.id.text_all_random_profile);

        text_all_random_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((FragmentActivity) getActivity()).getSupportFragmentManager().beginTransaction().replace(R.id
                        .fragment_home_container_main, new InterestingPeopleFragment()).commit();
            }
        });

        recyclerPeopleRandom = view.findViewById(R.id.recyclerPeopleRandom);
        recyclerPeopleRandom.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        userPeopleRandomList = new ArrayList<>();

        userRandomAdapterProfile = new UserRandomAdapterProfile(getContext(), userPeopleRandomList, true);
        recyclerPeopleRandom.setAdapter(userRandomAdapterProfile);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userPeopleRandomList.clear();
                userListRanFoll.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    if (!user.getId().equals(fUser.getUid())) {
                        userListRanFoll.add(user);
                    }
                }

                Set<User> items_id = new HashSet<>();
                if (userListRanFoll.size() != 0) {
                    for (int i = 0; i < 30; i++) {
                        int index = random.nextInt(userListRanFoll.size());
                        User userRandom = userListRanFoll.get(index);
                        items_id.add(userRandom);
                    }
                }
                for (User user : items_id) {
                    userPeopleRandomList.add(user);
                }
                userRandomAdapterProfile.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        recycler_view_my_photo.setHasFixedSize(true);
        recycler_view_my_photo.setLayoutManager(new GridLayoutManager(getContext(), 3));
        photoAdapter = new PhotoAdapter(getContext(), myPhotoList);
        recycler_view_my_photo.setAdapter(photoAdapter);

        recycler_view_my_saved = view.findViewById(R.id.recycler_view_my_saved);
        recycler_view_my_saved.setHasFixedSize(true);
        recycler_view_my_saved.setLayoutManager(new GridLayoutManager(getContext(), 3));

        mySavedPostsList = new ArrayList<>();

        postAdapterSaves = new PhotoAdapter(getContext(), mySavedPostsList);
        recycler_view_my_saved.setAdapter(postAdapterSaves);
    }

    // Нажатия на фото пользователя
    private void clickProfile() {
        image_profile_my.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog = new Dialog(getContext());
                dialog.setContentView(R.layout.aler_dialog_profile_add);
                dialog.setCanceledOnTouchOutside(true);
                dialog.setCancelable(true);

                TextView textAddPhoto = dialog.findViewById(R.id.textAddPhoto);
                textAddPhoto.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(getContext(), EditProfileActivity.class));
                    }
                });

                TextView textAddStories = dialog.findViewById(R.id.textAddStories);
                textAddStories.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(getContext(), AddStoryActivity.class));
                    }
                });

                dialog.show();
            }
        });
    }

    //    Установка информации профиля
    private void setProfileInfo(View view) {
        recycler_fill_account = view.findViewById(R.id.recycler_fill_account);
        recycler_fill_account.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false));
        fillProfileList = new ArrayList<>();
        profileAdapter = new ProfileFillAdapter(getContext(), fillProfileList);

        String bio = sharedPref.getString("bio", "");
        int num = sharedPref.getInt("num", 0);
        if (bio.equals("")) {
            biog = false;
            fillProfileList.add(new Profil(2, "ic_bio_set", "Добавьте биографию", "Раскажите своим", "подписичкам немного о себе.",
                    "Изменить биографию", "bg_button_blue", "#FFFFFFFF"));
            profileAdapter.notifyDataSetChanged();
        } else {
            biog = true;
            fillProfileList.add(new Profil(2, "ic_bio_profile_finish", "Добавьте биографию",
                    "Раскажите своим", "подписичкам немного о себе.", "Изменить биографию", "bg_item_profile", "#FF000000"));
            profileAdapter.notifyDataSetChanged();
        }

        if (num >= 1) {
            foll = true;
            fillProfileList.add(new Profil(4, "ic_profile_people", "Найдите, на кого\nподписаться",
                    "Подписитесь на людей и", "темы, которые вам интерестны", "Найти ещё", "bg_item_profile", "#FF000000"));
            recycler_fill_account.setAdapter(profileAdapter);
        } else {
            foll = false;
            fillProfileList.add(new Profil(4, "ic_image_user", "Найдите, на кого\nподписаться",
                    "Подписитесь на людей и", "темы, которые вам интерестны", "Найти ещё", "bg_button_blue", "#FFFFFFFF"));
            recycler_fill_account.setAdapter(profileAdapter);
        }

        if (biog || prof || foll) {
            countFillAccountText.setText("3");
        }
        if (biog && prof && foll) {
            countFillAccountText.setText("4");
        }

        fillProfileList.add(new Profil(1, "ic_user_name_final", "Добавьте свое имя", "Добавьте имя и фамилию,",
                "чтобы друзья узнали что эта вы", "Редактировать имя", "bg_item_profile", "#FF000000"));
        profileAdapter.notifyDataSetChanged();

        recycler_fill_account.setAdapter(profileAdapter);
    }

    //  Получить сохроненые посты
    private void getSavedPosts() {
        final List<String> savedIds = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference().child("Saves").child(fUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    savedIds.add(snapshot.getKey());
                }

                FirebaseDatabase.getInstance().getReference().child("Posts").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot1) {
                        mySavedPostsList.clear();
                        for (DataSnapshot snapshot1 : dataSnapshot1.getChildren()) {
                            Post post = snapshot1.getValue(Post.class);

                            for (String id : savedIds) {
                                if (post.getPostid().equals(id)) {
                                    if (profileId.equals(fUser.getUid())) {
                                        mySavedPostsList.add(post);
                                    }
                                }
                            }
                        }
                        progressBarProfile.setVisibility(View.GONE);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //    Мои Фотографии
    private void myPhotos() {
        FirebaseDatabase.getInstance().getReference().child("Posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                myPhotoList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Post post = snapshot.getValue(Post.class);
                    if (post.getPublisher().equals(profileId)) {
                        myPhotoList.add(post);
                    }
                }
                if (myPhotoList.size() == 0) {
                    if (profileId.equals(fUser.getUid())) {
                        linearNoPublicationsMyAccount.setVisibility(View.GONE);
                        linearProfileEmpty.setVisibility(View.VISIBLE);
                    } else {
                        linearNoPublicationsMyAccount.setVisibility(View.VISIBLE);
                    }
                } else {
                    if (profileId.equals(fUser.getUid())) {
                        linearProfileEmpty.setVisibility(View.GONE);
                    }
                    Collections.reverse(myPhotoList);
                    photoAdapter.notifyDataSetChanged();
                }
                if (myPhotoList.size() >= 3) {
                    if (profileId.equals(fUser.getUid())) {
                        imageProfileAddRandomUser.setImageResource(R.drawable.ic_add_user);
                        UserRandomAdd.setImageResource(R.drawable.ic_add_user);
                    }
                } else {
                    if (profileId.equals(fUser.getUid())) {
                        imageProfileAddRandomUser.setImageResource(R.drawable.ic_add_user_cancel);
                        UserRandomAdd.setImageResource(R.drawable.ic_add_user_cancel);
                    }
                }
                progressBarProfile.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //  Получить количество постов пользователя
    private void getPostCount() {
        myPostsCount.setText(sharedPref.getString("postCounter", ""));
        FirebaseDatabase.getInstance().getReference().child("Posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int counter = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Post post = snapshot.getValue(Post.class);

                    if (post.getPublisher().equals(profileId)) counter++;
                }
                myPostsCount.setText(String.valueOf(counter));
                sharedPref.edit().putString("postCounter", String.valueOf(counter)).apply();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //    Получить подписки и количетво
    private void getFollowersAndFollowingCount() {
        followersCount.setText(sharedPref.getString("followers", ""));
        myFollowingCount.setText(sharedPref.getString("following", ""));

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Follow").child(profileId);
        ref.child("followers").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                followersCount.setText("" + dataSnapshot.getChildrenCount());
                sharedPref.edit().putString("followers", "" + dataSnapshot.getChildrenCount()).apply();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        ref.child("following").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                sharedPref.edit().putInt("num", (int) dataSnapshot.getChildrenCount()).apply();
                myFollowingCount.setText("" + dataSnapshot.getChildrenCount());
                sharedPref.edit().putString("following", "" + dataSnapshot.getChildrenCount()).apply();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //    Получить информацию о пользователе
    private void userInfo() {
        fullNameUser.setText(sharedPref.getString("name", ""));
        userNameProfile.setText(sharedPref.getString("username", ""));
        bioUser.setText(sharedPref.getString("bio", ""));

        FirebaseDatabase.getInstance().getReference().child("Users").child(profileId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                fullNameUser.setText(user.getUsername());
                userNameProfile.setText(user.getName());
                bioUser.setText(user.getBio());
                nameProfileUser.setText(user.getUsername());

                userMain = user;

                if (user.isPosition()) {
                    doneIconImageMyOfficial.setVisibility(View.VISIBLE);
                    doneIconImageUserOfficial.setVisibility(View.VISIBLE);
                } else {
                    doneIconImageMyOfficial.setVisibility(View.GONE);
                    doneIconImageUserOfficial.setVisibility(View.GONE);
                }

                ChatUserBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getContext(), ChatActivity.class);
                        intent.putExtra("useridrandom", user.getUseridraidom());
                        intent.putExtra("profailPic", user.getImageurl());
                        intent.putExtra("username", user.getUsername());
                        intent.putExtra("name", user.getName());
                        intent.putExtra("userid", user.getId());
                        intent.putExtra("userstatus", user.getStatus());
                        startActivity(intent);
                    }
                });

                sharedPref.edit().putString("name", user.getName()).apply();
                sharedPref.edit().putString("username", user.getUsername()).apply();
                sharedPref.edit().putString("bio", user.getBio()).apply();

                if (user.getImageurl().equals("default")) {
                    prof = false;
                    image_profile_my.setImageResource(R.drawable.ic_profile_user);
                    fillProfileList.add(new Profil(3, "ic_image_user", "Добавьте фото профиля", "Выберите фото для своего",
                            "профиля Instagram.", "Изменить фото", "bg_button_blue", "#FFFFFFFF"));
                    profileAdapter.notifyDataSetChanged();
                } else {
                    prof = true;
                    fillProfileList.add(new Profil(3, "ic_profile_fullness_done", "Добавьте фото профиля",
                            "Выберите фото для своего", "профиля Instagram.", "Изменить фото", "bg_item_profile", "#FF000000"));
                    profileAdapter.notifyDataSetChanged();
                    if (getView() != null) {
                        Glide.with(getContext())
                                .load(userMain.getImageurl())
                                .into(image_profile_my);
                    }
                }

                if (biog || prof || foll) {
                    countFillAccountText.setText("3");
                }
                if (biog && prof && foll) {
                    countFillAccountText.setText("4");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //    Закрывает деологовое окно
    @Override
    public void onResume() {
        super.onResume();

        if (dialog != null) {
            dialog.dismiss();
        } else {
        }
    }
}
