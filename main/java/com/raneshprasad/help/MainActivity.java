package com.raneshprasad.help;

import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;

//import android.support.v4.media.session.MediaControllerCompatApi21;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;

import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.PlaceDetectionApi;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.ibm.mobilefirstplatform.clientsdk.android.analytics.api.Analytics;
import com.ibm.mobilefirstplatform.clientsdk.android.core.api.BMSClient;
import com.ibm.mobilefirstplatform.clientsdk.android.core.api.ResponseListener;
import com.ibm.mobilefirstplatform.clientsdk.android.logger.api.*;
import com.ibm.watson.developer_cloud.android.library.audio.MicrophoneInputStream;
import com.ibm.watson.developer_cloud.android.library.audio.StreamPlayer;
import com.ibm.watson.developer_cloud.android.library.audio.utils.ContentType;
import com.ibm.watson.developer_cloud.conversation.v1.ConversationService;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageRequest;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageResponse;
import com.ibm.watson.developer_cloud.dialog.v1.model.Conversation;
import com.ibm.watson.developer_cloud.speech_to_text.v1.SpeechToText;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.RecognizeOptions;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.SpeechResults;
import com.ibm.watson.developer_cloud.speech_to_text.v1.websocket.RecognizeCallback;
import com.ibm.watson.developer_cloud.text_to_speech.v1.TextToSpeech;
import com.ibm.watson.developer_cloud.text_to_speech.v1.model.Voice;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

public class MainActivity extends AppCompatActivity implements CallBack{
    private static final int SPEECH_REQUEST_CODE = 0;
    public int counter = 0;
    public static double mode = 0;
    public int doubleTap = 0;

    HomeWatcher mHomeWatcher;
    public static LocationManager locationManager;
    public static LocationListener locationListener;
    public static String spokenText = "";
    Button buttonSendLoc;
    public static int add = 0;
    public static String location_str = "http://maps.google.com?q=";
    String note = "";
    ArrayList<String> final_phone = new ArrayList<String>();
    View view;
    public static String temp = "";
    boolean check_base = false;
    //Logger myLogger;
    Conversation conversation;
    public static Context context1;
    public static ArrayList<String> medVals;
    public static ArrayList<String> addresses;
    public static GoogleApiClient mGoogleApiClient;
    public static RecyclerView recyclerView;
    public static ChatAdapter mAdapter;
    public static ArrayList messageArrayList;
    public static EditText inputMessage;
    private ImageButton btnSend;
    private ImageButton btnRecord;
    private Map<String,Object> context = new HashMap<>();
    StreamPlayer streamPlayer;
    private boolean initialRequest;
    private boolean permissionToRecordAccepted = false;
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private static String TAG = "MainActivity";
    private static final int RECORD_REQUEST_CODE = 101;
    private boolean listening = false;
    private SpeechToText speechService;
    private MicrophoneInputStream capture;
    public static Logger myLogger;
    private Context mContext;
    public static String workspace_id;
    public static String conversation_username;
    public static String conversation_password;
    private String STT_username;
    private String STT_password;
    private String TTS_username;
    private String TTS_password;
    private String analytics_APIKEY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        medVals = new ArrayList<>();
        addresses = new ArrayList<>();
        mContext = getApplicationContext();
        conversation_username = mContext.getString(R.string.conversation_username);
        conversation_password = mContext.getString(R.string.conversation_password);
        workspace_id = mContext.getString(R.string.workspace_id);
        STT_username = mContext.getString(R.string.STT_username);
        STT_password = mContext.getString(R.string.STT_password);
        TTS_username = mContext.getString(R.string.TTS_username);
        TTS_password = mContext.getString(R.string.TTS_password);
        analytics_APIKEY = mContext.getString(R.string.mobileanalytics_apikey);


        //Bluemix Mobile Analytics


        locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                temp = location.getLatitude() + "," + location.getLongitude();
                Log.d("My Location", location.getLatitude() + "," + location.getLongitude());
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {
                Intent intent  = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        };

        Logger.send(new ResponseListener() {
            @Override
            public void onSuccess(com.ibm.mobilefirstplatform.clientsdk.android.core.api.Response response) {

            }

            @Override
            public void onFailure(com.ibm.mobilefirstplatform.clientsdk.android.core.api.Response response, Throwable t, JSONObject extendedInfo) {

            }
        });

        // Send logs to the Mobile Analytics Service


        inputMessage = (EditText) findViewById(R.id.message);
        btnSend = (ImageButton) findViewById(R.id.btn_send);
        btnRecord = (ImageButton) findViewById(R.id.btn_record);
        //String customFont = "Montserrat-Regular.ttf";
        //Typeface typeface = Typeface.createFromAsset(getAssets(), );

        inputMessage.setTypeface(Typeface.DEFAULT);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);


        messageArrayList = new ArrayList<>();
        //messageArrayList.add(new Message("10", "Hi", "Hi"));
        mAdapter = new ChatAdapter(messageArrayList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        this.inputMessage.setText("");
        this.initialRequest = true;


        sendMessage();





        //Watson Text-to-Speech Service on Bluemix
        final TextToSpeech service = new TextToSpeech();
        service.setUsernameAndPassword(TTS_username, TTS_password);

        int permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "Permission to record denied");
            makeRequest();
        }



        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, final int position) {
                Thread thread = new Thread(new Runnable() {
                    public void run() {
                        Message audioMessage;
                        try {

                            audioMessage = (Message) messageArrayList.get(position);
                            streamPlayer = new StreamPlayer();
                            if (audioMessage != null && !audioMessage.getMessage().isEmpty())
                                //Change the Voice format and choose from the available choices
                                streamPlayer.playStream(service.synthesize(audioMessage.getMessage(), Voice.EN_LISA).execute());
                            else
                                streamPlayer.playStream(service.synthesize("No Text Specified", Voice.EN_LISA).execute());

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                thread.start();
                Log.d("Progress", "Progress");
            }

            @Override
            public void onLongClick(View view, int position) {
                recordMessage();

            }
        }));

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkInternetConnection()) {
                    sendMessage();
                }
            }
        });

        btnRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recordMessage();
            }
        });

        context1 = getApplicationContext();



        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Log.d("Well, this is annoyying Also", "Oh, what r u gonna do");
            if (MainActivity.this.checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && MainActivity.this.checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Log.d("Now, Im actually mad", "MAD Mad mad");
                requestPermissions(new String[]{
                        android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION,
                        android.Manifest.permission.INTERNET
                }, 10);
                //configureButton();
            }else{
                configureButton();
            }

        }else{
            Log.d("Well, this is annoyying", "Oh, what r u gonna do");
            configureButton();
        }


        mHomeWatcher = new HomeWatcher(this);
        mHomeWatcher.setOnHomePressedListener(new OnHomePressedListener() {
            @Override
            public void onHomePressed() {
                // do something here...
                doubleTap++;
                //Toast.makeText(getApplicationContext(), "Hello World", Toast.LENGTH_LONG).show();
                if(doubleTap == 2) {
                    Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
// Start the activity, the intent will be populated with the speech text
                    startActivityForResult(intent, SPEECH_REQUEST_CODE);
                    mHomeWatcher.stopWatch();
                    doubleTap = 0;
                }

            }
            @Override
            public void onHomeLongPressed() {

            }
        });
        mHomeWatcher.startWatch();
        //finish();

        BMSClient.getInstance().initialize(getApplicationContext(), BMSClient.REGION_US_SOUTH);
        //Analytics is configured to record lifecycle events.
        Analytics.init(getApplication(), "WatBot", analytics_APIKEY, false, Analytics.DeviceEvent.ALL);
        //Analytics.send();
        myLogger = Logger.getLogger("myLogger");
        // Send recorded usage analytics to the Mobile Analytics Service
        Analytics.send(new ResponseListener() {
            @Override
            public void onSuccess(com.ibm.mobilefirstplatform.clientsdk.android.core.api.Response response) {

            }

            @Override
            public void onFailure(com.ibm.mobilefirstplatform.clientsdk.android.core.api.Response response, Throwable t, JSONObject extendedInfo) {

            }
        });
        Analytics.send(new ResponseListener() {
            @Override
            public void onSuccess(com.ibm.mobilefirstplatform.clientsdk.android.core.api.Response response) {

                // Handle Analytics send success here.
            }

            @Override
            public void onFailure(com.ibm.mobilefirstplatform.clientsdk.android.core.api.Response response, Throwable throwable, JSONObject jsonObject) {
                // Handle Analytics send failure here.
            }
        });


    }


    @Override
    protected void onUserLeaveHint(){
        //Toast.makeText(getApplicationContext(), "Hello World", Toast.LENGTH_LONG).show();
        super.onUserLeaveHint();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_POWER)){
            //Do something
            //Toast.makeText(getApplicationContext(), "Hello World", Toast.LENGTH_LONG).show();
        }
        return super.onKeyLongPress(keyCode, event);
    }

    @Override
    protected void onPause(){
        super.onPause();
        //Toast.makeText(getApplicationContext(), "Hello from Pause!", Toast.LENGTH_LONG).show();
        mHomeWatcher = new HomeWatcher(this);
        mHomeWatcher.stopWatch();
        mHomeWatcher.setOnHomePressedListener(new OnHomePressedListener() {
            @Override
            public void onHomePressed() {
                // do something here...
                doubleTap++;
                //Toast.makeText(getApplicationContext(), "Hello World", Toast.LENGTH_LONG).show();
                if(doubleTap == 2) {
                    Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
// Start the activity, the intent will be populated with the speech text
                    startActivityForResult(intent, SPEECH_REQUEST_CODE);

                    doubleTap = 0;
                }

            }
            @Override
            public void onHomeLongPressed() {

            }
        });
        mHomeWatcher.startWatch();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        if (requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK) {
            List<String> results = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);
            spokenText = results.get(0);
            ReadWeb wb = new ReadWeb();
            wb.execute(spokenText);
            /*Log.d("Here is mode", mode + "");
            if(spokenText.equalsIgnoreCase("Help")){
                Toast.makeText(getApplicationContext(), "Help is on the way first.", Toast.LENGTH_LONG).show();
                //location = "http://maps.google.com?q=";
                check_base = true;
                //location = "http://maps.google.com?q=";
                try {
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                            requestPermissions(new String[]{
                                    android.Manifest.permission.READ_CONTACTS,
                                    Manifest.permission.WRITE_CONTACTS
                            }, 10);
                            return;
                        }
                    }
                    final_phone.clear();
                    note = "";
                    ContentResolver resolver = getApplicationContext().getContentResolver();
                    Cursor cursor = resolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

                    while (cursor.moveToNext()) {
                        String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                        String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                        //String favorites = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.EXTRA_ADDRESS_BOOK_INDEX_TITLES));
                        String noteWhere = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
                        String[] noteWhereParams = new String[]{id,
                                ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE};
                        Cursor noteCur = resolver.query(ContactsContract.Data.CONTENT_URI, null, noteWhere, noteWhereParams, null);
                        while (noteCur.moveToNext()){
                            note = noteCur.getString(noteCur.getColumnIndex(ContactsContract.CommonDataKinds.Note.NOTE));
                            Log.d("Notes", note);
                            //String final_ph = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            //final_phone.add(final_ph);

                        }
                        Cursor phoneCursor = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);
                        Log.d("MY INFO", id + " = " + name);
                        Log.d("Extra details", "Hi");
                        while (phoneCursor.moveToNext()) {
                            String phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            Log.d("MY INFO", phoneNumber);
                            if (note.equalsIgnoreCase("Emergency")){
                                Log.d("Help number", phoneNumber + ", ");
                                final_phone.add(phoneNumber + ", ");
                            }
                        }
                        Cursor emailCursor = resolver.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
                                ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?", new String[]{id}, null);
                        while (emailCursor.moveToNext()) {
                            String email = emailCursor.getString(emailCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                            Log.d("MY INFO", email);
                        }

                    }

                    String total_num = "";
                    for (int i = 0; i < final_phone.size(); i++) {
                        if (i % 2 == 0) {
                            total_num = total_num + final_phone.get(i) + "";
                        }
                    }
                    total_num = total_num.substring(0, total_num.length() - 1);
                    String message = "I need help." + Uri.parse(location_str + temp);
                    Uri sendSMSTo = Uri.parse("smsto:" + total_num);
                    System.out.println(total_num);
                    //SmsManager.getDefault().sendTextMessage("6692240132", null, message, null,null);
                    Intent intent = new Intent(Intent.ACTION_SENDTO, sendSMSTo);
                    intent.putExtra("sms_body", message);
                    check_base = false;
                    startActivity(intent);

                   if(check_base) {
                        final String android_id1 = Settings.Secure.getString(getContext().getContentResolver(),
                                Settings.Secure.ANDROID_ID);
                        myRef.child("currentlocation").child(android_id1).addValueEventListener(new ValueEventListener() {

                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (check_base) {
                                    int count = 0;
                                    long count_final = dataSnapshot.getChildrenCount();
                                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                        count++;
                                        if (count == (int)count_final) {
                                            location += postSnapshot.getValue().toString();
                                            break;
                                        }


                                    }
                                    //addresses = geocoder.getFromLocation()
                                    if(final_phone.size() == 0){
                                        Toast.makeText(getContext(), "Go to your contacts and in the Notes, add \"Emergency\"", Toast.LENGTH_LONG);
                                    }else {
                                        Log.d("Array element", final_phone.size() + "");
                                        String total_num = "";
                                        for (int i = 0; i < final_phone.size(); i++) {
                                            if (i % 2 == 0) {
                                                total_num = total_num + final_phone.get(i) + "";
                                            }
                                        }
                                        total_num = total_num.substring(0, total_num.length() - 1);
                                        String message = "I need help." + Uri.parse(location);
                                        Uri sendSMSTo = Uri.parse("smsto:" + total_num);
                                        Intent intent = new Intent(Intent.ACTION_SENDTO, sendSMSTo);
                                        intent.putExtra("sms_body", message);
                                        check_base = false;
                                        startActivity(intent);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    }

                }catch(SecurityException e){
                    Log.d("sec", "Security issue");
                }
                Toast.makeText(getApplicationContext(), "Help is on the way", Toast.LENGTH_LONG).show();
            }*/
            // Do something with spokenText
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public class HomeWatcher {

        static final String TAG = "hg";
        private Context mContext;
        private IntentFilter mFilter;
        private OnHomePressedListener mListener;
        private InnerRecevier mRecevier;

        public HomeWatcher(Context context) {
            mContext = context;
            mFilter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        }

        public void setOnHomePressedListener(OnHomePressedListener listener) {
            mListener = listener;
            mRecevier = new InnerRecevier();
        }

        public void startWatch() {
            if (mRecevier != null) {
                mContext.registerReceiver(mRecevier, mFilter);
            }
        }

        public void stopWatch() {
            if (mRecevier != null) {
                mContext.unregisterReceiver(mRecevier);
            }
        }

        class InnerRecevier extends BroadcastReceiver {
            final String SYSTEM_DIALOG_REASON_KEY = "reason";
            final String SYSTEM_DIALOG_REASON_GLOBAL_ACTIONS = "globalactions";
            final String SYSTEM_DIALOG_REASON_RECENT_APPS = "recentapps";
            final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";

            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
                    String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);
                    if (reason != null) {
                        Log.e(TAG, "action:" + action + ",reason:" + reason);
                        if (mListener != null) {
                            if (reason.equals(SYSTEM_DIALOG_REASON_HOME_KEY)) {
                                mListener.onHomePressed();
                            } else if (reason.equals(SYSTEM_DIALOG_REASON_RECENT_APPS)) {
                                mListener.onHomeLongPressed();
                            }
                        }
                    }
                }
            }
        }
    }
    public interface OnHomePressedListener {
        public void onHomePressed();

        public void onHomeLongPressed();
    }

    private void configureButton(){
        Log.d("My location ", "Hi");
        locationManager.requestLocationUpdates("gps",0, 1, locationListener);

    }

    /*@Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        switch(requestCode){
            case 10:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    configureButton();
                return;
        }
    }*/




    // Speech-to-Text Record Audio permission
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_RECORD_AUDIO_PERMISSION:
                permissionToRecordAccepted  = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
            case RECORD_REQUEST_CODE: {

                if (grantResults.length == 0
                        || grantResults[0] !=
                        PackageManager.PERMISSION_GRANTED) {

                    Log.i(TAG, "Permission has been denied by user");
                } else {
                    Log.i(TAG, "Permission has been granted by user");
                }
                return;
            }
            case 10:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    configureButton();
                return;
            case 11:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)return;
        }
        if (!permissionToRecordAccepted ) finish();

    }

    protected void makeRequest() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.RECORD_AUDIO},
                RECORD_REQUEST_CODE);
    }


    // Sending a message to Watson Conversation Service
    private void sendMessage() {
        DownloadTask da = new DownloadTask();
        da.execute("https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=37.5467335,-121.9873134&radius=500&type=hospital&key=AIzaSyBFxMxE-tIbAxRI3LbNtE0SB_c93R2zoX0");
        /*final String inputmessage = this.inputMessage.getText().toString().trim();
        if(!this.initialRequest) {
            Message inputMessage = new Message();
            inputMessage.setMessage(inputmessage);

            inputMessage.setId("1");
            PlacePicker.IntentBuilder intentBuilder = new PlacePicker.IntentBuilder();

            mGoogleApiClient = new GoogleApiClient
                    .Builder(this)
                    .addApi(Places.GEO_DATA_API)
                    .addApi(Places.PLACE_DETECTION_API)

                    .build();

            //Place place = Place.TYPE_HEALTH;
            //Place place = PlacePicker.getPlace()
            //Place place = PlacePicker.getPlace(PlacePicker.IntentBuilder(), )

            Intent i = PlacePicker.IntentBuilder();
            Place place = PlacePicker.getPlace(new Intent(PlacePicker.IntentBuilder()), this);
            Places.GeoDataApi.getPlaceById(mGoogleApiClient, Place.)
                    .setResultCallback(new ResultCallback<PlaceBuffer>() {
                        @Override
                        public void onResult(PlaceBuffer places) {
                            //Log.d("My Place", places.get);
                            if (places.getStatus().isSuccess() && places.getCount() > 0) {
                                final Place myPlace = places.get(0);
                                Log.d(TAG, "Place found: " + myPlace.getName());
                            } else {
                                Log.e(TAG, "Place not found");
                            }
                            places.release();
                        }
                    });

            //messageArrayList.add(inputMessage);

            //inputMessage.setMessage("https://www.google.com/maps/search/hospital");
            inputMessage.setMessage("https://www.google.com/maps/search/hospital");




            messageArrayList.add(inputMessage);

            myLogger.info("Sending a message to Watson Conversation Service");

        }
        else
        {
            Message inputMessage = new Message();
            inputMessage.setMessage(inputmessage);
            Log.d("Input", inputmessage);
            inputMessage.setId("100");
            this.initialRequest = false;
            Toast.makeText(getApplicationContext(),"Tap on the message for Voice",Toast.LENGTH_LONG).show();

        }

        this.inputMessage.setText("");

        mAdapter = new ChatAdapter(messageArrayList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        this.inputMessage.setText("");
        this.initialRequest = true;

        mAdapter.notifyDataSetChanged();

        Thread thread = new Thread(new Runnable(){
            public void run() {
                try {

                    ConversationService service = new ConversationService(ConversationService.VERSION_DATE_2017_02_03);
                    service.setUsernameAndPassword(conversation_username, conversation_password);
                    MessageRequest newMessage = new MessageRequest.Builder().inputText(inputmessage).context(context).build();
                    MessageResponse response = service.message(workspace_id, newMessage).execute();

                    //Passing Context of last conversation
                    if(response.getContext() !=null)
                    {
                        context.clear();
                        context = response.getContext();

                    }
                    Message outMessage=new Message();
                    if(response!=null)
                    {
                        if(response.getOutput()!=null && response.getOutput().containsKey("text"))
                        {

                            ArrayList responseList = (ArrayList) response.getOutput().get("text");
                            if(null !=responseList && responseList.size()>0){
                                outMessage.setMessage((String)responseList.get(0));
                                outMessage.setId("2");
                            }
                            messageArrayList.add(outMessage);
                        }

                        runOnUiThread(new Runnable() {
                            public void run() {

                                mAdapter.notifyDataSetChanged();
                                if (mAdapter.getItemCount() > 1) {

                                    recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView, null, mAdapter.getItemCount() - 1);

                                }


                            }
                        });


                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();*/

    }

    //Record a message via Watson Speech to Text
    private void recordMessage() {
        //mic.setEnabled(false);
        speechService = new SpeechToText();
        speechService.setUsernameAndPassword(STT_username, STT_password);

        if(listening != true) {
            capture = new MicrophoneInputStream(true);
            new Thread(new Runnable() {
                @Override public void run() {
                    try {
                        speechService.recognizeUsingWebSocket(capture, getRecognizeOptions(), new MicrophoneRecognizeDelegate());
                    } catch (Exception e) {
                        showError(e);
                    }
                }
            }).start();
            listening = true;
            //Toast.makeText(MainActivity.this,"Listening....Click to Stop", Toast.LENGTH_LONG).show();

        } else {
            try {
                capture.close();
                listening = false;
                //Toast.makeText(MainActivity.this,"Stopped Listening....Click to Start", Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * Check Internet Connection
     * @return
     */
    private boolean checkInternetConnection() {
        // get Connectivity Manager object to check connection
        ConnectivityManager cm =
                (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        // Check for network connections
        if (isConnected){
            return true;
        }
        else {
            //Toast.makeText(this, " No Internet Connection available ", Toast.LENGTH_LONG).show();
            return false;
        }

    }

    //Private Methods - Speech to Text
    private RecognizeOptions getRecognizeOptions() {
        return new RecognizeOptions.Builder()
                .continuous(true)
                .contentType(ContentType.OPUS.toString())
                //.model("en-UK_NarrowbandModel")
                .interimResults(true)
                .inactivityTimeout(2000)
                .build();
    }

    //Watson Speech to Text Methods.
    private class MicrophoneRecognizeDelegate implements RecognizeCallback {

        @Override
        public void onTranscription(SpeechResults speechResults) {
            System.out.println(speechResults);
            if(speechResults.getResults() != null && !speechResults.getResults().isEmpty()) {
                String text = speechResults.getResults().get(0).getAlternatives().get(0).getTranscript();
                showMicText(text);
            }
        }

        @Override public void onConnected() {

        }

        @Override public void onError(Exception e) {
            showError(e);
            enableMicButton();
        }

        @Override public void onDisconnected() {
            enableMicButton();
        }

        @Override
        public void onInactivityTimeout(RuntimeException runtimeException) {

        }

        @Override
        public void onListening() {

        }
    }

    private void showMicText(final String text) {
        runOnUiThread(new Runnable() {
            @Override public void run() {
                inputMessage.setText(text);
            }
        });
    }

    private void enableMicButton() {
        runOnUiThread(new Runnable() {
            @Override public void run() {
                btnRecord.setEnabled(true);
            }
        });
    }

    private void showError(final Exception e) {
        runOnUiThread(new Runnable() {
            @Override public void run() {
                //Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        });
    }

































    /*ConversationService service = new ConversationService(ConversationService.VERSION_DATE_2016_09_20);

        service.setUsernameAndPassword("bc986e49-08a6-49e7-a284-69eb6c1d639f", "70KRShhcSm3S");

        MessageRequest newMessage = new MessageRequest.Builder().inputText("Hi").build();

        MessageResponse response = service.message("a8b068f4-ed7f-4147-ae5b-04663bbd75b8", newMessage).execute();*/







    public void methodToCallBack(){
        Log.d("Here is mode", mode + "");
        if(spokenText.contains("help")) {
            Toast.makeText(context1, "Help is on the way first.", Toast.LENGTH_LONG).show();
            //location = "http://maps.google.com?q=";
            check_base = true;
            //location = "http://maps.google.com?q=";
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ActivityCompat.checkSelfPermission(context1, android.Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{
                                android.Manifest.permission.READ_CONTACTS,
                                Manifest.permission.WRITE_CONTACTS
                        }, 11);
                        return;
                    }
                }
                final_phone.clear();
                note = "";
                ContentResolver resolver = context1.getContentResolver();
                Cursor cursor = resolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

                while (cursor.moveToNext()) {
                    String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                    String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    //String favorites = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.EXTRA_ADDRESS_BOOK_INDEX_TITLES));
                    String noteWhere = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
                    String[] noteWhereParams = new String[]{id,
                            ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE};
                    Cursor noteCur = resolver.query(ContactsContract.Data.CONTENT_URI, null, noteWhere, noteWhereParams, null);
                    while (noteCur.moveToNext()) {
                        note = noteCur.getString(noteCur.getColumnIndex(ContactsContract.CommonDataKinds.Note.NOTE));
                        Log.d("Notes", note);
                        //String final_ph = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        //final_phone.add(final_ph);

                    }
                    Cursor phoneCursor = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);
                    Log.d("MY INFO", id + " = " + name);
                    Log.d("Extra details", "Hi");
                    while (phoneCursor.moveToNext()) {
                        String phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        Log.d("MY INFO", phoneNumber);
                        if(mode < 0.2) {
                            if (note.equalsIgnoreCase("Emergency")) {
                                Log.d("Help number", phoneNumber + ", ");
                                final_phone.add(phoneNumber + ", ");
                            }
                        }else if(mode >= 0.2){
                            final_phone.add(phoneNumber + ", ");
                        }
                    }
                    Cursor emailCursor = resolver.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
                            ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?", new String[]{id}, null);
                    while (emailCursor.moveToNext()) {
                        String email = emailCursor.getString(emailCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                        Log.d("MY INFO", email);
                    }

                }
                if(mode > 4.5){
                    final_phone.add("4088023276, ");
                }

                String total_num = "";
                for (int i = 0; i < final_phone.size(); i++) {
                    if (i % 2 == 0) {
                        total_num = total_num + final_phone.get(i) + "";
                    }
                }
                total_num = total_num.substring(0, total_num.length() - 1);
                Log.d("Temp", temp);
                //Uri.parse(location_str + temp)
                String message = "I need help. " + location_str + "37.5467335,-121.9873134";
                Uri sendSMSTo = Uri.parse("smsto:" + total_num);
                System.out.println(total_num);
                //SmsManager.getDefault().sendTextMessage("6692240132", null, message, null,null);
                Intent intent = new Intent(Intent.ACTION_SENDTO, sendSMSTo);
                intent.putExtra("sms_body", "I need help. " + location_str + "37.5467335,-121.9873134");
                check_base = false;
                context1.startActivity(intent);

                    /*if(check_base) {
                        final String android_id1 = Settings.Secure.getString(getContext().getContentResolver(),
                                Settings.Secure.ANDROID_ID);
                        myRef.child("currentlocation").child(android_id1).addValueEventListener(new ValueEventListener() {

                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (check_base) {
                                    int count = 0;
                                    long count_final = dataSnapshot.getChildrenCount();
                                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                        count++;
                                        if (count == (int)count_final) {
                                            location += postSnapshot.getValue().toString();
                                            break;
                                        }


                                    }
                                    //addresses = geocoder.getFromLocation()
                                    if(final_phone.size() == 0){
                                        Toast.makeText(getContext(), "Go to your contacts and in the Notes, add \"Emergency\"", Toast.LENGTH_LONG);
                                    }else {
                                        Log.d("Array element", final_phone.size() + "");
                                        String total_num = "";
                                        for (int i = 0; i < final_phone.size(); i++) {
                                            if (i % 2 == 0) {
                                                total_num = total_num + final_phone.get(i) + "";
                                            }
                                        }
                                        total_num = total_num.substring(0, total_num.length() - 1);
                                        String message = "I need help." + Uri.parse(location);
                                        Uri sendSMSTo = Uri.parse("smsto:" + total_num);
                                        Intent intent = new Intent(Intent.ACTION_SENDTO, sendSMSTo);
                                        intent.putExtra("sms_body", message);
                                        check_base = false;
                                        startActivity(intent);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    }*/

            } catch (SecurityException e) {
                Log.d("sec", "Security issue");
            }
            Toast.makeText(context1, "Help is on the way", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void methodToHospitalCallBack(){
        final String inputmessage = this.inputMessage.getText().toString().trim();
        if(!this.initialRequest) {
            Message inputMessage = new Message();
            inputMessage.setMessage(inputmessage);

            inputMessage.setId("1");
            PlacePicker.IntentBuilder intentBuilder = new PlacePicker.IntentBuilder();

            mGoogleApiClient = new GoogleApiClient
                    .Builder(context1)
                    .addApi(Places.GEO_DATA_API)
                    .addApi(Places.PLACE_DETECTION_API)

                    .build();

            //Place place = Place.TYPE_HEALTH;
            //Place place = PlacePicker.getPlace()
            //Place place = PlacePicker.getPlace(PlacePicker.IntentBuilder(), )

            /*Intent i = PlacePicker.IntentBuilder();
            Place place = PlacePicker.getPlace(new Intent(PlacePicker.IntentBuilder()), this);
            Places.GeoDataApi.getPlaceById(mGoogleApiClient, Place.)
                    .setResultCallback(new ResultCallback<PlaceBuffer>() {
                        @Override
                        public void onResult(PlaceBuffer places) {
                            //Log.d("My Place", places.get);
                            if (places.getStatus().isSuccess() && places.getCount() > 0) {
                                final Place myPlace = places.get(0);
                                Log.d(TAG, "Place found: " + myPlace.getName());
                            } else {
                                Log.e(TAG, "Place not found");
                            }
                            places.release();
                        }
                    });*/

            //messageArrayList.add(inputMessage);

            //inputMessage.setMessage("https://www.google.com/maps/search/hospital");
            Log.d("medVals Main", medVals.toString());
            String total = "";
            for(int i  =0; i < medVals.size(); i++){
                total += medVals.get(i) + ", Address: " + addresses.get(i) + "; ";

                Log.d("Loop val", i  +"");





            }
            if(inputmessage.contains("help") || inputmessage.contains("Help")) {
                inputMessage.setMessage(total);
                messageArrayList.add(inputMessage);
            }
            //inputMessage.setMessage(inputmessage);
            add++;
            //messageArrayList.add(inputMessage);
            myLogger.info("Sending a message to Watson Conversation Service");

        }
        else
        {
            Message inputMessage = new Message();
            inputMessage.setMessage(inputmessage);
            Log.d("Input", inputmessage);
            inputMessage.setId("100");
            this.initialRequest = false;
            //Toast.makeText(getApplicationContext(),"Tap on the message for Voice",Toast.LENGTH_LONG).show();

        }

        this.inputMessage.setText("");

        /*mAdapter = new ChatAdapter(messageArrayList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        this.inputMessage.setText("");
        this.initialRequest = true;*/

        mAdapter.notifyDataSetChanged();

        Thread thread = new Thread(new Runnable(){
            public void run() {
                try {

                    ConversationService service = new ConversationService(ConversationService.VERSION_DATE_2017_02_03);
                    service.setUsernameAndPassword(conversation_username, conversation_password);
                    MessageRequest newMessage = new MessageRequest.Builder().inputText(inputmessage).context(context).build();
                    MessageResponse response = service.message(workspace_id, newMessage).execute();

                    //Passing Context of last conversation
                    if(response.getContext() !=null)
                    {
                        context.clear();
                        context = response.getContext();

                    }
                    Message outMessage=new Message();
                    if(response!=null)
                    {
                        if(response.getOutput()!=null && response.getOutput().containsKey("text"))
                        {

                            ArrayList responseList = (ArrayList) response.getOutput().get("text");
                            if(null !=responseList && responseList.size()>0){
                                outMessage.setMessage((String)responseList.get(0));
                                outMessage.setId("2");
                            }
                            messageArrayList.add(outMessage);
                        }

                        runOnUiThread(new Runnable() {
                            public void run() {

                                mAdapter.notifyDataSetChanged();
                                if (mAdapter.getItemCount() > 1) {

                                    recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView, null, mAdapter.getItemCount() - 1);

                                }


                            }
                        });


                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }


}

