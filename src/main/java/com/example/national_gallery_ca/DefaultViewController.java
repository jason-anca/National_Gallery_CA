package com.example.national_gallery_ca;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class DefaultViewController {
    static Image NGMap;
    public HashMap<String, GraphNodeAL<Room>> roomMap;

    private List<GraphNodeAL<Room>> roomNodes;
    @FXML
    private ImageView displayNGMap;
    @FXML
    private Rectangle ivBackground;
    @FXML
    private void initialize(){
    this.roomNodes = new ArrayList<>();
    this.roomMap = new HashMap<>();

    NGMap = new Image("ngmap.png", displayNGMap.getFitWidth(), displayNGMap.getFitHeight(), true, true);
    displayNGMap.setImage(NGMap);
    ivBackground.setVisible(true);
    mouseListener();
    readInDatabase();
    readInConnections();
    }


    private void mouseListener(){
        displayNGMap.setOnMouseClicked(e -> {
            System.out.println("["+(int)e.getX()+", "+(int)e.getY()+"]");
        });
    }
    //Makes a list of graph nodes of type room


    //Reads in rooms csv file and creates room nodes with names and coords
    //Adds room names into hash map
    private void readInDatabase() {
        String line = "";
        try {
            File file = new File("src/main/resources/rooms.csv");
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            while ((line = bufferedReader.readLine()) != null){
                String[] vals = line.split(",");
                Room room = new Room(vals[0],Integer.parseInt(vals[1]),Integer.parseInt(vals[2]));
                GraphNodeAL<Room> node = new GraphNodeAL<>(room);
                roomNodes.add(node);
                roomMap.put(vals[0], node);

                System.out.println(vals[0] + " [" + vals[1] + "] [" + vals[2] + "]");
            }
        } catch (IOException ioException){
            ioException.printStackTrace();
        }
    }


        public void connectNodes(String node1, String node2) {
            GraphNodeAL<Room> roomA = roomMap.get(node1);
            GraphNodeAL<Room> roomB = roomMap.get(node2);
            roomB.connectToNodeDirected(roomA,1);
        }

    private void readInConnections() {
        String line = "";
        try {
            File file = new File("src/main/resources/room connections 1.csv");
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            while ((line = bufferedReader.readLine()) != null){
                String[] vals = line.split(",");
                connectNodes(vals[0],vals[1]);
                System.out.println(vals[0] + " " + vals[1]);
            }
        } catch (IOException ioException){
            ioException.printStackTrace();
        }
    }



    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static <T> CostedPath findCheapestPathDijkstra(GraphNodeAL<?> startNode, T lookingfor) {
        CostedPath cp = new CostedPath(); //Create result object for cheapest path
        List<GraphNodeAL<?>> encountered = new ArrayList<>(), unencountered = new ArrayList<>(); //Create encountered/unencountered lists
        startNode.nodeValue = 0; //Set the starting node value to zero
        unencountered.add(startNode); //Add the start node as the only value in the unencountered list to start
        GraphNodeAL<?> currentNode;


        do { //Loop until unencountered list is empty
            currentNode = unencountered.remove(0); //Get the first unencountered node (sorted list, so will have lowest value)
            encountered.add(currentNode); //Record current node in encountered list
            if (currentNode.data.equals(lookingfor)) { //Found goal - assemble path list back to start and return it
                cp.pathList.add(currentNode); //Add the current (goal) node to the result list (only element)
                cp.pathCost = currentNode.nodeValue; //The total cheapest path cost is the node value of the current/goal node
                while (currentNode != startNode) { //While we're not back to the start node...
                    boolean foundPrevPathNode = false; //Use a flag to identify when the previous path node is identified
                    for (GraphNodeAL<?> n : encountered) { //For each node in the encountered list...
                        for (GraphLinkAL e : n.adjList) //For each edge from that node...
                            if (e.destNode == currentNode && currentNode.nodeValue - e.cost == n.nodeValue) { //If that edge links to the
//current node and the difference in node values is the cost of the edge -> found path node!
                                cp.pathList.add(0, n); //Add the identified path node to the front of the result list
                                currentNode = n; //Move the currentNode reference back to the identified path node
                                foundPrevPathNode = true; //Set the flag to break the outer loop
                                break; //We've found the correct previous path node and moved the currentNode reference
//back to it so break the inner loop
                            }
                        if (foundPrevPathNode)
                            break; //We've identified the previous path node, so break the inner loop to continue
                    }
                }
//Reset the node values for all nodes to (effectively) infinity so we can search again (leave no footprint!)
                for (GraphNodeAL<?> n : encountered) n.nodeValue = Integer.MAX_VALUE;
                for (GraphNodeAL<?> n : unencountered) n.nodeValue = Integer.MAX_VALUE;
                return cp; //The costed (cheapest) path has been assembled, so return it!
            }
//We're not at the goal node yet, so...
            for (GraphLinkAL e : currentNode.adjList) //For each edge/link from the current node...
                if (!encountered.contains(e.destNode)) { //If the node it leads to has not yet been encountered (i.e. processed)
                    e.destNode.nodeValue = Integer.min(e.destNode.nodeValue, currentNode.nodeValue + e.cost); //Update the node value at the end
                    //of the edge to the minimum of its current value or the total of the current node's value plus the cost of the edge
                    unencountered.add(e.destNode);
                }
            Collections.sort(unencountered, (n1, n2) -> n1.nodeValue - n2.nodeValue); //Sort in ascending node value order
        } while (!unencountered.isEmpty());
        return null; //No path found, so return null
    }

    ////////////////////////////////////////////////////////////////////////////

}