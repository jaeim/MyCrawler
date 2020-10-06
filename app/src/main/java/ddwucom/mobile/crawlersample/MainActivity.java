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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableReference;
import com.google.firebase.functions.HttpsCallableResult;

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
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

        Button btn_seoul_crawl = findViewById(R.id.btn_seoul_crawl);
        btn_seoul_crawl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SeoulJsoupAsyncTask seoulJsoupAsyncTask = new SeoulJsoupAsyncTask();
                seoulJsoupAsyncTask.execute();
            }
        });

        Button btn_rm = findViewById(R.id.btn_rm);
        btn_rm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removePatient();
            }
        });

        Button btn_getPath = findViewById(R.id.btn_getPath);
        btn_getPath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getPath();
            }
        });


        Button btn_log = findViewById(R.id.btn_Log);
        btn_log.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JsoupBeforeSaveAsyncTask JsoupBeforeSaveAsyncTask = new JsoupBeforeSaveAsyncTask();
                JsoupBeforeSaveAsyncTask.execute();
            }
        });


        //asyncTask 자동실행! .execute() 대신..
//        new JsoupAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//        removePatient();
//        getPath();
    }

    /*
    private class SeoulJsoupAsyncTask extends AsyncTask<Void, Void, Document> {

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

                maximumCalendar.add(Calendar.DATE, -1);

                maximumCalendar = initCalendar(maximumCalendar);
//                Log.d(TAG, patient_no + " " + dateFormat.format(diagCalender.getTime()) + " " + district + " ");

//                Log.d(TAG, "확진날짜 : " + dateFormat.format(diagCalender.getTime()) + " 비교날짜 : " + dateFormat.format(minimumCalendar.getTime()));

                if ((diagCalender.after(minimumCalendar) && diagCalender.before(maximumCalendar)) && (district.equals("종로구") | district.equals("광진구") ||
                district.equals("강북구")||  district.equals("관악구") ||district.equals("구로구")  || district.equals("동대문구") || district.equals("마포구") ||
                        district.equals("서대문구") || district.equals("용산구")|| district.equals("은평구")   || district.equals("영등포구")))
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
//                                Log.d(TAG, patient_no + " " + dateFormat.format(diagCalender.getTime()) + " " + district + " " + paths.isEmpty());
                                savePatient(patient_no, district, beforeDate);
                            }

                            for (Element path : paths) {
//                        Log.d(TAG, "tr : " + path.toString());
                                String place = null;
//                                String visitDate = null;
                                //날짜,시간을 분리하지 않은 원형태의 방문날짜 배열(html태그 그대로 남아있음)
                                String[] dividedDatesWithTag = null;
                                //날짜,시간을 분리하지 않은 원형태의 방문날짜 배열(html태그 제거)
                                ArrayList<String> dividedDatesNoTag = new ArrayList<>();
                                //"날짜"만 분리한 문자열 배열
                                ArrayList<String> dividedOnlyDate = new ArrayList<>();
                                String disinfect = null;

                                Elements path_tds = path.select("td");//각 path의 요소(동/장소/주소/노출일시/...)

                                int idx = 0;
                                for (Element path_td : path_tds) {
                                    switch (idx) {
                                        case 2:
                                            place = path_td.text();
                                            break;
                                        case 3:
//                                            visitDate = path_td.text();
                                            String allVisitDate = path_td.toString();
//                                            Log.d(TAG, allVisitDate);
                                            dividedDatesWithTag = allVisitDate.split("<br>");
                                            for(String date : dividedDatesWithTag) {
//                                                Log.d(TAG, place + " : " + date);
                                                Matcher m = Pattern.compile("(\\d{4}[\\.]\\d{1,2}[\\.]\\d{1,2}|\\d{4}[\\.]\\s\\d{1,2}[\\.]\\s\\d{1,2}|\\d{2}[\\.]\\d{1,2}[\\.]\\d{1,2}|\\d{2}[\\.]\\s\\d{1,2}[\\.]\\s\\d{1,2}|\\d{1,2}[\\.]\\d{1,2}|\\d{1,2}[\\.]\\s\\d{1,2}|\\d{4}[\\/]\\d{1,2}[\\/]\\d{1,2}|\\d{4}[\\/]\\s\\d{1,2}[\\/]\\s\\d{1,2}|\\d{2}[\\/]\\d{1,2}[\\/]\\d{1,2}|\\d{2}[\\/]\\s\\d{1,2}[\\/]\\s\\d{1,2}|\\d{1,2}[\\/]\\d{1,2}|\\d{1,2}[\\/]\\s\\d{1,2})",
                                                        Pattern.CASE_INSENSITIVE).matcher(date);
                                                while (m.find()) {
                                                    dividedOnlyDate.add(m.group(1));
                                                    //dividedDates[i]에서 <td>나</td> 문자는 제거한 후 dividedDatesNoTag에 다시 재 저장 필요함!!!!!
                                                    String regex = "<.+?>";
                                                    dividedDatesNoTag.add(date.replaceAll(regex, ""));
                                                    //dividedDatesNoTag[i],dvidedDate[i]는 한 쌍 => savePath
//                                                    Log.d(TAG, place + " : " + m.group(1) + " / " + date);
                                                }

                                    }
                                            break;
                                        case 4:
                                            disinfect = path_td.text();
                                            break;
                                    }
                                    idx++;

                                }
                                //path 한 줄을 객체화 -> DB 저장
//                                savePath(patient_no, disinfect, place, visitDate);

                                int index = 0;
                                for (String onlyDate : dividedOnlyDate) {
                                    //dividedDatesNoTag[i](날짜+시간),dvidedDate[i](날짜만)는 한 쌍 => savePath
//                                    Log.d(TAG, place + " / " + onlyDate + " / " + dividedDatesNoTag.get(index));
                                    try {
                                        Log.d(TAG, patient_no + "/ " + district + " / 날짜: " +  onlyDate +  " / 장소: " + place);
                                        savePath(patient_no, disinfect, place, onlyDate, dividedDatesNoTag.get(index));
                                        index++;
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }

                                }
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


    private void savePath(final String patient_no, final String disinfect, final String place, final String onlyDate, final String dividedDatesNoTag) throws ParseException {
        //해당 patient의 path중 place와 visitDate가 이미 존재한다면 (이미 위도 경도를 구해서 path로 저장됐다면) return
        //KEEP UPDATED@@
        if (place.contains("자택") || place.contains("집") || place.contains("지인") || place.contains("타구") || place.contains("비공개") || place.contains("→") || place.contains("없어") ||
                place.contains("공개하지 않음") || place.contains("상호") || place.contains("타지역") || place.contains("타 구") || place.contains("타 시도") || place.contains("완료") ||
                place.contains("역학조사중") || place.contains("공개여부") || place.contains("타시도") || place.contains("미공개") || place.contains("확인 후") || place.contains("*") ||
                place.contains("직장") || place.contains("해당 지역") || place.contains("타 지역") ||
                place.isEmpty() || place.equals(null) || place.equals("") ||
                place.equals(" ") || place.equals("-") || place.equals("능동") || place.equals("ATM기기") || place.equals("식당") || place.equals("공공기관") || place.equals("병원") ||
                place.equals("약국") || place.equals("마트") || place.equals("카페") || place.equals("음식점") || place.equals("은행") || place.equals("편의점") || place.equals("금융기관") ||
                place.equals("체육동호회") || place.equals("희망병원(자차 이용)") || place.equals("보건소 선별진료소 검체채취") || place.equals("보건소 선별진료소") || place.equals("선별진료소 검체채취") ||
                place.equals("주유소") || place.equals("상점") || place.equals("빵집") ||  place.equals("청과물점") || place.equals("A음식점") || place.equals("B음식점") || place.equals("부동산중개업소") ||
                place.equals("A병원") || place.equals("B약국") || place.equals("D약국") || place.equals("음식접") || place.equals("B마트") || place.equals("C마트") || place.equals("D병원") ||
                place.equals("A마트")
        )
        {
            //path 저장x..굳이 지도에 나타낼 일도 없으니까(위도,경도 구했을때만 저장함)
        } else {

            GetCoordinates getCoordinates = (GetCoordinates) new GetCoordinates(new AsyncResponse() {
                @Override
                public void processFinish(ArrayList<String> geoPoint) throws ParseException {
                    //Here you will receive the result fired from async class
                    //of onPostExecute(result) method.
//                    Log.d(TAG, "lat : " + geoPoint.get(0) + " lng : " + geoPoint.get(1));
                    if (geoPoint.get(0) != null && geoPoint.get(1) != null && geoPoint != null) {
                        //위도 경도가 존재하고 && 중복되는 place, visitDate가 없으면 path 저장
                        Double lat = Double.parseDouble(geoPoint.get(0));
                        Double lng = Double.parseDouble(geoPoint.get(1));

                        //string형태의 date에서 Date변환->format->/를 기준으로 split->year,month,day형태로 저장

                        //KEEP UPDATED@@@@
                        String dividedDate[] = null;
                        int year = 1900; int month = 01; int dayOfMonth = 01;
                        if(onlyDate != null && dividedDatesNoTag != null) {
                            //matcher를 통해 Date로 변환되었는지 check
                            boolean formatCheck = false;
                            Date date = null;
                            String onlyDateWithString = null;

                            SimpleDateFormat format1 = new SimpleDateFormat("yyyy.MM.dd");
                            SimpleDateFormat format2 = new SimpleDateFormat("yy.MM.dd");
                            SimpleDateFormat format3 = new SimpleDateFormat("MM.dd");
                            SimpleDateFormat format4 = new SimpleDateFormat("MM. dd");

                            SimpleDateFormat commonFormat = new SimpleDateFormat("yyyy/MM/dd");

                            //각각 m1에서 처리한 내용이 m3,m4에 가서도 처리되어 오류남..formatCheck 사용
                            Matcher m1 = Pattern.compile("(\\d{4}[\\.]\\d{1,2}[\\.]\\d{1,2}|\\d{4}[\\.]\\s\\d{1,2}[\\.]\\s\\d{1,2})", Pattern.CASE_INSENSITIVE).matcher(onlyDate);
                            while (m1.find() && !formatCheck) {
                                date = format1.parse(onlyDate);
                                formatCheck = true;
                            }
                            Matcher m2 = Pattern.compile("(\\d{2}[\\.]\\d{1,2}[\\.]\\d{1,2}|\\d{2}[\\.]\\s\\d{1,2}[\\.]\\s\\d{1,2})", Pattern.CASE_INSENSITIVE).matcher(onlyDate);
                            while (m2.find() && !formatCheck) {
                                date = format2.parse(onlyDate);
                                formatCheck = true;
                            }
                            Matcher m3 = Pattern.compile("(\\d{1,2}[\\.]\\d{1,2})", Pattern.CASE_INSENSITIVE).matcher(onlyDate);
                            while (m3.find() && !formatCheck) {
                                date = format3.parse(onlyDate);
                                formatCheck = true;
                            }
                            Matcher m4 = Pattern.compile("(\\d{1,2}[\\.]\\s\\d{1,2})", Pattern.CASE_INSENSITIVE).matcher(onlyDate);
                            while (m4.find() && !formatCheck) {
                                date = format4.parse(onlyDate);
                                formatCheck = true;
                            }
                            //year를 2020년으로 지정
                            Calendar dateToCalendar = Calendar.getInstance();
                            dateToCalendar.setTime(date);
                            dateToCalendar.set(Calendar.YEAR, 2020);
                            dateToCalendar = initCalendar(dateToCalendar);

                            date = dateToCalendar.getTime();
//                          해당 방문 날짜를 yyyy/MM/dd 형식으로 변환 후 /를 기준으로 나눈다.
                            onlyDateWithString = commonFormat.format(date);
//                          Log.d(TAG, place + " / " + onlyDateWithString + " / " + onlyDate);
                            dividedDate = onlyDateWithString.split("/");

                            year = Integer.parseInt(dividedDate[0]);
                            month = Integer.parseInt(dividedDate[1]);
                            dayOfMonth = Integer.parseInt(dividedDate[2]);
                        }
                        FirebaseFirestore database = FirebaseFirestore.getInstance();

                        Path path = new Path(patient_no, place, year, month, dayOfMonth, dividedDatesNoTag, disinfect, lat, lng);
//                        Log.d(TAG, "장소: " + place + " / y : " + year + " m: " + month + " d : " + dayOfMonth + " / 날짜만 : " + onlyDate + " / 날짜와 시간 : " + dividedDatesNoTag);
                        //set해서 id 지정하여 path 덮어쓰는 ver
                        database.collection("patients").document(patient_no).
                                collection("paths").document(patient_no+place+month+dayOfMonth)
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
        void processFinish(ArrayList<String> geoPoint) throws ParseException;
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

            } catch (JSONException | ParseException e) {
                e.printStackTrace();
            }
        }
    }
*/
    public interface AsyncResponse {
        void processFinish(ArrayList<String> geoPoint) throws ParseException;
    }

    private Calendar initCalendar(Calendar calendar) {
        Calendar newCalender = calendar;

        newCalender.set(Calendar.HOUR_OF_DAY, 0);
        newCalender.set(Calendar.MINUTE, 0);
        newCalender.set(Calendar.SECOND, 0);
        newCalender.set(Calendar.MILLISECOND, 0);

        return newCalender;
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
                            Log.d(TAG, path.getPatient_no() + " / " + path.getPlace() + " / LAT, LNG : " + path.getLat() + ", " + path.getLng());
//                            Log.d(TAG, snap.getId() + " => " + snap.getData());
                        }
                    }
                });
    }

    private void removePatient() {

        SimpleDateFormat dateFormat = new SimpleDateFormat ("M/d");

        long now = System.currentTimeMillis();
        Date compareDate = new Date(now);
        Calendar compareCalender = Calendar.getInstance();
        compareCalender.setTime(compareDate);

        compareCalender.add(Calendar.DATE, -3);

        compareCalender = initCalendar(compareCalender);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Log.d(TAG, dateFormat.format(compareCalender.getTime()));

        db.collection("patients")
                .whereLessThan("diagDate", dateFormat.format(compareCalender.getTime()))
//                .whereEqualTo("diagDate", dateFormat.format(compareCalender.getTime()))
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                //You can only delete a document once you have a DocumentReference to it.
                                Log.d(TAG, document.getId() + " " + document.get("diagDate"));
                                deleteAtPath(document.getId());
//                                DocumentSnapshot documentSnapshot = document;
//                                document.getReference().delete();
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

        //일단 각각 patients의 문서 하나 가져온담에 거기서 diagdate 가져오고 캘린더 객체 만들어서 비교 할까..???
    }



    /**
     * Call the 'recursiveDelete' callable function with a path to initiate
     * a server-side delete.
     */
    public void deleteAtPath(String path) {
        path = "/patients/" + path;
        //path is "/patients/22411"
        Map<String, Object> data = new HashMap<>();
        data.put("path", path);

        Log.d(TAG, path);
        HttpsCallableReference deleteFn =
                FirebaseFunctions.getInstance().getHttpsCallable("recursiveDelete");
        deleteFn.call(data)
                .addOnSuccessListener(new OnSuccessListener<HttpsCallableResult>() {
                    @Override
                    public void onSuccess(HttpsCallableResult httpsCallableResult) {
                        // Delete Success
                        Log.d(TAG, "deleted");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                        // Delete failed
                        // ...
                    }
                });
    }
}



