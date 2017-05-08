package together.com.homely.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import together.com.homely.R;
import together.com.homely.utils.Constants;
import together.com.homely.utils.FileSystemUtils;
import together.com.homely.utils.MessageStore;
import together.com.homely.utils.http.DownloadContentTask;

/**
 * Created by Sagar on 4/10/2017.
 */

public class MessageListAdapter extends BaseAdapter {
    private Context context;
    private List<Map<String, Object>> messagesItems;
    private String family;
    private String area;
    VideoView videoView;

    private GestureDetector.SimpleOnGestureListener mGestureListener = new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            if (videoView.isPlaying())
                videoView.pause();
            else
                videoView.start();
            return true;
        };
    };

    GestureDetector mGestureDetector = new GestureDetector(context, mGestureListener);

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            mGestureDetector.onTouchEvent(event);
            return true;
        }
    };

    public MessageListAdapter(Context context, String family, String area) {
        this.context = context;
        this.family = family;
        this.area = area;
        this.messagesItems = MessageStore.getInstance().getMessages(family, area);
    }

    @Override
    public void notifyDataSetChanged(){
        this.messagesItems = MessageStore.getInstance().getMessages(family, area);
        super.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return messagesItems.size();
    }

    @Override
    public Object getItem(int position) {
        return messagesItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return Long.parseLong((String) messagesItems.get(position).get(Constants.DB._ID));
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        /**
         * The following list not implemented reusable list items as list items
         * are showing incorrect data Add the solution if you have one
         * */

        Map<String, Object> m = messagesItems.get(position);

        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        // Identifying the message owner
        if (m.get("sender").equals("self")) {
            // message belongs to you, so load the right aligned layout
            convertView = mInflater.inflate(R.layout.list_item_message_right,
                    null);
        } else {
            // message belongs to other person, load the left aligned layout
            convertView = mInflater.inflate(R.layout.list_item_message_left,
                    null);
        }

        TextView lblFrom = (TextView) convertView.findViewById(R.id.lblMsgFrom);

        final String messageType = (String) m.get("type");
        final String data = (String) m.get(Constants.DB.MESSAGE);
        //byte[] decodedData = Base64.decode(data, Base64.DEFAULT);
        if(messageType.startsWith("text") || messageType.equals("text")){
            TextView txtMsg = (TextView) convertView.findViewById(R.id.txtMsg);
            txtMsg.setText(new String(data));
        }else if(messageType.startsWith("image") || messageType.equals("image")){
            downloadImage(data);
            ImageView imgMsg = (ImageView) convertView.findViewById(R.id.imgMsg);
            final String contentPath = FileSystemUtils.getInstance().getContentPath(data, messageType);
            imgMsg.setImageURI(Uri.fromFile(new File(contentPath)));
            imgMsg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    String type = messageType;
                    Uri uri = Uri.parse(contentPath);
                    intent.setDataAndType(uri, type);
                    context.startActivity(intent);
                }
            });
            //imgMsg.setImageBitmap(BitmapFactory.decodeByteArray(content, 0, content.length));
        }else if(messageType.startsWith("video") || messageType.equals("video")){
            downloadVideo(data);

            ImageView imgMsg = (ImageView) convertView.findViewById(R.id.imgMsg);
            final String contentPath = FileSystemUtils.getInstance().getContentPath(data, messageType);
            Bitmap bmThumbnail = ThumbnailUtils.createVideoThumbnail(contentPath, MediaStore.Video.Thumbnails.MINI_KIND);
            imgMsg.setImageBitmap(bmThumbnail);

            imgMsg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    String type = messageType;
                    Uri uri = Uri.parse(contentPath);
                    intent.setDataAndType(uri, type);
                    context.startActivity(intent);
                }
            });
        }else if(messageType.equals("externalLink")){
            TextView txtMsg = (TextView) convertView.findViewById(R.id.txtMsg);
            txtMsg.setText(new String(data));
            final WebView webMsg = (WebView) convertView.findViewById(R.id.webMsg);
            txtMsg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    webMsg.getSettings().setJavaScriptEnabled(true);
                    webMsg.loadUrl(data);
                }
            });
        }

        lblFrom.setText((String)m.get(Constants.DB.SENDER));

        return convertView;
    }

    private void downloadImage(String data) {
        if(!FileSystemUtils.getInstance().imageFileExists(data)){
            AsyncTask contentDownloadMonitor = new DownloadContentTask(data).execute();
            try {
                byte[] content = (byte[]) contentDownloadMonitor.get();
                FileSystemUtils.getInstance().createImageFile(data.split("/")[2], content);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            } catch (ExecutionException e2) {
                e2.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    private void downloadVideo(String data) {
        if(!FileSystemUtils.getInstance().videoFileExists(data)){
            AsyncTask contentDownloadMonitor = new DownloadContentTask(data).execute();
            try {
                byte[] content = (byte[]) contentDownloadMonitor.get();
                FileSystemUtils.getInstance().createVideoFile(data.split("/")[2], content);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            } catch (ExecutionException e2) {
                e2.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }
}
