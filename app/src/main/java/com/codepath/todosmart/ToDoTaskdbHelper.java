package com.codepath.todosmart;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by anjalik on 7/21/16.
 */
public class ToDoTaskdbHelper extends SQLiteOpenHelper {

    private static final String TAG = "ToDoTaskdbHelper";
    //database info
    public static final int DATABASE_VERSION = 6;
    public static final String DATABASE_NAME = "TaskDBv1.db";
    //table info
    public static final String TABLE_NAME = "tasks";
    public static final String COLUMN_NAME_TASK_ID ="taskId";
    public static final String COLUMN_NAME_TASK = "task";
    public static final String COLUMN_NAME_DUE_DATE = "dueDate";
    public static final String COLUMN_NAME_PRIORITY = "priority";
    public static final String COLUMN_NAME_NOTES = "notes";

    private static ToDoTaskdbHelper sInstance;

    //Singleton pattern
    public static synchronized ToDoTaskdbHelper getInstance(Context context) {
        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        if (sInstance == null) {
            sInstance = new ToDoTaskdbHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    /**
     * Constructor should be private to prevent direct instantiation.
     * Make a call to the static method "getInstance()" instead.
     */
    private ToDoTaskdbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Configure database settings for things like foreign key support, write-ahead logging, etc.
    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        //db.setForeignKeyConstraintsEnabled(true);
    }
    public void onCreate(SQLiteDatabase db) {

        Log.v(TAG, "creating table");
        db.execSQL(ToDoTaskContract.SQL_CREATE_ENTRIES);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        Log.v(TAG, "upgrading  db");
        db.execSQL(ToDoTaskContract.SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.v(TAG, "downgrading  db");
        onUpgrade(db, oldVersion, newVersion);
    }

    // Insert a new Todo Item into the database
    public long addTask(TaskRecord record) {

        // Create and/or open the database for writing
        SQLiteDatabase db = getWritableDatabase();
        Log.v(TAG, "adding new task to database");
        db.beginTransaction();
        long last_id = -1;
        try {

            ContentValues values = new ContentValues();
            //values.put(COLUMN_NAME_TASK_ID,record.taskID);
            values.put(COLUMN_NAME_TASK,record.taskName);
            values.put(COLUMN_NAME_DUE_DATE, record.dueDate);
            values.put(COLUMN_NAME_PRIORITY, record.priority);
            values.put(COLUMN_NAME_NOTES,record.notes);
            // Notice how we haven't specified the primary key. SQLite auto increments the primary key column.
            last_id = db.insertOrThrow(TABLE_NAME, null, values);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to add post to database:" + e.getMessage());
        } finally {
            db.endTransaction();
        }
        return last_id;
    }

    // Get all tasks in the database
    public ArrayList<TaskRecord> getAllTasks() {
        ArrayList<TaskRecord> taskList = new ArrayList<TaskRecord>();

        // SELECT * FROM POSTS
        // LEFT OUTER JOIN USERS
        // ON POSTS.KEY_POST_USER_ID_FK = USERS.KEY_USER_ID
        String TASKS_SELECT_QUERY =
                "SELECT * FROM " + TABLE_NAME ;
        Log.v(TAG, "fetching all the records from the  database");
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(TASKS_SELECT_QUERY, null);
        try {
            if (cursor.moveToFirst()) {
                do {

                    String taskID = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_TASK_ID));
                    String taskName = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_TASK));
                    String prio = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_PRIORITY));
                    String dueDate = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_DUE_DATE));
                    String notes = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_NOTES));
                    taskList.add(new TaskRecord(Integer.parseInt(taskID),taskName,prio,dueDate,notes));

                } while(cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to get tasks from database");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return taskList;
    }

    //update a task in the database
    public void update(int taskID, TaskRecord record){
       /* UPDATE Customers
        SET ContactName='Alfred Schmidt', City='Hamburg'
        WHERE CustomerName='Alfreds Futterkiste';*/
        //populate content values
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME_TASK_ID,taskID);
        values.put(COLUMN_NAME_TASK,record.taskName);
        values.put(COLUMN_NAME_DUE_DATE, record.dueDate);
        values.put(COLUMN_NAME_PRIORITY, record.priority);
        values.put(COLUMN_NAME_NOTES,record.notes);


        Log.v(TAG, "updating the record in the  database");
        SQLiteDatabase db = getReadableDatabase();
        int rows = db.update(TABLE_NAME, values, COLUMN_NAME_TASK_ID + "= ?", new String[]{String.valueOf(record.taskID)});

    }

    //remove a task
  public void  removeTask(int taskID){

      Log.v(TAG, "deleting the record from the  database");
      SQLiteDatabase db = getWritableDatabase();
      db.beginTransaction();
      try {
          //int rows = db.delete(TABLE_NAME, COLUMN_NAME_TASK_ID + "= ?", new String[]{String.valueOf(taskID)});
          int rows = db.delete(TABLE_NAME, COLUMN_NAME_TASK_ID + "="+ (long)taskID, null);
          //db.delete(TABLE_NAME,null,null);
          db.setTransactionSuccessful();
          Log.i("TODOTaskdbHelper", "rows deleted:"+rows);
      }
      catch (Exception e) {
          Log.d(TAG,"Error while deleting the record:" + e.getMessage());
      }finally{
          db.endTransaction();
      }

  }
}
