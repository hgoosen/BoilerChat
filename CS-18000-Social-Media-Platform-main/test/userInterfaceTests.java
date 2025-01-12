import org.junit.*;
/**
 * A test class that tests if UserInterface exists and is implemented
 *
 * @author Isaac Riley
 * @version November 1, 2024
 */
public class userInterfaceTests {

    @Test
    public void testExistence() {
        try {
            //Creates Class object of ChatInterface
            Class<?> interfaceClass = Class.forName("UserInterface");
            //Checks if ChatInterface is an interface
            Assert.assertTrue("Class is not an interface", interfaceClass.isInterface());
        } catch (ClassNotFoundException e) { //catch if class isn't found
            e.printStackTrace();
        }
    }

    @Test
    public void testImplementation() {
        //Creates object

        Class<?> interfaceClass = UserInterface.class;
        //List of classes to check
        Class<?>[] classesToCheck = {Chat.class, Database.class, Message.class, User.class};

        boolean found = false;
        //iterates through all classes looking for if one implements ChatInterface
        for (Class<?> clazz : classesToCheck) {
            if (interfaceClass.isAssignableFrom(clazz)) {
                found = true;
                break;
            }
        }
        //Checks if a class implementing ChatInterface was found
        Assert.assertTrue("Interface is not implemented", found);

    }
}
