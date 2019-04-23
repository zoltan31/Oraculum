import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;

public class Main {
    /**
     * Az outputFile fájlban megkeresi azt az objektumot, ami a data-val egyenlő
     * @param outputFile A fájl, amiben a keresés történik
     * @param data Az objektum adatai, amit keresünk
     * @return Igaz, ha van ilyen objektum a fájlban. Hamis, ha nincs
     */
    public static boolean FindAndCompare(BufferedReader outputFile, ClassData data){
        try{
            // Megjelöljük a fájl elejét, ha kilépünk a függvényből az elejétől szeretnénk
            // folytatni a következő keresést
            outputFile.mark(30000);
            String oLine;
            while((oLine = outputFile.readLine()) != null){
                // Olyan objektumokat keresünk, aminek a típusa megegyezik a keresett objektuméval
                if (oLine.equals(data.className)){
                    String prop;
                    // Változó, amiben tároljuk, hogy az objektum megegyezett-e a keresett objektummal
                    boolean success = true;
                    // Megnézzük az objektum összes tagváltozóját
                    while((prop = outputFile.readLine()) != null && prop.charAt(0) == '-'){
                        // Eltávolítjuk a '-' karaktert az elejéről
                        prop = prop.substring(1);
                        // Ha a '-' karakter után van még szóköz, akkor azt is kivesszük
                        if (prop.charAt(0) == ' ')
                            prop = prop.substring(1);
                        // Két stringbe választjuk a tagváltozó nevét és értékét
                        String[] keyValuePair = prop.split(":");
                        // A neighbourID egy speciális eset
                        if (keyValuePair[0].equals("neighbourID")) {
                            if (!data.properties.get(keyValuePair[0] + keyValuePair[1]).equals("")){
                                success = false;
                                break;
                            }
                        }
                        else{
                            //Ha nincs ilyen tagváltozónk, akkor nem egyezhetnek meg
                            if (data.properties.get(keyValuePair[0]) == null){
                                success = false;
                                break;
                            }
                            //Ha a két tagváltozóban két különböző érték van, akkor nem egyeznek meg
                            if (!data.properties.get(keyValuePair[0]).equals(keyValuePair[1])){
                                success = false;
                                break;
                            }
                        }

                    }
                    //Hogyha a szűrésen túljutott az objektum, akkor a teszt sikeres volt
                    if (success){
                        //Beállítjuk, hogy a következő olvasásnál az elejéről kezdjük
                        outputFile.reset();
                        return true;
                    }
                }
            }
            //Beállítjuk, hogy a következő olvasásnál az elejéről kezdjük
            outputFile.reset();
            //Végigolvastuk a kapott fájlt, de nem találtunk vele megegyezző objektumot,
            // ezért hamissal térünk vissza
            return false;
            //Hiba történt, a két objektum nem egyenlő
        }catch(Exception e){
            return false;
        }
    }

    /**
     * ClassData objektumot generál a paraméterként megadott fájlból
     * @param expectedFile Fájl, amiből az objektum generálódik
     * @return ClassData, ami tartalmazza az objektum adatait (osztálynév, tagfüggvények az értékeikkel)
     */
    public static ClassData generateClassData(BufferedReader expectedFile){
        try {
            //Létrehozunk egy üres objektum tárolót
            ClassData data = new ClassData();
            //Az objektum típusa mindig az első sor
            data.className = expectedFile.readLine();
            String line;
            while ((line = expectedFile.readLine()) != null && line.charAt(0) == '-'){
                //Eltávolítjuk a '-' karaktert az elejéről
                line = line.substring(1);
                //Ha a '-' karakter után szóköz volt, azt is eltávolítjuk
                if (line.charAt(0) == ' ')
                    line = line.substring(1);
                //Két stringbe választjuk a tagváltozót és értékét
                String[] keyValuePair = line.split(":");
                //A neighbourID különleges eljárást igényel
                if (keyValuePair[0].equals("neighbourID"))
                    data.properties.put(keyValuePair[0] + keyValuePair[1],"");
                else
                    data.properties.put(keyValuePair[0],keyValuePair[1]);
                //Megjelöljük a fájlt, hogy ha már a következő sor már nem tagváltozó
                //(nem '-'-vel kezdődik), akkor tudjuk hova kell visszaugorni
                expectedFile.mark(30000);
            }
            //Visszalépünk egy sort, mert már a következő objektum típusát is beolvastuk
            expectedFile.reset();
            //Visszaadjuk az objektum adatait
            return data;
        }catch(Exception e){
            //Hiba esetén null-t adunk vissza
            return null;
        }
    }

    /**
     * Main függvény
     * @param args indulási argumentumok
     */
    public static void main(String[] args) {
        //Létrehozunk egy streamet a konzolról
        InputStreamReader isr = new InputStreamReader(System.in);
        //Létrehozunk egy buffered readert az inputstreamből
        BufferedReader br = new BufferedReader(isr);
        try {
            //Beolvassunk egy sort a konzolról
            String input = br.readLine();
            //Space szerint kettéválasztjuk a beolvasott sort
            String[] words = input.split(" ");
            //Megkeressük a két fájlt az input szerint
            BufferedReader expectedFile = new BufferedReader(new FileReader("Expected/" + words[0] + ".txt"));
            BufferedReader outputFile = new BufferedReader(new FileReader("Output/" + words[1] + ".txt"));

            //Megjelöljük a fájl elejét
            expectedFile.mark(30000);
            String line;
            while ((line = expectedFile.readLine()) != null){
                //Addig olvasunk, amíg van keresett objektum a fájlban
                expectedFile.reset();
                //Generálunk egy ClassData-t az expected fájlból
                ClassData data = generateClassData(expectedFile);
                //Ha nincs ilyen adat, akkor hibával visszatérünk
                if (data == null){
                    System.out.println("Problem reading class from: " + words[0] + ".txt");
                    return;
                }
                //Lekérdezzük, hogy sikeresen találtunk-e
                boolean success = FindAndCompare(outputFile,data);
                //Ha nem sikerült, akkor megállunk, és kiírjuk, hogy nem sikerült a tesztelés
                if (!success){
                    System.out.println("Tests failed!");
                    return;
                }
                //Megjelöljük a fájlt
                expectedFile.mark(30000);
            }
            //Az összes objektumnak volt egy vele azonos párja, azaz a teszt sikeres volt
            System.out.println("Test succeeded!");

        }catch(Exception e){
            //Hiba történt a program futásakor
            System.out.println("There was a problem during startup");
        }
    }
}
