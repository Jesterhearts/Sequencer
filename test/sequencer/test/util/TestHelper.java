package sequencer.test.util;

import java.util.Date;
import static org.junit.Assert.fail;


/**
 *
 * @author Ron Coleman
 */
public class TestHelper {
    public static void signon(Object obj) {
        System.out.println("--- STARTING "+new Date()+" => "+obj.getClass().getName());
    }
    
    public static void signoff(Object obj) {
        System.out.println("--- ENDED: "+new Date() +" => "+obj.getClass().getName());
    }
    
    public static void passed() {
        System.out.println("PASSED ***");
    }
    
    public static void failed(String msg) {
        System.out.println("FAILED ***");
        fail(msg);
    }
    
    public static void asserting(Boolean condition) {
        if(!condition)
            System.out.println("FAILED ***");
        
        assert(condition);
    }
}
