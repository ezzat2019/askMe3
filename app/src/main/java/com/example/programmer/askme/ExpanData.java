package com.example.programmer.askme;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExpanData {
    public static HashMap<String,List<String>> getData(){

        HashMap<String,List<String>>myList=new HashMap<String,List<String>>();

        List<String> list=new ArrayList<>();
        list.add("Question");
        list.add("Request");
        list.add("Query");
        myList.put("Choose the type of question",list);

        List<String> list2=new ArrayList<>();
        list2.add("Tecnolgy");
        list2.add("Healthy");
        list2.add("Knewlodge");
        list2.add("Economy");
        list2.add("Police");


        myList.put("Select Enquiry Category",list2);
        return myList;

    }
}
