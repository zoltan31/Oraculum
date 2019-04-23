import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;

public class Main {

    public static boolean FindAndCompare(BufferedReader outputFile, ClassData data){
        try{
            outputFile.mark(30000);
            String oLine;
            while((oLine = outputFile.readLine()) != null){
                if (oLine.equals(data.className)){
                    String prop;
                    boolean success = true;
                    while((prop = outputFile.readLine()) != null && prop.charAt(0) == '-'){
                        prop = prop.substring(1);
                        if (prop.charAt(0) == ' ')
                            prop = prop.substring(1);
                        String[] keyValuePair = prop.split(":");
                        if (keyValuePair[0].equals("neighbourID")) {
                            if (!data.properties.get(keyValuePair[0] + keyValuePair[1]).equals("")){
                                success = false;
                                break;
                            }
                        }
                        else{
                            if (data.properties.get(keyValuePair[0]) == null){
                                success = false;
                                break;
                            }
                            if (!data.properties.get(keyValuePair[0]).equals(keyValuePair[1])){
                                success = false;
                                break;
                            }
                        }

                    }
                    if (success){
                        outputFile.reset();
                        return true;
                    }
                }
            }
            outputFile.reset();
            return false;
        }catch(Exception e){
            return false;
        }
    }

    public static ClassData generateClassData(BufferedReader expectedFile){
        try {
            ClassData data = new ClassData();
            data.className = expectedFile.readLine();
            String line;
            while ((line = expectedFile.readLine()) != null && line.charAt(0) == '-'){
                line = line.substring(1);
                if (line.charAt(0) == ' ')
                    line = line.substring(1);
                String[] keyValuePair = line.split(":");
                if (keyValuePair[0].equals("neighbourID"))
                    data.properties.put(keyValuePair[0] + keyValuePair[1],"");
                else
                    data.properties.put(keyValuePair[0],keyValuePair[1]);
                expectedFile.mark(30000);
            }
            expectedFile.reset();
            return data;
        }catch(Exception e){
            return null;
        }
    }

    public static void main(String[] args) {
        InputStreamReader isr = new InputStreamReader(System.in);
        BufferedReader br = new BufferedReader(isr);
        try {
            String input = br.readLine();
            String[] words = input.split(" ");
            BufferedReader expectedFile = new BufferedReader(new FileReader("Expected/" + words[0] + ".txt"));
            BufferedReader outputFile = new BufferedReader(new FileReader("Output/" + words[1] + ".txt"));

            expectedFile.mark(30000);
            String line;
            while ((line = expectedFile.readLine()) != null){
                expectedFile.reset();
                ClassData data = generateClassData(expectedFile);
                if (data == null){
                    System.out.println("Problem reading class from: " + words[0] + ".txt");
                    return;
                }
                boolean success = FindAndCompare(outputFile,data);
                if (!success){
                    System.out.println("Tests failed!");
                    return;
                }
                expectedFile.mark(30000);
            }
            System.out.println("Test succeeded!");

        }catch(Exception e){
            System.out.println("There was a problem during startup");
        }
    }
}
