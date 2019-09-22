package com.example.solarcalculator.utilities;

import android.text.format.DateFormat;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import java.util.TimeZone;

import static java.lang.Math.acos;
import static java.lang.Math.asin;
import static java.lang.Math.atan;
import static java.lang.Math.cos;
import static java.lang.Math.floor;
import static java.lang.Math.sin;
import static java.lang.Math.tan;

public class CalculateTime {

    private Date datetime;
    private LatLng location;
    private Double zenith = 90.50d;
    private double lgnHour;
    private double t;
    private double RA;
    private double risingtime;
    private double settingtime;

    public CalculateTime(Date datetime, LatLng location) {
        this.datetime = datetime;
        this.location = location;
        //this.location = new LatLng(40.9,-74.3);
       // Log.e("zenith","cos "+ cos(zenith));
    }

    public void timerequired() {

        String day1 = (String) DateFormat.format("dd", datetime);
        double day = Double.parseDouble(day1);

        String monthNumber = (String) DateFormat.format("MM",   datetime);

        double month = Double.parseDouble(monthNumber) ;

        String yearn  = (String) DateFormat.format("yyyy", datetime);
        double year = Double.parseDouble(yearn);

        Log.e("Day "+day," month "+month+" year "+year);

        dayoftheyear(day, month, year);


    }


    private void dayoftheyear(Double day, Double month, Double year) {

        double N1 = floor(275 * month / 9);
        double N2 = floor((month + 9) / 12);

        double N3 = (1 + floor((year - 4 * floor(year / 4) + 2) / 3));

        double N = N1 - (N2 * N3) + day - 30;

        approxtimerising(N);
        approxtimesetting(N);

    }

    private void approxtimerising(double N) {


        double lonngitude = location.longitude;

        lgnHour = lonngitude / 15;

        t = N + ((6 - lgnHour) / 24);

        meanAnamoly(t);

    }

    private void approxtimesetting(double N) {


        double lonngitude = location.longitude;

        lgnHour = lonngitude / 15;

        t = N + ((18 - lgnHour) / 24);

        meanAnamoly(t);

    }

    private void meanAnamoly(double t) {

        double M = (0.9856 * t) - 3.289;



        truelong(M);

    }


    private void truelong(double M) {

        double L = M + (1.916 * sin(M*(Math.PI/180))) + (0.020 * sin(2 * M*(Math.PI/180))) + 282.634;

        if (L < 0)
            L = L + 360;
        else if (L > 360)
            L = L - 360;

        rightAcsension(L);
        declination(L);

    }

    private void rightAcsension(double L) {

        RA = (180/Math.PI)*atan(0.91764 * tan((Math.PI/180)*L));

        if (RA < 0)
            RA = RA + 360;
        else if (RA > 360)
            RA = RA - 360;

        //same quadrant as L
        double Lquadrant = (floor(L / 90)) * 90;
        double RAquadrant = (floor(RA / 90)) * 90;
        RA = RA + (Lquadrant - RAquadrant);

        //converting RA to hours
        RA = RA / 15;


    }

    //needs to be checked
    private void declination(double L) {

        double sinDec = 0.39782 * sin(L*(Math.PI/180));
        double cosDec = cos(asin(sinDec));



        localhour(sinDec, cosDec);

    }

    private void localhour(double sinDec, double cosDec) {

        double cosH = (cos(zenith*(Math.PI/180)) - (sinDec * sin(location.latitude*(Math.PI/180)))) / (cosDec * cos(location.latitude*(Math.PI/180)));

        if (cosH > 1) {
            //the sun never rises on this location (on the specified date)
        }

        if (cosH < -1) {
            //the sun never sets on this location (on the specified date)
        }



        hoursrising(cosH);
        hourssetting(cosH);
    }


    private void hoursrising(double cosH) {

        double H;
        H = 360 - (180/Math.PI)*acos(cosH);

        H = H / 15;



        meanTime(H, RA, t);

    }

    private void hourssetting(double cosH) {

        double H;
        H = (180/Math.PI)*acos(cosH);

        H = H / 15;


        meanTime(H, RA, t);

    }

    private void meanTime(double H, double RA, double t) {

        double T = H + RA - (0.06571 * t) - 6.622;



        adjusttoUTC(T, lgnHour);
    }

    private void adjusttoUTC(double T, double lngHour) {
        double UT = T - lngHour;



        if (UT > 24)
            UT = UT - 24;
        else if (UT < 0)
            UT = UT + 24;

        Log.e("UT", " "+UT);


        localtime(UT);


    }

    private double localtime(double UT) {

        double localOffset = 5.50;
        double localT = UT + localOffset;

        if (localT>24)
            localT = localT-24;

        long timeInMilliSeconds = (long) Math.floor(localT * 60 * 1000);
        Date date = new Date(timeInMilliSeconds);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));

        if (localT>12)
            settingtime =localT;
        else
            risingtime = localT;






        return localT;

    }

    public double getRisingtime(){
        return risingtime;
    }

    public double getSettingtime(){
        return settingtime;
    }

    public double getalarmtime(){
        return settingtime-1;
    }

}