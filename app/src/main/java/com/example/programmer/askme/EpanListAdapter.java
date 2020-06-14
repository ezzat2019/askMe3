package com.example.programmer.askme;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EpanListAdapter extends BaseExpandableListAdapter {
    Context context;
    private List<String> listItem;
    private HashMap<String, List<String>> rootList;
    public EpanListAdapter(Context  context,List<String> expandableListTitle,HashMap<String, List<String>> expandableListDetail)
    {this.context=context;
   listItem=expandableListTitle;
   rootList=expandableListDetail;

    }

    @Override
    public int getGroupCount() {
        return listItem.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return rootList.get(listItem.get(i)).size();
    }

    @Override
    public Object getGroup(int i) {
        return listItem.get(i);
    }

    @Override
    public Object getChild(int i, int i1) {
        return rootList.get(listItem.get(i)).get(i1);
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
       String groupText= (String) getGroup(i);
       if(view==null)
       {
           LayoutInflater layoutInflater = (LayoutInflater) this.context.
                   getSystemService(Context.LAYOUT_INFLATER_SERVICE);
           view = layoutInflater.inflate(R.layout.list_group, null);
       }
       TextView view1=view.findViewById(R.id.listTitle);
       view1.setText(groupText);
       view1.setTypeface(null,Typeface.BOLD);
       return view;
    }

    @Override
    public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
        String listItemText= (String) getChild(i,i1);
        if(view==null)
        {
            LayoutInflater inflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view=inflater.inflate(R.layout.list_item,null);

        }
        TextView txt=view.findViewById(R.id.expandedListItem);
        txt.setText(listItemText);
        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }
}
