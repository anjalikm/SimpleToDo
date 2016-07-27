package com.codepath.todosmart;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {
   // ArrayList<String> items;
   // ArrayAdapter<String> itemsAdapter;
    ListView lvItems;
    //construct the data sourse
    ArrayList<TaskRecord> arrayOfTasks;
    // Create the adapter to convert the array to views
    TasksAdapter taskAdapter ;

    // Attach the adapter to a ListView
    //ListView listView = (ListView) findViewById(R.id.lvItems);

    ToDoTaskdbHelper databaseHelper;

    // REQUEST_CODE  used to determine the result type from the launched activity
    private final int REQUEST_CODE_ADD = 20;
    private final int REQUEST_CODE_EDIT = 30;


    int editPos;     // index of the task in the arrayList to be clicked to be edited
    int editTaskID;  //taskId of the task to be clicked to edit


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        lvItems = (ListView) findViewById(R.id.lvlItems);

        arrayOfTasks = new ArrayList<TaskRecord>();


        //temporary debug
        // Get singleton instance of database
        databaseHelper = ToDoTaskdbHelper.getInstance(this);

        // Add sample post to the database
        //databaseHelper.addTask(new TaskRecord("first task","11/11/11","HIGH","temp task"));
        //load the previously stored items
        readItems();

        Collections.sort(arrayOfTasks, new TaskComparator());
        taskAdapter = new TasksAdapter(this, arrayOfTasks);

        lvItems.setAdapter(taskAdapter);


        // Add item to adapter
       // TaskRecord newTask = new TaskRecord("add sqlite", "med","2/3/12","fksfksfkjs");
        //taskAdapter.add(newTask);


        //itemsAdapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,items);
        //lvItems.setAdapter(itemsAdapter);
        setupListViewListener();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_task:
                // User chose action add new task. Launch New Task activity
               // onAddItem(item);

                // put "extras" into the bundle for access in the second activity
                Intent intent = new Intent(MainActivity.this, EditItemActivity.class);
                // brings up the second activity
                intent.putExtra("code", "101"); // code to add new task
                startActivityForResult(intent, REQUEST_CODE_ADD);
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
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
                        int removeTaskID = arrayOfTasks.get(pos).taskID;
                        Log.i("MainActivity","deleting the task:" + removeTaskID + arrayOfTasks.get(pos).taskName);

                        //remove from the file too
                        //writeItems();
                        //remove from the database
                        deleteTask(removeTaskID,pos);
                        Collections.sort(arrayOfTasks, new TaskComparator());
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
                        //save the taskID of this task to be edited
                        editTaskID = arrayOfTasks.get(pos).taskID;
                        Log.i("MainActivity", "task to be edited:" + editTaskID +":" + arrayOfTasks.get(pos).taskName);
                        startEditItemActivity(arrayOfTasks.get(pos));

                    }
                }
        );
    }
    private int toIntPriority(String priority){
        if(priority.equals("LOW"))
            return 0;
        else if(priority.equals("MED"))
            return 1;
        else
            return 2;

    }
    // Prepare data to start new EditItemActivity and start that activity
    public void startEditItemActivity(TaskRecord record){
        // put "extras" into the bundle for access in the second activity
        Intent intent = new Intent(MainActivity.this, EditItemActivity.class);
        intent.putExtra("code","102");
        intent.putExtra("item",record.taskName.toString());
        intent.putExtra("dueDate",record.dueDate.toString());
        intent.putExtra("priority", toIntPriority(record.priority));
        intent.putExtra("notes",record.notes.toString());
        // brings up the second activity
        startActivityForResult(intent, REQUEST_CODE_EDIT);
    }

    // handle the result and response from the EditItemActivity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // REQUEST_CODE is defined above
        if (resultCode == RESULT_OK) {
            // Extract edited task value from result extras
            String editedTask = data.getExtras().getString("editedTask");
            String dueDate = data.getStringExtra("dueDate");
            String prio = data.getStringExtra("priority");
            String notes = data.getStringExtra("notes");
            TaskRecord newRec = new TaskRecord(editTaskID,editedTask, prio, dueDate, notes);
            if(requestCode == REQUEST_CODE_EDIT) {
                //get the TaskId of the task to be edited
                // edit the task in the list
                //check if the task is requested to delete from the child activity
                String action_code = data.getStringExtra("action_code");
                if(action_code != null && action_code.equals("DELETE")){
                    deleteTask(editTaskID,editPos);

                }
                //the task is edited
                else if (editedTask.equals("")) {
                    arrayOfTasks.remove(editPos);
                    Toast.makeText(this, "Null item removed", Toast.LENGTH_SHORT).show();
                    //remove from the db also
                } else {
                    //update the task in the list as well as in the db
                    arrayOfTasks.set(editPos, new TaskRecord(editTaskID,editedTask, prio, dueDate, notes));
                    Toast.makeText(this, "Task Edited", Toast.LENGTH_SHORT).show();
                    //update in the database
                    databaseHelper.update(editTaskID,newRec);
                }
            }
            else if(requestCode == REQUEST_CODE_ADD){
               // arrayOfTasks.add(newRec);
                if(!editedTask.equals("")) {
                    long last_id = databaseHelper.addTask(newRec);
                    newRec.taskID = (int) last_id;
                    Log.i("MainAcitivity", "added new task:" + last_id);
                    taskAdapter.add(newRec);
                }
            }
            taskAdapter.notifyDataSetChanged();
            Collections.sort(arrayOfTasks, new TaskComparator());
            //update the item in the file
            //writeItems();
        }
    }

    private void deleteTask(int taskId, int pos){
        databaseHelper.removeTask( taskId);
        arrayOfTasks.remove(pos);
        taskAdapter.notifyDataSetChanged();
        Toast.makeText(this, "Task deleted", Toast.LENGTH_SHORT).show();
    }
    // add new item to the listview and the file stored
    //currently not used
    public void onAddItem(MenuItem v){

       // taskAdapter.add(new TaskRecord(itemText,"low","1/2/12","new task"));

        //write the new item to the file
        //writeItems();
    }
    /* Read the previously stored items from the text file and load
     * them into the ListView
     */
     private void readItems(){

         arrayOfTasks = new ArrayList<TaskRecord>(databaseHelper.getAllTasks());
         for(int i = 0 ; i < arrayOfTasks.size(); i++)
             Log.i("MainActivity", arrayOfTasks.get(i).taskName);
         /*File filesDir = getFilesDir();
         File todoFile = new File(filesDir,"todo.txt");
         try {
             arrayOfTasks = new ArrayList<String>(FileUtils.readLines(todoFile));
         }catch(IOException e){
             arrayOfTasks = new ArrayList<String>();

         }*/
     }
    /* Update the text file
     * This method should be called when the new item is added or removed
     */
    private void writeItems(){
        File filesDir = getFilesDir();
        File todoFile = new File(filesDir,"todo.txt");
        try {
            FileUtils.writeLines(todoFile, arrayOfTasks);
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}
