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

import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {

    ListView lvItems;
    //construct the data source
    ArrayList<TaskRecord> arrayOfTasks;
    // Create the adapter to convert the array to views
    TasksAdapter taskAdapter ;
    //database handler
    ToDoTaskdbHelper databaseHelper;

    // REQUEST_CODE  used to determine the result type from the launched activity
    private final int REQUEST_CODE_ADD = 20;        //add new task
    private final int REQUEST_CODE_EDIT = 30;       //edit a task


    int editPos;     // index of the task in the arrayList to be clicked to be edited
    int editTaskId;  //taskId of the task to be clicked to edit


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //custom toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setIcon(R.drawable.ic_calendar);


        lvItems = (ListView) findViewById(R.id.lvlItems);
        arrayOfTasks = new ArrayList<TaskRecord>();

        // Get singleton instance of database
        databaseHelper = ToDoTaskdbHelper.getInstance(this);

        //load the previously stored items
        readItems();
        //sort them according to the due date and the priority
        Collections.sort(arrayOfTasks, new TaskComparator());
        //attach the adapter to the listview to display it
        taskAdapter = new TasksAdapter(this, arrayOfTasks);
        lvItems.setAdapter(taskAdapter);

        setupListViewListener();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }
    //handle the click event on the action button in the toolbar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_task:
                // User chose action add new task. Launch New Task activity
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
                        editTaskId = arrayOfTasks.get(pos).taskID;
                        Log.i("MainActivity", "task to be edited:" + editTaskId +":" + arrayOfTasks.get(pos).taskName);
                        startEditItemActivity(arrayOfTasks.get(pos));

                    }
                }
        );
    }


    // converts the Priority to integer ( 0 - LOW, 1- MED, 2-HIGH
    // Integer priority is used to index the item in prioritySpinner
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
        intent.putExtra("status",record.status);
        // brings up the second activity
        startActivityForResult(intent, REQUEST_CODE_EDIT);
    }

    // handle the result and response from the EditItemActivity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {
            // Extract edited task value from result extras
            String editedTask = data.getExtras().getString("editedTask");
            String dueDate = data.getStringExtra("dueDate");
            String prio = data.getStringExtra("priority");
            String notes = data.getStringExtra("notes");
            boolean status = data.getBooleanExtra("status",false);
            TaskRecord newRec = new TaskRecord(editTaskId,editedTask, prio, dueDate, notes,status);
            if(requestCode == REQUEST_CODE_EDIT) {
                //check if the task is requested to delete from the child activity
                String action_code = data.getStringExtra("action_code");
                if(action_code != null && action_code.equals("DELETE")){
                    deleteTask(editTaskId,editPos);

                }
                //remove if the task edited is null
                else if (editedTask.equals("")) {
                    //remove from the db
                    deleteTask(editTaskId,editPos);
                    arrayOfTasks.remove(editPos);
                    Toast.makeText(this, "Null item removed", Toast.LENGTH_SHORT).show();
                } else {
                    //update the task in the list as well as in the db
                    arrayOfTasks.set(editPos, new TaskRecord(editTaskId,editedTask, prio, dueDate, notes,status));
                    Toast.makeText(this, "Task Edited", Toast.LENGTH_SHORT).show();
                    //update in the database
                    databaseHelper.update(editTaskId,newRec);
                }
            }
            else if(requestCode == REQUEST_CODE_ADD){

                if(!editedTask.equals("")) {
                    long last_id = databaseHelper.addTask(newRec);
                    newRec.taskID = (int) last_id;
                    Log.i("MainAcitivity", "added new task:" + last_id);
                    taskAdapter.add(newRec);
                }
            }
            taskAdapter.notifyDataSetChanged();
            Collections.sort(arrayOfTasks, new TaskComparator());

        }
    }

    private void deleteTask(int taskId, int pos){
        databaseHelper.removeTask( taskId);
        arrayOfTasks.remove(pos);
        taskAdapter.notifyDataSetChanged();
        Toast.makeText(this, "Task deleted", Toast.LENGTH_SHORT).show();
    }

    /* Read the previously stored items from the database and load
     * them into the ListView
     */
     private void readItems(){

         arrayOfTasks = new ArrayList<TaskRecord>(databaseHelper.getAllTasks());
         for(int i = 0 ; i < arrayOfTasks.size(); i++)
             Log.i("MainActivity", arrayOfTasks.get(i).taskName);

     }

}
