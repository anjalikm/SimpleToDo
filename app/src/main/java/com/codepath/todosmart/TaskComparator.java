package com.codepath.todosmart;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

/**
 * Created by anjalik on 7/25/16.
 */
public class TaskComparator implements Comparator<TaskRecord> {

    @Override
    public int compare(TaskRecord rec1, TaskRecord rec2) {
        SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        Date date1 = new Date(), date2 = new Date();
        try {
            date1 = df.parse(rec1.dueDate);
            date2 = df.parse(rec2.dueDate);


        }
        catch(ParseException pe){

        }
        int result = date1.compareTo(date2);
        int pri;
        if(result == 0){
            pri = rec2.priority.hashCode() - rec1.priority.hashCode();
            return pri;
        }
        return result;
    }
}
