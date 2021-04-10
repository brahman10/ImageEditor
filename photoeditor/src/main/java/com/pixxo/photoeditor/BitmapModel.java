package com.pixxo.photoeditor;

import android.graphics.Bitmap;

import java.io.Serializable;

public class BitmapModel implements Serializable {
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
}
