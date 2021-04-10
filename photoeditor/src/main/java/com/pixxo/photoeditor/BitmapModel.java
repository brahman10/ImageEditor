package com.pixxo.photoeditor;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class BitmapModel implements Serializable, Parcelable {
    Bitmap bitmap;
    boolean isSelected=false;

    public BitmapModel(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public BitmapModel() {
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
}
