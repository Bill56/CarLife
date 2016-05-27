package com.bill56.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bill56.carlife.R;
import com.bill56.entity.UserCar;

import java.util.List;

/**
 * Created by Bill56 on 2016/5/23.
 */
public class CarInfoAdapter extends BaseAdapter {

    // 上下文环境
    private Context mContext;
    // 数据
    private List<UserCar> mData;
    // 布局服务加载器
    private LayoutInflater layoutInflater;

    public CarInfoAdapter(Context context, List<UserCar> data) {
        this.mContext = context;
        mData = data;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        // 加载模板，创建视图项——将布局创建成一个View对象
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.item_car_info, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        UserCar userCar = mData.get(position);
        viewHolder.bindData(userCar);
        return convertView;
    }

    /**
     * 保存布局控件实例的类
     */
    private class ViewHolder {

        // 字段
        ImageView imgCarLogo;
        TextView textCarBrandVersion;
        TextView textTotalOil;
        TextView textCarLicence;
        TextView textTransState;
        TextView textEngineState;
        TextView textLightState;
        TextView textEngineNo;

        /**
         * 构造方法，获得布局中的控件绑定对象
         * @param v 布局参数
         */
        public ViewHolder(View v) {
            imgCarLogo = (ImageView) v.findViewById(R.id.image_car_logo);
            textCarBrandVersion = (TextView) v.findViewById(R.id.text_car_brand_version);
            textTotalOil = (TextView) v.findViewById(R.id.text_total_oil);
            textCarLicence = (TextView) v.findViewById(R.id.text_car_licence);
            textTransState = (TextView) v.findViewById(R.id.text_trans_state);
            textEngineState = (TextView) v.findViewById(R.id.text_engine_state);
            textLightState = (TextView) v.findViewById(R.id.text_light_state);
            textEngineNo = (TextView) v.findViewById(R.id.text_engine_no);
        }

        /**
         * 绑定数据
         * @param userCar   用户车辆对象
         */
        public void bindData(UserCar userCar) {
            // 设置图标
            setCarLogo(userCar.getCarBrand());
            // 设置其他参数
            textCarBrandVersion.setText(userCar.getCarBrand()+userCar.getCarVersion());
            textTotalOil.setText(userCar.getCarOilTotal() + "L");
            textCarLicence.setText(userCar.getCarLicence());
            textEngineState.setText(userCar.getCarEngineState());
            textTransState.setText(userCar.getCarTransState());
            textLightState.setText(userCar.getCarLightState());
            textEngineNo.setText(userCar.getCarEngineNo());
        }

        private void setCarLogo(String carBrand) {
            // 根据车品牌设置图标
            switch (carBrand) {
                case "奥迪":
                    imgCarLogo.setImageResource(R.drawable.auto_audi);
                    break;
                case "本田":
                    imgCarLogo.setImageResource(R.drawable.auto_honda);
                    break;
                case "福特":
                    imgCarLogo.setImageResource(R.drawable.auto_ford);
                    break;
                case "玛莎拉蒂":
                    imgCarLogo.setImageResource(R.drawable.auto_maserati);
                    break;
                case "马自达":
                    imgCarLogo.setImageResource(R.drawable.auto_mazda);
                    break;
                case "现代":
                    imgCarLogo.setImageResource(R.drawable.auto_hyundai);
                    break;
                case "大众":
                    imgCarLogo.setImageResource(R.drawable.auto_volkswagen);
                    break;
                case "日产":
                    imgCarLogo.setImageResource(R.drawable.auto_nissan);
                    break;
                case "斯柯达":
                    imgCarLogo.setImageResource(R.drawable.auto_skoda);
                    break;
                case "雪弗兰":
                    imgCarLogo.setImageResource(R.drawable.auto_chevrolet);
                    break;
                case "三菱":
                    imgCarLogo.setImageResource(R.drawable.auto_mitsubishi);
                    break;
                default:
                    imgCarLogo.setImageResource(R.drawable.auto_abarth);
                    break;
            }
        }
    }

}
