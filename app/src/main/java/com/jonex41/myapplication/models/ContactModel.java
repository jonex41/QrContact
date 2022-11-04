package com.jonex41.myapplication.models;

import java.io.Serializable;

public class ContactModel implements Serializable, Comparable<ContactModel> {
    private String name;
    private String number;
    private String gmail;
    private boolean isSelected = false;

    public ContactModel() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getGmail() {
        return gmail;
    }

    public void setGmail(String gmail) {
        this.gmail = gmail;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    @Override
    public int compareTo(ContactModel contactModel) {
        return getName().compareTo(contactModel.getName());
    }
}
