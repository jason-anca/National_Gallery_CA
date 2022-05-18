package com.example.national_gallery_ca;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.shape.Rectangle;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class DefaultViewController {
    static Image NGMap;
    @FXML
    private Button exitButton;
    @FXML
    private ImageView displayNGMap;
    @FXML
    private Rectangle ivBackground;

    @FXML
    private void initialize(){
    NGMap = new Image("ngmap.png", displayNGMap.getFitWidth(), displayNGMap.getFitHeight(), true, true);
    displayNGMap.setImage(NGMap);
    ivBackground.setVisible(true);
    mouseListener();
    }

    @FXML
    private void buttonExit(ActionEvent actionEvent){
        System.exit(0);
    }

    private void mouseListener(){
        displayNGMap.setOnMouseClicked(e -> {
            System.out.println("["+(int)e.getX()+", "+(int)e.getY()+"]");
        });
    }

    private void readInDatabase() {
        String line = "";
        try {
            File file = new File("rooms.csv");
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            while ((line = bufferedReader.readLine()) != null){
                String[] vals = line.split(",");
                
                Room room = new Room(vals[0],Integer.parseInt(vals[1]),Integer.parseInt(vals[2]));
            }
        } catch (IOException ioException){
            ioException.printStackTrace();
        }
    }
}