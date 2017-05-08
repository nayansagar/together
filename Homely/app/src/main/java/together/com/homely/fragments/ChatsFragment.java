package together.com.homely.fragments;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import together.com.homely.R;
import together.com.homely.adapter.MessageListAdapter;
import together.com.homely.utils.JSONUtils;
import together.com.homely.utils.MessageListAdapterCache;
import together.com.homely.utils.MessageStore;
import together.com.homely.utils.PersistenceUtils;
import together.com.homely.utils.WSUtils;

/**
 * Created by Sagar on 4/4/2017.
 */
public class ChatsFragment extends Fragment {

    private Button btnSend;
    private EditText inputMsg;

    // Chat messages list adapter
    private MessageListAdapter adapter;
    private ListView listViewMessages;

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

        adapter = new MessageListAdapter(this.getActivity(), utils.getFamilyId(), "family");
        listViewMessages.setAdapter(adapter);
        MessageListAdapterCache.getInstance().addAdapter(utils.getFamilyId(), "family", adapter);

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

