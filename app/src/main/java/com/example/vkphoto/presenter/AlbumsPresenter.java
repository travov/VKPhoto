package com.example.vkphoto.presenter;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.example.vkphoto.R;
import com.example.vkphoto.model.VKAlbum;
import com.example.vkphoto.requests.RequestResult;
import com.example.vkphoto.requests.VKAlbumsRequest;
import com.example.vkphoto.requests.VKCreateAlbumRequest;
import com.example.vkphoto.requests.VKDeleteAlbumRequest;
import com.example.vkphoto.utils.AlbumRecyclerViewAdapter;
import com.example.vkphoto.view.MainActivity;
import com.example.vkphoto.view.fragments.AlbumsPhotoFragment;
import com.vk.api.sdk.VK;
import com.vk.api.sdk.VKApiCallback;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;

public class AlbumsPresenter {

    private final static String TAG = AlbumsPresenter.class.getSimpleName();
    private Fragment fragment;
    private RecyclerView rv;
    private ViewGroup view;

    public AlbumsPresenter(Fragment fragment, ViewGroup view) {
        this.fragment = fragment;
        this.view = view;
        this.rv = view.findViewById(R.id.albums_recycler);
    }

    public void init() {
        AlbumRecyclerViewAdapter recyclerViewAdapter = new AlbumRecyclerViewAdapter(this, new ArrayList<>(), (album, adapterPosition) -> {
            Bundle arguments = new Bundle();
            arguments.putInt("album_id", album.getId());
            arguments.putString("title", album.getTitle());
            arguments.putString("description", album.getDescription());
            arguments.putString("privacyView", album.getViewCategory());
            arguments.putString("privacyComment", album.getCommentCategory());
            Fragment photoFragment = new AlbumsPhotoFragment();
            photoFragment.setArguments(arguments);
            MainActivity activity = (MainActivity) fragment.getActivity();
            activity.getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, photoFragment, "5").commit();
            activity.changeFragment(activity.getSupportFragmentManager(), photoFragment);
            activity.pushFragmentIntoStack(R.id.action_home);
        });
        rv.setAdapter(recyclerViewAdapter);
        rv.setLayoutManager(new GridLayoutManager(fragment.getActivity(), 2));
    }

    public void getAll() {
        VKAlbumsRequest req = new VKAlbumsRequest();
        VK.execute(req, new VKApiCallback<RequestResult<VKAlbum>>() {
            @Override
            public void success(RequestResult<VKAlbum> requestResult) {
                Log.i(TAG, "get albums");
                AlbumRecyclerViewAdapter adapter = (AlbumRecyclerViewAdapter) rv.getAdapter();
                adapter.addAlbums(requestResult.getList());
                adapter.notifyDataSetChanged();
            }

            @Override
            public void fail(@NotNull Exception e) {
                Log.e(TAG, e.getMessage());
                Toast.makeText(fragment.getContext(), fragment.getString(R.string.error_get_albums), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void delete(int albumId, int position) {
        VKDeleteAlbumRequest req = new VKDeleteAlbumRequest(albumId);
        VK.execute(req, new VKApiCallback<Integer>() {
            @Override
            public void success(Integer integer) {
                AlbumRecyclerViewAdapter adapter = (AlbumRecyclerViewAdapter) rv.getAdapter();
                adapter.getAlbums().remove(position);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void fail(@NotNull Exception e) {
                Log.e(TAG, e.getMessage());
                Toast.makeText(fragment.getContext(), fragment.getString(R.string.error_remove_albums), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void createAlbum(String title, String description, String[] privacyView, String[] privacyComment, AlertDialog dialog) {
        VKCreateAlbumRequest req = new VKCreateAlbumRequest(title, description, privacyView, privacyComment);
        VK.execute(req, new VKApiCallback<VKAlbum>() {
            @Override
            public void success(VKAlbum vkAlbum) {
                Log.i(TAG, "album created with id " + vkAlbum.getId());
                AlbumRecyclerViewAdapter adapter = (AlbumRecyclerViewAdapter) rv.getAdapter();
                adapter.addAlbum(vkAlbum);
                adapter.notifyDataSetChanged();
                dialog.dismiss();
            }

            @Override
            public void fail(@NotNull Exception e) {
                Log.e(TAG, e.getMessage());
                dialog.dismiss();
                Toast.makeText(fragment.getContext(), fragment.getString(R.string.error_create_album), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public SwipeRefreshLayout setRefreshBehaviour(SwipeRefreshLayout layout) {
        view.removeView(layout);
        layout.setOnRefreshListener(() -> {
            ((AlbumRecyclerViewAdapter)rv.getAdapter()).clear();
            getAll();
            layout.setRefreshing(false);
        });
        return layout;
    }

    public void menuSelectedItem(int itemId) {
        switch (itemId) {
            case R.id.add_album:
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(fragment.getContext());
                alertDialogBuilder.setTitle(R.string.add_album);
                AlertDialog dialog = alertDialogBuilder.create();
                View layout = fragment.getLayoutInflater().inflate(R.layout.add_album_layout, null);
                dialog.setView(layout);
                dialog.setButton(Dialog.BUTTON_NEGATIVE, fragment.getResources().getString(R.string.cancel), (dialog12, which) -> dialog.dismiss());
                dialog.setButton(Dialog.BUTTON_POSITIVE, fragment.getResources().getString(R.string.apply),
                        (dialog2, which) -> {
                            Spinner privacyViewSpinner = layout.findViewById(R.id.privacy_view);
                            Spinner privacyCommentSpinner = layout.findViewById(R.id.privacy_comment);
                            EditText titleEdit = layout.findViewById(R.id.album_title);
                            EditText descriptionEdit = layout.findViewById(R.id.album_description);
                            String privacyViewCategory = "";
                            String privacyCommentCategory = "";
                            switch (privacyViewSpinner.getSelectedItem().toString()) {
                                case "by all":
                                    privacyViewCategory = "all";
                                    break;
                                case "only by friends":
                                    privacyViewCategory = "friends";
                                    break;
                                case "by friends and their friends":
                                    privacyViewCategory = "friends_of_friends";
                                    break;
                                case "only by me":
                                    privacyViewCategory = "only_me";
                                    break;
                            }
                            switch (privacyCommentSpinner.getSelectedItem().toString()) {
                                case "by all":
                                    privacyCommentCategory = "all";
                                    break;
                                case "only by friends":
                                    privacyCommentCategory = "friends";
                                    break;
                                case "by friends and their friends":
                                    privacyCommentCategory = "friends_of_friends";
                                    break;
                                case "only by me":
                                    privacyCommentCategory = "only_me";
                                    break;
                            }
                            String title = titleEdit.getText().toString();
                            String description = descriptionEdit.getText().toString();
                            createAlbum(title, description, new String[]{privacyViewCategory}, new String[]{privacyCommentCategory}, dialog);
                        });
                dialog.show();
                dialog.getButton(Dialog.BUTTON_POSITIVE).setTextColor(fragment.getResources().getColor(R.color.colorPrimary));
                dialog.getButton(Dialog.BUTTON_NEGATIVE).setTextColor(fragment.getResources().getColor(R.color.colorPrimary));
        }
    }
}
