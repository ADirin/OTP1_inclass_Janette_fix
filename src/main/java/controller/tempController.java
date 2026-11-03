package controller;

import dao.temperatureRecordsDAO;
import entity.temperatureRecord;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

public class tempController {
    private String convFrom;
    private String convTo;
    private double valueToConvert;
    TemperatureConverter converter = new TemperatureConverter();
    temperatureRecord record;
    temperatureRecordsDAO dao = new temperatureRecordsDAO();
    Locale locale = Locale.getDefault();

    @FXML public ChoiceBox<String> convertTo;
    @FXML public ChoiceBox<String> convertFrom;
    @FXML public TextField value;
    @FXML public ListView<String> historyList;
    @FXML public Button convert;
    @FXML public Label result;
    @FXML public ChoiceBox<String> languageChoice;
    @FXML public Label historyHead;
    @FXML public Label convFLabel;
    @FXML public Label convTLabel;
    @FXML public Label resultLabel;
    @FXML public Label converterLabel;

    @FXML
    public void initialize() {
        if (languageChoice != null && languageChoice.getItems().isEmpty()) {
            languageChoice.getItems().addAll("English", "Finnish");
        }
        if (convertFrom != null && convertFrom.getItems().isEmpty()) {
            convertFrom.getItems().addAll("Celsius", "Fahrenheit", "Kelvin");
        }
        if (convertTo != null && convertTo.getItems().isEmpty()) {
            convertTo.getItems().addAll("Celsius", "Fahrenheit", "Kelvin");
        }
    }

    public void getConvertValues() throws NumberFormatException {
        if (convertFrom == null || convertFrom.getValue() == null ||
                convertTo == null || convertTo.getValue() == null) {
            throw new IllegalArgumentException("Please select units.");
        }
        this.convFrom = convertFrom.getValue().trim();
        this.convTo = convertTo.getValue().trim();
        this.valueToConvert = Double.parseDouble(value.getText().trim());
    }

    @FXML
    public void convert() {
        try {
            getConvertValues();
            double r;

            // Handle all conversion combinations
            if (convFrom.equalsIgnoreCase(convTo)) {
                r = valueToConvert;
            } else if (convFrom.equalsIgnoreCase("Celsius") && convTo.equalsIgnoreCase("Fahrenheit")) {
                r = converter.celsiusToFahrenheit(valueToConvert);
            } else if (convFrom.equalsIgnoreCase("Fahrenheit") && convTo.equalsIgnoreCase("Celsius")) {
                r = converter.fahrenheitToCelsius(valueToConvert);
            } else if (convFrom.equalsIgnoreCase("Kelvin") && convTo.equalsIgnoreCase("Celsius")) {
                r = converter.kelvinToCelsius(valueToConvert);
            } else if (convFrom.equalsIgnoreCase("Celsius") && convTo.equalsIgnoreCase("Kelvin")) {
                r = converter.celsiusToKelvin(valueToConvert);
            } else if (convFrom.equalsIgnoreCase("Fahrenheit") && convTo.equalsIgnoreCase("Kelvin")) {
                double inCelsius = converter.fahrenheitToCelsius(valueToConvert);
                r = converter.celsiusToKelvin(inCelsius);
            } else if (convFrom.equalsIgnoreCase("Kelvin") && convTo.equalsIgnoreCase("Fahrenheit")) {
                double inCelsius = converter.kelvinToCelsius(valueToConvert);
                r = converter.celsiusToFahrenheit(inCelsius);
            } else {
                result.setText("Invalid units selected");
                return;
            }

            // Update UI label
            result.setText(String.format("%.2f %s", r, convTo));

            // DB Record creation inside try-catch to prevent breaking UI on DB failure
            try {
                Timestamp time = Timestamp.valueOf(LocalDateTime.now());
                record = new temperatureRecord(r, convTo, time);
                addToHistory(record);
                updateHistory();
            } catch (Exception dbException) {
                System.err.println("Database history update failed: " + dbException.getMessage());
            }

        } catch (NumberFormatException e) {
            result.setText("Please enter a valid number");
        } catch (IllegalArgumentException e) {
            result.setText(e.getMessage());
        } catch (Exception e) {
            result.setText("Conversion error");
            e.printStackTrace();
        }
    }

    public void addToHistory(temperatureRecord t) {
        if (dao != null) {
            dao.addRecord(t);
        }
    }

    public void updateHistory() {
        if (dao == null || historyList == null) return;
        List<temperatureRecord> records = dao.getHistory();
        historyList.getItems().clear();
        if (records != null) {
            for (temperatureRecord r : records) {
                historyList.getItems().add(r.getValue() + " " + r.getUnit() + " at " + r.getTime());
            }
        }
    }

    @FXML
    public void changeLanguage(ActionEvent e) throws Exception {
        if (languageChoice == null) return;
        int lang = languageChoice.getSelectionModel().getSelectedIndex();
        switch (lang) {
            case 0 -> locale = new Locale("en", "US");
            case 1 -> locale = new Locale("fi", "FI");
            case 2 -> locale = new Locale("ja", "JP");
            case 3 -> locale = new Locale("sv", "SE");
            case 4 -> locale = new Locale("ar", "AE");
            default -> locale = Locale.ENGLISH;
        }

        ResourceBundle rs = ResourceBundle.getBundle("MessagesBundle", locale);
        if (convFLabel != null) convFLabel.setText(rs.getString("convertFrom"));
        if (convTLabel != null) convTLabel.setText(rs.getString("convertTo"));
        if (historyHead != null) historyHead.setText(rs.getString("history"));
        if (value != null) value.setPromptText(rs.getString("prompt2"));
        if (convert != null) convert.setText(rs.getString("convert"));
        if (resultLabel != null) resultLabel.setText(rs.getString("result"));
        if (converterLabel != null) converterLabel.setText(rs.getString("converter"));
    }
}