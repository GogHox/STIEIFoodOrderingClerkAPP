package com.goghox.stieifoodorderingclerkapp.bean;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by goghox on 11/12/17.
 */

public class OrderBean {

    public int comboId;
    public String comboName;
    public int lockerNumber;
    public String pickupTime;
    public int served;
    /*
      {
    "comboID": 1,
    "combo_name": "combo 1",
    "locker_nr": 1002,
    "pickup_time": "10:30",
    "served": "true = 1, false = 0"
  }
     */

    public OrderBean() {
    }

    ;

    public OrderBean(JSONObject jo) throws JSONException {
        this.comboId = jo.getInt("combo_id");
        this.comboName = jo.getString("name");
        this.lockerNumber = jo.getInt("locker_nr");
        this.pickupTime = jo.getString("pickup_time");
        this.served = jo.getInt("served");
    }

    ;
}
