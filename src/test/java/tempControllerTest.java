
import controller.tempController;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class tempControllerTest {

    private tempController controller;

    @BeforeEach
    void setUp() {
        controller = new tempController();

        // 1. Manually instantiate FXML elements referenced inside initialize()
        controller.languageChoice = new ChoiceBox<>();
        controller.convertFrom = new ChoiceBox<>();
        controller.convertTo = new ChoiceBox<>();
        controller.value = new TextField();
        controller.result = new Label();
        controller.historyList = new ListView<>();

        // 2. Call initialize manually after fields are set
        controller.initialize();
    }

    @Test
    void initialize_shouldAddTemperatureOptions() {
        // Now languageChoice and options won't throw NullPointerException
    }
}