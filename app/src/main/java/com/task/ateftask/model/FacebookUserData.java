package com.task.ateftask.model;

import android.os.Parcel;
import android.os.Parcelable;

public class FacebookUserData implements Parcelable {

    private String name;
    private String imageUrl;

    public String getName() {
        return name;
    }

    public FacebookUserData setName(String name) {
        this.name = name;
        return this;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public FacebookUserData setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
        return this;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.imageUrl);
    }

    public FacebookUserData() {
    }

    protected FacebookUserData(Parcel in) {
        this.name = in.readString();
        this.imageUrl = in.readString();
    }

    public static final Parcelable.Creator<FacebookUserData> CREATOR = new Parcelable.Creator<FacebookUserData>() {
        @Override
        public FacebookUserData createFromParcel(Parcel source) {
            return new FacebookUserData(source);
        }

        @Override
        public FacebookUserData[] newArray(int size) {
            return new FacebookUserData[size];
        }
    };
}
