package testThree;

import java.io.*;

public class Output {
    private PrintWriter inputToFile;
    private File outputFile;
    public Output(OutputStream streamForOutput) { //stream could be System.in
        try {
            outputFile = new File("outputAstar.txt");
            outputFile.createNewFile();
            /**
             * We create an empty outputFile.txt,
             * it gets overwritten every time we run the code.
             * */
            if (outputFile.canWrite()) {
                inputToFile = new PrintWriter(outputFile);
            } else {
                System.out.println("couldn't write into the file");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void out(Object... o) { //merges strings to output as a one string in a text file
        for (int i = 0; i < o.length; i++) {
            if (i != 0)
                inputToFile.print(' ');
            inputToFile.print(o[i]);
        }
    }
    public void printLn(Object... o) {  //prints merged strings to the file
        out(o);
        inputToFile.println();
    }
    public void close() { //closes the file
        inputToFile.close();
    }
}