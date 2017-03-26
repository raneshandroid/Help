package com.raneshprasad.help;

import android.os.AsyncTask;
import android.util.Log;

import com.ibm.watson.developer_cloud.alchemy.v1.AlchemyLanguage;
import com.ibm.watson.developer_cloud.alchemy.v1.model.DocumentSentiment;
import com.ibm.watson.developer_cloud.tone_analyzer.v3.ToneAnalyzer;
import com.ibm.watson.developer_cloud.tone_analyzer.v3.model.ToneAnalysis;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by anubhaprasad on 2/5/17.
 */

public class ReadWeb extends AsyncTask<String, Void, String>{
    public static double bigScore = 0;
    double currentScore;
    private Exception exception;
    String unifinal = "";
    public static String desc = "";
    ArrayList<Double> arrDoub;
    @Override
    protected  String doInBackground(String... args){
        ArrayList<Objs> objes = new ArrayList<Objs>();
        //AlchemyLanguage service = new AlchemyLanguage();
        //service.setUsernameAndPassword("prasad_ranesh@yahoo.com", "Krishna173!");

        Map<String, Object> params = new HashMap<String, Object>();
        params.put(AlchemyLanguage.TEXT, args[0]);
        //Log.d("TEXT");
        //DocumentSentiment sentiment = service.getSentiment(params).execute();

        //Log.d("Sentiment", sentiment.toString());

        ToneAnalyzer service1 = new ToneAnalyzer(ToneAnalyzer.VERSION_DATE_2016_05_19);
        service1.setUsernameAndPassword("7490784b-5435-41f4-9ad3-fcd96bb7c5b2", "qifdkMXC52Ju");
        ToneAnalysis tone = service1.getTone(args[0], null).execute();
        Double[] arrEmotions = new Double[5];
        Log.d("TOne", tone.toString());
        try {
            arrDoub = new ArrayList<Double>();
            JSONObject obj = new JSONObject(tone.getDocumentTone().toString());
            JSONArray arr = obj.getJSONArray("tone_categories");
            JSONObject mainElem = arr.getJSONObject(0);
            JSONArray arr2 = mainElem.getJSONArray("tones");

            for (int i = 0; i < arr2.length(); i++) {
                JSONObject currentTone = arr2.getJSONObject(i);
                String toneName = currentTone.getString("tone_name");

                if(toneName.equals("Fear")){
                    bigScore = currentTone.getDouble("score");

                }

                arrDoub.add(currentScore);
                objes.add(new Objs(currentScore, toneName));
            }

            //Double score = arr2.getDouble(2);
            //JSONArray arr12 = arr.getJSONArray(0);
            /*for(int i = 0; i < arr12.length(); i++){
                JSONObject obj12 = arr12.getJSONObject(i);
                arrDoub.add(obj12.getDouble("score"));
            }*/

            final String anger = "\ud83d\ude20";
            final String disgust = "\ud83d\ude16";
            final String fear = "\ud83d\ude31";
            final String joy = "\ud83d\ude03";
            final String sadness = "\ud83d\ude1e";
            ArrayList<Integer> index = new ArrayList<Integer>();
            ArrayList<Double> highestVal = new ArrayList<Double>();

            for(int i = 0; i < objes.size(); i++){
                for(int j = objes.size() - 1; j > 0; j--){
                    if(objes.get(j).compareTo(objes.get(j - 1)) < 0){
                        Objs temp = objes.get(j - 1);
                        objes.set(j - 1, objes.get(j));
                        objes.set(j, temp);
                    }
                }
            }

            if(objes.get(objes.size() - 1).arr.equals("Anger")){
                unifinal += anger;
            }else if(objes.get(objes.size() - 1).arr.equals("Disgust")){
                unifinal += disgust;
            }else if(objes.get(objes.size() - 1).arr.equals("Fear")){
                unifinal += fear;
            }else if(objes.get(objes.size() - 1).arr.equals("Joy")){
                unifinal += joy;

            }else if(objes.get(objes.size() - 1).arr.equals("Sadness")){
                unifinal += sadness;
            }

            if(objes.get(objes.size() - 2).arr.equals("Anger")){
                unifinal += anger;
            }else if(objes.get(objes.size() - 2).arr.equals("Disgust")){
                unifinal += disgust;
            }else if(objes.get(objes.size() - 2).arr.equals("Fear")){
                unifinal += fear;
            }else if(objes.get(objes.size() - 2).arr.equals("Joy")){
                unifinal += joy;

            }else if(objes.get(objes.size() - 2).arr.equals("Sadness")){
                unifinal += sadness;
            }

            if(objes.get(objes.size() - 3).arr.equals("Anger")){
                unifinal += anger;
            }else if(objes.get(objes.size() - 3).arr.equals("Disgust")){
                unifinal += disgust;
            }else if(objes.get(objes.size() - 3).arr.equals("Fear")){
                unifinal += fear;
            }else if(objes.get(objes.size() - 3).arr.equals("Joy")){
                unifinal += joy;

            }else if(objes.get(objes.size() - 3).arr.equals("Sadness")){
                unifinal += sadness;
            }






        }catch(Exception e){
            e.printStackTrace();
        }
        return service1.toString() + "...." + unifinal;
        //return null;

    }





    @Override
    protected  void onPostExecute(String result){

        MainActivity.mode = bigScore;
        CallBack cBack = new MainActivity();
        cBack.methodToCallBack();
        if(result.contains("neutral")){
            //GetSpeech.emogiDesc.setText("Neutral");
        }else if(result.contains("negative")){
            //GetSpeech.emogiDesc.setText("Negative");
        }else if(result.contains("positive")){
            //GetSpeech.emogiDesc.setText("Positive");
        }

        String emojistr = result.substring(result.indexOf("....") + 1, result.length() );
        //GetSpeech.emogi.setText(emojistr);





    }

    class CallBackImpl implements CallBack {
        public void methodToCallBack() {

        }
        public void methodToHospitalCallBack(){

        }
    }



    class Objs implements Comparable<Objs>{
        double number = 0;
        String arr = "";
        public Objs(double number, String arr){
            this.number = number;
            this.arr = arr;
        }
        @Override
        public int compareTo(Objs o){
            if(this.number > o.number){
                return 1;
            }else if(this.number == o.number){
                return 0;
            }else{
                return -1;
            }
        }


    }


            //return sentiment.toString();

            //return "HI";



}
