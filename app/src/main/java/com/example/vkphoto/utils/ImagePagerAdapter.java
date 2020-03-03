package com.example.vkphoto.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.alexvasilkov.gestures.views.GestureImageView;
import com.example.vkphoto.R;
import com.example.vkphoto.model.VKPhoto;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ImagePagerAdapter extends PagerAdapter {

    private Context context;
    private int listSize;
    private int current;
    private PagerClickListener listener;
    private List<VKPhoto> photos;

    public ImagePagerAdapter(Context context, int listSize, int current, PagerClickListener listener, List<VKPhoto> photos) {
        this.context = context;
        this.listSize = listSize;
        this.current = current;
        this.listener = listener;
        this.photos = photos;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup collection, int position, @NonNull Object view) {
        collection.removeView((View) view);
    }

    @Override
    public int getCount() {
        return listSize;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.photo_item, container, false);
        VKPhoto photo = photos.get(position);
        GestureImageView image = v.findViewById(R.id.photo_item);
        ProgressBar bar = v.findViewById(R.id.item_progress_bar);
        Picasso.get().load(photo.getUrl()).into(image, new Callback() {
            @Override
            public void onSuccess() {
                bar.setVisibility(View.GONE);
            }

            @Override
            public void onError(Exception e) {
                bar.setVisibility(View.GONE);
            }
        });
        container.addView(v);
        String name = context.getString(R.string.transition_name, position);
        image.setTag(position);
        image.setTransitionName(name);
        if (position == current) {
            listener.setStartPostTransition(image);
        }
        return v;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    public interface PagerClickListener {
        void setStartPostTransition(View view);
    }
}
