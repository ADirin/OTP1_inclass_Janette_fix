import view.Main; // <--- ADD THIS IMPORT
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MainClassExistenceUnitTest {

    @Test
    void testMainClassExists() {
        assertNotNull(new Main());
    }
}