package com.example.national_gallery_ca;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.shape.Rectangle;

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

}