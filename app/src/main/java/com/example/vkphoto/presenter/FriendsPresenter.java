package com.example.vkphoto.presenter;

import android.util.Log;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.example.vkphoto.R;
import com.example.vkphoto.model.VKUser;
import com.example.vkphoto.requests.RequestResult;
import com.example.vkphoto.requests.VKFriendsRequest;
import com.example.vkphoto.utils.FriendsRecyclerViewAdapter;
import com.example.vkphoto.view.fragments.FriendsFragment;
import com.vk.api.sdk.VK;
import com.vk.api.sdk.VKApiCallback;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;

public class FriendsPresenter {

    public static final String TAG = FriendsPresenter.class.getSimpleName();
    private Fragment fragment;
    private ViewGroup view;
    private RecyclerView rv;

    public FriendsPresenter(Fragment fragment, ViewGroup view) {
        this.fragment = fragment;
        this.view = view;
        rv = view.findViewById(R.id.friends_recycler);
        rv.setAdapter(new FriendsRecyclerViewAdapter(new ArrayList<>()));
        rv.setLayoutManager(new LinearLayoutManager(fragment.getActivity()));
    }

    public void getAll() {
        VKFriendsRequest req = new VKFriendsRequest();
        VK.execute(req, new VKApiCallback<RequestResult<VKUser>>() {
            @Override
            public void success(RequestResult<VKUser> result) {
                Log.i(TAG, "get all friends");
                FriendsRecyclerViewAdapter adapter = (FriendsRecyclerViewAdapter) rv.getAdapter();
                adapter.addUsers(result.getList());
                adapter.notifyDataSetChanged();
            }

            @Override
            public void fail(@NotNull Exception e) {
                Toast.makeText(view.getContext(), fragment.getString(R.string.error_friends), Toast.LENGTH_SHORT).show();
                Log.e(FriendsFragment.TAG, e.getMessage());
            }
        });
    }

    public SwipeRefreshLayout setRefreshBehaviour(SwipeRefreshLayout layout) {
        view.removeView(layout);
        layout.setOnRefreshListener(() -> {
            ((FriendsRecyclerViewAdapter)rv.getAdapter()).clear();
            getAll();
            layout.setRefreshing(false);
        });
        return layout;
    }
}
