package io.infoworks.support.utils;

import org.apache.commons.cli.*;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import java.lang.Object;
import java.io.*;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Random;

public class SampleDataGenerator {

    public static void main(String[] args) {
        int numOfRows = 700000; //Integer.getInteger(args[1]);
        int year = 2016, startDay = 306, endDay = 335;

        System.out.println("Usage: SampleDataGenerator");
        System.out.println("       -n --rows   num of rows to generate");
        System.out.println("       -y --year   which year ");
        System.out.println("       -s --start  which day of the year to start");
        System.out.println("       -e --end    which day of the year to end");
        System.out.println("       -f --file   (optional) csv output file name");
        /*    if (args.length > 0) {
            try {
                numOfRows = Integer.parseInt(args[0]);
                year = Integer.parseInt(args[1]);
                startDay = Integer.parseInt(args[2]);
                endDay= Integer.parseInt(args[3]);
                System.out.println("The number of row would be generated is: " + numOfRows
                        + "\nThe year is: " + year
                        + "\nThe start day of the year is: " + startDay
                        + "\nThe end day of the year is: " + endDay);
            } catch (NumberFormatException e) {
                System.err.println("Argument" + args[0] + " must be an integer.");
                System.exit(1);
            }
        } */

        Options options = new Options();

        Option num = new Option("n", "rows", true, "number of generated rows");
        num.setRequired(true);
        options.addOption(num);

        Option yr = new Option("y", "year", true, "year of the date");
        yr.setRequired(true);
        options.addOption(yr);

        Option start = new Option("s", "start", true, "start day of the year");
        start.setRequired(true);
        options.addOption(start);

        Option end = new Option("e", "end", true, "end day of the year");
        end.setRequired(true);
        options.addOption(end);

        Option file = new Option("f", "file", true, "csv file name");
        file.setRequired(false);
        options.addOption(file);

        CommandLineParser parser = new GnuParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("utility-name", options);
            System.exit(1);
            return;
        }

        numOfRows = Integer.parseInt(cmd.getOptionValue("n"));
        year = Integer.parseInt(cmd.getOptionValue("y"));
        startDay = Integer.parseInt(cmd.getOptionValue("s"));
        endDay = Integer.parseInt(cmd.getOptionValue("e"));
        String fileName = cmd.getOptionValue("f");
        if (fileName == null)
            fileName = "generated_out.csv";
        System.out.println("\nNum of rows genereated: " + numOfRows + ", for Year " + year + ", from day " + startDay + " to " + endDay);
        System.out.println("Output file name: " + fileName);

        long startTime = System.currentTimeMillis();
        generateCsvFile(fileName, numOfRows, year, startDay, endDay);
        long endTime = System.currentTimeMillis();
        System.out.println("The time for generating the cvs file is " + (endTime - startTime) + " milliseconds");
    }

    private static void generateCsvFile(String fileName, int numOfRows, int year, int startDay, int endDay) {
        //DataOutputStream out = null;
        PrintWriter out = null;

        try {
            // out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream("cvs_sample.txt")));
            out = new PrintWriter(fileName);

            out.println("prescription_id,fill_date,customer_id,store_id,description,create_date,update_date");
            //     System.out.printf("prescription_id,fill_date,customer_id,store_id,description,create_date,update_date\n");
            int count = 1;
            for (int i = 0; i < numOfRows; i++) {
                int prescription_id = count++;
                //       System.out.print(prescription_id + ",");
                out.write(Integer.toString(prescription_id));
                out.write(",");
           /*   fill_date: Random date between 2016-11-01 to 2016-11-30
                customer_id:  Random number between 1 & 1000 (duplicates ok)
                store_id: Random number between 1 & 100 (duplicates ok)
                description: Version 1.0
                create_date & update_date: Today date. */
                String fill_date = getRandomDate(year, startDay, endDay, false); //getRandomDate(2016, 306, 335, false);
                //      System.out.print(fill_date + ",");
                out.write(fill_date + ",");
                int customer_id = getRandomNumberInRange(1, 1000);
                out.write(Integer.toString(customer_id));
                out.write(",");
                //      System.out.print(customer_id + ",");
                int store_id = getRandomNumberInRange(1, 100);
                out.write(Integer.toString(store_id));
                out.write(",");
                //      System.out.print(store_id + ",");
                out.write("Version 1.0,");
                //      System.out.print("Version 1.0,");
                Calendar today = Calendar.getInstance();
                //      today.set(Calendar.DAY_OF_MONTH, 0); // same for minutes and seconds
                //String create_date = new SimpleDateFormat("yyyy-MM-dd").format(today.getTime());
                Timestamp create_date = new Timestamp(System.currentTimeMillis());
                out.write("\""+create_date+'\"' + ",");
                out.println("\""+create_date+'\"');
                //       System.out.printf(create_date + "," + create_date);
            }
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            out.close();
        }

    }
    private static int getRandomNumberInRange(int min, int max) {

        if (min >= max) {
            throw new IllegalArgumentException("max must be greater than min");
        }

        Random r = new Random();
        return r.nextInt((max - min) + 1) + min;
    }

    private static String getRandomDate(int yr, int min, int max, boolean isDate) {

        GregorianCalendar gc = new GregorianCalendar();

        int year = randBetween(yr, yr);

        gc.set(gc.YEAR, year);

      //  int dayOfYear = randBetween(1, gc.getActualMaximum(gc.DAY_OF_YEAR));
        int dayOfYear = randBetween(min, max);

        gc.set(gc.DAY_OF_YEAR, dayOfYear);

        String gDate = null;
        if (isDate) {
            gDate = gc.get(gc.YEAR) + "-" + (gc.get(gc.MONTH) + 1) + "-" + gc.get(gc.DAY_OF_MONTH);
        }
        else {
            String mm = null, dom = null;
            int month = gc.get(gc.MONTH) + 1;
            if (month < 10)
                mm = "0" + month;
            else
                mm = String.valueOf(month);
            int day = gc.get(gc.DAY_OF_MONTH);
            if (day < 10)
                dom = "0" + day;
            else
                dom = String.valueOf(day);
            gDate = gc.get(gc.YEAR) + mm + dom;
        }

        return gDate;
    }

    public static int randBetween(int start, int end) {
        return start + (int)Math.round(Math.random() * (end - start));
    }
}
