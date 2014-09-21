package com.tygrdevelopment.swimsetter.swimsetter.swimsetter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

import com.parse.Parse;
import com.parse.ParseAnalytics;


public class Overview extends Activity {

    private final ArrayList<String> setNames = new ArrayList<String>();
    private TableLayout setContainer;
    private final String NAME_EXTRA = "set name";
    private final String DEFAULT_EXTRA = "is default";
    private final String EXISTING_EXTRA = "already created";
    public boolean tutorial = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Parse.initialize(this, "TFgAedkayADvYBaaxKQCvz75pGupYsn2vFGQZgUm", "8ds6DMxWXjwy14eCWty1OvRjwqfhfT7epbS6CvcB");
        Parse.enableLocalDatastore(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);
        setContainer = (TableLayout)findViewById(R.id.set_container);

        Button newSetButton = (Button)findViewById(R.id.new_set_button);
        newSetButton.setOnTouchListener(newSetListener);

        loadFiles();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.overview, menu);
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

    void createNewSet(){
        final Dialog dialog = new Dialog(this);
        dialog.setTitle(R.string.create_new_set);
        dialog.setContentView(this.getLayoutInflater().inflate(R.layout.dialog_create_new_set, null));
        Button cancel = (Button)dialog.findViewById(R.id.cancel_set);
        Button blankSet = (Button)dialog.findViewById(R.id.create_blank_set);
        Button defaultSet = (Button)dialog.findViewById(R.id.create_default_set);
        final EditText newName = (EditText)dialog.findViewById(R.id.new_set_name);

        dialog.show();

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        blankSet.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                String name = newName.getText().toString();
                try{
                    if(setNames.contains(name) || name.isEmpty()){
                        Toast.makeText(Overview.this, R.string.file_name_error, Toast.LENGTH_SHORT).show();
                        newName.setText(null);
                        newName.requestFocus();
                    }
                    else {
                        Intent intent = new Intent(Overview.this, WorkoutLoader.class);
                        Bundle extras = new Bundle();
                        extras.putString(NAME_EXTRA, name);
                        extras.putBoolean(DEFAULT_EXTRA, false);
                        extras.putBoolean(EXISTING_EXTRA, false);
                        intent.putExtras(extras);
                        dialog.dismiss();
                        startActivityForResult(intent, 1);
                    }
                }
                catch(Exception e){
                    Toast.makeText(Overview.this, R.string.file_name_error, Toast.LENGTH_SHORT).show();
                }
            }
        });

        defaultSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = newName.getText().toString();
                if(setNames.contains(name) || name.isEmpty()){
                    Toast.makeText(Overview.this, R.string.file_name_error, Toast.LENGTH_SHORT).show();
                    newName.setText(null);
                    newName.requestFocus();
                }
                else {
                    Intent intent = new Intent(Overview.this, WorkoutLoader.class);
                    Bundle extras = new Bundle();
                    extras.putString(NAME_EXTRA, name);
                    extras.putBoolean(DEFAULT_EXTRA, true);
                    extras.putBoolean(EXISTING_EXTRA, false);
                    intent.putExtras(extras);
                    dialog.dismiss();
                    startActivityForResult(intent, 1);
                }
            }
        });

    }

    void loadSet(int index){
        String name = setNames.get(index);
        Intent intent = new Intent(Overview.this, WorkoutLoader.class);
        Bundle extras = new Bundle();
        extras.putString(NAME_EXTRA, name);
        extras.putBoolean(EXISTING_EXTRA, true);
        intent.putExtras(extras);
        startActivityForResult(intent, 1);
    }

    @Override
    public void onResume(){
        super.onResume();
        loadFiles();
    }

    public void deleteSet(View v){
        int index = ((TableLayout)v.getParent().getParent()).indexOfChild((TableRow)v.getParent());
        String tempName = setNames.get(index);


        int spaceIndex = tempName.indexOf(" ");
        while(spaceIndex > -1){
            tempName = tempName.substring(0, spaceIndex) + "%20" + tempName.substring(spaceIndex + 1);
            spaceIndex = tempName.indexOf(" ");
        }
        final String name = tempName;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.dialog_delete_set);
        builder.setMessage(getString(R.string.delete_set_message) + " " + setNames.get(index) + "?");

        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                File file = new File(getApplicationContext().getFilesDir()+"/"+name + ".xml");
                file.delete();
                loadFiles();
            }
        });

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        Dialog dialog = builder.create();
        dialog.show();
    }

    void loadFiles(){
        setContainer.removeAllViews();
        setNames.clear();
        File[] files = getApplicationContext().getFilesDir().listFiles();

        //loop variables
        TableRow setLoader;
        TextView nameView;
        TextView infoView;
        String info = "";

        String nameXML;
        String name;
        Set set;

        for (File file : files) {
            nameXML = file.getName();
            name = nameXML.substring(0, nameXML.length() - 4);
            int spaceIndex = name.indexOf("%20");
            while(spaceIndex > -1){
                name = name.substring(0, spaceIndex) + " " + name.substring(spaceIndex+3);
                spaceIndex = name.indexOf("%20");
            }
            setNames.add(name);

            try {
                set = XMLParser.parseSet(getApplicationContext().getFilesDir() + "/" + nameXML);
                info = set.getInfo();
            }catch(Exception e){
                e.printStackTrace();
            }

            setLoader = (TableRow) this.getLayoutInflater().inflate(R.layout.fragment_set_loader, null);
            setContainer.addView(setLoader);
            nameView = (TextView)setLoader.findViewById(R.id.set_name);
            infoView = (TextView)setLoader.findViewById(R.id.set_info);

            setLoader.setOnTouchListener(loadSetListener);
            nameView.setText(name);
            infoView.setText(info);
        }
    }

    private final View.OnTouchListener loadSetListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            int event = motionEvent.getAction();

            if(event == MotionEvent.ACTION_DOWN || event == MotionEvent.ACTION_POINTER_DOWN){
                view.setBackgroundColor(getResources().getColor(R.color.set_pressed));
                return true;
            }
            if(event == MotionEvent.ACTION_UP || event == MotionEvent.ACTION_POINTER_UP){
                int index = setContainer.indexOfChild(view);
                view.setBackgroundColor(getResources().getColor(R.color.background));
                loadSet(index);
            }
            return false;
        }
    };

    private final View.OnTouchListener newSetListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            int event = motionEvent.getAction();

            if(event == MotionEvent.ACTION_DOWN || event == MotionEvent.ACTION_POINTER_DOWN){
                view.setBackgroundColor(getResources().getColor(R.color.button_pressed));
                return true;
            }
            if(event == MotionEvent.ACTION_UP || event == MotionEvent.ACTION_POINTER_UP){
                view.setBackgroundColor(getResources().getColor(R.color.button));
                createNewSet();
            }

            return false;
        }
    };
}
