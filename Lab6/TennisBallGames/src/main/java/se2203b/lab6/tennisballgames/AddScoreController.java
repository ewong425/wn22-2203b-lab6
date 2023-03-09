package se2203b.lab6.tennisballgames;

// import the required libraries
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;


public class AddScoreController implements Initializable {
    // Some @FXML declarations
    @FXML
    Button saveButton;
    @FXML
    Button cancelButton;

    @FXML
    ComboBox<String> matchComboBox;
    @FXML
    TextField homeScore;
    @FXML
    TextField visitorScore;

    // The data variable is used to populate the ComboBoxs
    final ObservableList<String> data = FXCollections.observableArrayList();
    // To reference the models inside the controller
    private MatchesAdapter matchesAdapter;
    private TeamsAdapter teamsAdapter;

    public void setModel(MatchesAdapter match, TeamsAdapter team) throws SQLException {
        matchesAdapter = match;
        teamsAdapter = team;
        buildComboBoxData();
    }

    @FXML
    public void cancel() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    public void save() {
        try {
            String[] p = matchComboBox.getValue().split("-");
            int matchNumber = Integer.parseInt(p[0]);
            String[] k = matchComboBox.getValue().split("-");

            matchesAdapter.setTeamsScore(matchNumber, Integer.parseInt(homeScore.getText()), Integer.parseInt(visitorScore.getText()));
            teamsAdapter.setStatus(k[1], k[2], Integer.parseInt(homeScore.getText()), Integer.parseInt(visitorScore.getText()));
        } catch (SQLException ex) {
            displayAlert("ERROR: " + ex.getMessage());
        }

        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    public void buildComboBoxData() throws SQLException {
        try {
            data.addAll(matchesAdapter.getMatchesNamesList());
        } catch (SQLException ex) {
            displayAlert("ERROR: " + ex.getMessage());
        }
    }

    private void displayAlert(String s) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Alert.fxml"));
            Parent ERROR = loader.load();
            AlertController controller = (AlertController) loader.getController();

            Scene scene = new Scene(ERROR);
            Stage stage = new Stage();
            stage.setScene(scene);

            stage.getIcons().add(new Image("file:src/main/resources/se2203b/lab6/tennisballgames/WesternLogo.png"));
            controller.setAlertText(s);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

        } catch (IOException ex1) {

        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        matchComboBox.setItems(data);
    }

}