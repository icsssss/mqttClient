package android.internetofthings.mqttclient;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.internetofthings.mqttclient.fragment.UserRequestFragment;
import android.internetofthings.mqttclient.service.MQTTservice;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * https://developer.motorolasolutions.com/docs/DOC-2315
 * edited by Giorgio
 */

public class RootActivity extends ActionBarActivity implements UserRequestFragment.OnFragmentInteractionListener {

    private Messenger service = null;
    private final Messenger serviceHandler = new Messenger(new ServiceHandler());

    private IntentFilter intentFilter = null;
    private PushReceiver pushReceiver;

    private View clientconnectionView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_root);

        intentFilter = new IntentFilter();
        intentFilter.addAction("com.example.MQTT.PushReceived");
        pushReceiver = new PushReceiver();
        registerReceiver(pushReceiver, intentFilter, null, null);

        startService(new Intent(this, MQTTservice.class));


        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new UserRequestFragment())
                    .commit();
        }

    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName arg0, IBinder binder) {
            service = new Messenger(binder);
            Bundle data = new Bundle();
            data.putSerializable(MQTTservice.CLASSNAME, RootActivity.class);
            data.putCharSequence(MQTTservice.INTENTNAME, "com.example.MQTT.PushReceived");
            Message msg = Message.obtain(null, MQTTservice.REGISTER);
            msg.setData(data);
            msg.replyTo = serviceHandler;
            try {
                service.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
        }
    };


    @Override
    protected void onStart()
    {
        super.onStart();
        bindService(new Intent(this, MQTTservice.class), serviceConnection, 0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(pushReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(pushReceiver);
    }


    @Override
    public void onFragmentInteraction(String text, View rootView) {

        clientconnectionView = rootView;
        //instanceFragment = fragment;

        switch (text)
        {
            case "Subscribe":
                addSubscribeButtonListener();
                break;
            case "Publish":
                addPublishButtonListener();
                break;
        }

    }

    private void addSubscribeButtonListener() {

        Button subscribeButton = (Button) clientconnectionView.findViewById(R.id.buttonSubscribe);
        subscribeButton.setOnClickListener(new View.OnClickListener() {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

            @Override
            public void onClick(View arg0) {
                TextView result = (TextView) clientconnectionView.findViewById(R.id.textResultStatus);
                EditText t = (EditText) clientconnectionView.findViewById(R.id.EditTextTopic);
                String topic = t.getText().toString().trim();
                inputMethodManager.hideSoftInputFromWindow(result.getWindowToken(), 0);

                if (topic != null && topic.isEmpty() == false) {
                    result.setText("");
                    Bundle data = new Bundle();
                    data.putCharSequence(MQTTservice.TOPIC, topic);
                    Message msg = Message.obtain(null, MQTTservice.SUBSCRIBE);
                    msg.setData(data);
                    msg.replyTo = serviceHandler;
                    try {
                        service.send(msg);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                        result.setText("Subscribe failed with exception:" + e.getMessage());
                    }
                } else {
                    result.setText("Topic required.");
                }
            }
        });

    }

    private void addPublishButtonListener() {

        Button publishButton = (Button) clientconnectionView.findViewById(R.id.buttonPublish);
        publishButton.setOnClickListener(new View.OnClickListener() {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

            @Override
            public void onClick(View arg0) {
                EditText t = (EditText) clientconnectionView.findViewById(R.id.EditTextTopic);
                EditText m = (EditText) clientconnectionView.findViewById(R.id.editTextMessage);
                TextView result = (TextView) clientconnectionView.findViewById(R.id.textResultStatus);

                inputMethodManager.hideSoftInputFromWindow(result.getWindowToken(), 0);

                String topic = t.getText().toString().trim();
                String message = m.getText().toString().trim();

                if (topic != null && topic.isEmpty() == false && message != null && message.isEmpty() == false) {
                    result.setText("");
                    Bundle data = new Bundle();
                    data.putCharSequence(MQTTservice.TOPIC, topic);
                    data.putCharSequence(MQTTservice.MESSAGE, message);
                    Message msg = Message.obtain(null, MQTTservice.PUBLISH);
                    msg.setData(data);
                    msg.replyTo = serviceHandler;
                    try {
                        service.send(msg);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                        result.setText("Publish failed with exception:" + e.getMessage());
                    }
                } else {
                    result.setText("Topic and message required.");
                }
            }
        });

    }

    public class PushReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent i) {
            String topic = i.getStringExtra(MQTTservice.TOPIC);
            String message = i.getStringExtra(MQTTservice.MESSAGE);
            Toast.makeText(context, "Push message received - " + topic + ":" + message, Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_root, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    class ServiceHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MQTTservice.SUBSCRIBE:
                    break;
                case MQTTservice.PUBLISH:
                    break;
                case MQTTservice.REGISTER:
                    break;
                default:
                    super.handleMessage(msg);
                    return;
            }

            Bundle b = msg.getData();
            if (b != null) {
                TextView result = (TextView) clientconnectionView.findViewById(R.id.textResultStatus);
                Boolean status = b.getBoolean(MQTTservice.STATUS);
                if (status == false) {
                    result.setText("Fail");
                } else {
                    result.setText("Success");
                }
            }

        }
    }


}