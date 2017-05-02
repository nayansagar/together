package together.com.homely;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import together.com.homely.utils.PersistenceUtils;
import together.com.homely.utils.WSUtils;
import together.com.homely.utils.http.CreateFamilyTask;
import together.com.homely.utils.http.UploadContentTask;

public class ShareActivity extends ActionBarActivity {

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

                WSUtils.getInstance().send(persistenceUtils.getFamilyId(), "family", contentLocation, "internalLink");
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

    private byte[] compressContent(InputStream fis, String contentType) throws IOException {
        Bitmap original = BitmapFactory.decodeStream(fis);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Bitmap.CompressFormat compressFormat = null;
        if("image/jpg".equals(contentType) || "image/jpeg".equals(contentType)){
            compressFormat = Bitmap.CompressFormat.JPEG;
        }else if("image/png".equals(contentType)){
            compressFormat = Bitmap.CompressFormat.PNG;
        }else {
            byte[] content = new byte[fis.available()];
            fis.read(content);
            return content;
        }
        original.compress(compressFormat, 100, out);
        return out.toByteArray();
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
