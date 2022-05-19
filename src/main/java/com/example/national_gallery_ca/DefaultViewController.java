package com.example.national_gallery_ca;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class DefaultViewController {
    private Color color = Color.CYAN;
    static Image NGMap;
    public HashMap<String, GraphNodeAL<Room>> roomMap;
    public List<String> roomNames;

    private List<GraphNodeAL<Room>> roomNodes;
    @FXML
    private ImageView displayNGMap;
    @FXML
    private Rectangle ivBackground;
    @FXML
    ComboBox<String> start,finish,avoid;
    @FXML
    public AnchorPane aPane;

    @FXML
    private void initialize(){

        //data types
    this.roomNodes = new ArrayList<>();
    this.roomMap = new HashMap<>();
    this.roomNames = new LinkedList<>();


    NGMap = new Image("ngmap.png", displayNGMap.getFitWidth(), displayNGMap.getFitHeight(), true, true);
    displayNGMap.setImage(NGMap);
    ivBackground.setVisible(true);

    //database code
    mouseListener();
    readInDatabase();
    readInConnections();

        //combobox
        start.getItems().addAll(roomNames);
        finish.getItems().addAll(roomNames);
        avoid.getItems().addAll(roomNames);

        //System.out.println(roomMap.keySet());
    }

    //Used for finding the x and y coords for each room

    private void mouseListener(){
        displayNGMap.setOnMouseClicked(e -> {
            System.out.println("["+(int)e.getX()+", "+(int)e.getY()+"]");
        });
    }


    //Reads in the CSV file containing Room name and it's coordinates
    //Splits each column in the csv file by using a comma(delimiter)
    //A room is made with the aforementioned information and using that, a graph node is made.
    //The node is then added to a hashmap of type String, GraphNode<Room>
    //Key is the room name and values are the room name and node
    //Using try catch mostly for debugging
    private void readInDatabase() {
        String line = "";
        try {
            File file = new File("src/main/resources/rooms.csv");
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            while ((line = bufferedReader.readLine()) != null){
                String[] vals = line.split(",");
                Room room = new Room(vals[1],Integer.parseInt(vals[2]),Integer.parseInt(vals[3]));
                GraphNodeAL<Room> node = new GraphNodeAL<>(room);
                roomNodes.add(node);
                roomMap.put(vals[1], node);
                roomNames.add(vals[1]);

                //System.out.println(vals[0] + " [" + vals[1] + "] [" + vals[2] + "]");
            }
        } catch (IOException ioException){
            ioException.printStackTrace();
        }
    }


    //Connects a node to another node and assigning a cost of 1
        public void connectNodes(String node1, String node2){
            //System.out.println(node1 + ", "+ node2);
        try {
            GraphNodeAL<Room> roomA = roomMap.get(node1);
            GraphNodeAL<Room> roomB = roomMap.get(node2);
///
            roomA.connectToNodeUndirected(roomB, 1);
        }catch (Exception e){
            System.err.println(node1);
            System.err.println(e);
        }
        }

    //Reads in the connections between rooms
    //Comma is delimiter again
    //The connections listed in the CSV file are then used in the connectNodes method
    //Try Catch is again used for debugging purposes
    private void readInConnections() {
        String line = "";
        try {
            File file = new File("src/main/resources/RoomConnection.csv");
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            while ((line = bufferedReader.readLine()) != null){
                String[] vals = line.split(",");
                //System.out.println(vals[1] + ", " + vals[2]);
                connectNodes(vals[1],vals[2]);

            }
        } catch (IOException ioException){
            ioException.printStackTrace();
        }
    }

    //Hopefully removes any instance of lines in order to remap the routes
    //Loops through all the paths between nodes, the current and next one
    //Gets the edges between nodes and creates a line between the current node and next node
    //Sets lines stroke color and width and then displays them on the image
    public void drawLine(List<GraphNodeAL<?>> paths, Color c){
        displayNGMap.setImage(NGMap);
        aPane.getChildren().clear();

        for(int i = 0; i < paths.size(); i++ ){
            GraphNodeAL<Room> roomNode = (GraphNodeAL<Room>) paths.get(i);
            if(i + 1 < paths.size()){
                GraphNodeAL<Room> nextNode = (GraphNodeAL<Room>) paths.get(i+1);
                Line line = new Line(roomNode.data.xCoord,roomNode.data.yCoord,nextNode.data.xCoord,nextNode.data.yCoord);
                line.setFill(c);
                line.setStroke(c);
                line.setStrokeWidth(3);
                aPane.getChildren().add(line);
                //((Pane) displayNGMap.getParent()).getChildren().add(line);
            }
        }
    }

    @FXML
    public void clearMappings(ActionEvent actionEvent){
        aPane.getChildren().clear();
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static <T> CostedPath findCheapestPathDijkstra(GraphNodeAL<?> startNode, T lookingfor){
        CostedPath cp=new CostedPath(); //Create result object for cheapest path
        List<GraphNodeAL<?>> encountered=new ArrayList<>(), unencountered=new ArrayList<>(); //Create encountered/unencountered lists
        startNode.nodeValue=0; //Set the starting node value to zero
        unencountered.add(startNode); //Add the start node as the only value in the unencountered list to start
        GraphNodeAL<?> currentNode;
        do{ //Loop until unencountered list is empty
            currentNode=unencountered.remove(0); //Get the first unencountered node (sorted list, so will have lowest value)
            encountered.add(currentNode); //Record current node in encountered list
            if(currentNode.data.equals(lookingfor)){ //Found goal - assemble path list back to start and return it
                cp.pathList.add(currentNode); //Add the current (goal) node to the result list (only element)
                cp.pathCost=currentNode.nodeValue; //The total cheapest path cost is the node value of the current/goal node
                while(currentNode!=startNode) { //While we're not back to the start node...
                    boolean foundPrevPathNode=false; //Use a flag to identify when the previous path node is identified
                    for(GraphNodeAL<?> n : encountered) { //For each node in the encountered list...
                        for(GraphLinkAL e : n.adjList) //For each edge from that node...
                            if(e.destNode==currentNode && currentNode.nodeValue-e.cost==n.nodeValue){ //If that edge links to the
                                //current node and the difference in node values is the cost of the edge -> found path node!
                                cp.pathList.add(0,n); //Add the identified path node to the front of the result list
                                currentNode=n; //Move the currentNode reference back to the identified path node
                                foundPrevPathNode=true; //Set the flag to break the outer loop
                                break; //We've found the correct previous path node and moved the currentNode reference
                        //back to it so break the inner loop
                            }
                        if(foundPrevPathNode) break; //We've identified the previous path node, so break the inner loop to continue
                    }
                }
                //Reset the node values for all nodes to (effectively) infinity so we can search again (leave no footprint!)
                for(GraphNodeAL<?> n : encountered) n.nodeValue=Integer.MAX_VALUE;
                for(GraphNodeAL<?> n : unencountered) n.nodeValue=Integer.MAX_VALUE;
                System.out.println(cp.pathList.size());
                return cp; //The costed (cheapest) path has been assembled, so return it!
            }
            //We're not at the goal node yet, so...
            for(GraphLinkAL e : currentNode.adjList) //For each edge/link from the current node...
                if(!encountered.contains(e.destNode)) { //If the node it leads to has not yet been encountered (i.e. processed)
                    e.destNode.nodeValue=Integer.min(e.destNode.nodeValue, currentNode.nodeValue+e.cost); //Update the node value at the end
                    //of the edge to the minimum of its current value or the total of the current node's value plus the cost of the edge
                    unencountered.add(e.destNode);
                }
            Collections.sort(unencountered,(n1,n2)->n1.nodeValue-n2.nodeValue); //Sort in ascending node value order
        }while(!unencountered.isEmpty());
        return null; //No path found, so return null
    }

    public GraphNodeAL<Room> getNode(String roomName)
    {
        return roomMap.get(roomName);
    }

    public void dijkstras(ActionEvent actionEvent){
        List<GraphNodeAL<?>> pathList = new LinkedList<>();
        CostedPath cp = findCheapestPathDijkstra(getNode(start.getValue()), getNode(finish.getValue()).data);
        System.out.println(cp.pathList.size());
        pathList = cp.pathList;
        drawLine(pathList, color);
    }

    public static <T> List<GraphNodeAL<?>> findPathBreadthFirst(List<List<GraphNodeAL<?>>> agenda, List<GraphNodeAL<?>> encountered, T lookingfor){
        if(agenda.isEmpty()) return null; //Search failed
        List<GraphNodeAL<?>> nextPath=agenda.remove(0); //Get first item (next path to consider) off agenda
        GraphNodeAL<?> currentNode=nextPath.get(0); //The first item in the next path is the current node
        if(currentNode.data.equals(lookingfor)) return nextPath; //If that's the goal, we've found our path (so return it)
        if(encountered==null) encountered=new ArrayList<>(); //First node considered in search so create new (empty) encountered list
        encountered.add(currentNode); //Record current node as encountered so it isn't revisited again
        for(GraphLinkAL adjNode : currentNode.adjList) //For each adjacent node
            if(!encountered.contains(adjNode)) { //If it hasn't already been encountered
                List<GraphNodeAL<?>> newPath=new ArrayList<>(nextPath); //Create a new path list as a copy of the current/next path
                newPath.add(0,adjNode.destNode); //And add the adjacent node to the front of the new copy
                agenda.add(newPath); //Add the new path to the end of agenda (end->BFS!)
            }
        return findPathBreadthFirst(agenda,encountered,lookingfor); //Tail call
    }

//    public void BFS(ActionEvent actionEvent){
//        List<GraphNodeAL<?>> pathList = new LinkedList<>();
//        CostedPath cp = findPathBreadthFirst(getNode(start.getValue(), getNode(finish.getValue()).data));
//        pathList = cp.pathList;
//        drawLine(pathList, color);
//    }

    public void DFS(ActionEvent actionEvent){
        List<GraphNodeAL<?>> pathList = new LinkedList<>();
        CostedPath cp = searchGraphDepthFirstCheapestPath(getNode(start.getValue(),null,0, getNode(finish.getValue()).data));
        pathList = cp.pathList;
        drawLine(pathList, color);
    }

    //Retrieve cheapest path by expanding all paths recursively depth-first
    public static <T> CostedPath searchGraphDepthFirstCheapestPath(GraphNodeAL<?> from, List<GraphNodeAL<?>> encountered,int totalCost, T lookingfor){

        if(from.data.equals(lookingfor)){ //Found it - end of path
            CostedPath cp=new CostedPath(); //Create a new CostedPath object
            cp.pathList.add(from); //Add the current node to it - only (end of path) element
            cp.pathCost=totalCost; //Use the current total cost
            return cp; //Return the CostedPath object
        }
        if(encountered==null) encountered=new ArrayList<>(); //First node so create new (empty) encountered list
        encountered.add(from);
        List<CostedPath> allPaths=new ArrayList<>(); //Collection for all candidate costed paths from this node
        for(GraphLinkAL adjLink : from.adjList) //For every adjacent node
            if(!encountered.contains(adjLink.destNode)) //That has not yet been encountered
            {
//Create a new CostedPath from this node to the searched for item (if a valid path exists)
                CostedPath temp=searchGraphDepthFirstCheapestPath(adjLink.destNode,new ArrayList<>(encountered),
                        totalCost+adjLink.cost,lookingfor);
                if(temp==null) continue; //No path was found, so continue to the next iteration
                temp.pathList.add(0,from); //Add the current node to the front of the path list
                allPaths.add(temp); //Add the new candidate path to the list of all costed paths
            }
//If no paths were found then return null. Otherwise, return the cheapest path (i.e. the one with min pathCost)
        return allPaths.isEmpty() ? null : Collections.min(allPaths, (p1,p2)->p1.pathCost-p2.pathCost);
    }



    public static <T> List<GraphNodeAL<?>> findPathDepthFirst(GraphNodeAL<?> from, List<GraphNodeAL<?>> encountered, T lookingfor){
        List<GraphNodeAL<?>> result;
        if(from.data.equals(lookingfor)) { //Found it
            result=new ArrayList<>(); //Create new list to store the path info (any List implementation could be used)
            result.add(from); //Add the current node as the only/last entry in the path list
            return result; //Return the path list
        }
        if(encountered==null) encountered=new ArrayList<>(); //First node so create new (empty) encountered list
        encountered.add(from);
        for(GraphLinkAL adjNode : from.adjList)
            if(!encountered.contains(adjNode)) {
                result=findPathDepthFirst(adjNode.destNode,encountered,lookingfor);
                if(result!=null) { //Result of the last recursive call contains a path to the solution node
                    result.add(0,from); //Add the current node to the front of the path list
                    return result; //Return the path list
                }
            }
        return null;
    }


}