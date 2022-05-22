/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.carmanager;

/**
 *
 * @author TeoTeo23_
 */
public class Car {
    private String plate;
    private String matriculationYear;
    private String revDate;
    private int expMonth;
    private int expYear;
    private char esit;
    
    public Car(String p, String mY, String rD, int eM, int eY){
        this.plate = p;
        this.matriculationYear = mY;
        this.revDate = rD;
        this.expMonth = eM;
        this.expYear = eY;
    }

    public String getPlate() {
        return plate;
    }

    public String getMatriculationYear() {
        return matriculationYear;
    }

    public String getRevDate() {
        return revDate;
    }

    public int getExpMonth() {
        return expMonth;
    }

    public int getExpYear() {
        return expYear;
    }

    public char getEsit() {
        return esit;
    }
    
    public void setRevDate(String rD){
        this.revDate = rD;
    }
    public void setEsit(char e){ 
        this.esit = e;
    }
}
