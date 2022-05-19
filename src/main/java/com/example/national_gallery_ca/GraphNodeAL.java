package com.example.national_gallery_ca;

import java.util.ArrayList;
import java.util.List;

public class GraphNodeAL<T> {

    public T data;
    public int nodeValue= Integer.MAX_VALUE;

    public List<GraphLinkAL> adjList = new ArrayList<>(); //Adjacency list now contains link objects = key!
    //Could use any concrete List implementation
    public GraphNodeAL(T data) {
        this.data=data;
    }

    public void connectToNodeDirected(GraphNodeAL<T> destNode,int cost) {
        adjList.add( new GraphLinkAL(destNode,cost) ); //Add new link object to source adjacency list
    }
    public void connectToNodeUndirected(GraphNodeAL<T> destNode,int cost) {
        adjList.add( new GraphLinkAL(destNode,cost) ); //Add new link object to source adjacency list
        destNode.adjList.add(new GraphLinkAL(this,cost) ); //Add new link object to destination adjacency list
    }
}
