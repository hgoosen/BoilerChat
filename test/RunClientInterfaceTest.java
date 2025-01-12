import org.junit.Assert;
import org.junit.Test;

public class RunClientInterfaceTest {

    @Test
    public void testExistence() {
        try {
            Class<?> interfaceClass = Class.forName("ClientInterface");
            Assert.assertTrue("Class is not an interface", interfaceClass.isInterface());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testImplementation() {
        Class<?> interfaceClass = ClientInterface.class;
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
