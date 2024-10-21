package com.example.thehollowbar.models;

public class Order {
    private int id;
    private String type_document;
    private int num_document;
    private int id_table;
    private int id_status;
    private String order_date; // Añadir este campo

    // Getters
    public int getId() {
        return id;
    }

    public String getType_document() {
        return type_document;
    }

    public int getNum_document() {
        return num_document;
    }

    public int getId_table() {
        return id_table;
    }

    public int getId_status() {
        return id_status;
    }

    public String getOrder_date() { // Añadir el getter para order_date
        return order_date;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setType_document(String type_document) {
        this.type_document = type_document;
    }

    public void setNum_document(int num_document) {
        this.num_document = num_document;
    }

    public void setId_table(int id_table) {
        this.id_table = id_table;
    }

    public void setId_status(int id_status) {
        this.id_status = id_status;
    }

    public void setOrder_date(String order_date) { // Añadir el setter para order_date
        this.order_date = order_date;
    }
}
