package com.example.vkphoto.view.fragments;

import android.net.Uri;
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
import com.example.vkphoto.presenter.AlbumsPhotoPresenter;
import java.util.List;

public class AlbumsPhotoFragment extends Fragment {

    private AlbumsPhotoPresenter presenter;

    public AlbumsPhotoFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ConstraintLayout view = (ConstraintLayout) inflater.inflate(R.layout.fragment_albums_photo, container, false);
        Bundle bundle = getArguments();
        setHasOptionsMenu(true);
        int albumId = bundle.getInt("album_id");
        String title = bundle.getString("title");
        String description = bundle.getString("description");
        String privacyView = bundle.getString("privacyView");
        String privacyComment = bundle.getString("privacyComment");
        presenter = new AlbumsPhotoPresenter(this, view, albumId, title, description, privacyView, privacyComment);
        presenter.init();
        presenter.get(albumId);
        SwipeRefreshLayout swipeRefreshLayout = view.findViewById(R.id.swipe_container);
        presenter.setRefreshBehaviour(swipeRefreshLayout);
        return view;

    }

    public void onFragmentReenter(int exitPosition) {
        presenter.scrollToExitPosition(exitPosition);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_toolbar_albums_photo, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        presenter.menuSelectedItem(item.getItemId());
        return super.onOptionsItemSelected(item);
    }

    public List<String> convertUrisIntoFiles(List<Uri> uris) {
        return presenter.convertContentIntoFile(uris);
    }

    public void save(int albumId, List<String> paths) {
        presenter.save(albumId, paths);
    }

}
