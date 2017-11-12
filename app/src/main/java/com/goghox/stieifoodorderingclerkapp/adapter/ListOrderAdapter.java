package com.goghox.stieifoodorderingclerkapp.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.goghox.stieifoodorderingclerkapp.AppConstant;
import com.goghox.stieifoodorderingclerkapp.R;
import com.goghox.stieifoodorderingclerkapp.bean.OrderBean;
import com.goghox.stieifoodorderingclerkapp.util.NetworkUtils;

import java.io.IOException;
import java.util.ArrayList;

/**
 * 复用性为0，，，只为减少activity的代码，，
 * Created by goghox on 11/12/17.
 */

public class ListOrderAdapter extends ArrayAdapter<OrderBean> {
    private final Handler mHandler;
    private ArrayList<OrderBean> mOrderList;

    public ListOrderAdapter(@NonNull Context context, int resource, @NonNull ArrayList<OrderBean> objects, Handler handler) {
        super(context, resource, objects);
        mOrderList = objects;
        this.mHandler = handler;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final ViewHolder viewHolder;
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_order, parent, false);
            viewHolder = new ViewHolder();

            viewHolder.tvComboId = convertView.findViewById(R.id.tv_combo_id);
            viewHolder.tvComboName = convertView.findViewById(R.id.tv_combo_name);
            viewHolder.tvLockerNumber = convertView.findViewById(R.id.tv_locker_number);
            viewHolder.tvPickupTime = convertView.findViewById(R.id.tv_pickup_time);
            viewHolder.tvServed = convertView.findViewById(R.id.tv_served);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if(mOrderList.size() > 0) {
            viewHolder.tvComboId.setText("" + mOrderList.get(position).comboId);
            viewHolder.tvComboName.setText(mOrderList.get(position).comboName);
            viewHolder.tvLockerNumber.setText("" + mOrderList.get(position).lockerNumber);
            viewHolder.tvPickupTime.setText(mOrderList.get(position).pickupTime);
            if (mOrderList.get(position).served == 1) {
                // served
                viewHolder.tvServed.setText("Yes");
                viewHolder.tvServed.setTextColor(Color.GREEN);
            } else {
                // unserved
                viewHolder.tvServed.setText("No");
                viewHolder.tvServed.setTextColor(Color.RED);
            }

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // TODO push out a dialog
                    ViewHolder holder = viewHolder;
                    AlertDialog dialog = new AlertDialog.Builder(getContext())
                            .setTitle("Confirm set this order to served?")
                            .setMessage("combo id: " + holder.tvComboId.getText() + "\n" +
                                    "combo name: " + holder.tvComboName.getText() + "\n" +
                                    "locker number: " + holder.tvLockerNumber.getText() + "\n" +
                                    "pickup time: " + holder.tvPickupTime.getText() + "\n" +
                                    "please confirm this information")
                            .setPositiveButton("confirm", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    // set the order to served
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                String responseJson = NetworkUtils.setOrderToServed(viewHolder.tvComboId.getText(), viewHolder.tvPickupTime.getText(), viewHolder.tvLockerNumber.getText());
                                                mHandler.sendEmptyMessage(AppConstant.MSG_SET_ORDER_SERVED_SUCC);
                                            } catch (IOException e) {
                                                mHandler.sendEmptyMessage(AppConstant.MSG_CONNECT_ERROR);
                                                e.printStackTrace();
                                            }
                                        }
                                    }).start();
                                    dialogInterface.dismiss();
                                }
                            })
                            .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            }).show();
                }
            });
        }

        return convertView;
    }
    class ViewHolder{
        public TextView tvComboId;
        public TextView tvComboName;
        public TextView tvLockerNumber;
        public TextView tvPickupTime;
        public TextView tvServed;
    }

}

/*class ListOrderAdapter extends ArrayAdapter {


    }*/