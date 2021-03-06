package com.kaustubh.todoapp;

import java.util.Calendar;
import java.util.Date;

public class TaskInformation {
    private String task;
    private String time;
    private String isAlarm;
    private String date;
    private String day;
    private Integer pendingIntentNumber;
    private Long millis;


    public TaskInformation(String task, String time, String isAlarm, String date, String day,Integer pendingIntentNumber,Long millis) {
        this.task = task;
        this.time = time;
        this.isAlarm = isAlarm;
        this.date = date;
        this.day = day;
        this.pendingIntentNumber = pendingIntentNumber;
        this.millis = millis;
    }
    public String getDay(){
        return day;
    }
    public void setDay(String day){
        this.day = day;
    }
    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getIsAlarm() {
        return isAlarm;
    }

    public void setIsAlarm(String alarm) {
        isAlarm = alarm;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
    public void setPendingIntentNumber(int pendingIntentNumber){
        this.pendingIntentNumber = pendingIntentNumber;
    }
    public Integer getPendingIntentNumber(){
        return pendingIntentNumber;
    }
    public Long getMillis(){
        return this.millis;
    }
    public void setMillis(Long millis){
        this.millis = millis;
    }
}
