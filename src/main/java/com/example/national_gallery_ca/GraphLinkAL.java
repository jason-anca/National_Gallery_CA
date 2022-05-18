package com.example.national_gallery_ca;

import java.util.ArrayList;
import java.util.List;

public class GraphLinkAL {
    public GraphNodeAL2<?> destNode; //Could also store source node if required
    public int cost; //Other link attributes could be similarly stored
    public GraphLinkAL(GraphNodeAL2<?> destNode,int cost) {
        this.destNode=destNode;
        this.cost=cost;
    }
}
