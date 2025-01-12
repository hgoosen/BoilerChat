import org.junit.*;
/**
 * A test class that tests if MessageInterface exists and is implemented
 *
 * @author Isaac Riley, Samit Gadekar (SamitGadekar)
 * @version November 2, 2024
 */
public class RunMessageInterfaceTest {

    @Test
    public void testExistence() {
        try {
            //Creates Class object of MessageInterface
            Class<?> interfaceClass = Class.forName("MessageInterface");
            //Checks if MessageInterface is an interface
            Assert.assertTrue("MessageInterface exists but is not an interface", interfaceClass.isInterface());
        } catch (ClassNotFoundException e) { //catch if class isn't found
            Assert.fail("MessageInterface does not exist");
        }
    }

    @Test
    public void testImplementation() {
          //Creates object
          Class<?> interfaceClass = MessageInterface.class;

          //List of classes to check
          Class<?>[] classesToCheck = {Chat.class, Database.class, Message.class, User.class};

          boolean found = false;
          //iterates through all classes looking for if one implements MessageInterface
          for (Class<?> clazz : classesToCheck) {
              if (interfaceClass.isAssignableFrom(clazz)) {
                  found = true;
                  break;
              }
          }
          //Checks if a class implementing MessageInterface was found
          Assert.assertTrue("MessageInterface is not implemented", found);

      }
}
