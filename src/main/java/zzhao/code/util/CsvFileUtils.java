/*
 * @(#) CsvFileUtils.java 2015年12月2日
 * 
 * Copyright 2015 NetEase.com, Inc. All rights reserved.
 */
package zzhao.code.util;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

/**
 *
 * @author zzhao
 * @version 2015年12月2日
 */
public class CsvFileUtils {

    public static void exportExcel(OutputStream os, String headerContent, List<String> headers, List<List<String>> data)
                    throws IOException {
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(os, Charset.forName("utf-8")));
        CSVFormat format = CSVFormat.DEFAULT;
        CSVPrinter printer = new CSVPrinter(writer, format);
        printer.printRecord(headers);
        for (List<String> row : data) {
            printer.printRecord(row);
        }
        printer.close();
    }

    public static void exportExcel(OutputStream os, Charset charset, String headerContent, List<String> headers, List<List<String>> data)
                    throws IOException {
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(os, charset));
        CSVFormat format = CSVFormat.DEFAULT;
        CSVPrinter printer = new CSVPrinter(writer, format);
        printer.printRecord(headers);
        for (List<String> row : data) {
            printer.printRecord(row);
        }
        printer.close();
    }

}
