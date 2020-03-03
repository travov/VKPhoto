package com.example.vkphoto.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.example.vkphoto.R;
import com.example.vkphoto.model.VKUser;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.squareup.picasso.Target;

import java.util.List;
import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class FriendsRecyclerViewAdapter extends RecyclerView.Adapter<FriendsRecyclerViewAdapter.ViewHolder>{

    private List<VKUser> users;

    public FriendsRecyclerViewAdapter(List<VKUser> users) {
        this.users = users;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardView v = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.users_layout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        VKUser user = users.get(position);
        CardView cv = holder.cardView;
        ImageView avatar = cv.findViewById(R.id.avatar);
        TextView name = cv.findViewById(R.id.name);
        if (!user.getPhoto().isEmpty()) {
            RequestCreator transformation = Picasso.get().load(user.getPhoto()).resize(150, 150).transform(new CropCircleTransformation());
            if (user.isOnline())
                transformation.into(setBorders(avatar));
            else
                transformation.into(avatar);
        }
        else if (!user.getDeactivated().isEmpty())
            Picasso.get().load(R.drawable.deactivated_50).resize(150, 150).transform(new CropCircleTransformation()).into(avatar);
        /*else
            Picasso.get().load(R.drawable.camera_50).resize(150, 150).transform(new CropCircleTransformation()).into(avatar);*/

        if (user.isOnline())
            setBorders(avatar);
        String fullName = user.getFirstName() + " " + user.getLastName();
        name.setText(fullName);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public void clear() {
        users.clear();
        notifyDataSetChanged();
    }

    public void addUsers(List<VKUser> u) {
        users.addAll(u);
    }

    private Target setBorders(ImageView imageView) {
        Target target = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                Paint paint = new Paint();
                paint.setColor(Color.GREEN);
                paint.setStyle(Paint.Style.STROKE);
                paint.setAntiAlias(true);
                paint.setStrokeWidth(5);
                Canvas canvas = new Canvas(bitmap);
                canvas.drawCircle((imageView.getWidth() - 2) / 2, (imageView.getHeight() - 2) / 2, (imageView.getWidth() / 2) - 2, paint);
                imageView.setImageBitmap(bitmap);
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        };
        return target;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private CardView cardView;

        public ViewHolder(CardView cardView) {
            super(cardView);
            this.cardView = cardView;
        }
    }
}
