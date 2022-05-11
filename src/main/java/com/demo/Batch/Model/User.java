package com.demo.Batch.Model;

public class User {
    private  String itemGroup;
    private  String itemName;
    private String weight;
    private String cost;

    public User() {
    }

    public User(String itemGroup, String itemName, String weight, String cost) {
        this.itemGroup = itemGroup;
        this.itemName = itemName;
        this.weight = weight;
        this.cost = cost;
    }


    public String getItemGroup() {
        return itemGroup;
    }

    public void setItemGroup(String itemGroup) {
        this.itemGroup = itemGroup;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;

    }
}
