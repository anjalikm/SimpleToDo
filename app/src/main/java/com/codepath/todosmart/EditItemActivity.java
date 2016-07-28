package com.codepath.todosmart;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Calendar;


public class EditItemActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener,OnItemSelectedListener {

    EditText etEditText, etNotes;
    Button btnSaveEditItem;
    ImageButton btnSelectDate;
    TextView tvDateSet;
    MenuItem action_delete_item;
    Spinner spinnerPriority;
    int pos;
    Calendar calendar;
    private int year, month, day;
    String code; //101- Add new Task, 102 - Edit this task

    private static final String TAG = "EditItemActivity";
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.edit_menu, menu);
        //The activity UI is used for both editing task and adding new task
        //if this activity is launched for adding new task, set delete action button invisible in the toolbar
        if(code.equals("101")) {
            menu.getItem(1).setVisible(false);
        }

        return true;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //allow up navigation in the action bar

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setIcon(R.drawable.ic_calendar);

        tvDateSet = (TextView)findViewById(R.id.tvDateSet);
        etEditText = (EditText) findViewById(R.id.etEditItem);
        etNotes = (EditText)findViewById(R.id.etNotes);
        btnSelectDate = (ImageButton) findViewById(R.id.btnSelectDate);
        action_delete_item = (MenuItem) findViewById(R.id.action_delete_task);
        spinnerPriority = (Spinner) findViewById(R.id.spinnerPriority);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.priority_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinnerPriority.setAdapter(adapter);

        //access the data passed from the parent activity
        //first check the code to choose ADD /EDIT task
        code = getIntent().getStringExtra("code");

        if(code.equals("101")){
            // this activity is launched to add new task
            // set the form to defaults
            // show the today's date on the TextView

            calendar = Calendar.getInstance();
            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH);
            day = calendar.get(Calendar.DAY_OF_MONTH);
            showDate(year, month+1, day);
        }
        else if (code.equals("102")){
            //this activity is launched to edit the selected task
            // set the fields with curent task's values

            String task = getIntent().getStringExtra("item");
            etEditText.setText(task);
            tvDateSet.setText(getIntent().getStringExtra("dueDate"));
            int priority = getIntent().getIntExtra("priority",0);
            spinnerPriority.setSelection(priority);
            etNotes.setText(getIntent().getStringExtra("notes"));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // Respond to the action bar's Up/Home button
                //goto the parent activity
                NavUtils.navigateUpFromSameTask(this);
                return true;

            //respond to the save action button
            //prepare the response to save the current task details for the parent activity
            case R.id.action_save_task:
                Intent data = new Intent(EditItemActivity.this, MainActivity.class);
                data.putExtra("editedTask", etEditText.getText().toString()); // pass the edited task to main activity
                data.putExtra("dueDate", tvDateSet.getText().toString());
                data.putExtra("priority",spinnerPriority.getSelectedItem().toString());
                data.putExtra("notes", etNotes.getText().toString());
                setResult(RESULT_OK,data); // set result code and bundle data for response
                finish();
                return true;

            //respond to the delete action button
            case R.id.action_delete_task:
                // reassure that user wants to delete current task
                openAlertDialog();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    //show alert dialog after user clicks delete action button
    public void openAlertDialog(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Are you sure,You wanted to delete the task");

        alertDialogBuilder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                //launch the parent activity and delete this item
                Intent data = new Intent(EditItemActivity.this, MainActivity.class);
                data.putExtra("action_code","DELETE");
                setResult(RESULT_OK,data);
                finish();


            }
        });

        alertDialogBuilder.setNegativeButton("No",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                //do nothing - check users's next response
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
    //the selection event handler for a spinner
    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        // An item was selected. You can retrieve the selected item
        parent.getItemAtPosition(pos);
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

    // Save button click event
    public void onSaveEditedItem(View view){
        //prepare the response for the parent activity
        Intent data = new Intent(EditItemActivity.this, MainActivity.class);
        data.putExtra("editedTask", etEditText.getText().toString()); // pass the edited task to main activity
        data.putExtra("dueDate", tvDateSet.getText().toString());
        data.putExtra("priority",spinnerPriority.getSelectedItem().toString());
        data.putExtra("notes", etNotes.getText().toString());
        setResult(RESULT_OK,data); // set result code and bundle data for response
        finish();
    }

    // attach to an onclick handler to show the date picker
    public void showDatePickerDialog(View v) {
        DatePickerFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    // handle the date selected
    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        // store the values selected into a Calendar instance
        final Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, monthOfYear);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        showDate(year,monthOfYear, dayOfMonth);
    }
    //displays the date selected by the user
    private void showDate(int year, int month, int day) {
        tvDateSet.setText(new StringBuilder().append(day).append("/")
                .append(month).append("/").append(year));
    }

    //called when the user clicks on calendar button
    public void setDate(View view) {
        showDatePickerDialog(view);

    }

}
