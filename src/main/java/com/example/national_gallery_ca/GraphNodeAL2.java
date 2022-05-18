package com.example.national_gallery_ca;

import java.util.ArrayList;
import java.util.List;

public class GraphNodeAL2<T> {
    public T data;
    public int nodeValue=Integer.MAX_VALUE;
    public List<GraphLinkAL> adjList=new ArrayList<>(); //Could use any concrete List implementation
    public GraphNodeAL2(T data) {
        this.data=data;
    }
    public void connectToNodeDirected(GraphNodeAL2<T> destNode,int cost) {
        adjList.add(new GraphLinkAL(destNode,cost));
    }
    public void connectToNodeUndirected(GraphNodeAL2<T> destNode,int cost) {
        adjList.add(new GraphLinkAL(destNode,cost));
        destNode.adjList.add(new GraphLinkAL(this,cost));
    }
}