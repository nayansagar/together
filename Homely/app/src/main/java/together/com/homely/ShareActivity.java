package together.com.homely;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import together.com.homely.utils.PersistenceUtils;
import together.com.homely.utils.WSUtils;

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
            } else if (type.startsWith("image/")) {
                handleSendImage(intent); // Handle single image being sent
            }
        } else if (Intent.ACTION_SEND_MULTIPLE.equals(action) && type != null) {
            if (type.startsWith("image/")) {
                handleSendMultipleImages(intent); // Handle multiple images being sent
            }
        }

    }

    void handleSendText(Intent intent) {
        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (sharedText != null) {
            WSUtils.getInstance().send(persistenceUtils.getFamilyId(), "family", sharedText.getBytes(), "text");
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

                byte[] content = new byte[fis.available()];
                fis.read(content);
                WSUtils.getInstance().send(persistenceUtils.getFamilyId(), "family", content, getContentType(contentUri));
            }catch (IOException e){
                e.printStackTrace();
            }finally {
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

        return mime.split("/")[0];
    }
}
