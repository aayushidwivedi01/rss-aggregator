package edu.upenn.cis455.xpathengine;

/**
 * Implements DOM parser
 *  */
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

    /*
     * this method takes as input an XPath query and a Node
     * @query:an ArrayList of type PathNode containig XPath query
     * @currnode is the current parent node
     */
    public boolean parse(ArrayList<PathNode>query, Node currrNode){

        if (query == null || query.isEmpty())
            return false;

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
                    boolean foundAll = false;
                    ArrayList<String> filters = pn.filters;
                    for (int f = 0; f < filters.size() ; f++){
                        String filter = filters.get(f);
                        if (filter.matches("contains\\s*\\(\\s*text\\s*\\(\\)\\s*,\\s*\\\".*?\\\"\\s*\\)\\s*")){
                            String elementText = currrNode.getTextContent();
                            int indxO = filter.indexOf("\"");
                            int indxC = filter.lastIndexOf("\"");
                            String queryText  = filter.substring(indxO + 1 , indxC);
                            if (elementText.contains(queryText)){
                                foundAll = true;

                            }
                            else
                                return false;



                        }
                        else if (filter.matches("@\\s*[a-zA-Z0-9]*\\s*=\\s*\\\"\\s*[a-zA-Z0-9]*\\s*\\\"\\s*"
                        )){
                            //check if current node has this attributename
                            NamedNodeMap attMap= currrNode.getAttributes();
                            int indxA = filter.indexOf("@");
                            int indxE = filter.indexOf("=");
                            String attrName = filter.substring(indxA + 1, indxE).trim();
                            Node nodeAttr = attMap.getNamedItem(attrName);
                            if (nodeAttr != null && attrName.equals(nodeAttr.getNodeName())){
                                int indxO = filter.indexOf("\"");
                                int indxC = filter.lastIndexOf("\"");
                                String attrValue = filter.substring(indxO + 1, indxC);
                                if (attrValue.equals(nodeAttr.getNodeValue())){
                                    foundAll = true;

                                }
                                else
                                    return false;


                            }
                            else
                                return false;



                        }
                        else if (filter.matches("text\\s*\\(\\s*\\)\\s*=\\s*\\\"\\s*.*?\\s*\\\"\\s*")){
                            //check if current node has this text
                            String elementText = currrNode.getTextContent();
                            int indxO = filter.indexOf("\"");
                            int indxC = filter.lastIndexOf("\"");
                            String queryText  = filter.substring(indxO + 1 , indxC);
                            if (elementText.equals(queryText)){
                                foundAll= true;
                            }
                            else
                                return false;

                        }
                        else{
                            //filter is an xpath

                            filter = currrNode.getNodeName() + "/" + filter;
                            XPathValidity xpathValid = new XPathValidity(filter);
                            xpathValid.step(filter);

                            DomParser filterParser = new DomParser();
                            if (filterParser.parse(xpathValid.allNodes, currrNode)){
                                foundAll = true;
                                continue;
                            }
                            else {
                                return false;
                            }
                        }
                    }
                    if (foundAll){
                        allChildren = currrNode.getChildNodes();
                        start = false;
                        continue;
                    }
                    else{
                        return false;
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

                                    filter = allChildren.item(j).getNodeName() + "/" + filter;
                                    XPathValidity  xpathValid = new XPathValidity(filter);
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
