package com.codepath.todosmart;

import android.provider.BaseColumns;

/**
 * Created by anjalik on 7/21/16.
 * A contract class, a container for constants that define names for URIs, tables, and columns
 */
public final class ToDoTaskContract {
    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    public ToDoTaskContract() {
    }

    /* Inner class that defines the table contents */
    public static abstract class TaskEntry implements BaseColumns {
        public static final String TABLE_NAME = "tasks";
        public static final String _ID = "taskId";
        public static final String COLUMN_NAME_TASK = "task";
        public static final String COLUMN_NAME_DUE_DATE = "dueDate";
        public static final String COLUMN_NAME_PRIORITY = "priority";
        public static final String COLUMN_NAME_NOTES = "notes";



    }

    public static final String TEXT_TYPE = " TEXT";
    public static final String DATE_TYPE = " DATE";
    public static final String COMMA_SEP = ",";
    public static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TaskEntry.TABLE_NAME + " (" +
                    TaskEntry._ID + " INTEGER PRIMARY KEY," +
                    TaskEntry.COLUMN_NAME_TASK + TEXT_TYPE + COMMA_SEP +
                    TaskEntry.COLUMN_NAME_DUE_DATE + DATE_TYPE + COMMA_SEP +
                    TaskEntry.COLUMN_NAME_PRIORITY + TEXT_TYPE + COMMA_SEP +
                    TaskEntry.COLUMN_NAME_NOTES + TEXT_TYPE +
            " )";

    public static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TaskEntry.TABLE_NAME;


}
