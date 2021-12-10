package com.airjob.chatappfinal_05_12;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.airjob.chatappfinal_05_12.fragments.ChatsFragment;
import com.airjob.chatappfinal_05_12.fragments.ProfileFragment;
import com.airjob.chatappfinal_05_12.fragments.UsersFragment;
import com.airjob.chatappfinal_05_12.login.StartActivity;
import com.airjob.chatappfinal_05_12.model.UserModel;
import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    // Var des widgets
    private CircleImageView profile_image;
    private TextView username;

    // Var Firebase
    private FirebaseUser firebaseUser;
    private FirebaseFirestore db;
    private DocumentReference userDocumentRef;

    // Initialisation des widgets
    private void init() {
        profile_image = findViewById(R.id.profile_image);
        username = findViewById(R.id.username);
    }

    // Initialisation de FirebaseUser
    private void initFirebase() {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();
        userDocumentRef = db.collection("Users").document(firebaseUser.getUid());
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialisation des widgets
        init();
        // Initialisation de Firebase
        initFirebase();

        // Gestion de la toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(""); // Pour ne pas avoir de titre

        // Query pour le SnapshotListner
        Query query = db.collection("Users");

        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        for(QueryDocumentSnapshot documentSnapshot : value){
                            UserModel user = documentSnapshot.toObject(UserModel.class);
                            username.setText(user.getUsername());
                            if (user.getImageURL().equals("default")){
                                profile_image.setImageResource(R.mipmap.ic_launcher);
                            } else {

                                Glide.with(getApplicationContext()).load(user.getImageURL()).into(profile_image);
                            }
                        }
                    }
                });


        // Gestion de l'affichage du ViewPager
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        ViewPager viewPager = findViewById(R.id.view_pager);

        ViewPagerAdapter viewPagerAdapter= new ViewPagerAdapter(getSupportFragmentManager());

        viewPagerAdapter.addFragment(new ChatsFragment(),"Chats");
        viewPagerAdapter.addFragment(new UsersFragment(),"Users");
        viewPagerAdapter.addFragment(new ProfileFragment()  ,"Profile");

        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    // Création du menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    // Gestion des clics du menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(MainActivity.this, StartActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                return true;
        }

        return false;
    }

    // Classe niché (inner claas) du ViewPager (version 1)
    class ViewPagerAdapter extends FragmentPagerAdapter {

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

        public void addFragment(Fragment fragment, String title) {
            fragments.add(fragment);
            titles.add(title);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }
    }

    // Gestion du statut de l'utilisateur
    private void status(String status) {
        userDocumentRef.update("status", status);
    }

    @Override
    protected void onResume() {
        super.onResume();
        status("Online");
    }

    @Override
    protected void onStop() {
        super.onStop();
        status("offline");
    }

}