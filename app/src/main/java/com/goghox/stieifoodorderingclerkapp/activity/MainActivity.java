package com.goghox.stieifoodorderingclerkapp.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.goghox.stieifoodorderingclerkapp.AppConstant;
import com.goghox.stieifoodorderingclerkapp.R;
import com.goghox.stieifoodorderingclerkapp.adapter.ListOrderAdapter;
import com.goghox.stieifoodorderingclerkapp.bean.ComboBean;
import com.goghox.stieifoodorderingclerkapp.bean.OrderBean;
import com.goghox.stieifoodorderingclerkapp.util.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "TAG";
    private TextView tvCombo1;
    private TextView tvCombo2;
    private TextView tvCombo3;
    private ListView lvOrderList;
    private ArrayList<TextView> mComboViewList = new ArrayList<>();
    private ArrayList<ComboBean> mComboList = new ArrayList<>();
    private ArrayList<OrderBean> mOrderList = new ArrayList<>();

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what){
                case AppConstant.MSG_UPDATE_VIEW:
                    mAdapter.notifyDataSetChanged();
                    break;
                case AppConstant.MSG_CONNECT_ERROR:
                    Toast.makeText(MainActivity.this, "Network ERROR", Toast.LENGTH_SHORT).show();
                    break;
                case AppConstant.MSG_SET_ORDER_SERVED_SUCC:
                    Toast.makeText(MainActivity.this, "Success!", Toast.LENGTH_SHORT).show();
                    break;
                case AppConstant.MSG_UPDATE_COMBO_VIEW:
                    updateCombo();
                    break;
                case AppConstant.MSG_SET_COMBO_AVAILABLE_SUCC:
                    getComboFromServer();
                    Toast.makeText(MainActivity.this, "Success!", Toast.LENGTH_SHORT).show();
                    break;
            }
            return true;
        }
    });

    private void updateCombo() {
        for(int i = 0; i < mComboViewList.size(); i++){
            mComboViewList.get(i).setText(mComboList.get(i).name);
            Log.i(TAG, "updateCombo: " + mComboList.get(i).name);
            if(mComboList.get(i).combo_available == 0) {
                mComboViewList.get(i).setBackgroundColor(Color.RED);
            }else {
                mComboViewList.get(i).setBackgroundColor(Color.WHITE);
            }
        }
    }

    private ListOrderAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initData();
    }

    private void initData() {


        mAdapter = new ListOrderAdapter(this, R.layout.item_order, mOrderList, mHandler);
        lvOrderList.setAdapter(mAdapter);

        getOrderFromServer();
        getComboFromServer();
    }

    private void initView() {
        tvCombo1 = (TextView) findViewById(R.id.tv_combo1);
        tvCombo2 = (TextView)findViewById(R.id.tv_combo2);
        tvCombo3 = (TextView)findViewById(R.id.tv_combo3);
        lvOrderList = (ListView)findViewById(R.id.lv_order_list);

        mComboViewList.add(tvCombo1);
        mComboViewList.add(tvCombo2);
        mComboViewList.add(tvCombo3);

        tvCombo1.setOnClickListener(this);
        tvCombo2.setOnClickListener(this);
        tvCombo3.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int clickId = 0;
        switch (view.getId()){
            case R.id.tv_combo1:
                clickId = 0;
                break;
            case R.id.tv_combo2:
                clickId = 1;
                break;
            case R.id.tv_combo3:
                clickId = 2;
                break;
        }
        final ComboBean comboBean = mComboList.get(clickId);
        TextView tv = mComboViewList.get(clickId);
        final int changedAvailable;
        changedAvailable = (comboBean.combo_available == 0 ?  1 :  0);



        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MainActivity.this);
        if(comboBean.combo_available == 0){
            // show set combo to available
            dialogBuilder.setTitle("Set the combo to available?");
        }else {
            // show set combo to unavailable
            dialogBuilder.setTitle("Set the combo to unavailable?");
        }
        dialogBuilder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String resJson = NetworkUtils.setComboAvailable(comboBean.id, comboBean.name, comboBean.money, changedAvailable);
                            if(resJson != null){
                                mHandler.sendEmptyMessage(AppConstant.MSG_SET_COMBO_AVAILABLE_SUCC);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                dialogInterface.dismiss();
            }
        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        dialogBuilder.show();

    }

    public void getOrderFromServer() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    // clear order list
                    mOrderList.clear();
                    try {
                        String json = NetworkUtils.getOrderList();
                        if(json != null){
                            JSONArray array = new JSONArray(json);
                            for(int i = 0; i  < array.length(); i++){
                                JSONObject jo = array.getJSONObject(i);
                                OrderBean bean = new OrderBean(jo);
                                mOrderList.add(bean);
                            }
                            if(mOrderList.size() > 0){
                                mHandler.removeMessages(AppConstant.MSG_UPDATE_VIEW);
                                mHandler.sendEmptyMessage(AppConstant.MSG_UPDATE_VIEW);
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    SystemClock.sleep(2000);
                }
            }
        }).start();

    }

    public void getComboFromServer() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    mComboList.clear();
                    String comboJson = NetworkUtils.getComboList();
                    if(comboJson != null){
                        JSONArray ja = new JSONArray(comboJson);
                        for(int i = 0; i < ja.length(); i++){
                            JSONObject jo = ja.getJSONObject(i);
                            ComboBean bean = new ComboBean(jo);
                            mComboList.add(bean);
                        }
                        if(mComboList.size() > 0){
                            mHandler.sendEmptyMessage(AppConstant.MSG_UPDATE_COMBO_VIEW);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }
}
