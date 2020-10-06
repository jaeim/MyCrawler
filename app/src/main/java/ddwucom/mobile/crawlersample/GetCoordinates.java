package ddwucom.mobile.crawlersample;


import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;

public class GetCoordinates extends AsyncTask<String, Void, String >  {
    public interface AsyncResponse {
        void processFinish(ArrayList<String> geoPoint) throws ParseException;
    }


//        ProgressDialog dialog = new ProgressDialog(MainActivity.this);


    public MainActivity.AsyncResponse delegate = null;

    protected GetCoordinates(MainActivity.AsyncResponse delegate) {
        this.delegate = delegate;
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        //밑에 세 줄 필요하나?
//            dialog.setMessage("Please wait...");
//            dialog.setCanceledOnTouchOutside(false);
//            dialog.show();
    }

    @Override
    protected String doInBackground(String... strings) {
        String response;
        try {
            String address = strings[0];//얜 어디서 받아오니..
            HttpDataHandler http = new HttpDataHandler();
            String url = String.format("https://maps.googleapis.com/maps/api/geocode/json?address=%s&key=AIzaSyC0YT5JJWRigLrkopfsdHPvsOD6WbCMk3w", address);
            response = http.getHTTPData(url);
            return response;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        try {
            JSONObject jsonObject = new JSONObject(s);

            ArrayList<String> geoPoint = new ArrayList<>();

            String lat = null;
            String lng = null;

            JSONArray results = (JSONArray)jsonObject.get("results");
            if (results.length() != 0) {
                // handle this case, for example
                lat = ((JSONArray)jsonObject.get("results")).getJSONObject(0).getJSONObject("geometry")
                        .getJSONObject("location").get("lat").toString();
                lng = ((JSONArray)jsonObject.get("results")).getJSONObject(0).getJSONObject("geometry")
                        .getJSONObject("location").get("lng").toString();
            }
//                Log.d(TAG, "lat : " + lat + " lng : " + lng);

            geoPoint.clear();
            geoPoint.add(lat);
            geoPoint.add(lng);

            delegate.processFinish(geoPoint);

        } catch (JSONException | ParseException e) {
            e.printStackTrace();
        }
    }

}
