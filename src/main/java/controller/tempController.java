package controller;

import dao.temperatureRecordsDAO;
import entity.temperatureRecord;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import java.sql.Timestamp;
import java.time.*;
import java.util.*;

public class tempController {
    private String convFrom;
    private String convTo;
    private double valueToConvert;
    TemperatureConverter converter = new TemperatureConverter();
    temperatureRecord record;
    temperatureRecordsDAO dao = new temperatureRecordsDAO();
    Locale locale = Locale.getDefault();
    final ChoiceBox cb = new ChoiceBox(FXCollections.observableArrayList("English", "Suomeksi", "日本語で" , "Svenska"));


    @FXML
    public ChoiceBox<String> convertTo;

    @FXML
    public ChoiceBox<String> convertFrom;

    @FXML
    public TextField value;

    @FXML
    public ListView historyList;

    @FXML
    public Button convert;

    @FXML
    public Label result;

    @FXML
    public ChoiceBox languageChoice;

    @FXML
    public Label historyHead;

    @FXML
    public Label convFLabel;

    @FXML
    public Label convTLabel;

    @FXML
    public Label resultLabel;

    @FXML
    public Label converterLabel;


    public void initialize() {
        languageChoice.setItems(cb.getItems());
        //todo: vaihda näihin että kielen perusteella tulevat samoin käännöksien jutut
        convertTo.getItems().addAll("Celsius", "Fahrenheit", "Kelvin");
        convertFrom.getItems().addAll("Celsius", "Fahrenheit", "Kelvin");
    }

    public void getConvertValues() {
        this.convFrom = convertFrom.getValue();
        this.convTo = convertTo.getValue();
        this.valueToConvert = Double.parseDouble(value.getText());
    }

    public void convert() {
        updateHistory();
        getConvertValues();
        Timestamp time = Timestamp.valueOf(LocalDateTime.now());
        if (convFrom.equals("Celsius") && convTo.equals("Fahrenheit")) {
            double r = (converter.celsiusToFahrenheit(valueToConvert));
            result.setText(String.valueOf(r));
            record = new temperatureRecord(r, "Fahrenheit", time);
            addToHistory(record);
        }
        else if (convFrom.equals("Fahrenheit") && convTo.equals("Celsius")) {
            double r = (converter.fahrenheitToCelsius(valueToConvert));
            result.setText(String.valueOf(r));
            record = new temperatureRecord(r, "Celsius", time);
            addToHistory(record);
        }
        else if (convFrom.equals("Kelvin") && convTo.equals("Celsius")) {
            double r = (converter.kelvinToCelsius(valueToConvert));
            result.setText(String.valueOf(r));
            record = new temperatureRecord(r, "Celsius", time);
            addToHistory(record);
        }
        else if (convFrom.equals("Celsius") && convTo.equals("Kelvin")){
            double r = (converter.celsiusToKelvin(valueToConvert));
            result.setText(String.valueOf(r));
            record = new temperatureRecord(r, "Kelvin", time);
            addToHistory(record);
        }
        else {
            result.setText("Nothing to convert");
        }

        updateHistory();
    }

    public void addToHistory(temperatureRecord t) {
        dao.addRecord(t);
    }

    public void updateHistory(){
        List<temperatureRecord> records = dao.getHistory();
        for (temperatureRecord r : records) {
            historyList.getItems().add(r.getValue() + " " + r.getUnit() + " at " + r.getTime());
        }
    }

    @FXML
    public void changeLanguage(ActionEvent e) throws Exception{
        int lang = languageChoice.getSelectionModel().getSelectedIndex();
        switch (lang) {
            case 0:
                locale = new Locale("en", "US");
            break;
            case 1:
                locale = new Locale("fi", "FI");
                break;
            case 2:
                locale = new Locale("ja", "JP");
                break;
            case 3:
                locale = new Locale("sv", "SE");
                break;
            default:
                System.out.println("Invalid language, defaulting to English.");
        }

        ResourceBundle rs = ResourceBundle.getBundle("MessagesBundle", locale);
        convFLabel.setText(rs.getString("convertFrom"));
        convertFrom.setItems(FXCollections.observableArrayList(rs.getString("convertList").split(",")));
        convTLabel.setText(rs.getString("convertTo"));
        convertTo.setItems(FXCollections.observableArrayList(rs.getString("convertList").split(",")));
        historyHead.setText(rs.getString("history"));
        value.setPromptText(rs.getString("prompt2"));
        convert.setText(rs.getString("convert"));
        resultLabel.setText(rs.getString("result"));
        converterLabel.setText(rs.getString("converter"));
        updateHistory();
    }

    public void consoleVersion(){
        Scanner scanner = new Scanner(System.in);
        System.out.println("Select language: 1. English, 2. Suomeksi, 3. Sverige, 4. Nihongo");
        int lang = scanner.nextInt();
        switch (lang) {
            case 1:
                locale = new Locale("en", "US");
                break;

            case 2:
                locale = new Locale("fi", "FI");
                break;

            case 3:
                locale = new Locale("sv", "SE");
                break;

            case 4:
                locale = new Locale("ja", "JP");
                break;

            default:
                System.out.println("Invalid language, defaulting to English.");


        }

        ResourceBundle rs = ResourceBundle.getBundle("MessagesBundle", locale);
        System.out.println(rs.getString("prompt1"));
        int choice = scanner.nextInt();
        System.out.println(rs.getString("prompt2"));
        double value = scanner.nextDouble();
        switch (choice) {
            case 1:
                System.out.println(converter.celsiusToFahrenheit(value));
                break;
            case 2:
                System.out.println(converter.fahrenheitToCelsius(value));
                break;
        }
    }

}
