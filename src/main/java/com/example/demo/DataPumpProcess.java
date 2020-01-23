package com.example.demo;

import com.github.javafaker.Faker;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import static com.example.demo.DataPumpMain.prop;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

@Data
@Slf4j
public class DataPumpProcess {
    private String field_name;
    private String row_num;
    private int row_number;
    private String dtp_file_name;

    public static List<String> field_config_name = new ArrayList<>();
    public static List<String> field_config_formats = new ArrayList<>();
    public static List<String> field_arg_name = new ArrayList<>();

    private List<String> createLine;
    public static List<List<String>> listDataPump = new ArrayList<>();
    private int[] count;
    private int counter;
    private String firstName;
    private String lastName;
    private String days;
    private int random = 0;
    private boolean check = false;
    int length_row_number = 0;


    public DataPumpProcess(String field_name, String row_num, int row_number, String dtp_file_name) {
        this.field_name = field_name;
        this.row_num = row_num;
        this.row_number = row_number;
        this.dtp_file_name = dtp_file_name;
    }

    public void setupConfig() throws IOException, NoSuchAlgorithmException {
        log.info(String.valueOf(field_name));
        log.info(String.valueOf(row_num));

        String path_fileConfig = prop.getProperty("part.data_pump_config");
        File fileConfigFormat = new File(path_fileConfig);

        boolean checkFile = fileConfigFormat.isFile();

        log.info("CheckFile = " + checkFile);
        if (checkFile) {
            getFormat_Row(path_fileConfig);
        }
    }

    private void getFormat_Row(String path_fileConfig) throws IOException, NoSuchAlgorithmException {
        FileInputStream inputStream = null;

        try {
            inputStream = new FileInputStream(path_fileConfig);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

        XSSFWorkbook workbook = null;
        try {
            assert inputStream != null;
            workbook = new XSSFWorkbook(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        assert workbook != null;
        XSSFSheet sheet = workbook.getSheetAt(0); // sheet Config_format
        System.out.println("Get Sheet Name ==> " + sheet.getSheetName());

        CreationHelper createHelper = workbook.getCreationHelper();
        XSSFCellStyle dateCellStyle = workbook.createCellStyle();
        dateCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("yyyy-MM-dd"));

        int lastRowNum = sheet.getLastRowNum();

        log.info("lastRowNum = " + lastRowNum);
        for (int i = 1; i <= lastRowNum; i++) {
            Row row = sheet.getRow(i);

            Cell cellFieldName = row.getCell(1);
            Cell cellFormat = row.getCell(2);
            field_config_formats.add(cellFormat.getStringCellValue());
            field_config_name.add(cellFieldName.getStringCellValue());
        }
        log.info("field_config_name = \n" + field_config_name);
        log.info("field_config_formats = \n" + field_config_formats);

        /** parse field name array split "," */
        String field_names = field_name;
        String[] tokens = field_names.split("/");

        field_arg_name.addAll(Arrays.asList(tokens));

        log.info("field_arg_name = " + field_arg_name);
        log.info("field_arg_name = " + field_arg_name.size());

        listDataPump.add(field_arg_name);

        count = new int[4];
        for (int loop = 0; loop < row_number; loop++) {
            random = 0;
            createLine = new ArrayList<>();
            for (String field_argument : field_arg_name) {
                check = false;
                for (int j = 0; j < field_config_name.size(); j++) {
                    if (field_argument.contains(field_config_name.get(j))) {
                        check = true;
                        String field_format = field_config_formats.get(j); // f = field_formats
                        if (field_format.toUpperCase().contains("SSSS")) {
                            countUp();
                            String format1 = ("" + count[3] + count[2] + count[1] + count[0]);
                            log.info(format1);
                            createLine.add(format1);
                        } else if (field_format.toUpperCase().contains("NNNNNN")) {
                            randomIdentifier();
                            String format2 = (firstName + " " + lastName);
                            log.info(format2);
                            createLine.add(format2);
                        } else if (field_format.toUpperCase().contains("FFFFFFFF")) {
                            String format3 = (firstName);
                            log.info(format3);
                            createLine.add(format3);
                        } else if (field_format.toUpperCase().contains("LLLLLL")) {
                            String format4 = (lastName);
                            log.info(format4);
                            createLine.add(format4);
                        } else if (field_format.toUpperCase().contains("JJ")) {
                            String format5 = (String.valueOf(RandomNumber(10, 20)));
                            log.info(format5);
                            createLine.add(format5);
                        } else if (field_format.toUpperCase().contains("CC")) {
                            String format6 = (randomChar());
                            log.info(format6);
                            createLine.add(format6);
                        } else if (field_format.toUpperCase().contains("YYYY-MM-DD")) {
                            random++;
                            switch (random) {
                                case 1:
                                    days = randomDates(1957, 1997, 1, 1);
                                    break;
                                case 2:
                                    days = randomDates(2019, 2020, 1, 1);
                                    break;
                                case 3:
                                    days = randomDates(2018, 2018, 12, 31);
                                    break;
                                default:
                                    days = randomDates(1997, 2020, 1, 1);
                                    break;
                            }
                            String format7 = (days);
                            log.info(format7);
                            createLine.add(format7);
                        } else if (field_format.contains("Single|Marry")) {
                            String[] status = {"Single", "Marry"};
                            String format8 = (status[radgrt()]);
                            log.info(format8);
                            createLine.add(format8);
                        } else if (field_format.contains("Male|Female")) {
                            String[] sex = {"Male", "Female"};
                            String format9 = (sex[radgrt()]);
                            log.info(format9);
                            createLine.add(format9);
                        } else if (field_format.toUpperCase().contains("AA")) {
                            String format10 = (String.valueOf(RandomNumber(27, 50)));
                            log.info(format10);
                            createLine.add(format10);
                        } else {
                            log.info("matching NO == > Default! " + field_config_name.get(field_config_name.size() - 1) + "create format ==> " + field_config_formats.get(field_config_name.size() - 1));
                            String formatDefault = "DDDDDDDD";
                            log.info(formatDefault);
                            createLine.add(formatDefault);
                        }
                    }
                }
                if (!check) {
                    log.info("matching NO == > Default! " + field_config_name.get(field_config_name.size() - 1) + "create format ==> " + field_config_formats.get(field_config_name.size() - 1));
                    String formatDefault = "DDDDDDDD";
                    log.info(formatDefault);
                    createLine.add(formatDefault);
                }
            }
            listDataPump.add(createLine);
        }
        new DataPumpWriter().Writer(listDataPump);
    }

    private void countUp() {
        counter++;
        count[0] = counter % 10;
        count[1] = counter / 10 % 10;
        count[2] = counter / 100 % 10;
        count[3] = counter / 1000 % 10;
    }

    private void randomIdentifier() {
        Faker faker = new Faker();
        firstName = faker.name().firstName();
        lastName = faker.name().lastName();
    }

    private String randomChar() {
        String chars = "CP";
        Random rnd = new Random();
        return String.valueOf(chars.charAt(rnd.nextInt(chars.length())));
    }

    private String randomDates(int firstYear, int lastYear, int lastMonth, int lastDay) {
        LocalDate startDate = LocalDate.of(firstYear, 1, 1); //start date
        long start = startDate.toEpochDay();

        LocalDate endDate = LocalDate.of(lastYear, lastMonth, lastDay); //end date
        long end = endDate.toEpochDay();

        long randomEpochDay = ThreadLocalRandom.current().longs(start, end).findAny().getAsLong();
        return String.valueOf(LocalDate.ofEpochDay(randomEpochDay));
    }

    private int radgrt() {
        return (int) Math.ceil(Math.random() * 2) - 1;
    }

    private int RandomNumber(int min, int max) {
        if (min >= max) {
            throw new IllegalArgumentException("max must be greater than min");
        }
        Random r = new Random();
        return r.nextInt((max - min) + 1) + min;
    }
}
