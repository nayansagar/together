package gism.com.gism.adapter;

import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import gism.com.gism.JoinFamilyActivity;
import gism.com.gism.R;
import gism.com.gism.utils.http.JoinFamilyTask;

/**
 * Created by Sagar on 4/5/2017.
 */
public class FamilyListAdapter extends ArrayAdapter<Map<String, String>> {


        private static String userId;
        Context context;
        int layoutResourceId;
        ArrayList<Map<String, String>> data = new ArrayList<Map<String, String>>();

        public FamilyListAdapter(Context context, int layoutResourceId,
                                 ArrayList<Map<String, String>> data, String userId) {
            super(context, layoutResourceId, data);
            this.layoutResourceId = layoutResourceId;
            this.context = context;
            this.data = data;
            this.userId = userId;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row = convertView;
            UserHolder holder = null;

            if (row == null) {
                LayoutInflater inflater = ((JoinFamilyActivity) context).getLayoutInflater();
                row = inflater.inflate(layoutResourceId, parent, false);
                holder = new UserHolder();
                holder.familyName = (TextView) row.findViewById(R.id.invite_fname);
                holder.joinCode = (EditText) row.findViewById(R.id.invite_joinCode);
                holder.btnJoin = (Button) row.findViewById(R.id.invite_join_button);
                holder.familyImage = (ImageView) row.findViewById(R.id.invite_fimage);
                row.setTag(holder);
            }
            else {
                holder = (UserHolder) row.getTag();
            }
            final Map<String, String> invite = data.get(position);
            holder.familyName.setText(invite.get("name"));
            holder.familyImage.setImageURI(null);
            holder.btnJoin.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    try {

                        AsyncTask joinFamilyMonitor = new JoinFamilyTask(userId, invite.get("id")).execute();
                        Boolean joined = (Boolean) joinFamilyMonitor.get();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                }
            });
            return row;

        }

        static class UserHolder {
            TextView familyName;
            EditText joinCode;
            Button btnJoin;
            ImageView familyImage;
        }
}
