package com.moodapp.moodcolor;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import com.pubnub.api.Callback;
import com.pubnub.api.Pubnub;
import com.pubnub.api.PubnubError;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    public static final int COLOR_RED   = 0;
    public static final int COLOR_BLUE  = 1;
    public static final int COLOR_GREEN = 2;

    private Pubnub mPubNub;
    public static final String PUBLISH_KEY = "pub-c-11ee32fc-9811-4de8-ba8f-594a79ecc709";
    public static final String SUBSCRIBE_KEY = "sub-c-d57009f0-dc46-11e6-a669-0619f8945a4f";
    public static final String CHANNEL = "phue";

    // set the default values
    public boolean blink = true;
    public String text = "Hello";
    public int duration = 20;
    public int rate = 90;

    private long lastUpdate = System.currentTimeMillis();
    private boolean pHueOn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initPubNub();
    }

    public void initPubNub(){
        this.mPubNub = new Pubnub(
                PUBLISH_KEY,
                SUBSCRIBE_KEY
        );
        this.mPubNub.setUUID("MoodColor");

        subscribe();
    }

    public void publish(int red, int green, int blue){
        JSONObject js = new JSONObject();
        try {
            // color params
            js.put("RED",   red);
            js.put("GREEN", green);
            js.put("BLUE",  blue);

            // display params
            js.put("BLINK", blink);
            js.put("TEXT", text);
            js.put("RATE", rate);
            js.put("TIME", duration);

        } catch (JSONException e) { e.printStackTrace(); }

        Callback callback = new Callback() {
            public void successCallback(String channel, Object response) {
                Log.d("PUBNUB",response.toString());
            }
            public void errorCallback(String channel, PubnubError error) {
                Log.d("PUBNUB",error.toString());
            }
        };
        this.mPubNub.publish(CHANNEL, js, callback);
    }

    public void subscribe(){
        try {
            this.mPubNub.subscribe(CHANNEL, new Callback() {
                @Override
                public void connectCallback(String channel, Object message) {
                    Log.d("PUBNUB","SUBSCRIBE : CONNECT on channel:" + channel
                            + " : " + message.getClass() + " : "
                            + message.toString());
                }

                @Override
                public void disconnectCallback(String channel, Object message) {
                    Log.d("PUBNUB","SUBSCRIBE : DISCONNECT on channel:" + channel
                            + " : " + message.getClass() + " : "
                            + message.toString());
                }

                public void reconnectCallback(String channel, Object message) {
                    Log.d("PUBNUB","SUBSCRIBE : RECONNECT on channel:" + channel
                            + " : " + message.getClass() + " : "
                            + message.toString());
                }

                @Override
                public void successCallback(String channel, Object message) {
                    Log.d("PUBNUB","SUBSCRIBE : " + channel + " : "
                            + message.getClass() + " : " + message.toString());
                }

                @Override
                public void errorCallback(String channel, PubnubError error) {
                    Log.d("PUBNUB","SUBSCRIBE : ERROR on channel " + channel
                            + " : " + error.toString());
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void missOn(View view){
        publish(0, 100, 0);
    }

    public void thinkOn(View view){
        publish(100, 0, 0);
    }

    public void madOn(View view){
        publish(0, 0, 100);
    }
}
