package gism.com.gism.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.text.Layout;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.VideoView;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import gism.com.gism.R;
import gism.com.gism.WebViewActivity;
import gism.com.gism.utils.Constants;
import gism.com.gism.utils.FileSystemUtils;
import gism.com.gism.utils.MessageStore;
import gism.com.gism.utils.http.DownloadContentTask;
import gism.com.gism.utils.http.GetGroupActivitiesTask;

public class ActivityListAdapter extends BaseAdapter {
    private Context context;
    private List<Map<String, String>> groupActivities;
    private String groupId;

    public ActivityListAdapter(Context context, String groupId) {
        this.context = context;
        this.groupId = groupId;
    }

    @Override
    public void notifyDataSetChanged(){
        AsyncTask getActivitesMonitor = new GetGroupActivitiesTask(groupId).execute();
        try {
            groupActivities = (List<Map<String, String>>) getActivitesMonitor.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        super.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if(groupActivities == null){
            notifyDataSetChanged();
        }
        return groupActivities.size();
    }

    @Override
    public Object getItem(int position) {
        return groupActivities.get(position);
    }

    @Override
    public long getItemId(int position) {
        return Long.parseLong((String) groupActivities.get(position).get(Constants.DB._ID));
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        convertView = mInflater.inflate(R.layout.groupactivity_webview, null);
        Button button = new Button(context);
        button.setText(groupActivities.get(position).get("name"));
        final WebView webMsg = (WebView) convertView.findViewById(R.id.groupactivity_webview);
        webMsg.setWebViewClient(new WebViewClient());
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, WebViewActivity.class);
                intent.putExtra("url", groupActivities.get(position).get("url"));
                context.startActivity(intent);
            }
        });
        return button;
    }
}
