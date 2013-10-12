import java.io.*;
import java.nio.file.*;
import java.util.*;

public class Run {
    public static void main(String[] args) {
        ArrayList<String> program = new ArrayList<String>();
        Scanner input = new Scanner(System.in);
        ObjectOutputStream output;

        // get path of source code file; load file; parse instructions; load
        // each into array; serialize array object into "binary" file.
        Path source = Paths.get(args[0]);
        Scanner s = null;
        try {
            s = new Scanner(source);
            while (s.hasNextLine()) {
                String line = new String();
                line = s.nextLine();
                //System.out.println(line);
                if (!line.equals("")) {
                    if(!program.add(Parser.parse(line))) {
                        System.err.println("Could not add element to linked list.");
                    }
                }
            } //end while

        }
        catch(IOException ioException) {
            System.err.printf("Could not open source file. Enter a number to exit.");
            while (!input.hasNextInt()) {
                input.next();
            }
            System.exit(1);

        } // end catch
        finally {
            if (s!=null) {
                s.close();
            }
        }

        // take list of instructions and build serialized object file
        CPU cpu = new CPU(program);
        cpu.execute();

    } // end main
}
