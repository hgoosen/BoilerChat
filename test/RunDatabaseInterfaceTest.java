import org.junit.*;

/**
 * A test class that tests if DatabaseInterface exists and is implemented
 *
 * @author Isaac Riley, Samit Gadekar (SamitGadekar)
 * @version November 2, 2024
 */
public class RunDatabaseInterfaceTest {

    @Test
    public void testExistence() {
        try {
            //Creates Class object of DatabaseInterface
            Class<?> interfaceClass = Class.forName("DatabaseInterface");
            //Checks if DatabaseInterface is an interface
            Assert.assertTrue("DatabaseInterface exists but is not an interface", interfaceClass.isInterface());
        } catch (ClassNotFoundException e) { //catch if class isn't found
            Assert.fail("DatabaseInterface does not exist");
        }
    }

    @Test
    public void testImplementation() {

            //Creates object
            Class<?> interfaceClass = DatabaseInterface.class;

            //List of classes to check
            Class<?>[] classesToCheck = {Chat.class, Database.class, Message.class, User.class};

            boolean found = false;
            //iterates through all classes looking for if one implements DatabaseInterface
            for (Class<?> clazz : classesToCheck) {
                if (interfaceClass.isAssignableFrom(clazz)) {
                    found = true;
                    break;
                }
            }
            //Checks if a class implementing ChatInterface was found
            Assert.assertTrue("DatabaseInterface is not implemented", found);

    }
}
