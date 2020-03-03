package com.example.vkphoto.model;

import android.os.Parcel;
import android.os.Parcelable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class VKPhoto implements Parcelable {

    private int id;
    private int albumId;
    private int ownerId;
    private String url;
    private Map<Type, PhotoSize> sizes;
    private String text;
    private Date dateTime;
    private int likesCount;
    private int repostsCount;
    private boolean userLike;

    public VKPhoto(int id, int albumId, int ownerId, Map<Type, PhotoSize> sizes, String text, Date dateTime, int likesCount, int repostsCount, boolean userLike) {
        this.id = id;
        this.albumId = albumId;
        this.ownerId = ownerId;
        this.sizes = sizes;
        this.text = text;
        this.dateTime = dateTime;
        this.likesCount = likesCount;
        this.repostsCount = repostsCount;
        this.userLike = userLike;
    }

    protected VKPhoto(Parcel in) {
        /*int size = in.readInt();
        sizes = new HashMap<>();
        for (int i = 0; i < size; i++) {
            Type key = (Type) in.readSerializable();
            PhotoSize value = in.readParcelable(VKPhoto.class.getClassLoader());
            sizes.put(key, value);
        }*/
        id = in.readInt();
        albumId = in.readInt();
        ownerId = in.readInt();
        url = in.readString();
        text = in.readString();
        likesCount = in.readInt();
        repostsCount = in.readInt();
        userLike = in.readByte() != 0;
    }

    public static final Creator<VKPhoto> CREATOR = new Creator<VKPhoto>() {
        @Override
        public VKPhoto createFromParcel(Parcel in) {
            return new VKPhoto(in);
        }

        @Override
        public VKPhoto[] newArray(int size) {
            return new VKPhoto[size];
        }
    };

    public static VKPhoto parse(JSONObject o) throws JSONException {
        Date date = new Date((long) o.optInt("date", 0) * 1000);
        JSONArray sizes = o.getJSONArray("sizes");
        Map<Type, PhotoSize> resultSizes = new HashMap<>();
        for (int i = 0;i < sizes.length();i++) {
            JSONObject jsonSizeObject = sizes.getJSONObject(i);
            PhotoSize size = new PhotoSize(jsonSizeObject.optString("url",""),
                    jsonSizeObject.optInt("width", 0),
                    jsonSizeObject.optInt("height", 0));
            resultSizes.put(Type.valueOf(jsonSizeObject.optString("type", "").toUpperCase()), size);
        }
        int likesCount = 0;
        boolean userLike = false;
        if (o.has("likes")) {
            JSONObject likes = o.getJSONObject("likes");
            likesCount = likes.optInt("count", 0);
            userLike = likes.optInt("user_likes", 0) == 1;
        }

        int repostsCount = 0;
        if (o.has("reposts")) {
            JSONObject reposts = o.getJSONObject("reposts");
            repostsCount = reposts.optInt("count", 0);
        }
        VKPhoto photo = new VKPhoto(o.optInt("id", 0), o.optInt("album_id", 0), o.optInt("owner_id", 0),
                resultSizes, o.optString("text", ""), date, likesCount, repostsCount, userLike);
        photo.setUrl(photo.getMax().getUrl());
        return photo;
    }

    public PhotoSize getMax() {
        return sizes.get(Collections.max(sizes.keySet()));
    }

    public PhotoSize getWithType(Type t) {
        return sizes.get(t);
    }

    public int getId() {
        return id;
    }

    public int getAlbumId() {
        return albumId;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public String getText() {
        return text;
    }

    public Date getDateTime() {
        return dateTime;
    }

    public int getLikesCount() {
        return likesCount;
    }

    public boolean isUserLike() {
        return userLike;
    }



    public int getRepostsCount() {
        return repostsCount;
    }

    public Map<Type, PhotoSize> getSizes() {
        return sizes;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        /*dest.writeInt(sizes.size());
        for (Map.Entry<Type, PhotoSize> entry: sizes.entrySet()) {
            dest.writeSerializable(entry.getKey());
            dest.writeParcelable(entry.getValue(), flags);
        }*/
        dest.writeInt(id);
        dest.writeInt(albumId);
        dest.writeInt(ownerId);
        dest.writeString(url);
        dest.writeString(text);
        dest.writeInt(likesCount);
        dest.writeInt(repostsCount);
        dest.writeByte((byte) (userLike ? 1 : 0));

    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }


    public static class PhotoSize {
        private String url;
        private int width;
        private int height;

        public PhotoSize(String src, int width, int height) {
            this.url = src;
            this.width = width;
            this.height = height;
        }


        public String getUrl() {
            return url;
        }

        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
        }

    }

    public enum Type{
        S,
        M,
        X,
        O,
        P,
        Q,
        R,
        Y,
        Z,
        W;
    }
}
