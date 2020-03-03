package com.example.vkphoto.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class VKAlbum {

    private int id;
    private int thumbId;
    private int ownerId;
    private String title;
    private String description;
    private Date created;
    private Date lastUpdated;
    private int size;
    private String thumbSrc;
    private String viewCategory;
    private String commentCategory;

    public VKAlbum(int id, int thumbId, int ownerId, String title, String description,
                   Date created, Date lastUpdated, int size, String thumbSrc, String viewCategory, String commentCategory) {
        this.id = id;
        this.thumbId = thumbId;
        this.ownerId = ownerId;
        this.title = title;
        this.description = description;
        this.created = created;
        this.lastUpdated = lastUpdated;
        this.size = size;
        this.thumbSrc = thumbSrc;
        this.viewCategory = viewCategory;
        this.commentCategory = commentCategory;
    }

    public static VKAlbum parse(JSONObject o) throws JSONException {
        Date created = new Date((long) o.optInt("created", 0) * 1000);
        Date updated = new Date((long) o.optInt("updated", 0) * 1000);
        JSONObject privacyView = o.getJSONObject("privacy_view");
        String viewCategory = privacyView.optString("category");
        String commentCategory = o.getJSONObject("privacy_comment").optString("category", "");
        return new VKAlbum(o.optInt("id", 0),
                o.optInt("thumb_id", 0),
                o.optInt("owner_id", 0),
                o.optString("title", ""),
                o.optString("description", ""),
                created,
                updated,
                o.optInt("size", 0),
                o.optString("thumb_src", ""),
                viewCategory, commentCategory);
    }

    public int getId() {
        return id;
    }

    public int getThumbId() {
        return thumbId;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Date getCreated() {
        return created;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public int getSize() {
        return size;
    }

    public String getThumbSrc() {
        return thumbSrc;
    }

    public String getViewCategory() {
        return viewCategory;
    }

    public String getCommentCategory() {
        return commentCategory;
    }
}
