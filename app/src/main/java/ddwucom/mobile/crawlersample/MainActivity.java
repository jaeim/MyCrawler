package ddwucom.mobile.crawlersample;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
//import org.w3c.dom.Element;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity  {

    final static String TAG = "MainActivity";

//    String url = "https://www.seoul.go.kr/coronaV/coronaStatus.do?menu_code=01#route_page_top";
    String url = "https://www.seoul.go.kr/coronaV/coronaStatus.do";
    String data = "";
    TextView textView;

    String today = null;
    Path userPath = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        long now = System.currentTimeMillis(); // 1970년 1월 1일부터 몇 밀리세컨드가 지났는지를 반환함
        Date date = new Date(now);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("M/d");
        today = simpleDateFormat.format(date);

        textView = findViewById(R.id.data);

        Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JsoupAsyncTask jsoupAsyncTask = new JsoupAsyncTask();
                jsoupAsyncTask.execute();
            }
        });

        //asyncTask 자동실행! .execute() 대신..
//        new JsoupAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);


//        removePatient();
//        getPath();
    }

    private class JsoupAsyncTask extends AsyncTask<Void, Void, Document> {

        Document doc = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Document doInBackground(Void... voids) {

            try {
                //file 테스트 not working..no such directory
//               File input = new File("/Users/thdwo/Desktop/소프트웨어 경진대회/wjsqn.html");
//               Document doc = Jsoup.parse(input, "UTF-8");

                //url 테스트
                doc = Jsoup.connect(url).
                        get();

                Log.d(TAG, "connected");


            } catch (IOException e) {
                e.printStackTrace();
            }
            return doc;
        }

        @Override
        protected void onPostExecute(Document doc) {
            String patient_no = null;
            String district = null;
            //확진날짜의 String 형태
            String beforeDate = null;

            Log.d(TAG, doc.title());

            Elements patients = doc.select(".patient");
            Elements scripts = patients.tagName("script");

            for (Element script : scripts) {//각 확진자마다 변수 제대로 초기화하는지 체크!!
//                Log.d(TAG, script.attr("class", "extra-row-content").toString());
//                Log.d(TAG, script.toString());

                Elements tds = script.select("script > td");//script의 자식인 td들

                int i = 0;
                for (Element td : tds) {
                    switch (i) {
                        case 1:
                            patient_no = td.text();
                            break;
                        case 3:
                            beforeDate = td.text();
//                            Log.d(TAG, diagDate);
                            break;
                        case 4:
                            district = td.text();
                    }
                    i++;
                }
                //script태그안의 내용을 script 태그 제거한 상태로 가져옴

                //확진날짜 + 50년
                Date diagDate = null;

                SimpleDateFormat dateFormat = new SimpleDateFormat ("M/d");
                try {
                    diagDate = dateFormat.parse(beforeDate);
//                    maximunDate = dateFormat.parse("8/21");
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Calendar diagCalender = Calendar.getInstance();
                diagCalender.setTime(diagDate);

                diagCalender.add(Calendar.YEAR, 50);

                diagCalender = initCalendar(diagCalender);
//                diagCalender.set(Calendar.HOUR_OF_DAY, 0);
//                diagCalender.set(Calendar.MINUTE, 0);
//                diagCalender.set(Calendar.SECOND, 0);
//                diagCalender.set(Calendar.MILLISECOND, 0);

                //오늘날짜 - 14일
                long now = System.currentTimeMillis();
                Date mininumDate = new Date(now);
                Calendar minimumCalendar = Calendar.getInstance();
                minimumCalendar.setTime(mininumDate);

                minimumCalendar.add(Calendar.DATE, -14);

                minimumCalendar = initCalendar(minimumCalendar);

                //오늘 날짜 - 2일
                Date maximunDate = new Date(now);
                Calendar maximumCalendar = Calendar.getInstance();
                maximumCalendar.setTime(maximunDate);

                maximumCalendar.add(Calendar.DATE, -2);

                maximumCalendar = initCalendar(maximumCalendar);
//                Log.d(TAG, patient_no + " " + dateFormat.format(diagCalender.getTime()) + " " + district + " ");

//                Log.d(TAG, "확진날짜 : " + dateFormat.format(diagCalender.getTime()) + " 비교날짜 : " + dateFormat.format(minimumCalendar.getTime()));


                //원래 코드:(diagCalender.after(minimumCalendar)) && district.equals("종로구")
                if ((diagCalender.after(minimumCalendar) && diagCalender.before(maximumCalendar)) && (district.equals("종로구") || district.equals("광진구") ||
                district.equals("강북구") || district.equals("관악구") || district.equals("구로구") || district.equals("동대문구") || district.equals("마포구") ||
                        district.equals("서대문구") || district.equals("용산구") || district.equals("은평구")))
                {

                    //script태그로 묶여진 내용은 select로 확인이 안됨..->String으로 추출 후 Document화->parsing
                    Node node = null;
                    node = script.childNode(1).childNode(3);

                    if (node != null) {

//                    Log.d(TAG, "size: " + node.childNodeSize());
                        String tr = node.childNode(0).toString();
//                    Log.d(TAG, "node :" + tr);
                        Document trDoc = Jsoup.parse(tr);
                        if (trDoc.select(".table-path tbody tr") != null) {//테이블 형식으로 동선을 표현한 행이라면..
                            Elements paths = trDoc.select(".table-path tbody tr");

                            //저장할 동선이 있는 경우에만 환자정보 저장..?
                            if(paths.isEmpty() == false) {
                                Log.d(TAG, patient_no + " " + dateFormat.format(diagCalender.getTime()) + " " + district + " " + paths.isEmpty());
                                savePatient(patient_no, district, beforeDate);
                            }

                            for (Element path : paths) {
//                        Log.d(TAG, "tr : " + path.toString());
                                String place = null;
                                String visitDate = null;
                                String disinfect = null;

                                Elements path_tds = path.select("td");//각 path의 요소(동/장소/주소/노출일시/...)

                                int idx = 0;
                                for (Element path_td : path_tds) {
                                    switch (idx) {
                                        case 2:
                                            place = path_td.text();
                                            break;
                                        case 3:
                                            visitDate = path_td.text();
                                            break;
                                        case 4:
                                            disinfect = path_td.text();
                                            break;
                                    }
                                    idx++;

                                }
                                //path 한 줄을 객체화 -> DB 저장
//                        Log.d(TAG, "Place : " + place + " VisitDate : " + visitDate  + "\n");
                                savePath(patient_no, disinfect, place, visitDate);
                            }
                        }
                    }
                }


            }
        }
    }

    private void savePatient(String patient_no, String district, String diagDate) {


        Patient patient = new Patient(patient_no, district, diagDate);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("patients").document(patient_no)
                .set(patient)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
//                        Log.d(TAG, "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });


    }


    private void savePath(final String patient_no, final String disinfect, final String place, final String visitDate) {
        //해당 patient의 path중 place와 visitDate가 이미 존재한다면 (이미 위도 경도를 구해서 path로 저장됐다면) return

//컬렉션 그룹

        FirebaseFirestore db = FirebaseFirestore.getInstance();
/*
        db.collectionGroup("paths").whereEqualTo("patient_no", patient_no).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot snap : queryDocumentSnapshots) {;
                            if(snap.get("place").equals(place) && snap.get("visitDate").equals(visitDate)) {
                                    Log.d(TAG, snap.get("patient_no") + " / 기존저장된 장소: " + snap.get("place") + " /새로 저장하려는 장소 : " + place);


                            }
                        }
                    }
                });
*/

        if (place.contains("자택") || place.contains("집") || place.contains("지인") || place.contains("타구") || place.contains("비공개") || place.contains("→") || place.contains("없어") ||
                place.contains("공개하지 않음") || place.contains("상호") || place.contains("타지역") || place.contains("타 구") || place.contains("타 시도") || place.contains("완료") ||
                place.contains("역학조사중") || place.contains("공개여부") || place.contains("타시도") || place.contains("미공개") || place.contains("확인 후") || place.contains("*") ||
                place.contains("직장") || place.contains("해당 지역") ||
                place.isEmpty() || place.equals(null) || place.equals("") ||
                place.equals(" ") || place.equals("-") || place.equals("능동") || place.equals("ATM기기") || place.equals("식당") || place.equals("공공기관") || place.equals("병원") ||
                place.equals("약국") || place.equals("마트") || place.equals("카페") || place.equals("음식점") || place.equals("은행") || place.equals("편의점") || place.equals("금융기관") ||
                place.equals("체육동호회") || place.equals("희망병원(자차 이용)") || place.equals("보건소 선별진료소 검체채취") || place.equals("보건소 선별진료소") || place.equals("선별진료소 검체채취")
        )
        {
            //path 저장x..굳이 지도에 나타낼 일도 없으니까(위도,경도 구했을때만 저장함)
        } else {

            GetCoordinates getCoordinates = (GetCoordinates) new GetCoordinates(new AsyncResponse() {
                @Override
                public void processFinish(ArrayList<String> geoPoint) {
                    //Here you will receive the result fired from async class
                    //of onPostExecute(result) method.
//                    Log.d(TAG, "lat : " + geoPoint.get(0) + " lng : " + geoPoint.get(1));
                    if (geoPoint.get(0) != null && geoPoint.get(1) != null && geoPoint != null) {
                        //위도 경도가 존재하고 && 중복되는 place, visitDate가 없으면 path 저장
                        Double lat = Double.parseDouble(geoPoint.get(0));
                        Double lng = Double.parseDouble(geoPoint.get(1));

                        Path path = new Path(patient_no, place, visitDate, disinfect, lat, lng);
                        FirebaseFirestore database = FirebaseFirestore.getInstance();
                        

                        //set해서 id 지정하여 path 덮어쓰는 ver
                        database.collection("patients").document(patient_no).
                                collection("paths").document(patient_no + " " + place)
                                .set(path)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
//                                        Log.d(TAG, "DocumentSnapshot successfully written!");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
//                                        Log.w(TAG, "Error writing document", e);
                                    }
                                });


                    }
                }
            }).execute(place.replace(" ", "+"));


        }

    }

    public interface AsyncResponse {
        void processFinish(ArrayList<String> geoPoint);
    }

    public static class GetCoordinates extends AsyncTask<String, Void, String > {
//        ProgressDialog dialog = new ProgressDialog(MainActivity.this);


        public AsyncResponse delegate = null;

        protected GetCoordinates(AsyncResponse delegate) {
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

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private Calendar initCalendar(Calendar calendar) {
        Calendar newCalender = calendar;

        newCalender.set(Calendar.HOUR_OF_DAY, 0);
        newCalender.set(Calendar.MINUTE, 0);
        newCalender.set(Calendar.SECOND, 0);
        newCalender.set(Calendar.MILLISECOND, 0);

        return newCalender;
    }


    //별도의 클래스로 관리하는게 좋을듯
    private void removePatient() {
        SimpleDateFormat dateFormat = new SimpleDateFormat ("M/d");

        long now = System.currentTimeMillis();
        Date compareDate = new Date(now);
        Calendar compareCalender = Calendar.getInstance();
        compareCalender.setTime(compareDate);

        compareCalender.add(Calendar.DATE, -13);

        compareCalender = initCalendar(compareCalender);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("patients")
                .whereLessThan("diagDate", dateFormat.format(compareCalender.getTime()))
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                //You can only delete a document once you have a DocumentReference to it.
                                Log.d(TAG, document.getId());
                                document.getReference().delete();
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }


    protected void getPath() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collectionGroup("paths").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot snap : queryDocumentSnapshots) {
                            DocumentSnapshot documentSnapshot = snap;
                            Path path = documentSnapshot.toObject(Path.class);
                            Log.d(TAG, path.getPatient_no() + " / " + path.getPlace());
//                            Log.d(TAG, snap.getId() + " => " + snap.getData());
                        }
                    }
                });

    }

}



