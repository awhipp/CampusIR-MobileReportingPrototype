package com.rzepka.hsiao.whipp.campusir;

public class Point {
    private double lat;
    private double lng;

    public Point(double lat, double lng){
        this.lat = lat;
        this.lng = lng;
    }

    public double getLat(){ return this.lat; }

    public double getLng(){ return this.lng; }


    public double distance(Point p){
        return Math.sqrt(Math.pow(this.lat - p.getLat(), 2) + Math.pow(this.lng - p.getLng(), 2));
    }



}
