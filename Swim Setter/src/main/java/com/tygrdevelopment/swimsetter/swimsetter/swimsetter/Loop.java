package com.tygrdevelopment.swimsetter.swimsetter.swimsetter;

import java.util.ArrayList;

public class Loop {

    private final int reps;
    private final ArrayList<Slice> slices;

    public Loop(int r){
        reps = r;
        slices = new ArrayList<Slice>();
    }

    public Loop(){
        reps = 0;
        slices = new ArrayList<Slice>();
    }

    public void addSlice(Slice s){
        slices.add(s);
    }

    public void addSlice(Slice s, int index){
        slices.add(index, s);
    }

    public int getReps(){
        return reps;
    }

    public int getNumSlices() {
        return slices.size();
    }

    public int getDuration(){
        int seconds = 0;
        for(Slice slice : slices){
            seconds += slice.getDuration();
        }
        return seconds*reps;
    }

    public void deleteSlice(int index){
        slices.remove(index);
    }

    public int getLength(){
        int length = 0;
        for(Slice slice: slices){
            length += slice.getLength();
        }
        return length*reps;
    }

    public Slice[] getSlices(){
        return slices.toArray(new Slice[slices.size()]);
    }

    public Slice getSlice(int i){
        return slices.get(i);
    }
}
