package com.example.vkphoto.view.fragments;

import android.os.Bundle;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.vkphoto.R;
import com.example.vkphoto.presenter.FriendsPresenter;

public class FriendsFragment extends Fragment {

    public static final String TAG = FriendsFragment.class.getSimpleName();
    private FriendsPresenter presenter;

    public FriendsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ConstraintLayout rootView = (ConstraintLayout) inflater.inflate(R.layout.fragment_friends, container, false);
        presenter = new FriendsPresenter(this, rootView);
        presenter.getAll();
        SwipeRefreshLayout swipeRefreshLayout = rootView.findViewById(R.id.friends_swipe_container);
        presenter.setRefreshBehaviour(swipeRefreshLayout);
        return swipeRefreshLayout;
    }

}
