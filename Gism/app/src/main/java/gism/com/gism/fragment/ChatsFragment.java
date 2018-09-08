package gism.com.gism.fragment;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.GregorianCalendar;

import gism.com.gism.R;
import gism.com.gism.adapter.ActivityListRecyclerViewAdapter;
import gism.com.gism.adapter.MessageListAdapter;
import gism.com.gism.utils.MessageListAdapterCache;
import gism.com.gism.utils.MessageStore;
import gism.com.gism.utils.PersistenceUtils;
import gism.com.gism.utils.WSUtils;

/**
 * Created by Sagar on 4/4/2017.
 */
public class ChatsFragment extends Fragment {

    private Button btnSend;
    private EditText inputMsg;

    // Chat messages list messageListAdapter
    private MessageListAdapter messageListAdapter;
    private ListView listViewMessages;

    // Activities list adapter
    private ActivityListRecyclerViewAdapter activityListAdapter;
    private RecyclerView horizontalScrollViewActivities;

    private PersistenceUtils utils;

    private OnFragmentInteractionListener mListener;

    public ChatsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        btnSend = (Button) view.findViewById(R.id.btnSend);
        inputMsg = (EditText) view.findViewById(R.id.inputMsg);
        listViewMessages = (ListView) view.findViewById(R.id.list_view_messages);
        horizontalScrollViewActivities = view.findViewById(R.id.list_view_activities);

        utils = new PersistenceUtils(getActivity().getApplicationContext());

        btnSend.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String body = inputMsg.getText().toString();
                String msgType = "text";
                if(body.startsWith("http")){
                    msgType = "externalLink";
                }
                MessageStore.getInstance().storeMessage(utils.getFamilyId(), "family", "self",
                        GregorianCalendar.getInstance().toString(), msgType, body);

                WSUtils.getInstance().send(utils.getFamilyId(), "family", body, msgType);
                inputMsg.setText("");
            }
        });

        messageListAdapter = new MessageListAdapter(this.getActivity(), utils.getFamilyId(), "family");
        listViewMessages.setAdapter(messageListAdapter);
        MessageListAdapterCache.getInstance().addAdapter(utils.getFamilyId(), "family", messageListAdapter);

        LinearLayoutManager horizontalLayoutManager
                = new LinearLayoutManager(this.getContext(), LinearLayoutManager.HORIZONTAL, false);
        horizontalScrollViewActivities.setLayoutManager(horizontalLayoutManager);
        activityListAdapter = new ActivityListRecyclerViewAdapter(this.getActivity(), utils.getFamilyId());
        horizontalScrollViewActivities.setAdapter(activityListAdapter);

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }


}

