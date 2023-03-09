package se2203b.lab6.tennisballgames;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReferenceArray;

/**
 *
 * @author Abdelkader Ouda
 */
public class TeamsAdapter {

    Connection connection;

    public TeamsAdapter(Connection conn, Boolean reset) throws SQLException {
        connection = conn;
        if (reset) {
            Statement stmt = connection.createStatement();
            try {
                // Remove tables if database tables have been created.
                // This will throw an exception if the tables do not exist
                // We drop Matches first because it references the table Teams
                stmt.execute("DROP TABLE Matches");
                stmt.execute("DROP TABLE Teams");
                // then do finally
            } catch (SQLException ex) {
                // No need to report an error.
                // The table simply did not exist.
                // do finally to create it
            } finally {
                // Create the table of teams
                stmt.execute("CREATE TABLE Teams ("
                        + "TeamName CHAR(15) NOT NULL PRIMARY KEY, "
                        + "Wins INT, Losses INT, Ties INT)");
                populateSampls();
            }
        }
    }

    private void populateSampls() throws SQLException {
        // Add some teams
        this.insertTeam("Astros");
        this.insertTeam("Marlins");
        this.insertTeam("Brewers");
        this.insertTeam("Cubs");
    }

    public void insertTeam(String name) throws SQLException {
        Statement stmt = connection.createStatement();
        stmt.executeUpdate("INSERT INTO Teams (TeamName, Wins, Losses, Ties) VALUES ('" + name + "', 0, 0, 0)");
    }

    // Get all teams Data
    public ObservableList<Teams> getTeamsList() throws SQLException {
        ObservableList<Teams> list = FXCollections.observableArrayList();
        ResultSet rs;

        // Create a Statement object
        Statement stmt = connection.createStatement();

        // Create a string with a SELECT statement
        String sqlStatement = "SELECT * FROM Teams";

        // Execute the statement and return the result
        rs = stmt.executeQuery(sqlStatement);

        while (rs.next()) {
            list.add(new Teams(rs.getString("TeamName"),
                    rs.getInt("Wins"),
                    rs.getInt("Losses"),
                    rs.getInt("Ties")));
        }
        return list;
    }

    // Get all teams names to populate the ComboBoxs used in Task #3.
    public ObservableList<String> getTeamsNames() throws SQLException {
        ObservableList<String> list = FXCollections.observableArrayList();
        ResultSet rs;

        // Create a Statement object
        Statement stmt = connection.createStatement();

        // Create a string with a SELECT statement
        String sqlStatement = "SELECT * FROM Teams";

        // Execute the statement and return the result
        rs = stmt.executeQuery(sqlStatement);
        
        // loop for the all rs rows and update list
        while(rs.next()) {
            list.add(rs.getString("TeamName"));
        }
        return list;
    }

    public void setStatus(String hTeam, String vTeam, int hScore, int vScore) throws SQLException {
        // Create PreparedStatement objects
        String updateWinStatement = "UPDATE Teams SET Wins = ? WHERE TeamName = ?";
        String updateLossStatement = "UPDATE Teams SET Losses = ? WHERE TeamName = ?";
        String updateTieStatement = "UPDATE Teams SET Ties = ? WHERE TeamName = ?";
        PreparedStatement updateWinStmt = connection.prepareStatement(updateWinStatement);
        PreparedStatement updateLossStmt = connection.prepareStatement(updateLossStatement);
        PreparedStatement updateTieStmt = connection.prepareStatement(updateTieStatement);
        int hWins = 0, hLosses = 0, vWins = 0, vLosses = 0, hTies = 0, vTies = 0;
        if (hScore > vScore) {
            // Update home team's wins
            PreparedStatement selectHWinStmt = connection.prepareStatement("SELECT Wins, Losses FROM Teams WHERE TeamName = ?");
            selectHWinStmt.setString(1, hTeam);
            ResultSet rs = selectHWinStmt.executeQuery();
            if (rs.next()) {
                hWins = rs.getInt("Wins");
                updateWinStmt.setInt(1, hWins + 1);
                updateWinStmt.setString(2, hTeam);
                updateWinStmt.executeUpdate();
            }

            // Update away team's losses
            PreparedStatement selectVLossStmt = connection.prepareStatement("SELECT Wins, Losses FROM Teams WHERE TeamName = ?");
            selectVLossStmt.setString(1, vTeam);
            rs = selectVLossStmt.executeQuery();
            if (rs.next()) {
                vLosses = rs.getInt("Losses");
                updateLossStmt.setInt(1, vLosses + 1);
                updateLossStmt.setString(2, vTeam);
                updateLossStmt.executeUpdate();
            }
        } else if (hScore < vScore) {
            // Update home team's wins
            PreparedStatement selectHWinStmt = connection.prepareStatement("SELECT Wins, Losses FROM Teams WHERE TeamName = ?");
            selectHWinStmt.setString(1, vTeam);
            ResultSet rs = selectHWinStmt.executeQuery();
            if (rs.next()) {
                vWins = rs.getInt("Wins");
                updateWinStmt.setInt(1, vWins + 1);
                updateWinStmt.setString(2, vTeam);
                updateWinStmt.executeUpdate();
            }

            // Update away team's losses
            PreparedStatement selectVLossStmt = connection.prepareStatement("SELECT Wins, Losses FROM Teams WHERE TeamName = ?");
            selectVLossStmt.setString(1, hTeam);
            rs = selectVLossStmt.executeQuery();
            if (rs.next()) {
                hLosses = rs.getInt("Losses");
                updateLossStmt.setInt(1, hLosses + 1);
                updateLossStmt.setString(2, hTeam);
                updateLossStmt.executeUpdate();
            }
        } else {
            // Update both teams' ties
            PreparedStatement selectHTieStmt = connection.prepareStatement("SELECT Wins, Losses, Ties FROM Teams WHERE TeamName = ?");
            selectHTieStmt.setString(1, hTeam);
            ResultSet rs = selectHTieStmt.executeQuery();
            if (rs.next()) {
                hWins = rs.getInt("Wins");
                hLosses = rs.getInt("Losses");
                hTies = rs.getInt("Ties");
                updateTieStmt.setInt(1, hTies + 1);
                updateTieStmt.setString(2, hTeam);
                updateTieStmt.executeUpdate();
            }

            PreparedStatement selectVTieStmt = connection.prepareStatement("SELECT Wins, Losses, Ties FROM Teams WHERE TeamName = ?");
            selectVTieStmt.setString(1, vTeam);
            rs = selectVTieStmt.executeQuery();
            if (rs.next()) {
                vWins = rs.getInt("Wins");
                vLosses = rs.getInt("Losses");
                vTies = rs.getInt("Ties");
                updateTieStmt.setInt(1, vTies + 1);
                updateTieStmt.setString(2, vTeam);
                updateTieStmt.executeUpdate();
            }
        }
    }
}
