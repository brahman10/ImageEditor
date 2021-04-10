package com.pixxo.photoeditor;

import java.io.Serializable;

public class ImageModel implements Serializable{
    String path;
    boolean isSelected=false;

    public ImageModel(String path) {
        this.path = path;
        this.isSelected = isSelected;
    }

    public ImageModel() {
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}