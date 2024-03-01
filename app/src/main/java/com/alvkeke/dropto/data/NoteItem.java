package com.alvkeke.dropto.data;

import android.graphics.Bitmap;
import android.util.Log;

import java.io.File;
import java.io.Serializable;

public class NoteItem implements Cloneable, Serializable {

    private String _text;
    private long _create_time_ms;
    private boolean _is_edited;
    private File _img_file;

    public NoteItem(String text, long create_time) {
        _text = text;
        _create_time_ms = create_time;
    }

    @Override
    public NoteItem clone() {
        return new NoteItem(_text, _create_time_ms);
    }

    public void setText(String text, boolean set_edited) {
        _text = text;
        if (set_edited) {
            _is_edited = true;
        }
    }

    public String getText() {
        return _text;
    }

    public void setCreateTime(long ms) {
        this._create_time_ms = ms;
    }

    public long getCreateTime() {
        return _create_time_ms;
    }

    public boolean setImageFile(File img) {

        if (img == null) {
            Log.d(this.toString(), "add image abort, null");
            return false;
        }
        if (!img.exists()) {
            Log.d(this.toString(), "add image abort, file not exist: " + img);
            return false;
        }
        if (!img.isFile()) {
            Log.d(this.toString(), "add image abort, not a file: " + img);
            return false;
        }

        this._img_file = img;

        return true;
    }

    public File getImageFile() {
        return this._img_file;
    }

    public Bitmap loadImage() {
        if (_img_file == null) return null;

        return null;
    }


    public boolean isEdited() {
        return _is_edited;
    }

}
