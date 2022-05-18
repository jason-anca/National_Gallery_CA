package com.example.national_gallery_ca;

public class Node {
    private int data;
    private Node next;

    public Node(int d, Node nx){
        data = d;
        next = nx;
    }
    public int getData() {
        return data;
    }
    public Node getNext(){
        return next;
    }
    public void setData(int d){
        data = d;
    }

    public void setNext(Node n){
        next = n;
    }
}
