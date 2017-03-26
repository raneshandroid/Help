package com.raneshprasad.help;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import com.ibm.watson.developer_cloud.conversation.v1.ConversationService;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageRequest;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by anubhaprasad on 12/21/16.
 */

public class DownloadTask extends AsyncTask<String, Void, String> {
    Bitmap bitmap;
    String new_icon;
    String nameText = "";
    String temperatureText = "";
    DownloadTask dTask;
    @Override
    protected String doInBackground(String... urls){
        String result = "";
        URL url;
        HttpURLConnection urlConnection = null;
        try {
            url = new URL(urls[0]);
            urlConnection = (HttpURLConnection) url.openConnection();

            InputStream in = urlConnection.getInputStream();
            InputStreamReader reader = new InputStreamReader(in);
            int data = reader.read();

            while(data != -1){
                char current = (char) data;
                result += current;
                data = reader.read();
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
        /*ConversationService service = new ConversationService(ConversationService.VERSION_DATE_2016_09_20);

        service.setUsernameAndPassword("bc986e49-08a6-49e7-a284-69eb6c1d639f", "70KRShhcSm3S");

        MessageRequest newMessage = new MessageRequest.Builder().inputText("Hi").build();

        MessageResponse response = service.message("a8b068f4-ed7f-4147-ae5b-04663bbd75b8", newMessage).execute();*/

        //return null;
    }

    @Override
    protected void onPostExecute(String result){
        super.onPostExecute(result);
        ArrayList<String> medVals = new ArrayList<>();
        ArrayList<String> addresses = new ArrayList<>();
        try{
            JSONObject jsonObject = new JSONObject(result);
            JSONArray mainArr = jsonObject.getJSONArray("results");
            //Log.d("Main Arr length", )
            for(int i = 0; i < mainArr.length(); i++){
                Log.d("Main arr info", mainArr.getJSONObject(i).getString("name"));
                medVals.add(mainArr.getJSONObject(i).getString("name"));
                addresses.add(mainArr.getJSONObject(i).getString("vicinity"));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        Log.d("MedVals", medVals.toString());
        Log.d("addresses", addresses.toString());

        /*try {
            JSONObject jsonObject = new JSONObject(result);
            String weatherInfo = jsonObject.getString("weather");
            //LookResources lookResources = new LookResources();

            JSONObject weatherDatas = new JSONObject(jsonObject.getString("main"));
            //String climateType = weatherDatas.getString("description");
            double tempInt = Double.parseDouble(weatherDatas.getString("temp"));
            int tempIn = (int) (tempInt*1.8-459.67);
            temperatureText = String.valueOf(tempIn) + "F";
            nameText = jsonObject.getString("name");
            //lookResources.city.setText(nameText);
            //lookResources.temp.setText(temperatureText);
            java.net.URL Url = new URL("http://icons.iconarchive.com/icons/benjigarner/softdimension/48/");
            //setWeatherIcon(jsonObject.getInt("id"), jsonObject.getJSONObject("sys").getLong("sunrise"), jsonObject.getJSONObject("sys").getLong("sunrise"), view);
            //Charset charset = Charset.forName("UTF-8").encode("icon");
            //lookResources.setWeatherIcon(jsonObject.getInt("id"), jsonObject.getJSONObject("sys").getLong("sunrise"), jsonObject.getJSONObject("sys").getLong("sunrise"));
            //Byte[] icon = weatherInfo;
            //Log.d("Picture", jsonObject.getString("icon"));
            //bitmap = dTask.StringToBitMap(ImageUrl);
            //lookResources.imgView.setText(new_icon);
            Log.d("City:", nameText);
            Log.d("Temperature", temperatureText);
            //Log.d("Weather Information", climateType);
            JSONArray jsonArray = new JSONArray(weatherInfo);
            for(int i = 0; i < jsonArray.length(); i++){
                JSONObject jsonPart = jsonArray.getJSONObject(i);
                //JSONObject weathweather Data =
            }
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        MainActivity.medVals = medVals;
        MainActivity.addresses = addresses;
        CallBack cb = new MainActivity();
        cb.methodToHospitalCallBack();


    }
    public Bitmap StringToBitMap(String encodedString){
        try {
            byte [] encodeByte= Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap1= BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap1;
        } catch(Exception e) {
            e.getMessage();
            return null;
        }
    }

    class CallBackImpl implements CallBack {
        public void methodToCallBack() {

        }

        public void methodToHospitalCallBack(){

        }
    }


}
