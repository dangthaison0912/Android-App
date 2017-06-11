package com.example.mac.recipez;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.mac.recipez.fragment.SupermarketMap;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import com.google.android.gms.maps.model.MarkerOptions;


/**
 * Created by julia on 08-Jun-17.
 */

public class GetGooglePlaces extends AsyncTask<String, Integer, String> {
    GoogleMap googleMap;

    public GetGooglePlaces(GoogleMap googleMap){
        this.googleMap = googleMap;
    }
    @Override
    protected String doInBackground(String... strings) {
        Log.e("doInBackGround", "getting URL");
        String data = null;
        try{
            data = getURL(strings[0]);
        } catch (Exception e){
            e.printStackTrace();
        }
        Log.e("url:", data.toString());
        return data;
    }

    @Override
    protected void onPostExecute(String result){
        ParseJSON parseJSON = new ParseJSON();
        parseJSON.execute(result);
    }

    private String getURL(String urlString) throws IOException {
        String data = "";
        InputStream inputStream = null;
        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL(urlString);
            //Create http connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();
            inputStream = urlConnection.getInputStream();

            BufferedReader buffReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuffer stgBuffer = new StringBuffer();
//            String string = "";

            while(buffReader.readLine()!=null) {
                data = buffReader.readLine();
                stgBuffer.append(data);
            }
            inputStream.close();
            urlConnection.disconnect();
        } catch (Exception e){
            e.printStackTrace();
        }
        return data; //TODO: check what to return

    }

    private class ParseJSON extends AsyncTask<String, Integer, List<HashMap<String, String>>> {

        @Override
        protected List<HashMap<String, String>> doInBackground(String... params) {
            List<HashMap<String, String>> listSup = null;
            JSONObject jsonObject;
            SupermarketMap supermarketMap = new SupermarketMap();
            try{
                jsonObject = new JSONObject(params[0]);
                listSup = supermarketMap.parseJSONResult(jsonObject);
            } catch (JSONException e){
                e.printStackTrace();;
            }
//            Log.e("listSup:", listSup.toString());
            return listSup;
        }

        @Override
        protected void onPostExecute(List<HashMap<String, String>> listSup){
        //Want to set up all the supermarket markets nearby
            googleMap.clear();//To clear all the markers currently on the map
//
            //TODO
//            for (int i = 0; i < listSup.size() ; i++) {
                MarkerOptions makerOptions = new MarkerOptions();

//                HashMap<String, String> supermarket = listSup.get(i);
//                String name = supermarket.get("name");
//                double latitude = Double.parseDouble(supermarket.get("lat"));
//                double longitude = Double.parseDouble(supermarket.get("lng"));
//
//                LatLng latlng = new LatLng(latitude, longitude);
//                makerOptions.position(latlng);
//                googleMap.addMarker(makerOptions);
//            }


        }
    }
}
