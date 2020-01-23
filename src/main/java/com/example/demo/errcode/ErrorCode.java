package com.example.demo.errcode;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ErrorCode {

    @Getter
    @Setter
    private static int iReturnCode;

    private static void convertToNumber(String strCode) {
        try {
            //convert string to number (int)
            setIReturnCode(Integer.valueOf(strCode.substring(1)));
        } catch (NumberFormatException nfe) {
            //set default error code
            setIReturnCode(999);
            log.error("NumberFormatException: " + nfe.getMessage());
        }
    }

    private static void AppTerminate() {
        log.error("Application Terminlate by iReturnCode[" + getIReturnCode() + "]");
        System.exit(getIReturnCode());
    }


    public static void toValidate(String strReturnCode, String msg) {

        String strUpperReturnCode = strReturnCode.toUpperCase();
        convertToNumber(strReturnCode);

        switch (strReturnCode.toUpperCase().charAt(0)) {

            //Warning code
            case 'W':
                switch (strUpperReturnCode) {
                    case "W2001":
                        log.warn(msg);
                        break;
                    default:
                        log.warn("Unknow Warning Code strReturnCode[" + strReturnCode + "] " + msg);
                }
                break;

            //Error code and default
            case 'E':
            default:
                switch (strUpperReturnCode) {

                    case "E1099":
                        log.error(msg);
                        log.error(" Please try use command line follow as by below.");
                        log.error("   $mvn spring-boot:run -Dspring-boot.run.arguments=\"--JobName=All\"");

                        AppTerminate();
                        break;
                    case "E1000":
                        log.error(msg);
                        log.error("JobName DataPump to nested command line arguments [--JobName] [--field_name] [--row_num] [--file_name] ");
                        log.error(" Please try use command line follow as by below.");
                        log.error("$mvn spring-boot:run -Dspring-boot.run.arguments=\"--JobName=datapump\",\"--field_name=Empoyee_No/Empoyee_Name/FirstName/LastName/JobGrade/Contract_Type/Birthday/Status/Sex/Age/Update_DT/Create_DT\",\"--row_num=1000\",\"--file_name=employee\"");

                        AppTerminate();
                        break;
                    default:
                        log.error("Unknow Error Code strReturnCode[" + strReturnCode + "] " + msg);
                }
                break;
        }
    }

}
