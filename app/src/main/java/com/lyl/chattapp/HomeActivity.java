package com.lyl.chattapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lyl.chattapp.Fragments.ChatsFragment;
import com.lyl.chattapp.Fragments.ProfileFragment;
import com.lyl.chattapp.Fragments.UsersFragment;
import com.lyl.chattapp.Model.Chat;
import com.lyl.chattapp.Model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeActivity extends AppCompatActivity {

    CircleImageView profileImage;
    TextView username;

    FirebaseUser firebaseUser;
    DatabaseReference dbReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("");

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        dbReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        profileImage = (CircleImageView) findViewById(R.id.profileImage);
        username = (TextView) findViewById(R.id.username);

        dbReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                username.setText(user.getUsername());
                if(user.getImageUrl().equals("default")){
                    profileImage.setImageResource(R.mipmap.ic_launcher);
                }
                else{

                  Glide.with(getApplicationContext()).load(user.getImageUrl()).into(profileImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        final TabLayout tabLayout = findViewById(R.id.tabLayout);
        final ViewPager viewPager = findViewById(R.id.viewPager);



        dbReference = FirebaseDatabase.getInstance().getReference("Chats");
        dbReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
                int unread =0;
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Chat chat =dataSnapshot.getValue(Chat.class);

                    if(chat.getReceiver().equals(firebaseUser.getUid()) && !chat.isIsseen()){
                        unread++;
                    }
                }

                if(unread == 0){
                    viewPagerAdapter.addFragment(new ChatsFragment(), "Chats");
                }
                else{
                    viewPagerAdapter.addFragment(new ChatsFragment(), "("+unread+")Chats");
                }

                viewPagerAdapter.addFragment(new UsersFragment(),"Users");
                viewPagerAdapter.addFragment(new ProfileFragment(),"Profile");

                viewPager.setAdapter(viewPagerAdapter);
                tabLayout.setupWithViewPager(viewPager);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.logout:
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(HomeActivity.this,MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            return true;
        }
        return false;
    }

    class ViewPagerAdapter extends FragmentPagerAdapter{

        private ArrayList<Fragment> fragments;
        private ArrayList<String> titles;

        ViewPagerAdapter(FragmentManager fm){
            super(fm);
            this.fragments = new ArrayList<>();
            this.titles = new ArrayList<>();
        }
        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        public void addFragment(Fragment fragment, String title){
            fragments.add(fragment);
            titles.add(title);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }
    }

    private void status(String status){
        dbReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status",status);

        dbReference.updateChildren(hashMap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        status("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        status("offline");
    }
}