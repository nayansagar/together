package gism.com.gism.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import gism.com.gism.R;
import gism.com.gism.WebViewActivity;
import gism.com.gism.utils.http.GetGroupActivitiesTask;

public class ActivityListRecyclerViewAdapter extends RecyclerView.Adapter<ActivityListRecyclerViewAdapter.CustomRecyclerViewHolder> {

    private Context context;
    private List<Map<String, String>> groupActivities;

    public ActivityListRecyclerViewAdapter(Context context, String groupId) {
        this.context = context;
        AsyncTask getActivitiesMonitor = new GetGroupActivitiesTask(groupId).execute();
        try {
            groupActivities = (List<Map<String, String>>) getActivitiesMonitor.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Override
    public CustomRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View itemView = layoutInflater.inflate(R.layout.groupactivity_horizontal_list_item, parent, false);

        CustomRecyclerViewHolder ret = new CustomRecyclerViewHolder(itemView);
        return ret;
    }

    @Override
    public void onBindViewHolder(CustomRecyclerViewHolder  holder, final int position) {
        Button button = holder.getButton();
        button.setText(groupActivities.get(position).get("name"));
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, WebViewActivity.class);
                intent.putExtra("url", groupActivities.get(position).get("url"));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return groupActivities.size();
    }

    public static class CustomRecyclerViewHolder extends RecyclerView.ViewHolder {

        private Button button = null;

        public CustomRecyclerViewHolder(View itemView) {
            super(itemView);

            if(itemView != null)
            {
                button = (Button) itemView.findViewById(R.id.custom_refresh_recycler_view_text_view);
            }
        }

        public Button getButton() {
            return button;
        }
    }
}
