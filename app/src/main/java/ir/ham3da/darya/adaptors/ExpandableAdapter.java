package ir.ham3da.darya.adaptors;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.os.Handler;
import android.util.Log;

import android.view.View;
import android.view.ViewGroup;

import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.ImageView;
import android.widget.TextView;

import ir.ham3da.darya.R;

/**
 * Created by dbhat on 15-03-2016.
 */

public class ExpandableAdapter extends BaseExpandableListAdapter {


    private LayoutInflater layoutInflater;
    private LinkedHashMap<ExpandRPItem, ArrayList<ExpandRPItem>> groupList;
    private ArrayList<ExpandRPItem> mainGroup;
    private int[] groupStatus;
    private ExpandableListView listView;

    public ExpandableAdapter(Context context, ExpandableListView listView, LinkedHashMap<ExpandRPItem, ArrayList<ExpandRPItem>> groupsList) {
        layoutInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        this.groupList = groupsList;
        groupStatus = new int[groupsList.size()];

        listView.setOnGroupExpandListener(new OnGroupExpandListener() {

            public void onGroupExpand(int groupPosition) {
                ExpandRPItem group = mainGroup.get(groupPosition);
                if (groupList.get(group).size() > 0)
                    groupStatus[groupPosition] = 1;

            }
        });

        listView.setOnGroupCollapseListener(new OnGroupCollapseListener() {

            public void onGroupCollapse(int groupPosition) {
                ExpandRPItem group = mainGroup.get(groupPosition);
                if (groupList.get(group).size() > 0)
                    groupStatus[groupPosition] = 0;

            }
        });

        mainGroup = new ArrayList<ExpandRPItem>();
        for (Map.Entry<ExpandRPItem, ArrayList<ExpandRPItem>> mapEntry : groupList.entrySet()) {
            mainGroup.add(mapEntry.getKey());
        }
    }

    public ExpandRPItem getChild(int groupPosition, int childPosition) {
        // TODO Auto-generated method stub
        ExpandRPItem item = mainGroup.get(groupPosition);
        return groupList.get(item).get(childPosition);

    }

    public long getChildId(int groupPosition, int childPosition) {
        // TODO Auto-generated method stub
        return 0;
    }

    public View getChildView(final int groupPosition, int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub

        final ChildHolder holder;

        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.group_item, null);
            holder = new ChildHolder();
            holder.cb = (CheckBox) convertView.findViewById(R.id.cb);
            holder.title = (TextView) convertView.findViewById(R.id.title);
            convertView.setTag(holder);
        } else {
            holder = (ChildHolder) convertView.getTag();
        }


        final ExpandRPItem child = getChild(groupPosition, childPosition);

        holder.cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                ExpandRPItem parentGroup = getGroup(groupPosition);
                child.isChecked = isChecked;

                //if the CHILD is checked
                //TODO: Here add/remove from list

                if (isChecked) {
                    ArrayList<ExpandRPItem> childList = getChild(parentGroup);
                    int childIndex = childList.indexOf(child);
                    boolean isAllChildClicked = true;
                    for (int i = 0; i < childList.size(); i++) {
                        if (i != childIndex) {
                            ExpandRPItem siblings = childList.get(i);
                            if (!siblings.isChecked) {
                                isAllChildClicked = false;

                                DataHolder.checkedChilds.put(child.cat_id, parentGroup.cat_id);

                                break;
                            }
                        }
                    }

                    //All the children are checked
                    if (isAllChildClicked) {
                        Log.i("All should be checked", "Each child is Clicked!!");
                        parentGroup.isChecked = true;

                        if (!(DataHolder.checkedChilds.containsKey(child.cat_id))) {
                            DataHolder.checkedChilds.put(child.cat_id, parentGroup.cat_id);
                        }
                        checkAll = false;
                    }
                }
                //not all of the children are checked
                else {
                    if (parentGroup.isChecked) {
                        parentGroup.isChecked = false;
                        checkAll = false;
                        DataHolder.checkedChilds.remove(child.cat_id);
                    } else {
                        checkAll = true;
                        DataHolder.checkedChilds.remove(child.cat_id);
                    }
                    // child.isChecked =false;
                }
                notifyDataSetChanged();
            }
        });
        holder.cb.setChecked(child.isChecked);
        holder.title.setText(child.name);
        Log.i("childrens", DataHolder.checkedChilds.toString());
        return convertView;
    }

    public int getChildrenCount(int groupPosition) {
        // TODO Auto-generated method stub
        ExpandRPItem item = mainGroup.get(groupPosition);
        return groupList.get(item).size();
    }

    public ExpandRPItem getGroup(int groupPosition) {
        // TODO Auto-generated method stub
        return mainGroup.get(groupPosition);
    }

    public int getGroupCount() {
        // TODO Auto-generated method stub
        return mainGroup.size();
    }

    public long getGroupId(int groupPosition) {
        // TODO Auto-generated method stub
        return 0;
    }
    //works with the GroupView

    public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        final GroupHolder holder;

        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.group_list, null);
            holder = new GroupHolder();
            holder.cb = (CheckBox) convertView.findViewById(R.id.cb);
            holder.imageView = (ImageView) convertView.findViewById(R.id.label_indicator);
            holder.title = (TextView) convertView.findViewById(R.id.title);
            convertView.setTag(holder);
        } else {
            holder = (GroupHolder) convertView.getTag();
        }

        holder.imageView.setImageResource(groupStatus[groupPosition] == 0 ? R.drawable.ic_arrow_drop_down_gray_24dp : R.drawable.ic_arrow_drop_up_gray_24dp);
        final ExpandRPItem groupItem = getGroup(groupPosition);

        holder.title.setText(groupItem.name);

        holder.cb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckBox cb2 = (CheckBox) view;
                if (checkAll) {

                    ArrayList<ExpandRPItem> childItem = getChild(groupItem);
                    for (ExpandRPItem children : childItem) {
                        children.isChecked = cb2.isChecked();
                    }
                }
                groupItem.isChecked = cb2.isChecked();

                notifyDataSetChanged();

                new Handler().postDelayed(new Runnable() {

                    public void run() {
                        if (!checkAll)
                            checkAll = true;
                    }
                }, 50);

            }
        });


        holder.cb.setChecked(groupItem.isChecked);
        return convertView;
    }

    private boolean checkAll = true;

    private ArrayList<ExpandRPItem> getChild(ExpandRPItem group) {
        return groupList.get(group);
    }

    public boolean hasStableIds() {
        // TODO Auto-generated method stub
        return true;
    }

    public boolean isChildSelectable(int groupPosition, int childPosition) {
        // TODO Auto-generated method stub
        return true;
    }

    /**
     * get Selected Category Ids
     *
     * @return List<Integer>
     */
    public String getSelectedPoets() {

        String poets = "";
        List<Integer> Selected_poets = new ArrayList<>();

        for (int i = 0; i < this.mainGroup.size(); i++) {
            ArrayList<ExpandRPItem> ListItems;
            if (mainGroup.get(i).isChecked) {
                Selected_poets.add(mainGroup.get(i).poet_id);
            }
        }

        if (Selected_poets.size() > 0) {
            poets = TextUtils.join(",", Selected_poets);
        }
        return poets;
    }

    /**
     * get Selected Category Ids
     *
     * @return List<Integer>
     */
    public String getSelectedCategories() {


        String cats = "";
        List<Integer> Selected_cat = new ArrayList<>();

        for (int i = 0; i < this.mainGroup.size(); i++) {
            ArrayList<ExpandRPItem> ListItems;
            //item.isChecked = true;
            // item.parent_id = this.mainGroup.get(i).cat_id;

            ListItems = getChild(mainGroup.get(i));
            for (ExpandRPItem item : ListItems) {

                if (item.isChecked) {
                    Selected_cat.add(item.cat_id);
                }

            }

        }
        if (Selected_cat.size() > 0) {
            cats = TextUtils.join(",", Selected_cat);
        }
        return cats;
    }

    private class GroupHolder {
        public ImageView imageView;
        public CheckBox cb;
        public TextView title;

    }

    private class ChildHolder {
        public TextView title;
        public CheckBox cb;
    }
}