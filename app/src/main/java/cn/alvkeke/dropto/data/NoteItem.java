package cn.alvkeke.dropto.data;

import android.graphics.Bitmap;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.Serializable;

public class NoteItem implements Cloneable, Serializable {

    public static final long ID_NOT_ASSIGNED = -1;
    private long id;
    private long category_id;
    private String _text;
    private long _create_time_ms;
    private boolean _is_edited;
    private File _img_file;
    private String _img_name;

    /**
     * construct a new NoteItem instance, with auto generated create_time
     * @param text the content of the item
     */
    public NoteItem(String text) {
        this.id = ID_NOT_ASSIGNED;
        this._text = text;
        this._create_time_ms = System.currentTimeMillis();
    }

    /**
     * construct a new NoteItem instance, with a specific create_time
     * this should be use to restore the items from database
     * @param text content of the item
     * @param create_time the specific create_time
     */
    public NoteItem(String text, long create_time) {
        this.id = ID_NOT_ASSIGNED;
        _text = text;
        _create_time_ms = create_time;
    }

    @NonNull
    @Override
    public NoteItem clone() {
        NoteItem item = new NoteItem(_text, _create_time_ms);
        item.setId(this.id);
        item.setCategoryId(this.category_id);
        item.setImageFile(this._img_file);
        return item;
    }

    public void update(NoteItem item, boolean set_edited) {
        setText(item.getText(), set_edited);
        setCreateTime(item.getCreateTime());
        setImageFile(item.getImageFile());
        setImageName(item.getImageName());
        setCategoryId(item.getCategoryId());
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
            Log.d(this.toString(), "clear image");
            this._img_file = null;
            return true;
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

    public void setImageName(String name) {
        this._img_name = name;
    }

    public String getImageName() {
        return this._img_name;
    }

    @SuppressWarnings("unused")
    public Bitmap loadImage() {

        return null;
    }


    public boolean isEdited() {
        return _is_edited;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return this.id;
    }

    public void setCategoryId(long id) {
        this.category_id = id;
    }

    public long getCategoryId() {
        return this.category_id;
    }
}
