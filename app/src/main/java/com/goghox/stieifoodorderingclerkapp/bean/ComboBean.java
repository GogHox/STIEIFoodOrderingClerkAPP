package com.goghox.stieifoodorderingclerkapp.bean;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by GogHox on 2017/10/17.
 */

public class ComboBean {
    public String name;
    public int id;
    public double money;
    public int combo_available;
    public String description;
    public String picture;

    public ComboBean() {
    }

    public ComboBean(JSONObject comboItemObj) throws JSONException {
        this.id = comboItemObj.getInt("id");
        this.name = comboItemObj.getString("name");
        this.money = comboItemObj.getDouble("price");
        this.combo_available = comboItemObj.getInt("combo_available");
        this.description = "Combo Name: " + name;
    }
}
