package com.example.vkphoto.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import com.example.vkphoto.R;
import com.example.vkphoto.presenter.AlbumsPhotoPresenter;
import com.example.vkphoto.view.fragments.AlbumsFragment;
import com.example.vkphoto.view.fragments.AlbumsPhotoFragment;
import com.example.vkphoto.view.fragments.FavoriteFragment;
import com.example.vkphoto.view.fragments.FriendsFragment;
import com.example.vkphoto.view.fragments.HomeFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Fragment homeFragment;
    private Fragment friendsFragment;
    private Fragment favoriteFragment;
    private Fragment albumsFragment;
    private Fragment active;

    private Spinner spinner;
    private BottomNavigationView bottomNavigationView;
    private Deque<Integer> stack = new ArrayDeque<>();
    private boolean isBackPressed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar tb = findViewById(R.id.toolbar);
        setSupportActionBar(tb);
        ActionBar supportActionBar = getSupportActionBar();
        supportActionBar.setDisplayShowTitleEnabled(false);
        final FragmentManager manager = getSupportFragmentManager();
        spinner = tb.findViewById(R.id.toolbar_spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.simple_spinner_item,
                new String[]{getString(R.string.all_photos_spinner), getString(R.string.albums)});
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown);
        spinner.setAdapter(adapter);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(menuItem -> {
            switch (menuItem.getItemId()){
                case R.id.action_home:
                    if (!isBackPressed)
                        pushFragmentIntoStack(R.id.action_home);
                    isBackPressed = false;
                    changeFragment(manager, homeFragment);
                    spinner.setVisibility(View.VISIBLE);
                    spinner.setSelection(0);
                    supportActionBar.setDisplayShowTitleEnabled(false);
                    return true;
                case R.id.action_friends:
                    if (!isBackPressed)
                        pushFragmentIntoStack(R.id.action_friends);
                    isBackPressed = false;
                    if (friendsFragment == null) {
                        friendsFragment = new FriendsFragment();
                        manager.beginTransaction().add(R.id.fragment_container, friendsFragment, "2").hide(friendsFragment).commit();
                    }
                    changeFragment(manager, friendsFragment);
                    spinner.setVisibility(View.INVISIBLE);
                    supportActionBar.setDisplayShowTitleEnabled(true);
                    supportActionBar.setTitle(R.string.friends);
                    return true;
                case R.id.action_favorite:
                    if (!isBackPressed)
                        pushFragmentIntoStack(R.id.action_favorite);
                    isBackPressed = false;
                    if (favoriteFragment == null) {
                        favoriteFragment = new FavoriteFragment();
                        manager.beginTransaction().add(R.id.fragment_container, favoriteFragment, "3").hide(favoriteFragment).commit();
                    }
                    changeFragment(manager, favoriteFragment);
                    spinner.setVisibility(View.INVISIBLE);
                    supportActionBar.setDisplayShowTitleEnabled(true);
                    supportActionBar.setTitle(R.string.fave_photos);
                    return true;
            }
            return false;
        });

        if (savedInstanceState == null) {
            homeFragment = new HomeFragment();
            active = homeFragment;
            manager.beginTransaction().add(R.id.fragment_container, homeFragment, "1").commit();
            bottomNavigationView.setSelectedItemId(R.id.action_home);
        }
        else {
            String tag = savedInstanceState.getString("tag");
            active = manager.findFragmentByTag(tag);
            Fragment savedHomeFragment = manager.findFragmentByTag("1");
            Fragment savedFriendsFragment = manager.findFragmentByTag("2");
            Fragment savedFavoriteFragment = manager.findFragmentByTag("3");
            Fragment savedAlbumsFragment = manager.findFragmentByTag("4");
            if (savedHomeFragment != null)
                homeFragment = savedHomeFragment;
            if (savedFriendsFragment != null)
                friendsFragment = savedFriendsFragment;
            if (savedFavoriteFragment != null)
                favoriteFragment = savedFavoriteFragment;
            if (savedAlbumsFragment != null)
                albumsFragment = savedAlbumsFragment;
            bottomNavigationView.setSelectedItemId(savedInstanceState.getInt("selectedItem"));
            stack = new ArrayDeque<>(savedInstanceState.getIntegerArrayList("stack"));
        }

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (active instanceof HomeFragment) {
                    switch (parent.getItemAtPosition(position).toString()) {
                        case "All photos":
                            changeFragment(manager, homeFragment);
                            break;
                        case "Albums":
                            if (!isBackPressed)
                                pushFragmentIntoStack(R.id.action_home);
                            isBackPressed = false;
                            if (albumsFragment == null) {
                                albumsFragment = new AlbumsFragment();
                                manager.beginTransaction().add(R.id.fragment_container, albumsFragment, "4").hide(albumsFragment).commit();
                            }
                            changeFragment(manager, albumsFragment);
                            break;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                return;
            }
        });
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("tag", active.getTag());
        outState.putInt("selectedItem", bottomNavigationView.getSelectedItemId());
        outState.putIntegerArrayList("stack", new ArrayList<>(stack));
    }

    @Override
    public void onActivityReenter(int resultCode, Intent data) {
        super.onActivityReenter(resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            if (active instanceof HomeFragment) {
                HomeFragment fragment = (HomeFragment) active;
                int exitPosition = data.getIntExtra("exit_position", 0);
                fragment.onFragmentReenter(exitPosition);
            }
            else if (active instanceof FavoriteFragment) {
                FavoriteFragment fragment = (FavoriteFragment) active;
                int exitPosition = data.getIntExtra("exit_position", 0);
                fragment.onFragmentReenter(exitPosition);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AlbumsPhotoPresenter.RESULT_LOAD_IMG && resultCode == RESULT_OK && active instanceof AlbumsPhotoFragment) {
            if (data.getClipData() != null) {
                int count = data.getClipData().getItemCount();
                List<Uri> uris = new ArrayList<>();
                for(int i = 0; i < count; i++) {
                    Uri imageUri = data.getClipData().getItemAt(i).getUri();
                    uris.add(imageUri);
                }
                AlbumsPhotoFragment fragment = (AlbumsPhotoFragment) active;
                List<String> paths = fragment.convertUrisIntoFiles(uris);
                SharedPreferences settings = getSharedPreferences("PREFS_NAME", MODE_PRIVATE);
                int albumId = settings.getInt("albumId", 0);
                fragment.save(albumId, paths);

            }
            else if (data.getData() != null) {
                Uri uri = data.getData();
                List<Uri> uris = new ArrayList<>();
                uris.add(uri);
                AlbumsPhotoFragment fragment = (AlbumsPhotoFragment) active;
                List<String> path = fragment.convertUrisIntoFiles(uris);
                SharedPreferences settings = getSharedPreferences("PREFS_NAME", MODE_PRIVATE);
                int albumId = settings.getInt("albumId", 0);
                fragment.save(albumId, path);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (stack.size() > 1) {
            isBackPressed = true;
            stack.pop();
            bottomNavigationView.setSelectedItemId(stack.peek());
            spinner.setSelection(0);
        }
        else
            super.onBackPressed();
    }

    public void changeFragment(FragmentManager manager, Fragment fragment) {
        manager.beginTransaction().hide(active).show(fragment).commit();
        active = fragment;
    }


    public void pushFragmentIntoStack(int id) {
        if (stack.size() < 5)
            stack.push(id);
        else {
            stack.removeLast();
            stack.push(id);
        }
    }
}
