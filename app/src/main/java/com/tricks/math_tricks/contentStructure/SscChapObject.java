package com.tricks.math_tricks.contentStructure;

import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

public class SscChapObject implements Parcelable {

    private Drawable chapIcon;
    private String chapName;
    private String chapDetails;

    public SscChapObject(Drawable cIcon, String cName, String cDetails){
        this.chapIcon = cIcon;
        this.chapName = cName;
        this.chapDetails = cDetails;
    }

    protected SscChapObject(Parcel in) {
        chapName = in.readString();
        chapDetails = in.readString();
    }

    public static final Creator<SscChapObject> CREATOR = new Creator<SscChapObject>() {
        @Override
        public SscChapObject createFromParcel(Parcel in) {
            return new SscChapObject(in);
        }

        @Override
        public SscChapObject[] newArray(int size) {
            return new SscChapObject[size];
        }
    };

    public Drawable getChapIcon() {
        return chapIcon;
    }

    public String getChapName() {
        return chapName;
    }

    public String getChapDetails() {
        return chapDetails;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(chapName);
        dest.writeString(chapDetails);
    }
}
