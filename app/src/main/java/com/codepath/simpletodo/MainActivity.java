package com.codepath.simpletodo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ArrayList<String> items;
    ArrayAdapter<String> itemsAdapter;
    ListView lvItems;
    // REQUEST_CODE  used to determine the result type from the launched activity
    private final int REQUEST_CODE = 20;
    int editPos;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lvItems = (ListView) findViewById(R.id.lvlItems);
        //load the previously stored items
        readItems();

        itemsAdapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,items);
        lvItems.setAdapter(itemsAdapter);
        setupListViewListener();

    }
    //handle the actions/events on the Listview
    private void setupListViewListener() {
        //Action - long click
        //delete the list item after it is long-clicked
        lvItems.setOnItemLongClickListener(
                new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> adapter,
                                                   View item, int pos, long id) {
                        items.remove(pos);
                        itemsAdapter.notifyDataSetChanged();
                        //remove from the file too
                        writeItems();
                        return true;
                    }
                }
        );

        //Action - click
        //Launch the EditItemActivity to edit the clicked item
        lvItems.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapter,
                                               View item, int pos, long id) {
                        //save the position of the clicked item
                        editPos = pos;
                        startEditItemActivity(items.get(pos));

                    }
                }
        );
    }

    // Prepare data to start new EditItemActivity and start that activity
    public void startEditItemActivity(String item){
        // put "extras" into the bundle for access in the second activity
        Intent intent = new Intent(MainActivity.this, EditItemActivity.class);
        intent.putExtra("item",item.toString());
        // brings up the second activity
        startActivityForResult(intent, REQUEST_CODE);
    }

    // handle the result and response from the EditItemActivity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // REQUEST_CODE is defined above
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            // Extract edited task value from result extras
            String editedTask = data.getExtras().getString("editedTask");
            if(editedTask.equals("")) {
                items.remove(editPos);
                Toast.makeText(this, "Null item removed", Toast.LENGTH_SHORT).show();
            }
            else {
                // edit the task in the list
                items.set(editPos, editedTask);
                Toast.makeText(this, "Task Edited", Toast.LENGTH_SHORT).show();
            }
            itemsAdapter.notifyDataSetChanged();
            //update the item in the file
            writeItems();
        }
    }

    // add new item to the listview and the file stored
    public void onAddItem(View v){
        EditText etNewItem = (EditText) findViewById(R.id.etNewItem);
        String itemText = etNewItem.getText().toString();
        itemsAdapter.add(itemText);
        etNewItem.setText("");
        //write the new item to the file
        writeItems();
    }
    /* Read the previously stored items from the text file and load
     * them into the ListView
     */
     private void readItems(){
         File filesDir = getFilesDir();
         File todoFile = new File(filesDir,"todo.txt");
         try {
             items = new ArrayList<String>(FileUtils.readLines(todoFile));
         }catch(IOException e){
             items = new ArrayList<String>();

         }
     }
    /* Update the text file
     * This method should be called when the new item is added or removed
     */
    private void writeItems(){
        File filesDir = getFilesDir();
        File todoFile = new File(filesDir,"todo.txt");
        try {
            FileUtils.writeLines(todoFile, items);
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}
