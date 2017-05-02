package together.com.homely.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

import together.com.homely.R;
import together.com.homely.utils.Constants;
import together.com.homely.utils.MessageStore;

/**
 * Created by Sagar on 4/10/2017.
 */

public class MessageListAdapter extends BaseAdapter {
    private Context context;
    private List<Map<String, Object>> messagesItems;
    private String family;
    private String area;

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

        String messageType = (String) m.get("type");
        byte[] data = (byte[]) m.get(Constants.DB.MESSAGE);
        //byte[] decodedData = Base64.decode(data, Base64.DEFAULT);
        if("text/plain".equals(messageType)){
            TextView txtMsg = (TextView) convertView.findViewById(R.id.txtMsg);
            txtMsg.setText(new String(data));
        }else if(messageType.startsWith("image") || messageType.equals("image")){
            ImageView imgMsg = (ImageView) convertView.findViewById(R.id.imgMsg);
            imgMsg.setImageBitmap(BitmapFactory.decodeByteArray(data, 0, data.length));
        }

        lblFrom.setText((String)m.get(Constants.DB.SENDER));

        return convertView;
    }
}
