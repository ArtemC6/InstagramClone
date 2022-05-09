package com.example.instagram.Adapter;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;

import com.example.instagram.Fragments.FollowersFragment;
import com.example.instagram.Fragments.FollowingFragment;
import com.example.instagram.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class FragmentFollowersAdapter extends FragmentPagerAdapter {

    private String id;
    private int followingCont, followersCont;
    private String title = null;

    public FragmentFollowersAdapter(@NonNull FragmentManager fm, String id, int followingCont,int followersCont) {
        super(fm);
        this.id = id;
        this.followersCont = followersCont;
        this.followingCont = followingCont;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        // создаем новые фрагменты по позиции
        switch (position) {
            case 0:
                FollowingFragment followingFragment = new FollowingFragment();
                Bundle bundle = new Bundle();
                bundle.putString("id", id);
                followingFragment.setArguments(bundle);
                return followingFragment;
            case 1:
                FollowersFragment followersFragment = new FollowersFragment();
                Bundle bundle1 = new Bundle();
                bundle1.putString("id", id);
                followersFragment.setArguments(bundle1);
                return followersFragment;
            default:
                FollowingFragment followingFragment_2 = new FollowingFragment();
                Bundle bundle_2 = new Bundle();
                bundle_2.putString("id", id);
                followingFragment_2.setArguments(bundle_2);
                return followingFragment_2;

        }
    }

    // количетво фрагментов
    @Override
    public int getCount() {
        return 2;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        if (position == 0) {
            title = "Подписчики: " + String.valueOf(followersCont);
        }
        if (position == 1) {
            title = "Подписки: " + String.valueOf(followingCont);
        }

        return title;
    }
}
