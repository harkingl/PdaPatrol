package com.pda.patrol.util;

import android.os.Environment;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileUtil {
    private static final String FORMAT_DATE = "yyyyMMdd_HHmmss";
    public static final String PATROL_FILE_PATH = Environment
            .getExternalStorageDirectory() + "/patrol/";

    public static File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat(FORMAT_DATE).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + ".jpg";
        File image = new File(PATROL_FILE_PATH, imageFileName);

        createOrExistsDir(image.getParentFile());

        return image;
    }

    public static boolean createOrExistsDir(final File file) {
        return file != null && (file.exists() ? file.isDirectory() : file.mkdirs());
    }

    public static boolean createOrExistsFile(final File file) {
        if (file == null) {
            return false;
        }
        if (file.exists()) {
            return file.isFile();
        }
        if (!createOrExistsDir(file.getParentFile())) return false;
        try {
            return file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
