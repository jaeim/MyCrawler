package ddwucom.mobile.crawlersample;

public class Path {

//    private String lat;
//    private String lon;
    //Date 타입으로 바꿀지?
    private String patient_no;
    private String place;
    private String visitDate;
    private String disinfect;
    private Double lat;
    private Double lng;



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
}
