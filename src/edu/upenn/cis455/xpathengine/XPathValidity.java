package edu.upenn.cis455.xpathengine;

import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.Stack;

/**
 * Created by Aayushi on 10/13/2015.
 */
public class XPathValidity{
    String path;
    char axis;
    String token;
    int i = 0;
    boolean isGlobal = true;
    Stack<Character> state = new Stack<>();
    PathNode pn = null;
    ArrayList<PathNode> allNodes= new ArrayList<>();
    Boolean valid = true;
    public  XPathValidity(String path){
        this.path = path;
    }
    
    public  XPathValidity(){
        
    }

    public void xpath(){
        if (!(path.charAt(0) == '/')){
            valid = false;
            return;
        }

        axis = path.charAt(0);
        token = path.substring(1);
        axis();
        step(token);
        return;

    }

    public void axis(){
        if(axis == '/')
            return;
        else {
            valid = false;
            return;
        }

    }

    public void step(String token) {
        if (token.length() <= 0) {
            System.out.println("Step length zero" + axis + " " + token + " i:" + i);
            i++;

            return;
        }

        if (token.contains("[")){
            int indxOfAxis = token.indexOf('/');
            int indxOfBrac = token.indexOf('[');
            //if indxOfAxis > indxOfBrac test appears before axis or end of current node
            if (indxOfAxis > indxOfBrac || indxOfAxis == -1){
                //call test()

                Stack<Integer> bracIndx = new Stack<>();
                int ind = token.indexOf("[");
                int i = ind+1;
                bracIndx.push(ind);
                if (isGlobal){
                    pn = new PathNode();
                    pn.setNodeName(token.substring(0, ind).trim());
                }

                while (!bracIndx.isEmpty()){
                    if(token.charAt(i) == '['){
                        bracIndx.push(i);
                    }
                    else if(token.charAt(i) == ']'){
                        int temp = bracIndx.pop();
                        if (bracIndx.empty()){
                            if (isGlobal){
                                isGlobal = false;
                                pn.addFilter(token.substring(ind +1 ,i));
                                state.push('$');
                            }
                            else{
                                state.push('*');
                            }
                            test(token.substring(ind +1, i));
                            char currState = state.pop();
                            if (currState == '$'){
                                isGlobal = true;
                            }

                            i++;
                            if (i >= token.length()){
                                break;
                            }
                            if(token.charAt(i) == '['){
                                bracIndx.push(i);
                                ind = i;
                                i++;
                                if (i >= token.length()){
                                    break;
                                }
                                continue;
                            }
                            else
                                break;
                        }
                    }
                    i++;
                    if (i >= token.length()){
                        break;
                    }
                }

                if (bracIndx.isEmpty() && i >= token.length()){
                    //all filters processed for this node
                    if (isGlobal){
                        allNodes.add(pn);
                    }
                    return;
                }
                else if (bracIndx.isEmpty() && token.charAt(i) == '/'){
                    axis = token.charAt(i);
                    token = token.substring(i + 1);
                    if (!isGlobal)
                        state.push('*');
                    else
                        allNodes.add(pn);
                    axis();
                    step(token);
                    if (!isGlobal)
                        state.pop();
                }
                else if (!bracIndx.isEmpty() ||  bracIndx.isEmpty() && !token.matches("\\s*/\\s*[.*?]*") ){
                    valid = false;
                }
            }
            else {
                // filter is somewhere later in the xpath
                //current content becomes nodename
                String nodename = token.substring(0, indxOfAxis);
                if (isGlobal){
                    pn = new PathNode();
                    pn.setNodeName(nodename.trim());
                    allNodes.add(pn);
                }
                axis = token.charAt(indxOfAxis);
                token = token.substring(indxOfAxis + 1);
                if (!isGlobal)
                    state.push('*');
                axis();
                step(token);
                if (!isGlobal)
                    state.pop();
            }

        }
        else {
            //no filters in remaining xpath
            int indexOfAxis = token.indexOf("/");
            if (indexOfAxis != -1){
                //complete xpath not processed
                String nodename = token.substring(0, indexOfAxis);
                if (isGlobal){
                    pn = new PathNode();
                    pn.setNodeName(nodename.trim());
                    allNodes.add(pn);

                }
                axis = token.charAt(indexOfAxis);
                token = token.substring(indexOfAxis + 1);
                if (!isGlobal)
                    state.push('*');
                axis();
                step(token);
                if (!isGlobal)
                    state.pop();
            }
            else{
                //xpath processed
                String nodename = token;
                if (isGlobal){
                    pn = new PathNode();
                    pn.setNodeName(nodename.trim());
                    allNodes.add(pn);
                }
                return;
            }


        }
    }


    public void test(String subToken){
        if(subToken.matches("\\s*contains\\s*\\(\\s*text\\s*\\(\\)\\s*,\\s*\\\".*?\\\"\\s*\\)\\s*")){
            System.out.println("Found contains in test");
            return ;
        }
        else if(subToken.matches("\\s*text\\s*\\(\\s*\\)\\s*=\\s*\\\"\\s*.*?\\s*\\\"\\s*")){
            System.out.println("Found text in test");
            return;
        } else if (subToken.matches("\\s*@\\s*[a-zA-Z0-9]*\\s*=\\s*\\\"\\s*[a-zA-Z0-9]*\\s*\\\"\\s*") ){
            if (subToken.matches("^@[a-zA-Z0-9]=?(.*?)$")) {
                System.out.println("Found attname in test");
                return ;
            }
            valid = false;

        }
        else {

            //send it to step()
            //System.out.println("Subtoken:" + subToken + " Match: " + subToken.matches("[a-zA-Z0-9\\-]*") + "   token: " + token);
            if (subToken.matches("(\\s*[\\s*a-zA-Z0-9\\-\\s*]*\\[.*)|(\\s*[\\s*a-zA-Z0-9\\-\\s*]*\\s*)")){
                step(subToken);
            }
            else{
                valid = false;
            }




        }

    }

}
