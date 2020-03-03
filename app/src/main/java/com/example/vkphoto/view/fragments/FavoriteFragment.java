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
import com.example.vkphoto.presenter.FavoritePresenter;

public class FavoriteFragment extends Fragment {

    public static final String TAG = FavoriteFragment.class.getSimpleName();
    private FavoritePresenter presenter;

    public FavoriteFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ConstraintLayout rootView = (ConstraintLayout) inflater.inflate(R.layout.fragment_favorite, container, false);
        setHasOptionsMenu(true);
        presenter = new FavoritePresenter(this, rootView);
        presenter.init();
        presenter.getFave();
        SwipeRefreshLayout swipeRefreshLayout = rootView.findViewById(R.id.swipe_container);
        presenter.setRefreshBehaviour(swipeRefreshLayout);
        return swipeRefreshLayout;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_toolbar_home, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        presenter.menuSelectedItem(item.getItemId());
        return super.onOptionsItemSelected(item);
    }

    public void onFragmentReenter(int exitPosition) {
        presenter.scrollToExitPosition(exitPosition);
    }

}
