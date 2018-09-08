package gism.com.gism;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.concurrent.ExecutionException;

import gism.com.gism.utils.FileSystemUtils;
import gism.com.gism.utils.MessageStore;
import gism.com.gism.utils.PersistenceUtils;
import gism.com.gism.utils.WSUtils;
import gism.com.gism.utils.http.UploadContentTask;

public class ShareActivity extends AppCompatActivity {

    private PersistenceUtils persistenceUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        persistenceUtils = new PersistenceUtils(this);
    }

    public void shareToFamily(View view){
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                handleSendText(intent); // Handle text being sent
            } else if (type.startsWith("image/") || type.startsWith("video/")) {
                handleSendImage(intent); // Handle single image being sent
            }
        } else if (Intent.ACTION_SEND_MULTIPLE.equals(action) && type != null) {
            if (type.startsWith("image/") || type.startsWith("video/")) {
                handleSendMultipleImages(intent); // Handle multiple images being sent
            }
        }

    }

    void handleSendText(Intent intent) {
        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (sharedText != null) {
            String msgType = "text";
            if(sharedText.startsWith("http")){
                msgType = "externalLink";
            }
            MessageStore.getInstance().storeMessage(persistenceUtils.getFamilyId(), "family", "self",
                    GregorianCalendar.getInstance().toString(), msgType, sharedText);
            WSUtils.getInstance().send(persistenceUtils.getFamilyId(), "family", sharedText, msgType);
        }
    }

    void handleSendImage(Intent intent){
        Uri contentUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
        sendContent(contentUri);
    }

    private void sendContent(Uri contentUri) {
        if (contentUri != null) {
            InputStream fis = null;
            try {
                if("content".equals(contentUri.getScheme())){
                    fis = getContentResolver().openInputStream(contentUri);
                }else if("file".equals(contentUri.getScheme())){
                    fis = new FileInputStream(contentUri.getPath());
                }

                String contentType = getContentType(contentUri);
                byte[] content = compressContent(fis, contentType);

                AsyncTask uploadContentMonitor = new UploadContentTask(content, contentType).execute();
                String contentLocation = (String) uploadContentMonitor.get();
                if(contentType.startsWith("video")){
                    FileSystemUtils.getInstance().createVideoFile(getFileName(contentLocation), content);
                }else {
                    FileSystemUtils.getInstance().createImageFile(getFileName(contentLocation), content);
                }

                MessageStore.getInstance().storeMessage(persistenceUtils.getFamilyId(), "family", "self",
                        GregorianCalendar.getInstance().toString(), contentType, contentLocation);
                WSUtils.getInstance().send(persistenceUtils.getFamilyId(), "family", contentLocation, contentType);
            }catch (IOException e){
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } finally {
                if(fis != null){
                    try {
                        fis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private String getFileName(String contentLocation) {
        return contentLocation.split("/")[2];
    }

    private byte[] compressContent(InputStream fis, String contentType) throws IOException {

        if("image/jpg".equals(contentType) || "image/jpeg".equals(contentType)){
            Bitmap original = BitmapFactory.decodeStream(fis);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            original.compress(Bitmap.CompressFormat.JPEG, 100, out);
            return out.toByteArray();
        }else if("image/png".equals(contentType)){
            Bitmap original = BitmapFactory.decodeStream(fis);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            original.compress(Bitmap.CompressFormat.PNG, 100, out);
            return out.toByteArray();
        }else {
            byte[] content = new byte[fis.available()];
            fis.read(content);
            return content;
        }

    }

    void handleSendMultipleImages(Intent intent) {
        ArrayList<Uri> imageUris = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
        if (imageUris != null) {
            for(Uri uri : imageUris){
                sendContent(uri);
            }
        }
    }

    private String getContentType(Uri uri){
        ContentResolver cr = this.getContentResolver();
        String mime = cr.getType(uri);

        return mime;
    }
}
