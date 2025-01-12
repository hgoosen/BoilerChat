import org.junit.*;
/**
 * A test class that tests if ServerInterface exists and is implemented
 *
 * @author Isaac Riley
 * @version November 15, 2024
 */
public class RunServerInterfaceTest {

    @Test
    public void testExistence() {
        try {
            Class<?> interfaceClass = Class.forName("ServerInterface");
            Assert.assertTrue("Class is not an interface", interfaceClass.isInterface());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testImplementation() {
        Class<?> interfaceClass = ServerInterface.class;
        Class<?>[] classesToCheck = {Client.class, Server.class};

        boolean found = false;

        for (Class<?> clazz : classesToCheck) {
            if (interfaceClass.isAssignableFrom(clazz)) {
                found = true;
                break;
            }
        }

        Assert.assertTrue("Interface is not implemented", found);

    }

}
