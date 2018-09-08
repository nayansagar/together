package gism.com.gism.utils;

import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by admin on 5/6/2017.
 */

public class FileSystemUtils {

    private static final String EXTERNAL_STORAGE_IMAGE_ROOT = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getPath();
    private static final String EXTERNAL_STORAGE_VIDEO_ROOT = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES).getPath();
    private static final String HOMELY_HOME = "homely";
    public static final String HOMELY_VIDEO_DIR = EXTERNAL_STORAGE_VIDEO_ROOT + "/" + HOMELY_HOME + "/content";
    public static final String HOMELY_IMAGE_DIR = EXTERNAL_STORAGE_IMAGE_ROOT + "/" + HOMELY_HOME + "/content";

    private static FileSystemUtils fileSystemUtils = new FileSystemUtils();

    private FileSystemUtils(){
        File myDir = new File(HOMELY_IMAGE_DIR);
        myDir.mkdirs();
        myDir = new File(HOMELY_VIDEO_DIR);
        myDir.mkdirs();
    }

    public static FileSystemUtils getInstance(){
        if(fileSystemUtils == null){
            synchronized (fileSystemUtils){
                fileSystemUtils = new FileSystemUtils();
            }
        }
        return fileSystemUtils;
    }

    public void createVideoFile(String fileName, byte[] content) throws IOException {
        createFile(HOMELY_VIDEO_DIR, fileName, content);
    }

    public void createImageFile(String fileName, byte[] content) throws IOException {
        createFile(HOMELY_IMAGE_DIR, fileName, content);
    }

    private void createFile(String rootDir, String fileName, byte[] content) throws IOException {
        File file;
        FileOutputStream outputStream = null;
        try {
            file = new File(rootDir, fileName);

            outputStream = new FileOutputStream(file);
            outputStream.write(content);
            outputStream.flush();
        } finally {
            if(outputStream != null){
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private byte[] readImageFile(String relativePath) throws IOException {
        return readFile(HOMELY_IMAGE_DIR.substring(0, HOMELY_IMAGE_DIR.lastIndexOf("/")), relativePath);
    }

    private byte[] readVideoFile(String relativePath) throws IOException {
        return readFile(HOMELY_VIDEO_DIR.substring(0, HOMELY_VIDEO_DIR.lastIndexOf("/")), relativePath);
    }

    private byte[] readFile(String rootDir, String relativePath) throws IOException {
        File file;
        FileInputStream inputStream = null;
        byte[] content = null;
        try {
            file = new File(rootDir + relativePath);
            inputStream = new FileInputStream(file);
            content = new byte[inputStream.available()];
            inputStream.read(content);
        } finally {
            if(inputStream != null){
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return content;
    }

    public String getContentPath(String contentId, String messageType){
        String baseUrl = HOMELY_IMAGE_DIR.substring(0, HOMELY_IMAGE_DIR.lastIndexOf("/"));
        if(messageType.startsWith("video")){
            baseUrl = HOMELY_VIDEO_DIR.substring(0, HOMELY_VIDEO_DIR.lastIndexOf("/"));
        }
        return baseUrl + contentId;
    }

    public boolean videoFileExists(String contentId){
        String filePath = HOMELY_VIDEO_DIR.substring(0, HOMELY_VIDEO_DIR.lastIndexOf("/")) + contentId;
        File file = new File(filePath);
        return file.exists();
    }

    public boolean imageFileExists(String contentId){
        String filePath = HOMELY_IMAGE_DIR.substring(0, HOMELY_IMAGE_DIR.lastIndexOf("/")) + contentId;
        File file = new File(filePath);
        return file.exists();
    }
}
