package com.tricks.math_tricks.contentStructure;

import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

public class SscTopicObject implements Parcelable {

    private Drawable topicIcon;
    private String topicName;
    private String PATH;


    public SscTopicObject(Drawable icon, String name, String path){
        this.topicIcon = icon;
        this.topicName = name;
        this.PATH = path;
    }

    protected SscTopicObject(Parcel in) {
        topicName = in.readString();
        PATH = in.readString();
    }

    public static final Creator<SscTopicObject> CREATOR = new Creator<SscTopicObject>() {
        @Override
        public SscTopicObject createFromParcel(Parcel in) {
            return new SscTopicObject(in);
        }

        @Override
        public SscTopicObject[] newArray(int size) {
            return new SscTopicObject[size];
        }
    };

    public Drawable getTopicIcon() {
        return topicIcon;
    }

    public String getTopicName() {
        return topicName;
    }

    public String getTopicPath() {
        return PATH;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(topicName);
        dest.writeString(PATH);
    }

}
