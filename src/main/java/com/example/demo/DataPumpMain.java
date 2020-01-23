package com.example.demo;

import com.example.demo.errcode.ErrorCode;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

@Data
@Slf4j
@Configuration
@PropertySource("classpath:application.properties")
public class DataPumpMain {

    Environment env;
    private ApplicationArguments args;

    private String JobName = "";
    public static Logger logger;
    public static FileHandler fh;
    public static Properties prop;

    public static List<String> field_name;
    public static List<String> row_num;
    public static int row_number;
    public static String dtp_file_name;

    @Autowired
    public DataPumpMain(Environment env, ApplicationArguments args) {
        this.env = env;
        this.args = args;
    }

    @Bean
    public void argsComponent() {

        log.info("start initial argsComponent");
        log.info("OptionNames: {}", args.getOptionNames());

        for (String name : args.getOptionNames()) {
            log.info("arg-" + name + "=" + args.getOptionValues(name));
        }

        if (args.containsOption("JobName")) {
            List<String> values = args.getOptionValues("JobName");

            this.setJobName(values.get(0));
            log.info("JobName = [" + this.getJobName() + "]");

            if (this.getJobName().equalsIgnoreCase("datapump")) {

                if (!args.containsOption("field_name") || !args.containsOption("row_num") || !args.containsOption("file_name")) {
                    ErrorCode.toValidate("E1000", "!!! Argument missing !!! ");
                }
                //Get argument values
                field_name = args.getOptionValues("field_name");
                row_num = args.getOptionValues("row_num");
                row_number = Integer.parseInt(row_num.get(0));
                dtp_file_name = args.getOptionValues("file_name").get(0);
            }
        }
    }

    @Bean
    public void get_Properties() {
        prop = new Properties();
        try (InputStream input = DemoApplication.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
            }
            prop.load(input);

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Bean
    public void process() throws IOException, NoSuchAlgorithmException {
        field_name = args.getOptionValues("field_name");
        row_num = args.getOptionValues("row_num");
        row_number = Integer.parseInt(row_num.get(0));
        dtp_file_name = args.getOptionValues("file_name").get(0);
        DataPumpProcess dataPumpProcess = new DataPumpProcess(field_name.get(0), row_num.get(0), row_number, dtp_file_name);
        dataPumpProcess.setupConfig();
    }

}
