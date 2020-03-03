package com.example.vkphoto.utils;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.vkphoto.R;
import com.example.vkphoto.model.VKPhoto;
import com.example.vkphoto.presenter.Presenter;
import com.squareup.picasso.Picasso;
import java.util.List;

public class PhotoRecyclerViewAdapter extends RecyclerView.Adapter<PhotoRecyclerViewAdapter.ViewHolder> {

    public List<VKPhoto> photos;
    private ViewHolderListener listener;
    private Presenter presenter;

    public PhotoRecyclerViewAdapter(List<VKPhoto> photos, ViewHolderListener listener) {
        this.photos = photos;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardView v = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.photo_cardview_layout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        VKPhoto photo = photos.get(position);
        CardView cv = holder.cardView;
        ImageView viewPhoto = cv.findViewById(R.id.photo);
        viewPhoto.setTag(position);
        viewPhoto.setTransitionName(cv.getContext().getString(R.string.transition_name, position));
        viewPhoto.setScaleType(ImageView.ScaleType.CENTER_CROP);
        viewPhoto.setOnClickListener(v -> listener.onItemClicked(viewPhoto, photo, photos.size(), position));
        VKPhoto.PhotoSize photoSize = photo.getWithType(VKPhoto.Type.Q);
        if (photoSize == null)
            photoSize = photo.getWithType(VKPhoto.Type.S);
        Picasso.get().load(photoSize.getUrl()).into(viewPhoto);
        viewPhoto.setOnLongClickListener(v -> {
            final CharSequence[] items = {cv.getResources().getString(R.string.delete)};
            AlertDialog.Builder builder = new AlertDialog.Builder(cv.getContext());
            builder.setItems(items, (dialog, item) -> {
                if (presenter != null) {
                    presenter.delete(photo.getId(), position);
                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return photos.size();
    }

    public void addPhotos(List<VKPhoto> p) {
        photos.addAll(p);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private CardView cardView;

        public ViewHolder(CardView cardView) {
            super(cardView);
            this.cardView = cardView;
        }
    }

    public List<VKPhoto> getPhotos() {
        return photos;
    }

    public void clear() {
        photos.clear();
        notifyDataSetChanged();
    }

    public void remove(int position) {
        photos.remove(position);
        notifyDataSetChanged();
    }

    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    public interface ViewHolderListener {
        void onItemClicked(ImageView image, VKPhoto photo, int listSize, int adapterPosition);
    }
}
