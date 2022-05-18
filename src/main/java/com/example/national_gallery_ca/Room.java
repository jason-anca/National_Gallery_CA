package com.example.national_gallery_ca;

public class Room {
    String roomName;
    int xCoord;
    int yCoord;

    public Room(String roomName, int xCoord, int yCoord) {
        this.roomName = roomName;
        this.xCoord = xCoord;
        this.yCoord = yCoord;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public int getxCoord() {
        return xCoord;
    }

    public void setxCoord(int xCoord) {
        this.xCoord = xCoord;
    }

    public int getyCoord() {
        return yCoord;
    }

    public void setyCoord(int yCoord) {
        this.yCoord = yCoord;
    }


}
