package com.ic.googletelus;

public class Store {
    private String name;
    private double lat;
    private double lon;
    private String address;
    private String phone;
    private String services;
    private String hours;
    private int fav;
    
    Store(String parent_Name, double parent_lati, double parent_longi, String parent_address, String parent_Phone, String parent_services, String parent_hours, int favorite)
    {
    	name = parent_Name;
    	lat = parent_lati;
    	lon = parent_longi;
    	address = parent_address;
    	phone = parent_Phone;
    	services = parent_services;
    	hours = parent_hours;
    	fav = favorite;			
    }
    
    String getName(){
        return name;
    }
    double getLat(){
        return lat;
    }
    double getLong(){
        return lon;
    }
    String getAddr(){
        return address;
    }
    String getPhone(){
        return phone;
    }
    String getServ(){
        return services;
    }
    String getHrs(){
        return hours;
    }
    
    int getFav(){
        return fav;
    }
}