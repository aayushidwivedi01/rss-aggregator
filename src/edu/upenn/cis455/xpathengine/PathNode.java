package edu.upenn.cis455.xpathengine;

import java.util.ArrayList;

public class PathNode {

    String nodeName = null;
    ArrayList<String>filters = new ArrayList<>();

    public void setNodeName(String name){
        nodeName = name;
    }

    public void addFilter(String filter){
        filters.add(filter);
    }
}
