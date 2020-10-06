package ddwucom.mobile.crawlersample;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JsoupBeforeSaveAsyncTask extends AsyncTask<Void, Void, Document> {


    final static String TAG = "Log";

    //    String url = "https://www.seoul.go.kr/coronaV/coronaStatus.do?menu_code=01#route_page_top";
    String url = "https://www.seoul.go.kr/coronaV/coronaStatus.do";
    String data = "";
    TextView textView;


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
                diagCalender.set(Calendar.HOUR_OF_DAY, 0);
                diagCalender.set(Calendar.MINUTE, 0);
                diagCalender.set(Calendar.SECOND, 0);
                diagCalender.set(Calendar.MILLISECOND, 0);

            //오늘날짜 - 14일
            long now = System.currentTimeMillis();
            Date mininumDate = new Date(now);
            Calendar minimumCalendar = Calendar.getInstance();
            minimumCalendar.setTime(mininumDate);

            minimumCalendar.add(Calendar.DATE, -14);
            minimumCalendar.set(Calendar.HOUR_OF_DAY, 0);
            minimumCalendar.set(Calendar.MINUTE, 0);
            minimumCalendar.set(Calendar.SECOND, 0);
            minimumCalendar.set(Calendar.MILLISECOND, 0);

            //오늘 날짜 - 2일
            Date maximunDate = new Date(now);
            Calendar maximumCalendar = Calendar.getInstance();
            maximumCalendar.setTime(maximunDate);

            maximumCalendar.add(Calendar.DATE, -1);

            maximumCalendar.set(Calendar.HOUR_OF_DAY, 0);
            maximumCalendar.set(Calendar.MINUTE, 0);
            maximumCalendar.set(Calendar.SECOND, 0);
            maximumCalendar.set(Calendar.MILLISECOND, 0);

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
//                            savePatient(patient_no, district, beforeDate);
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
                            for (String onlyDate : dividedOnlyDate) {
                                    Log.d(TAG, patient_no +  " / " + district + " / 장소: " + place + "\t / 날짜: " +  onlyDate  );
                            }
                        }
                    }
                }
            }


        }
    }
}
