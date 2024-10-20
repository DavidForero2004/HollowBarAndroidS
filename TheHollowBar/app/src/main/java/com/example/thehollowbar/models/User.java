package com.example.thehollowbar.models;

public class User {
    private int id;
    private String name;
    private String email;
    private String password;
    private int id_status;
    private int id_rol;

    //Getters
    public int getId()  {
        return  id;
    }

    public String getName()  {
        return  name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public int getId_status() {
        return id_status;
    }

    public int getId_rol() {
        return id_rol;
    }

    //Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setId_status(int id_status) {
        this.id_status = id_status;
    }

    public  void setId_rol(int id_rol) {
        this.id_rol = id_rol;
    }
}
