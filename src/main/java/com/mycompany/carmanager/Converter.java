/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.carmanager;

import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 *
 * @author TeoTeo23_
 */
public class Converter {
    
    public Converter(){}
    
    public java.sql.Date stringToDate(String date){
        java.sql.Date sqlDate = null;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try{
            java.util.Date uDate = format.parse(date);
            sqlDate = new java.sql.Date(uDate.getTime());
        }catch(ParseException exception){ exception.printStackTrace(); }
        return sqlDate;
    }
}
