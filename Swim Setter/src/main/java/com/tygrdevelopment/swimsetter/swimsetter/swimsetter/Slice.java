package com.tygrdevelopment.swimsetter.swimsetter.swimsetter;

public class Slice {

    private int reps;
    private int distance;
    private int seconds;
    private int type;
    private String details;
    private final static String[] TYPE_NAMES = {"freestyle", "backstroke", "breaststroke",
            "butterfly", "individual medley", "freestyle kick", "backstroke kick", "breaststroke kick", "fly kick",
            "drill", "choice", "choice kick"};

    public Slice(int r, int d, int s, int t, String det){
        reps = r;
        distance = d;
        seconds = s;
        type = t;
        if(det == null)
            details="";
        else
            details = det;
    }

    public Slice(){
        reps = 1;
        distance = 25;
        seconds = 5;
        type = 0;
        details = "";
    }

    public int getDuration(){
        return seconds * reps;
    }

    public int getLength(){
        return distance * reps;
    }

    public int getReps(){
        return reps;
    }

    public int getDistance(){
        return distance;
    }

    public int getSeconds(){
        return seconds;
    }

    public int getType(){
        return type;
    }

    public String getDetails(){
        return details;
    }

    public void setReps(int r){
        reps = r;
    }

    public void setDistance(int d){
        distance = d;
    }

    public void setSeconds(int s){
        seconds = s;
    }

    public void setType(int t){
        type = t;
    }

    public void setDetails(String det){
        details = det;
    }

    public String getInfo(){
        int min = getDuration()/60;
        int sec = getDuration()%60;
        String dur = "";
        if(min != 0) {
            if(sec < 10)
                dur += min + ":0" + sec;
            else
                dur += min + ":" + sec;
        }
        else {
            if(sec < 10)
                dur += ":0" + sec;
            else
                dur += ":" + sec;
        }

        return dur + ", " + getLength() + " yards";
    }

    public String toString(){
        String reps_dist;
        if(reps == 1)
            reps_dist = "" + distance;
        else
            reps_dist = "" + reps + " X " + distance;

        String interval;
        if(seconds/60 == 0) {
            if (seconds < 10)
                interval = ":0" + seconds;
            else
                interval = ":" + seconds;
        }
        else {
            if (seconds%60 < 10)
                interval = "" + seconds / 60 + ":0" + seconds % 60;
            else
                interval = "" + seconds / 60 + ":" + seconds % 60;
        }

        return reps_dist + " " + TYPE_NAMES[type] + " @ " + interval + " " + details;
    }

}
