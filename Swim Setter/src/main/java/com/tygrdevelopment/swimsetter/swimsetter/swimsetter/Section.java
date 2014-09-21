package com.tygrdevelopment.swimsetter.swimsetter.swimsetter;

import java.util.ArrayList;
import java.util.Collections;

public class Section {
    private String name;
    private final ArrayList<Object> slices;
    private final ArrayList<Integer> loopIndexes;

    private static final String[] DEFAULT_NAMES = {"Warm up", "Workout", "Warm down"};
    public static final int WARM_UP = 0;
    public static final int WORKOUT = 1;
    public static final int WARM_DOWN = 2;

    public Section(int n) {
        slices = new ArrayList<Object>();
        loopIndexes = new ArrayList<Integer>();
        name = DEFAULT_NAMES[n];

    }

    public Integer[] getLoopIndexes(){
        Collections.sort(loopIndexes);
        return loopIndexes.toArray(new Integer[loopIndexes.size()]);
    }

    public Object[] getSlices(){
        return slices.toArray(new Object[loopIndexes.size()]);
    }

    public Section(){
        name = "";
        slices = new ArrayList<Object>();
        loopIndexes = new ArrayList<Integer>();
    }

    public Section(String n){
        slices = new ArrayList<Object>();
        loopIndexes = new ArrayList<Integer>();
        name = n;
    }

    public void addSlice(Slice s){
        slices.add(s);
    }

    public void addLoop(Loop loop){
        loopIndexes.add(slices.size());
        slices.add(loop);
    }

    public void addLoop(int reps, int start, int numSlices){
        loopIndexes.add(start);
        Collections.sort(loopIndexes);
        int midpoint = loopIndexes.indexOf(start);
        for(int j = midpoint+1; j<loopIndexes.size(); j++){
            loopIndexes.set(j, loopIndexes.get(j)-numSlices+1);
        }
        Loop loop = new Loop(reps);
        for(int i = start; i < start+numSlices; i++){
            loop.addSlice((Slice)slices.get(i));
        }
        for(int i = start; i < start+numSlices; i++){
            slices.remove(start);
        }
        slices.add(start, loop);
    }

    public void addSlice(Slice s, int i){
        slices.add(i,s);
    }

    public void deleteSlice(int i){
        slices.remove(i);
    }

    public void deleteLoop(int i){
        slices.remove(i);
        loopIndexes.remove(loopIndexes.indexOf(i));
    }

    public Slice getSlice(int i){
        return (Slice)slices.get(i);
    }

    public Loop getLoop(int i){
        return (Loop)slices.get(i);
    }

    public int getNumSlices(){
        return slices.size();
    }

    public String getName(){
        return name;
    }

    public void setName(String n){
        name = n;
    }

    public String getInfo(){
        int length = 0;
        int seconds = 0;
        String dur = "";
        Slice slice;
        Loop loop;
        for (int i = 0; i < slices.size(); i++) {
            if(loopIndexes.contains(i)){
                loop = (Loop)slices.get(i);
                length += loop.getLength();
                seconds += loop.getDuration();
            }
            else {
                slice = (Slice)slices.get(i);
                length += slice.getLength();
                seconds += slice.getDuration();
            }
        }
        //todo: set meters or yards in settings
        int minutes = seconds/60;
        int s = seconds % 60;
        if(minutes != 0) {
            if (s < 10)
                dur += minutes + ":0" + s;
            else
                dur += minutes + ":" + s;
        }
        else {
            if (s < 10)
                dur += ":0" + s;
            else
                dur += ":" + s;
        }
        return dur + ", " + length + " yards";
    }

    public int getDuration(){
        int dur = 0;
        Slice slice;
        Loop loop;
        for (int i = 0; i < slices.size(); i++) {
            if (loopIndexes.contains(i)) {
                loop = (Loop) slices.get(i);
                dur += loop.getDuration();
            } else {
                slice = (Slice)slices.get(i);
                dur += slice.getDuration();
            }
        }
        return dur;
    }

    public int getLength(){
        int length = 0;
        Loop loop;
        Slice slice;
        for (int i = 0; i < slices.size(); i++) {
            if(loopIndexes.contains(i)){
                loop = (Loop)slices.get(i);
                length += loop.getLength();
            }
            else{
                slice = (Slice)slices.get(i);
                length += slice.getLength();
            }
        }
        return length;
    }
}
