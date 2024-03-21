package cn.alvkeke.dropto.debug;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import cn.alvkeke.dropto.BuildConfig;
import cn.alvkeke.dropto.R;
import cn.alvkeke.dropto.data.Category;
import cn.alvkeke.dropto.data.NoteItem;
import cn.alvkeke.dropto.storage.DataBaseHelper;

public class DebugFunction {

    public static final String LOG_TAG = "DebugFunction";

    public static boolean extract_raw_file(Context context, int id, File o_file) {
        if (!BuildConfig.DEBUG) return false;
        Log.e(LOG_TAG, "Perform Debug function to extract res file");

        if (o_file.exists()) {
            // file exist, return true to indicate can be load
            Log.d(LOG_TAG, "file exist, don't extract:" + o_file);
            return true;
        }
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.O) {
            Log.e(LOG_TAG, "SDK_VERSION error: " + Build.VERSION.SDK_INT);
            return false;
        }
        byte[] buffer = new byte[1024];
        try {
            InputStream is = context.getResources().openRawResource(id);
            OutputStream os = Files.newOutputStream(o_file.toPath());
            int len;
            while((len = is.read(buffer)) > 0) {
                os.write(buffer, 0, len);
            }
            os.flush();
            os.close();
            is.close();
        } catch (IOException e) {
            Log.e(LOG_TAG, "Failed to extract res: " +
                    context.getResources().getResourceEntryName(id) + " to " + o_file);
            return false;
        }
        return true;
    }

    public static List<File> try_extract_res_images(Context context, File folder) {
        if (!BuildConfig.DEBUG) return null;
        Log.e(LOG_TAG, "Perform Debug function to extract images");

        List<Integer> rawIds = new ArrayList<>();
        Field[] fields = R.raw.class.getFields();
        for (Field f : fields) {
            if (f.getType() == int.class) {
                try {
                    int id = f.getInt(null);
                    rawIds.add(id);
                } catch (IllegalAccessException e) {
                    Log.e(LOG_TAG, "failed to get resource ID of raw:" + f);
                }
            }
        }

        List<File> ret_files = new ArrayList<>();
        for (int id : rawIds) {
            File o_file = new File(folder, context.getResources().getResourceEntryName(id) + ".png");
            if (extract_raw_file(context, id, o_file))
                ret_files.add(o_file);
        }

        return ret_files;
    }


    /**
     * fill category database for debugging, this function will be exec only in DEBUG build
     * @param context context
     */
    public static void fill_database_for_category(Context context) {
        if (!BuildConfig.DEBUG) return;
        Log.e(LOG_TAG, "Perform Debug function to fill database for categories");
        try (DataBaseHelper dbHelper = new DataBaseHelper(context)) {
            dbHelper.start();

            dbHelper.insertCategory("Local(Debug)", Category.Type.LOCAL_CATEGORY, "");
            dbHelper.insertCategory("REMOTE USERS", Category.Type.REMOTE_USERS, "");
            dbHelper.insertCategory("REMOTE SELF DEVICE", Category.Type.REMOTE_SELF_DEV, "");

            dbHelper.finish();
        } catch (Exception e) {
            Log.e(LOG_TAG, "Failed to perform debug database filling");
        }
    }

    public static void fill_database_for_note(Context context, List<File> img_files, long cate_id) {
        if (!BuildConfig.DEBUG) return;
        Log.e(LOG_TAG, "Perform Debug function to fill database for noteItems");

        try (DataBaseHelper dataBaseHelper = new DataBaseHelper(context)) {
            dataBaseHelper.start();
            Random r = new Random();
            int idx = 0;
            for (int i = 0; i < 15; i++) {
                NoteItem e = new NoteItem("ITEM" + i + i, System.currentTimeMillis());
                e.setCategoryId(cate_id);
                if (r.nextBoolean()) {
                    e.setText(e.getText(), true);
                }
                if (idx < img_files.size() && r.nextBoolean()) {
                    File img_file = img_files.get(idx);
                    idx++;
                    if (img_file.exists()) {
                        Log.d("DebugFunction", "add image file: " + img_file);
                        e.setImageFile(img_file);
                    } else {
                        Log.e("DebugFunction", "add image file failed, not exist: " + img_file);
                    }

                }
                e.setId(dataBaseHelper.insertNote(e));
            }
            dataBaseHelper.finish();
        }
    }

}
