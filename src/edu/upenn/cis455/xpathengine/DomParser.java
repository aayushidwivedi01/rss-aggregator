package edu.upenn.cis455.xpathengine;

/**
 * Created by Aayushi on 10/14/2015.
 */

import javax.lang.model.element.Element;
import javax.lang.model.element.Name;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


import java.io.File;
import java.util.ArrayList;
import java.util.Map;

public class DomParser {
    NodeList allChildren = null;

    public boolean parse(ArrayList<PathNode>query, Node currrNode){

//            for (Node child = doc.getFirstChild(); child != null;
//                 child = child.getNextSibling()) {
//                System.out.println(child.getTextContent());
//            }
//            System.out.println(doc.getDocumentElement());
            boolean start = true;
            for (PathNode pn : query) {

                if (start){
                    if (!pn.nodeName.equals(currrNode.getNodeName())){
                        return false;
                    }
                    if (pn.filters.size() == 0){
                        System.out.println("Start:" + currrNode.getNodeName());
                        allChildren = currrNode.getChildNodes();
                        start = false;
                        continue;
                    }
                    else{
                        for (String filter : pn.filters){
                            if (filter.matches("contains")){
                                //check if current node has this text
                            }
                            else if (filter.matches("@")){
                                //check if current node has this attributename

                            }
                            else if (filter.matches("text()")){
                                //check if current node has this text

                            }
                           else{
                                //filter is an xpath
                                XPathValidity xpathValid = new XPathValidity();
                                filter = currrNode + "/" + filter;
                                xpathValid.step(filter);

                                DomParser filterParser = new DomParser();
                                if (filterParser.parse(xpathValid.allNodes, currrNode)){
                                    continue;
                                }
                                else {
                                    return false;
                                }
                            }
                        }
                    }
                }

                else{
                    boolean found = false;
                    System.out.println("All children: " +allChildren.getLength());
                    for (int x = 0; x < allChildren.getLength() ; x++){
                        System.out.println("Child  " + x + ":" + allChildren.item(x));
                    }
                    for (int j = 0; j< allChildren.getLength() ; j++){
                        if (pn.nodeName.equals(allChildren.item(j).getNodeName())){

                            // check for filters
                            if (pn.filters.size() == 0){
                                currrNode = allChildren.item(j);
                                System.out.println("New curr node:" + currrNode.getNodeName());
                                allChildren = currrNode.getChildNodes();
                                found = true;
                                break;
                            }
                            else {
                                ArrayList<String> filters = pn.filters;
                                for (int f = 0; f < filters.size() ; f++){
                                    String filter = filters.get(f);

                                    if (filter.matches("contains\\s*\\(\\s*text\\s*\\(\\)\\s*,\\s*\\\".*?\\\"\\s*\\)\\s*")){
                                        String elementText = allChildren.item(j).getTextContent();
                                        int indxO = filter.indexOf("\"");
                                        int indxC = filter.lastIndexOf("\"");
                                        String queryText  = filter.substring(indxO + 1 , indxC);
                                        if (elementText.contains(queryText)){
                                            found = true;

                                            if (f == filters.size()-1){
                                                currrNode = allChildren.item(j);
                                                allChildren = currrNode.getChildNodes();
                                                System.out.println("Found all filters" );

                                                System.out.println("Final curr node:" + currrNode);
                                                break;
                                            }
                                        }



                                    }
                                    else if (filter.matches("@\\s*[a-zA-Z0-9]*\\s*=\\s*\\\"\\s*[a-zA-Z0-9]*\\s*\\\"\\s*"
                                    )){
                                        //check if current node has this attributename
                                        NamedNodeMap attMap= allChildren.item(j).getAttributes();
                                        int indxA = filter.indexOf("@");
                                        int indxE = filter.indexOf("=");
                                        String attrName = filter.substring(indxA + 1, indxE).trim();
                                        Node nodeAttr = attMap.getNamedItem(attrName);
                                        if (nodeAttr != null && attrName.equals(nodeAttr.getNodeName())){
                                            int indxO = filter.indexOf("\"");
                                            int indxC = filter.lastIndexOf("\"");
                                            String attrValue = filter.substring(indxO + 1, indxC);
                                            if (attrValue.equals(nodeAttr.getNodeValue()))
                                                found = true;


                                            //check if all filters have been checked
                                            if (f == filters.size()-1){
                                                currrNode = allChildren.item(j);
                                                allChildren = currrNode.getChildNodes();
                                                System.out.println("Found all filters" );

                                                System.out.println("Final curr node:" + currrNode);
                                                break;
                                            }

                                        }



                                    }
                                    else if (filter.matches("text\\s*\\(\\s*\\)\\s*=\\s*\\\"\\s*.*?\\s*\\\"\\s*")){
                                        //check if current node has this text
                                        String elementText = allChildren.item(j).getTextContent();
                                        int indxO = filter.indexOf("\"");
                                        int indxC = filter.lastIndexOf("\"");
                                        String queryText  = filter.substring(indxO + 1 , indxC);
                                        if (elementText.equals(queryText)){
                                            found = true;

                                            if (f == filters.size()-1){
                                                currrNode = allChildren.item(j);
                                                allChildren = currrNode.getChildNodes();
                                                System.out.println("Found all filters" );

                                                System.out.println("Final curr node:" + currrNode);
                                                break;
                                            }
                                        }

                                    }
                                    else{
                                        //filter is an xpath
                                        System.out.println("FILTER:" + filters.get(f) + "Curr " + allChildren.item(j));
                                        XPathValidity xpathValid = new XPathValidity();
                                        filter = allChildren.item(j).getNodeName() + "/" + filter;
                                        xpathValid.step(filter);
                                        DomParser filterParser = new DomParser();
                                        if (filterParser.parse(xpathValid.allNodes, allChildren.item(j))){
                                            found = true;
                                            System.out.println("Found one filter");
                                            if (f == filters.size()-1){
                                                currrNode = allChildren.item(j);
                                                allChildren = currrNode.getChildNodes();
                                                System.out.println("Found all filters" );

                                                System.out.println("Final curr node:" + currrNode);
                                                break;
                                            }



                                        }
                                        else {
                                            found = false;
                                        }
                                        if (!found)
                                            return false;


                                    }
                                }

                            }
                        }
                    }
                    if (!found){
                        return false;
                    }
                    else
                        continue;


                }

            }

        return true;
    }
}

