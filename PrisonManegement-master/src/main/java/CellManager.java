import com.sun.net.httpserver.Authenticator;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.net.URL;
import java.sql.SQLException;
import java.util.*;
import java.util.List;

public class CellManager implements Initializable {

    DAO database = new DAO();

    @FXML
    private AnchorPane prison;
    public static int selectedCellNumber ;
    private Cell cell;

    private List<Cell> cells = new ArrayList<>();



    @FXML
    public void cellSelected(javafx.event.ActionEvent event){
        Object node = event.getSource();
        Button b = (Button) node;
        selectedCellNumber = Integer.parseInt(b.getText());
        System.out.println(selectedCellNumber);
        cell = cells.get(selectedCellNumber-1);
        if (cell.getCellMembers().isEmpty() || Math.abs(GUI.prisonerSecurityLevel - cell.getCellSecurityLevel()) <= 1 && cell.isThereFreeSlot()) {
            Alert alertwindow = new Alert(Alert.AlertType.INFORMATION);
            alertwindow.setTitle("Information");
            alertwindow.setContentText("Cell selected successfully");
            alertwindow.showAndWait();

            Stage stage = (Stage) prison.getScene().getWindow();
            stage.close();
        } else {
            Alert alertwindow = new Alert(Alert.AlertType.WARNING);
            alertwindow.setTitle("Warning");
            alertwindow.setContentText("Cell is not selectable!");
            alertwindow.showAndWait();
        }


    }

    private void addHoverEffect(){
        int cellCounter = 0;
        for (int i = 1; i < prison.getChildren().size(); i++) {
            GridPane currentblock = (GridPane) prison.getChildren().get(i);
            for (int j = 0; j < currentblock.getChildren().size(); j++){
                Node currentCell = currentblock.getChildren().get(j);
                cellCounter +=1;
                currentCell.hoverProperty().addListener(observable -> {
                    String originalStyle = "-fx-background-color: transparent";
                    if (currentCell.isHover()) {
                        originalStyle = currentCell.getStyle();
                        currentCell.setStyle("-fx-background-color: rgba(128,128,128,0.5)");
                    }
                    else
                        currentCell.setStyle(originalStyle);
                    });

                for (Cell c : cells){
                    if (c.getCellID() == cellCounter)
                        if (c.getCellMembers().isEmpty())
                            currentCell.setStyle("-fx-background-color: rgba(0,255,255,0.5)");
                        else if(GUI.prisonerSecurityLevel == c.getCellSecurityLevel() && c.isThereFreeSlot()){
                            currentCell.setStyle("-fx-background-color: rgba(50,205,50,0.5)");
                        } else if (Math.abs(GUI.prisonerSecurityLevel - c.getCellSecurityLevel()) <= 1 && c.isThereFreeSlot()) {
                            currentCell.setStyle("-fx-background-color: rgba(255,255,0,0.5)");

                        } else
                            currentCell.setStyle("-fx-background-color: rgba(255,0,0,0.5)");
                }
                System.out.println(cellCounter);

            }
        }
    }

    private void updateCells() throws SQLException {
        List<Prisoner> prisoners = database.getPrisonerList();
        int numberOfCells = 24;
        for (int i = 1; i <= numberOfCells; i++){
            Cell c = new Cell();
            c.setCellID(i);
            if (i < 18)
                c.setMaxCapacity(2);
            else
                c.setMaxCapacity(1);
            for (Prisoner p : prisoners)
                if (p.getCellNum() == i)
                    c.addPrisonerToCell(p);
            cells.add(c);
            }
    }




    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            updateCells();
            //for (Cell c : cells)System.out.println(c.getCellID());
            addHoverEffect();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


}
