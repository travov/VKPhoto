package com.example.vkphoto.utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.vkphoto.R;
import com.example.vkphoto.model.VKAlbum;
import com.example.vkphoto.presenter.AlbumsPresenter;
import com.squareup.picasso.Picasso;
import java.util.List;

public class AlbumRecyclerViewAdapter extends RecyclerView.Adapter<AlbumRecyclerViewAdapter.ViewHolder> {

    private AlbumsPresenter presenter;
    public static List<VKAlbum> albums;
    private ViewHolderListener listener;

    public AlbumRecyclerViewAdapter(AlbumsPresenter presenter, List<VKAlbum> albums, ViewHolderListener listener) {
        this.presenter = presenter;
        AlbumRecyclerViewAdapter.albums = albums;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardView v = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.album_cardview_layout, parent, false);
        return new AlbumRecyclerViewAdapter.ViewHolder(v);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        VKAlbum album = albums.get(position);
        CardView cv = holder.cardView;
        ImageView cover = cv.findViewById(R.id.album_cover);
        cover.setScaleType(ImageView.ScaleType.CENTER_CROP);
        if (!album.getThumbSrc().isEmpty())
            Picasso.get().load(album.getThumbSrc()).into(cover);
        else Picasso.get().load(cv.getResources().getString(R.string.url_no_album)).into(cover);
        TextView title = cv.findViewById(R.id.album_title);
        title.setText(album.getTitle());
        TextView albumSize = cv.findViewById(R.id.album_size);
        albumSize.setText(cv.getContext().getString(R.string.album_size, album.getSize()));
        if (album.getViewCategory().equals("only_me"))
            cv.findViewById(R.id.lock).setVisibility(View.VISIBLE);
        else
            cv.findViewById(R.id.lock).setVisibility(View.INVISIBLE);
        cv.setOnClickListener(v -> listener.onItemClicked(album, position));
        ImageButton moreButton = cv.findViewById(R.id.more_button);
        moreButton.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(cv.getContext(), moreButton);
            popup.getMenuInflater().inflate(R.menu.popup_menu_more_button, popup.getMenu());
            popup.setOnMenuItemClickListener(item -> {
                switch (item.getTitle().toString()) {
                    case "Remove album":
                        AlertDialog.Builder builder = new AlertDialog.Builder(cv.getContext());
                        builder.setTitle(R.string.sure_delete_album);
                        AlertDialog dialog = builder.create();
                        dialog.setButton(Dialog.BUTTON_NEGATIVE, cv.getResources().getString(R.string.cancel), (dialog12, which) -> dialog.dismiss());
                        dialog.setButton(Dialog.BUTTON_POSITIVE, cv.getResources().getString(R.string.delete),
                                (dialog2, which) -> {
                                    presenter.delete(album.getId(), position);
                                });
                        dialog.show();
                        dialog.getButton(Dialog.BUTTON_POSITIVE).setTextColor(cv.getResources().getColor(R.color.colorPrimary));
                        dialog.getButton(Dialog.BUTTON_NEGATIVE).setTextColor(cv.getResources().getColor(R.color.colorPrimary));
                }
                return true;
            });
            popup.show();
        });
    }

    @Override
    public int getItemCount() {
        return albums.size();
    }

    public void addAlbums(List<VKAlbum> a) {
        albums.addAll(a);
    }

    public void addAlbum(VKAlbum a) {
        albums.add(a);
    }

    public void clear() {
        albums.clear();
        notifyDataSetChanged();
    }

    public List<VKAlbum> getAlbums() {
        return albums;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private CardView cardView;

        public ViewHolder(CardView cardView) {
            super(cardView);
            this.cardView = cardView;
        }
    }

    public interface ViewHolderListener {
        void onItemClicked(VKAlbum album, int adapterPosition);
    }
}
