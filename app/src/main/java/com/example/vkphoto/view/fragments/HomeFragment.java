package com.example.vkphoto.view.fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.example.vkphoto.presenter.HomePresenter;

public class HomeFragment extends Fragment {

    public static final String TAG = HomeFragment.class.getSimpleName();
    static final String BUNDLE_RECYCLER_TAG = "recycler_state";
    private HomePresenter presenter;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        postponeEnterTransition();
        ConstraintLayout rootView = (ConstraintLayout) inflater.inflate(R.layout.fragment_home, container, false);
        setHasOptionsMenu(true);
        presenter = new HomePresenter(this, rootView);
        presenter.init();
        presenter.getAll();
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
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(BUNDLE_RECYCLER_TAG, presenter.getRecyclerState());
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            presenter.setScrollPosition(savedInstanceState.getInt(BUNDLE_RECYCLER_TAG));
        }
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
