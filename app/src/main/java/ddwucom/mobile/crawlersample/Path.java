package ddwucom.mobile.crawlersample;

public class Path {
    private String patient_no;
    private String place;
    private int year;
    private int month;
    private int dayOfMonth;
    private String visitDate;
    private String disinfect;
    private Double lat;
    private Double lng;

    public Path() {
        this.patient_no = null;
        this.place = null;
        this.visitDate = null;
        this.disinfect = null;
        this.lat = null;
        this.lng = null;
    }

    public Path(String place, String visitDate) {
        this.place = place;
        this.visitDate = visitDate;
        this.lat = null;
        this.lng = null;
    }

    public Path(String place, String visitDate, Double lat, Double lng) {
        this.place = place;
        this.visitDate = visitDate;
        this.lat = lat;
        this.lng = lng;
    }

    public Path(String place, String visitDate, String disinfect) {
        this.place = place;
        this.visitDate = visitDate;
        this.disinfect = disinfect;
        this.lat = null;
        this.lng = null;
    }

    public Path(String place, String visitDate, String disinfect, Double lat, Double lng) {
        this.place = place;
        this.visitDate = visitDate;
        this.disinfect = disinfect;
        this.lat = lat;
        this.lng = lng;
    }

    public Path(String patient_no, String place, String visitDate, String disinfect, Double lat, Double lng) {
        this.patient_no = patient_no;
        this.place = place;
        this.visitDate = visitDate;
        this.disinfect = disinfect;
        this.lat = lat;
        this.lng = lng;
    }

    public Path(String patient_no, String place, int year, int month, int dayOfMonth, String visitDate, String disinfect, Double lat, Double lng) {
        this.patient_no = patient_no;
        this.place = place;
        this.year = year;
        this.month = month;
        this.dayOfMonth = dayOfMonth;
        this.visitDate = visitDate;
        this.disinfect = disinfect;
        this.lat = lat;
        this.lng = lng;
    }

    public String getVisitDate() {
        return visitDate;
    }

    public void setVisitDate(String visitDate) {
        this.visitDate = visitDate;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public String getDisinfect() {
        return disinfect;
    }

    public void setDisinfect(String disinfect) {
        this.disinfect = disinfect;
    }

    public String getPatient_no() {
        return patient_no;
    }

    public void setPatient_no(String patient_no) {
        this.patient_no = patient_no;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDayOfMonth() {
        return dayOfMonth;
    }

    public void setDayOfMonth(int dayOfMonth) {
        this.dayOfMonth = dayOfMonth;
    }
}
