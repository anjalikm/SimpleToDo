package com.codepath.todosmart;

/**
 * Created by anjalik on 7/21/16.
 * A ToDo Task Record
 */
public class TaskRecord {
    public int taskID;
    public String taskName;
    public String priority;
    public String dueDate;
    public String notes;

    public TaskRecord(int tid,String tn, String p, String dd, String notes){
        this.taskID = tid;
        this.taskName = tn;
        this.priority = p;
        this.dueDate = dd;
        this.notes = notes;
    }

}
