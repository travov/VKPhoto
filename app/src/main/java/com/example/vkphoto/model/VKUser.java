package com.example.vkphoto.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

public class VKUser implements Parcelable  {

    private int id;
    private String firstName;
    private String lastName;
    private String photo;
    private Boolean online;
    private String deactivated;
    private Boolean closed;
    private Boolean canAccessClosed;


    public VKUser(int id, String firstName, String lastName, String photo, Boolean isOnline, String deactivated, Boolean isClosed, Boolean canAccessClosed) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.photo = photo;
        this.deactivated = deactivated;
        this.online = isOnline;
        this.closed = isClosed;
        this.canAccessClosed = canAccessClosed;
    }


    protected VKUser(Parcel in) {
        id = in.readInt();
        firstName = in.readString();
        lastName = in.readString();
        photo = in.readString();
        byte tmpIsOnline = in.readByte();
        online = tmpIsOnline == 0 ? null : tmpIsOnline == 1;
        deactivated = in.readString();
        byte tmpIsClosed = in.readByte();
        closed = tmpIsClosed == 0 ? null : tmpIsClosed == 1;
        byte tmpCanAccessClosed = in.readByte();
        canAccessClosed = tmpCanAccessClosed == 0 ? null : tmpCanAccessClosed == 1;
    }

    public static final Creator<VKUser> CREATOR = new Creator<VKUser>() {
        @Override
        public VKUser createFromParcel(Parcel in) {
            return new VKUser(in);
        }

        @Override
        public VKUser[] newArray(int size) {
            return new VKUser[size];
        }
    };

    public static VKUser parse(JSONObject o) {
        return new VKUser(o.optInt("id", 0),
                          o.optString("first_name", ""),
                          o.optString("last_name", ""),
                          o.optString("photo_200", ""),
                  (o.optInt("online", 0)) == 1,
                          o.optString("deactivated", ""),
                          o.optBoolean("is_closed", false),
                          o.optBoolean("can_access_closed", false));
    }



    public Boolean isClosed() {
        return closed;
    }

    public Boolean canAccessClosed() {
        return canAccessClosed;
    }

    public Boolean isOnline() {
        return online;
    }

    public String getDeactivated() {
        return deactivated;
    }

    public String getPhoto() {
        return photo;
    }

    public String getLastName() {
        return lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public int getId() {
        return id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(firstName);
        dest.writeString(lastName);
        dest.writeString(photo);
        dest.writeByte((byte) (online == null ? 0 : online ? 1 : 2));
        dest.writeString(deactivated);
        dest.writeByte((byte) (closed == null ? 0 : closed ? 1 : 2));
        dest.writeByte((byte) (canAccessClosed == null ? 0 : canAccessClosed ? 1 : 2));
    }
}
