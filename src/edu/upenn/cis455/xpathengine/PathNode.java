package edu.upenn.cis455.xpathengine;
/**
 * Used to store query nodes 
 * each nodename is a string 
 * nodename's filter is saved as an ArrayList of strings
 */
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
