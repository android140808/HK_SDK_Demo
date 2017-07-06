package cn.appscomm.netlib.bean.sport;

import java.util.List;

import cn.appscomm.netlib.bean.base.BaseObtainBean;

public class CountSportObtain  extends BaseObtainBean{

    private  float totalStep;
    private  float totalCalorie;
    private  float  totalDistance;

    public float getTotalStep() {
        return totalStep;
    }

    public void setTotalStep(float totalStep) {
        this.totalStep = totalStep;
    }

    public float getTotalCalorie() {
        return totalCalorie;
    }

    public void setTotalCalorie(float totalCalorie) {
        this.totalCalorie = totalCalorie;
    }

    public float getTotalDistance() {
        return totalDistance;
    }

    public void setTotalDistance(float totalDistance) {
        this.totalDistance = totalDistance;
    }

    public List<CountSportDetail> getDetails() {
        return details;
    }

    public void setDetails(List<CountSportDetail> details) {
        this.details = details;
    }

    private List<CountSportDetail>  details;

    @Override
    public String toString() {

        StringBuffer sDetails = new StringBuffer();
        for(CountSportDetail c: details){
            sDetails.append(c.toString()+"-----");
        }

        return "CountSportObtain{" +
                "totalStep=" + totalStep +
                ", totalCalorie=" + totalCalorie +
                ", totalDistance=" + totalDistance +
                ", details=" + sDetails +
                '}';
    }
}
