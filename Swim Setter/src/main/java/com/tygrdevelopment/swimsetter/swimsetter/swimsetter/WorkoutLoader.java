package com.tygrdevelopment.swimsetter.swimsetter.swimsetter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import java.util.ArrayList;
import java.util.List;


public class WorkoutLoader extends Activity {

    private String name;
    private Set set;
    private String fileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_loader);
        Bundle extras = getIntent().getExtras();
        String NAME_EXTRA = "set name";
        String DEFAULT_EXTRA = "is default";
        String EXISTING_EXTRA = "already created";

        name = extras.getString(NAME_EXTRA);
        setTitle(name);

        fileName = getApplicationContext().getFilesDir() + "/" + name + ".xml";
        int spaceIndex = fileName.indexOf(" ");
        while(spaceIndex > -1){
            fileName = fileName.substring(0, spaceIndex) + "%20" + fileName.substring(spaceIndex+1);
            spaceIndex = fileName.indexOf(" ");
        }

        if(extras.getBoolean(EXISTING_EXTRA)) {
            try {
                set = XMLParser.parseSet(fileName);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else
            set = new Set(extras.getBoolean(DEFAULT_EXTRA), name);
        refreshSetView();

        LinearLayout activityContainer = (LinearLayout)findViewById(R.id.activity_workout_loader);
        LinearLayout workoutButtons = (LinearLayout)getLayoutInflater().inflate(R.layout.buttons_workout, null);
        activityContainer.addView(workoutButtons, 1);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        setContentView(R.layout.activity_workout_loader);
        Bundle extras = data.getExtras();
        String NAME_EXTRA = "set name";
        String DEFAULT_EXTRA = "is default";
        String EXISTING_EXTRA = "already created";

        name = extras.getString(NAME_EXTRA);
        setTitle(name);
        fileName = getApplicationContext().getFilesDir() + "/" + name + ".xml";
        int spaceIndex = fileName.indexOf(" ");
        while(spaceIndex > -1){
            fileName = fileName.substring(0, spaceIndex) + "%20" + fileName.substring(spaceIndex+1);
            spaceIndex = fileName.indexOf(" ");
        }

        if(extras.getBoolean(EXISTING_EXTRA)) {
            try {
                set = XMLParser.parseSet(fileName);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else
            set = new Set(extras.getBoolean(DEFAULT_EXTRA), name);
        refreshSetView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.workout_loader, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return id == R.id.action_settings || super.onOptionsItemSelected(item);
    }

    @Override
    public void onPause(){
        super.onPause();
        int spaceIndex = fileName.indexOf("%20");
        while(spaceIndex > -1){
            fileName = fileName.substring(0, spaceIndex) + " " + fileName.substring(spaceIndex + 3);
            spaceIndex = fileName.indexOf("%20");
        }
        try {
            XMLParser.writeSet(fileName, set);
        }catch(Exception e){
        }
    }
    public void onStop(){
        super.onStop();
        int spaceIndex = fileName.indexOf("%20");
        while(spaceIndex > -1){
            fileName = fileName.substring(0, spaceIndex) + " " + fileName.substring(spaceIndex + 3);
            spaceIndex = fileName.indexOf("%20");
        }
        try {
            XMLParser.writeSet(fileName, set);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    public void onDestroy(){
        super.onDestroy();
        int spaceIndex = fileName.indexOf("%20");
        while(spaceIndex > -1){
            fileName = fileName.substring(0, spaceIndex) + " " + fileName.substring(spaceIndex + 3);
            spaceIndex = fileName.indexOf("%20");
        }
        try {
            XMLParser.writeSet(fileName, set);
        }catch(Exception e){
            e.printStackTrace();
        }
        finish();
    }

    public void flipSlice(View v){
        ViewSwitcher switcher = (ViewSwitcher)v.getParent().getParent();
        switcher.showNext();
    }

    void refreshSetView(){
        posLoopSection = -1;
        posLoopFinish = -1;
        posLoopStart = -1;

        int numSections = set.getNumSections();
        LinearLayout sectionContainer = (LinearLayout)findViewById(R.id.set_scroller);
        sectionContainer.removeAllViews();
        LayoutInflater inflater = this.getLayoutInflater();

        //loop variables
        Section section;
        Object[] slices;
        Integer[] loopIndexes;
        int isLoop;

        LinearLayout sectionFragment;
        TextView sectionName;
        TextView sectionInfo;
        LinearLayout sectionSliceContainer;

        Button add;
        Button edit;
        Button delete;

        LinearLayout loopFragment;
        TextView loopReps;
        LinearLayout loopSliceContainer;

        LinearLayout sliceFragment;
        TextView sliceRead;
        TextView sliceInfo;

        for(int i = 0; i < numSections; i++){
            section = set.getSection(i);
            slices = section.getSlices();
            loopIndexes = section.getLoopIndexes();
            isLoop = 0;

            //add section to scroller
            sectionFragment = (LinearLayout)inflater.inflate(R.layout.fragment_section, null);
            sectionContainer.addView(sectionFragment);
            sectionName = (TextView)sectionFragment.findViewById(R.id.section_name);
            sectionInfo = (TextView)sectionFragment.findViewById(R.id.section_info);
            sectionName.setText(section.getName());
            sectionInfo.setText(section.getInfo());

            add = (Button)sectionFragment.findViewById(R.id.add);
            edit = (Button)sectionFragment.findViewById(R.id.edit);
            delete = (Button)sectionFragment.findViewById(R.id.delete);
            add.setOnTouchListener(addEditDeleteListener);
            edit.setOnTouchListener(addEditDeleteListener);
            delete.setOnTouchListener(addEditDeleteListener);

            //slice container (contains loops as well)
            sectionSliceContainer = (LinearLayout)sectionFragment.findViewById(R.id.section_slice_container);

            for(int j = 0; j < slices.length; j++){
                if(isLoop < loopIndexes.length && j == loopIndexes[isLoop]){
                    Slice[] loopSlices = ((Loop)slices[j]).getSlices();

                    loopFragment = (LinearLayout) inflater.inflate(R.layout.fragment_loop, null);
                    sectionSliceContainer.addView(loopFragment);
                    loopReps = (TextView)loopFragment.findViewById(R.id.loop_reps);
                    loopReps.setText(Integer.toString(((Loop) slices[j]).getReps()));
                    loopSliceContainer = (LinearLayout)loopFragment.findViewById(R.id.loop_slice_container);

                    for(Slice slice : loopSlices){
                        sliceFragment = (LinearLayout)inflater.inflate(R.layout.fragment_slice, null);
                        loopSliceContainer.addView(sliceFragment);
                        sliceRead = (TextView)sliceFragment.findViewById(R.id.slice_read);
                        sliceInfo = (TextView)sliceFragment.findViewById(R.id.slice_info);

                        //buttons
                        add = (Button)sliceFragment.findViewById(R.id.add);
                        edit = (Button)sliceFragment.findViewById(R.id.edit);
                        delete = (Button)sliceFragment.findViewById(R.id.delete);
                        add.setOnTouchListener(addEditDeleteListener);
                        edit.setOnTouchListener(addEditDeleteListener);
                        delete.setOnTouchListener(addEditDeleteListener);

                        //remove radio button
                        sliceFragment.removeViewAt(0);
                        sliceRead.setText(slice.toString());
                        sliceInfo.setText(slice.getInfo());
                    }
                    isLoop++;
                }
                else {
                    Slice slice = (Slice)slices[j];
                    sliceFragment = (LinearLayout)inflater.inflate(R.layout.fragment_slice, null);
                    sectionSliceContainer.addView(sliceFragment);
                    sliceRead = (TextView)sliceFragment.findViewById(R.id.slice_read);
                    sliceInfo = (TextView)sliceFragment.findViewById(R.id.slice_info);
                    sliceRead.setText(slice.toString());
                    sliceInfo.setText(slice.getInfo());

                    //buttons
                    add = (Button)sliceFragment.findViewById(R.id.add);
                    edit = (Button)sliceFragment.findViewById(R.id.edit);
                    delete = (Button)sliceFragment.findViewById(R.id.delete);
                    add.setOnTouchListener(addEditDeleteListener);
                    edit.setOnTouchListener(addEditDeleteListener);
                    delete.setOnTouchListener(addEditDeleteListener);

                }
            }
        }
        TextView setInfo = (TextView)findViewById(R.id.set_info);
        setInfo.setText(set.getInfo());
    }

    void addSliceToSection(View v){
        //figure out which section is clicked
        LinearLayout curSection = (LinearLayout)v.getParent().getParent().getParent();
        int sectionIndex = ((LinearLayout)curSection.getParent()).indexOfChild(curSection);

        Slice newSlice = new Slice();

        Section section = set.getSection(sectionIndex);

        newSliceDialog(newSlice, section.getNumSlices(), null, section);

        section.addSlice(newSlice);

    }

    void editSlice(View v){

        int[] indexArray = findIndexesFromView(v);

        if(indexArray[1] > -1) {
            Slice slice = set.getSection(indexArray[2]).getLoop(indexArray[1]).getSlice(indexArray[0]);
            newSliceDialog(slice, indexArray[0], null, null);
        }
        else {
            Slice slice = set.getSection(indexArray[2]).getSlice(indexArray[0]);
            newSliceDialog(slice, indexArray[0], null, null);
        }
    }

    void addSliceAfter(View v){
        int[] indexArray = findIndexesFromView(v);

        if(indexArray[1] > -1){
            Slice slice = new Slice();
            Loop loop = set.getSection(indexArray[2]).getLoop(indexArray[1]);
            newSliceDialog(slice, indexArray[0] + 1, loop, null);
            loop.addSlice(slice, indexArray[0] + 1);
        }
        else{
            Slice slice = new Slice();
            Section section = set.getSection(indexArray[2]);
            newSliceDialog(slice, indexArray[0] + 1, null, section);
            section.addSlice(slice, indexArray[0] + 1);
        }
    }

    void newSliceDialog(final Slice slice, final int sliceIndex, final Loop loop, final Section section){

        final Dialog dialog = new Dialog(this);
        LinearLayout dialogView = (LinearLayout)getLayoutInflater().inflate(R.layout.dialog_new_slice, null);
        dialog.setContentView(dialogView);

        dialog.setTitle(R.string.new_slice);
        final Spinner repsSpinner = (Spinner)dialog.findViewById(R.id.reps_spinner);
        final Spinner lengthSpinner = (Spinner)dialog.findViewById(R.id.length_spinner);
        final Spinner paceSpinner = (Spinner)dialog.findViewById(R.id.pace_spinner);
        final Spinner typeSpinner = (Spinner)dialog.findViewById(R.id.type_spinner);
        final EditText detailsView = (EditText)dialog.findViewById(R.id.details);
        Button cancel = (Button)dialog.findViewById(R.id.cancel_slice);
        Button update = (Button)dialog.findViewById(R.id.update_slice);

        List repsValues = new ArrayList();
        for(int i = 0; i < 500; i++){
            repsValues.add(Integer.toString(i+1));
        }
        ArrayAdapter repsAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, repsValues);
        repsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        repsSpinner.setAdapter(repsAdapter);

        List lengthValues = new ArrayList();
        for(int i = 0; i < 40; i++){
            lengthValues.add(Integer.toString(i*25 + 25));
        }
        ArrayAdapter lengthAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, lengthValues);
        lengthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        lengthSpinner.setAdapter(lengthAdapter);

        List paceValues = new ArrayList();
        String time;
        for(int i = 0; i < 400; i++){
            int secs = 5*(i+1);
            time = secs/60 +":";
            if(secs%60 < 10){
                time += "0" + secs%60;
            }
            else{
                time += secs%60;
            }
            paceValues.add(time);
        }

        ArrayAdapter paceAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, paceValues);
        paceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        paceSpinner.setAdapter(paceAdapter);


        repsSpinner.setSelection(slice.getReps() - 1);
        lengthSpinner.setSelection(slice.getLength() / 25 - 1);
        paceSpinner.setSelection(slice.getSeconds() / 5 - 1);
        typeSpinner.setSelection(slice.getType());
        detailsView.setText(slice.getDetails());

        AdapterView.OnItemSelectedListener listener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(adapterView == repsSpinner){
                    slice.setReps(i + 1);
                    lengthSpinner.requestFocus();
                }
                if(adapterView == lengthSpinner){
                    slice.setDistance(i*25 + 25);
                    paceSpinner.requestFocus();
                }
                if(adapterView == paceSpinner){
                    slice.setSeconds(i*5 + 5);
                    typeSpinner.requestFocus();
                }
                if(adapterView == typeSpinner){
                    slice.setType(i);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        };

        repsSpinner.setOnItemSelectedListener(listener);
        lengthSpinner.setOnItemSelectedListener(listener);
        paceSpinner.setOnItemSelectedListener(listener);
        typeSpinner.setOnItemSelectedListener(listener);


        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
                slice.setDetails(detailsView.getText().toString());
                refreshSetView();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
                if(section != null){
                    section.deleteSlice(sliceIndex);
                }
                else if(loop != null){
                    loop.deleteSlice(sliceIndex);
                }

            }
        });

        dialog.show();
    }

    void editSection(View v){

    }

    void deleteSectionFromSet(View v){
        LinearLayout curSection = (LinearLayout)v.getParent().getParent().getParent();
        final int sectionIndex = ((LinearLayout)curSection.getParent()).indexOfChild(curSection);
        set.deleteSection(sectionIndex);
        refreshSetView();
    }

    public void addNewSection(View v){
        final Dialog dialog = new Dialog(WorkoutLoader.this);
        dialog.setTitle(R.string.new_section_dialog);
        dialog.setContentView(R.layout.dialog_new_section);
        Button create = (Button)dialog.findViewById(R.id.new_section_create);
        Button cancel = (Button)dialog.findViewById(R.id.new_section_cancel);
        dialog.show();

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText sectionName = (EditText)dialog.findViewById(R.id.new_section_name);
                String name = sectionName.getText().toString();
                set.addSection(name);
                refreshSetView();
                dialog.cancel();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });

    }

    void deleteSliceFromSection(View v) {

        int[] indexArray = findIndexesFromView(v);
        if(indexArray[1] > -1) {
            if (set.getSection(indexArray[2]).getLoop(indexArray[1]).getNumSlices() == 1) {
                set.getSection(indexArray[2]).deleteLoop(indexArray[1]);
            }
            else
                set.getSection(indexArray[2]).getLoop(indexArray[1]).deleteSlice(indexArray[0]);
        }
        else
            set.getSection(indexArray[2]).deleteSlice(indexArray[0]);

        refreshSetView();
    }

    int[] findIndexesFromView(View v){
        LinearLayout sliceFragment = (LinearLayout)v.getParent().getParent().getParent().getParent();

        int sliceIndex;
        int loopIndex;
        int sectionIndex;
        //is in a loop
        if(((View)sliceFragment.getParent()).getId() == R.id.loop_slice_container){
            LinearLayout loopContainer = (LinearLayout)sliceFragment.getParent();
            sliceIndex = loopContainer.indexOfChild(sliceFragment);

            LinearLayout sectionContainer = (LinearLayout)loopContainer.getParent().getParent();
            loopIndex = sectionContainer.indexOfChild((View)loopContainer.getParent());

            LinearLayout setContainer = (LinearLayout)sectionContainer.getParent().getParent();
            sectionIndex = setContainer.indexOfChild((View)sectionContainer.getParent());

        }
        else{
            LinearLayout sectionContainer = (LinearLayout)sliceFragment.getParent();
            sliceIndex = sectionContainer.indexOfChild(sliceFragment);

            loopIndex = -1;

            LinearLayout setContainer = (LinearLayout)sectionContainer.getParent().getParent();
            sectionIndex = setContainer.indexOfChild((View)sectionContainer.getParent());
        }

        return new int[]{sliceIndex, loopIndex, sectionIndex};
    }

    private int posLoopSection = -1;
    private int posLoopStart = -1;
    private int posLoopFinish = -1;

    public void posLoopClicked(View v){

        LinearLayout sectionContainer = (LinearLayout)v.getParent();
        int sliceIndex = sectionContainer.indexOfChild(v);

        LinearLayout setContainer = (LinearLayout)sectionContainer.getParent().getParent();
        int sectionIndex = setContainer.indexOfChild((View)sectionContainer.getParent());

        LinearLayout slice;

        if(sectionIndex == posLoopSection){
            if(sliceIndex == posLoopStart){
                //deselecting posLoopStart
                posLoopStart = -1;
                if(posLoopFinish == -1){
                    posLoopSection = -1;
                }
            }
            else if(sliceIndex == posLoopFinish){
                //deselecting posLoopFinish
                posLoopFinish = -1;
                if(posLoopStart == -1){
                    posLoopSection = -1;
                }
            }
            else{
                //selecting another slice in current selected section
                if(posLoopStart == -1){
                    if(posLoopFinish == -1 || sliceIndex < posLoopFinish) {
                        posLoopStart = sliceIndex;
                    }
                    else{
                        posLoopStart = posLoopFinish;
                        posLoopFinish = sliceIndex;
                    }
                }
                else if(posLoopFinish == -1){
                    if(posLoopStart < sliceIndex){
                        posLoopFinish = sliceIndex;
                    }
                    else{
                        posLoopFinish = posLoopStart;
                        posLoopStart = sliceIndex;
                    }
                }
                else if(sliceIndex < posLoopStart){
                    posLoopStart = sliceIndex;
                }
                else{
                    posLoopFinish = sliceIndex;
                }
            }
            //updating display
            if(posLoopStart == -1 || posLoopFinish == -1) {
                int skip = -1;
                if(posLoopStart > -1)
                    skip = posLoopStart;
                if(posLoopFinish > -1)
                    skip = posLoopFinish;

                for (int i = 0; i < sectionContainer.getChildCount(); i++) {
                    slice = (LinearLayout) sectionContainer.getChildAt(i);
                    if(i == skip)
                        slice.setBackgroundColor(getResources().getColor(R.color.slice_pressed));
                    else
                        slice.setBackgroundColor(Color.TRANSPARENT);
                }
            }
            else{
                for(int i = 0; i < sectionContainer.getChildCount(); i++){
                    slice = (LinearLayout) sectionContainer.getChildAt(i);
                    if (i >= posLoopStart && i <= posLoopFinish) {
                        slice.setBackgroundColor(getResources().getColor(R.color.slice_pressed));
                    } else {
                        slice.setBackgroundColor(Color.TRANSPARENT);
                    }
                }
            }
        }
        else if(posLoopSection == -1){
            //brand new selection
            v.setBackgroundColor(getResources().getColor(R.color.slice_pressed));
            posLoopStart = sliceIndex;
            posLoopFinish = -1;
            posLoopSection = sectionIndex;
        }
        else{
            //selection switched sections
            v.setBackgroundColor(getResources().getColor(R.color.slice_pressed));
            //new section is being selected, must delete old selections
            LinearLayout oldSectionContainer = (LinearLayout)((LinearLayout)setContainer.getChildAt(posLoopSection)).getChildAt(1);
            if(posLoopStart == -1){
                slice = (LinearLayout)oldSectionContainer.getChildAt(posLoopFinish);
                slice.setBackgroundColor(Color.TRANSPARENT);
            }
            else if(posLoopFinish == -1){
                slice = (LinearLayout)oldSectionContainer.getChildAt(posLoopStart);
                slice.setBackgroundColor(Color.TRANSPARENT);
            }
            else {
                for (int i = posLoopStart; i <= posLoopFinish; i++){
                    slice = (LinearLayout)oldSectionContainer.getChildAt(i);
                    slice.setBackgroundColor(Color.TRANSPARENT);
                }
            }
            posLoopStart = sliceIndex;
            posLoopFinish = -1;
            posLoopSection = sectionIndex;
        }


        Button createLoop = (Button)findViewById(R.id.create_loop);

        //show loop button
        if(posLoopSection > -1 && posLoopFinish > -1 && posLoopStart > -1){
            createLoop.setTextColor(Color.BLACK);
            createLoop.setClickable(true);
        }
        //hide loop button
        else{
            createLoop.setTextColor(getResources().getColor(R.color.hidden));
            createLoop.setClickable(false);
        }


    }

    public void showPosLoop(View v){
        posLoopSection = -1;
        posLoopStart = -1;
        posLoopFinish = -1;

        LinearLayout activityContainer = (LinearLayout)findViewById(R.id.activity_workout_loader);
        LinearLayout posLoopButtons = (LinearLayout)getLayoutInflater().inflate(R.layout.buttons_pos_loop, null);
        TextView posLoopTextView = (TextView)getLayoutInflater().inflate(R.layout.textview_pos_loop, null);
        activityContainer.removeViewAt(1);
        activityContainer.addView(posLoopButtons, 1);
        activityContainer.addView(posLoopTextView, 0);

        LinearLayout setContainer = (LinearLayout)findViewById(R.id.set_scroller);
        LinearLayout sliceContainer;

        for(int i = 0; i < set.getNumSections(); i++){
            Integer[] loopIndexes = set.getSection(i).getLoopIndexes();
            int isLoop = 0;
            sliceContainer = (LinearLayout)((LinearLayout)setContainer.getChildAt(i)).getChildAt(1);

            int numSlices = set.getSection(i).getNumSlices();
            for(int j = 0; j < numSlices; j++){
                if(isLoop < loopIndexes.length && loopIndexes[isLoop] == j){
                    isLoop++;
                }
                else{
                    sliceContainer.getChildAt(j).setClickable(true);
                    ((LinearLayout)((ViewSwitcher)((LinearLayout)sliceContainer.getChildAt(j)).getChildAt(1)).getChildAt(0)).getChildAt(0).setClickable(false);
                }
            }
        }
    }

    public void cancelLoop(View v){
        LinearLayout activityContainer = (LinearLayout)findViewById(R.id.activity_workout_loader);
        LinearLayout workoutButtons = (LinearLayout)getLayoutInflater().inflate(R.layout.buttons_workout, null);
        activityContainer.removeViewAt(0);
        activityContainer.removeViewAt(1);
        activityContainer.addView(workoutButtons, 1);

        LinearLayout setContainer = (LinearLayout)findViewById(R.id.set_scroller);
        LinearLayout sliceContainer;

        for(int i = 0; i < set.getNumSections(); i++){
            Integer[] loopIndexes = set.getSection(i).getLoopIndexes();
            int isLoop = 0;
            sliceContainer = (LinearLayout)((LinearLayout)setContainer.getChildAt(i)).getChildAt(1);

            int numSlices = set.getSection(i).getNumSlices();
            for(int j = 0; j < numSlices; j++){

                if(isLoop < loopIndexes.length && loopIndexes[isLoop] == j){
                    isLoop++;
                }
                else{
                    sliceContainer.getChildAt(j).setClickable(false);
                    sliceContainer.getChildAt(j).setBackgroundColor(Color.TRANSPARENT);
                    ((LinearLayout)((ViewSwitcher)((LinearLayout)sliceContainer.getChildAt(j)).getChildAt(1)).getChildAt(0)).getChildAt(0).setClickable(true);
                }
            }
        }
    }

    public void createLoop(View v){
        if(posLoopStart == -1 || posLoopFinish == -1 || posLoopSection == -1){
            Toast.makeText(this, R.string.not_enough_slices, Toast.LENGTH_SHORT).show();
        }
        else {

            final Dialog dialog = new Dialog(this);
            dialog.setContentView(getLayoutInflater().inflate(R.layout.dialog_create_loop, null));
            dialog.show();
            final NumberPicker repsPicker = (NumberPicker) dialog.findViewById(R.id.loop_rep_picker);
            repsPicker.setMinValue(1);
            repsPicker.setMaxValue(100);
            Button create = (Button) dialog.findViewById(R.id.create);
            Button cancel = (Button) dialog.findViewById(R.id.cancel);

            create.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.cancel();
                    int reps = repsPicker.getValue();
                    set.getSection(posLoopSection).addLoop(reps, posLoopStart, posLoopFinish - posLoopStart + 1);

                    LinearLayout activityContainer = (LinearLayout)findViewById(R.id.activity_workout_loader);
                    LinearLayout workoutButtons = (LinearLayout)getLayoutInflater().inflate(R.layout.buttons_workout, null);
                    activityContainer.removeViewAt(0);
                    activityContainer.removeViewAt(1);
                    activityContainer.addView(workoutButtons, 1);

                    LinearLayout setContainer = (LinearLayout)findViewById(R.id.set_scroller);
                    LinearLayout sliceContainer;

                    for(int i = 0; i < set.getNumSections(); i++){
                        Integer[] loopIndexes = set.getSection(i).getLoopIndexes();
                        int isLoop = 0;
                        sliceContainer = (LinearLayout)((LinearLayout)setContainer.getChildAt(i)).getChildAt(1);

                        int numSlices = set.getSection(i).getNumSlices();
                        for(int j = 0; j < numSlices; j++){

                            if(isLoop < loopIndexes.length && loopIndexes[isLoop] == j){
                                isLoop++;
                            }
                            else{
                                sliceContainer.getChildAt(j).setClickable(false);
                                sliceContainer.getChildAt(j).setBackgroundColor(Color.TRANSPARENT);
                                ((LinearLayout)((ViewSwitcher)((LinearLayout)sliceContainer.getChildAt(j)).getChildAt(1)).getChildAt(0)).getChildAt(0).setClickable(true);
                            }
                        }
                    }

                    refreshSetView();
                }
            });

            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.cancel();
                }
            });
        }

    }

    private final View.OnTouchListener addEditDeleteListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            int event = motionEvent.getAction();
            int index = ((LinearLayout)view.getParent()).indexOfChild(view);

            boolean isSlice = ((LinearLayout) view.getParent()).getId() == R.id.slice_buttons;

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {

                if (event == MotionEvent.ACTION_DOWN || event == MotionEvent.ACTION_POINTER_DOWN) {
                    switch (index) {
                        case 0:
                            view.setBackground(getResources().getDrawable(R.drawable.add_pressed_background));
                            break;
                        case 1:
                            view.setBackground(getResources().getDrawable(R.drawable.edit_pressed_background));
                            break;
                        case 2:
                            view.setBackground(getResources().getDrawable(R.drawable.delete_pressed_background));
                            break;
                        default:
                            break;
                    }
                    return false;
                }
                if (event == MotionEvent.ACTION_UP || event == MotionEvent.ACTION_POINTER_UP) {
                    switch (index) {
                        case 0:
                            view.setBackground(getResources().getDrawable(R.drawable.add_background));
                            if(isSlice)
                                addSliceAfter(view);
                            else
                                addSliceToSection(view);
                            break;
                        case 1:
                            view.setBackground(getResources().getDrawable(R.drawable.edit_background));
                            if(isSlice)
                                editSlice(view);
                            else
                                editSection(view);
                            break;
                        case 2:
                            view.setBackground(getResources().getDrawable(R.drawable.delete_background));
                            if(isSlice)
                                deleteSliceFromSection(view);
                            else
                                deleteSectionFromSet(view);
                            break;
                        default:
                            break;
                    }
                    return true;
                }
            }
            else{

                if (event == MotionEvent.ACTION_DOWN || event == MotionEvent.ACTION_POINTER_DOWN) {
                    switch (index) {
                        case 0:
                            view.setBackgroundDrawable(getResources().getDrawable(R.drawable.add_pressed_background));
                            break;
                        case 1:
                            view.setBackgroundDrawable(getResources().getDrawable(R.drawable.edit_pressed_background));
                            break;
                        case 2:
                            view.setBackgroundDrawable(getResources().getDrawable(R.drawable.delete_pressed_background));
                            break;
                        default:
                            break;
                    }
                    return false;
                }
                if (event == MotionEvent.ACTION_UP || event == MotionEvent.ACTION_POINTER_UP) {
                    switch (index) {
                        case 0:
                            view.setBackgroundDrawable(getResources().getDrawable(R.drawable.add_background));
                            if(isSlice)
                                addSliceAfter(view);
                            else
                                addSliceToSection(view);
                            break;
                        case 1:
                            view.setBackgroundDrawable(getResources().getDrawable(R.drawable.edit_background));
                            if(isSlice)
                                editSlice(view);
                            else
                                editSection(view);
                            break;
                        case 2:
                            view.setBackgroundDrawable(getResources().getDrawable(R.drawable.delete_background));
                            if(isSlice)
                                deleteSliceFromSection(view);
                            else
                                deleteSectionFromSet(view);
                            break;
                        default:
                            break;
                    }
                    return true;
                }
            }
            return false;
        }
    };
}
