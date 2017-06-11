package com.example.mac.recipez;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

/**
 * Created by julia on 01-Jun-17.
 */

public class Connection extends AppCompatActivity{

    public static final String server = "http://www.doc.ic.ac.uk/project/2016/271/g1627110/web/api/";

    //To add Recipe into the SQL server
    public class putDataIntoDB extends AsyncTask<Void, Void, Void> {

//        Recipe recipe;
//
//        public putDataIntoDB(Recipe recipe){
//            this.recipe = recipe;
//        }

        @Override
        protected void onPreExecute(){

        }

        @Override
        protected Void doInBackground(Void... params) {


            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            //to display any forms of progress while the background computation is still running
            super.onProgressUpdate(values);
        }


        protected void onPostExecute(Void result){
            //invoked on the UI thread after the background computation is finished
            super.onPostExecute(result);
        }
    }




}
