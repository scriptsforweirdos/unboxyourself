package com.kaycleaves.unboxyourself;

/**
 * Created by Roolet on 9/21/2016.
 * Defines an outing for DB usage
 */
public class Outing {

    // private variables
    int _id;
    String _date;
    String _mode;

    // empty constructor
    public Outing() {

    }

    // constructor
    public Outing(int id, String date, String mode){
        this._id = id;
        this._date = date;
        this._mode = mode;
    }

    // constructor
    public Outing(String date, String mode) {
        this._date = date;
        this._mode = mode;
    }

    // getting ID
    public int getID() {
        return this._id;
    }

    // setting ID
    public void setID(int id){
        this._id = id;
    }

    // getting date
    public String getDate(){
        return this._date;
    }

    // setting date
    public void setDate(String date) {
        this._date = date;
    }

    // getting mode
    public String getMode(){
        return this._mode;
    }

    // setting mode
    public void setMode(String mode){
        this._mode = mode;
    }

}
