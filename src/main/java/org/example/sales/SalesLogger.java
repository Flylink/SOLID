package org.example.sales;

import java.io.File;
import java.io.FileWriter;

public class SalesLogger {

    private static SalesLogger salesLogger = null;

    private SalesLogger() {
        salesLogger = this;
    }

    public static SalesLogger getInstance() {
        if (salesLogger == null) {
            salesLogger = new SalesLogger();
        }
        return salesLogger;
    }

    public void log(String message, String dirName, String fileName) {
        File outDir = new File(dirName);
        File outFile = new File(outDir, fileName);
        if (!outDir.exists()) {
            if (outDir.mkdir()) {
                System.out.println("Directory \"" + "out" + "\" created successfully");
            }
        }
        if (!outFile.exists()) {
            try {
                if (outFile.createNewFile()) {
                    System.out.println("File \"" + fileName + "\" created successfully");
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
        try (FileWriter fw = new FileWriter(outFile, true)) {
            fw.append(message.subSequence(0, message.length()));
            fw.flush();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
