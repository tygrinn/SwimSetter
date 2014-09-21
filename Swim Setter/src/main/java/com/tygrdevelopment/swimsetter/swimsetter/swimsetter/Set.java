package com.tygrdevelopment.swimsetter.swimsetter.swimsetter;

import java.util.ArrayList;

public class Set {
    private final ArrayList<Section> sections;
    private String name;

    public Set(boolean def, String n){
        name = n;
        sections = new ArrayList<Section>(0);
        if(def){
            sections.add(new Section(Section.WARM_UP));
            sections.add(new Section(Section.WORKOUT));
            sections.add(new Section(Section.WARM_DOWN));
        }
    }

    public Set(String n){
        sections = new ArrayList<Section>(0);
        name = n;
    }

    public Set(){
        name = "";
        sections = new ArrayList<Section>(0);
    }

    public int getNumSections(){
        return sections.size();
    }

    public void deleteSection(int i){
        sections.remove(i);
    }

    public void addSection(String n){
        sections.add(new Section(n));
    }

    public void addSection(Section section){
        sections.add(section);
    }

    public Section[] getSections(){
        return sections.toArray(new Section[sections.size()]);
    }

    public Section getSection(int i){
        return sections.get(i);
    }

    public String getInfo(){
        int secs = 0;
        int length = 0;
        for (Section section : sections) {
            secs += section.getDuration();
            length += section.getLength();
        }
        String dur = "";
        if(secs/60 == 0){
            if(secs < 10)
                dur += ":0" + secs;
            else
                dur += ":" + secs;
        }
        else{
            if(secs % 60 < 10)
                dur += secs / 60 + ":0" + secs % 60;
            else
                dur += secs / 60 + ":" + secs % 60;

        }

        return dur + ", " + length + " yards";
    }

    public void setName(String n){
        name = n;
    }

    public String getName(){
        return name;
    }

    public String toString(){
        String data = "";
        for(Section section : sections) {
            data += section.toString() + "\n";
        }
        return data;
    }

}
