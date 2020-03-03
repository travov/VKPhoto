package com.example.vkphoto.view.fragments;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import com.example.vkphoto.R;
import com.example.vkphoto.presenter.AlbumsPresenter;

public class AlbumsFragment extends Fragment {

    private AlbumsPresenter presenter;

    public AlbumsFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ConstraintLayout rootView = (ConstraintLayout) inflater.inflate(R.layout.fragment_albums, container, false);
        setHasOptionsMenu(true);
        presenter = new AlbumsPresenter(this, rootView);
        presenter.init();
        presenter.getAll();
        SwipeRefreshLayout layout = rootView.findViewById(R.id.swipe_container);
        presenter.setRefreshBehaviour(layout);
        return layout;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_toolbar_albums, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        presenter.menuSelectedItem(item.getItemId());
        return super.onOptionsItemSelected(item);
    }
}
