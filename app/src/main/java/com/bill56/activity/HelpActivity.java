package com.bill56.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.bill56.carlife.R;

import java.util.ArrayList;
import java.util.List;

/**帮助的活动
 * Created by Bill56 on 2016/6/1.
 */
public class HelpActivity extends BaseActivity {

    private List<String> groupData;
    private List<List<String>> childrenData;
    private void loadData() {
        groupData = new ArrayList<String>();
        groupData.add("   1. 预约加油");
        groupData.add("   2. 地图信息");
        groupData.add("   3. 路线选择");
        groupData.add("   4. 车辆信息维护");

        childrenData = new ArrayList<List<String>>();
        List<String> sub1 = new ArrayList<String>();
        sub1.add("        绑定汽车信息后即可查看预约订单详情");
        childrenData.add(sub1);
        List<String> sub2 = new ArrayList<String>();
        sub2.add("        地图可以实时显示当前汽车位置，并显示周围的加油站");

        childrenData.add(sub2);
        List<String> sub3 = new ArrayList<String>();
        sub3.add("        输入起始地点和目的地后，会给出最优路线，并可实时显示当前汽车位置");

        childrenData.add(sub3);
        List<String> sub4 = new ArrayList<String>();
        sub4.add("        扫描包含汽车信息的二维码，个人信息便可自动维护到手机里面");

        childrenData.add(sub4);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        // 设置返回键
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.activity_help_title);
        loadData();
        // 设置展开项的初始化操作
        ExpandableListView expandableListView = (ExpandableListView)findViewById(R.id.expandable_list_view);
        expandableListView.setAdapter(new ExpandableAdapter());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.back,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }
        return true;
    }

    private class ExpandableAdapter extends BaseExpandableListAdapter {

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return childrenData.get(groupPosition).get(childPosition);
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return 0;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            TextView text = null;
            if (convertView != null) {
                text = (TextView)convertView;
                text.setText(childrenData.get(groupPosition).get(childPosition));
            } else {
                text = createView(childrenData.get(groupPosition).get(childPosition));
            }
            // 设置子项的字体大小
            text.setTextSize(20);
            text.setTextColor(Color.GRAY);
            return text;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return childrenData.get(groupPosition).size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            return groupData.get(groupPosition);
        }

        @Override
        public int getGroupCount() {
            return groupData.size();
        }

        @Override
        public long getGroupId(int groupPosition) {
            return 0;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            TextView text = null;
            if (convertView != null) {
                text = (TextView)convertView;
                text.setText(groupData.get(groupPosition));
            } else {
                text = createView(groupData.get(groupPosition));
            }
            return text;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return false;
        }

        private TextView createView(String content) {
            AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            TextView text = new TextView(HelpActivity.this);
            text.setLayoutParams(layoutParams);
            text.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
            text.setPadding(40, 0, 0, 0);
            text.setText(content);
            text.setTextSize(24);
            return text;
        }
    }

}
