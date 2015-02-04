package android.internetofthings.mqttclient.fragment;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.internetofthings.mqttclient.service.MQTTservice;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.internetofthings.mqttclient.R;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * created by Giorgio
 */

public class UserRequestFragment extends Fragment {

    private View rootView=null;

    private OnFragmentInteractionListener mListener;

    public UserRequestFragment() {
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
        rootView =  inflater.inflate(R.layout.fragment_clientconnection, container, false);

        return rootView;
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

    @Override
    public void onActivityCreated (Bundle savedInstanceState)
    {
        Button subscribeButton = (Button) rootView.findViewById(R.id.buttonSubscribe);
        Button publishButton = (Button) rootView.findViewById(R.id.buttonPublish);

        addSubscribeButtonListener(subscribeButton);
        addPublishButtonListener(publishButton);

        super.onActivityCreated(savedInstanceState);
    }

    private void addSubscribeButtonListener(Button subscribeButton)
    {
        if (mListener != null) {
            mListener.onFragmentInteraction(subscribeButton.getText().toString(),rootView);
        }
    }

    private void addPublishButtonListener(Button publishButton)
    {
        if (mListener != null) {
            mListener.onFragmentInteraction(publishButton.getText().toString(),rootView);
        }
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
        public void onFragmentInteraction(String text, View rootView);
    }
}