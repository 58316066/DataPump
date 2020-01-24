package com.example.demo;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import static com.example.demo.DataPumpMain.*;
import static com.example.demo.DataPumpProcess.field_arg_name;

public class DataPumpWriter {
    private boolean writeHeader = false;

    public void Writer(List<List<String>> item) throws NoSuchAlgorithmException, IOException {
        int average = field_arg_name.size() / 2;
        PrintWriter p = null;
        String partFile = prop.getProperty("part.OutputCSV");
        String fileName = dtp_file_name;

        try {
            p = new PrintWriter(new FileWriter(partFile + fileName + ".csv", false));
        } catch (IOException e) {
            e.printStackTrace();
        }

        /** write Data  */
        int rows = 0;
        for (List<String> list : item) {
            rows++;

            int i = list.size();

            if (rows == 3) {
                /** Start write Data_Enter column */
                for (String row : list) {
                    if (i == 1) {
                        p.print(row);
                        continue;
                    }
                    if (i == (average)) {
                        p.print(row + ",");
                        p.println();
                        i--;
                        continue;
                    }
                    p.print(row + ",");
                    i--;
                }
                p.println();
                continue;
            }

            for (String row : list) {
                if (i == 1) {
                    p.print(row);
                    continue;
                }
                p.print(row + ",");
                i--;
            }
            p.println();

            /** Start write Data_Null and Data Blank */
            if (!writeHeader) {
                writeData_Null(p);
                writeData_Blank(p);
                writeDuplicate(p);
                writeHeader = true;
            }
            /** End write Data_Null and Data Blank
             Continue for original loop */
        }
        if (p != null) {
            p.close();
        }

        //Create checksum for this file
        File file = new File(partFile + fileName + ".csv");

        //Use MD5 algorithm
        MessageDigest md5Digest = MessageDigest.getInstance("MD5");

        //Get the checksum
        String checksum = null;
        checksum = getFileChecksum(md5Digest, file);

        //see checksum
        System.out.println(checksum);

        String scriptControl = "- Control: \n" +
                "    filename: " + fileName + ".csv\n" +
                "    md5sum: " + checksum + "\n" +
                "    line: " + (field_arg_name.size() * 2 + row_number + 5) + "\n";

        File fileControl = new File(partFile + fileName + ".control");

        FileWriter writer;
        writer = new FileWriter(fileControl, false);
        writer.write(scriptControl);
        writer.close();
        System.out.println("Write fileControl Success!");
        System.out.println(fileControl.getPath());
    }

    int fieldSize = field_arg_name.size();

    private void writeDuplicate(PrintWriter p) {
        /** Data Duplicate */
        int loop = 0;
        while (loop != 2) {
            loop++;
            for (int i = 1; i <= fieldSize; i++) {
                if (i == fieldSize) {
                    p.print("Dup");
                } else {
                    p.print("Dup,");
                }
            }
            p.println();
        }
    }

    private void writeData_Blank(PrintWriter p) {
        /** DataBlank */

        for (int i = 1; i <= fieldSize; i++) {
            for (int j = 1; j < i; j++) {
                p.print(",");
            }
            p.println();
        }

        /** Data Blank and NotBlank */
        for (int i = 1; i <= fieldSize; i++) {
            boolean check = false;
            for (int j = 1; j <= fieldSize; j++) {

                if (j == fieldSize) {
                    p.print("");
                    break;
                }

                if (!check) {
                    for (int y = 1; y <= i; y++) {
                        if (y == fieldSize) {
                            p.print("Test");
                            continue;
                        }
                        j++;
                        p.print("Test,");
                    }
                    check = true;
                }
                if (j == fieldSize) {
                    continue;
                }
                p.print(",");
            }
            p.println();
        }
    }

    private void writeData_Null(PrintWriter p) {
        p.println();
    }

    private static String getFileChecksum(MessageDigest digest, File file) throws IOException {
        //Get file input stream for reading the file content
        FileInputStream fis = new FileInputStream(file);

        //Create byte array to read data in chunks
        byte[] byteArray = new byte[1024];
        int bytesCount = 0;

        //Read file data and update in message digest
        while ((bytesCount = fis.read(byteArray)) != -1) {
            digest.update(byteArray, 0, bytesCount);
        }

        //close the stream; We don't need it now.
        fis.close();

        //Get the hash's bytes
        byte[] bytes = digest.digest();

        //This bytes[] has bytes in decimal format;
        //Convert it to hexadecimal format
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
        }
        //return complete hash
        return sb.toString();
    }
}
