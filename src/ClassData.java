import java.util.HashMap;

/**
 * Egy tároló osztály, amibe az objektumokat töltjük be összehasonlításhoz
 * Itt még nem történik deszerializáció
 */
public class ClassData {
    /**
     * Az osztály neve
     */
    public String className;
    /**
     * Az osztály tagváltozói
     */
    public HashMap<String,String> properties = new HashMap<>();
}
