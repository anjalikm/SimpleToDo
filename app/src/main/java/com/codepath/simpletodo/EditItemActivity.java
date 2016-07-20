package com.codepath.simpletodo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class EditItemActivity extends AppCompatActivity {

    EditText etEditText;
    Button btnSaveEditItem;
    int pos,code;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);
        //access the data passed from the main activity
        String task = getIntent().getStringExtra("item");
        //show the old task
        etEditText = (EditText) findViewById(R.id.etEditItem);
        etEditText.setText(task);
    }

    // Save button click event
    public void onSaveEditedItem(View view){
        //prepare the response for the parent activity
        Intent data = new Intent(EditItemActivity.this, MainActivity.class);
        data.putExtra("editedTask", etEditText.getText().toString()); // pass the edited task to main activity
        setResult(RESULT_OK,data); // set result code and bundle data for response
        finish();
    }
}
