package zzhao.code.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.record.formula.functions.T;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 *
 * @author zzhao
 * @version 2014-5-29
 */
public class FileContentUtils {

    public static void writeToFile(Collection<T> contents, String file) throws IOException {
        FileWriter writer = new FileWriter(file);
        for (Object obj : contents) {
            writer.write(obj.toString() + "\n");
        }
        writer.close();
    }

    public static List<String> readAsList(String file) {
        File in = new File(file);
        List<String> result = Lists.newLinkedList();
        try {
            FileReader fr = new FileReader(in);
            BufferedReader reader = new BufferedReader(fr);
            String data = reader.readLine();
            while (StringUtils.isNotEmpty(data)) {
                result.add(data);
                data = reader.readLine();
            }
            reader.close();
            fr.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public static Set<String> readAsSet(String file) {
        File in = new File(file);
        Set<String> result = Sets.newLinkedHashSet();
        try {
            FileReader fr = new FileReader(in);
            BufferedReader reader = new BufferedReader(fr);
            String data = reader.readLine();
            while (StringUtils.isNotEmpty(data)) {
                result.add(data);
                data = reader.readLine();
            }
            reader.close();
            fr.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public static String readAsString(String file) {
        File in = new File(file);
        StringBuffer sb = new StringBuffer();
        try {
            FileReader fr = new FileReader(in);
            BufferedReader reader = new BufferedReader(fr);
            String data = reader.readLine();
            while (data != null) {
                sb.append(data + "\n");
                data = reader.readLine();
            }
            reader.close();
            fr.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }
}
