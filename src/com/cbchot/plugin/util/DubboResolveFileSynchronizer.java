package com.cbchot.plugin.util;

import com.intellij.openapi.ui.Messages;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/4/1.
 */
public class DubboResolveFileSynchronizer {


    private static final String USER_DIR = System.getProperty("user.home");
    private static final String RESOLVE_FILE = USER_DIR + "/dubbo-resolve.properties";


    public static File getResolveFile() {
        return new File(RESOLVE_FILE);
    }

    @Nullable
    public static File synchronizeDubboResolve(BufferedReader reader) throws IOException {
        List<String> lineList = new ArrayList<String>();
        String readLine;
        while ((readLine = reader.readLine()) != null) {
            lineList.add(readLine);
        }
        reader.close();
        List<String> serviceInfoLines = new ArrayList<String>(150);
        String serviceInfo;
        if (lineList.size() > 0) {
            for (String line : lineList) {
                String lineWithoutBlank = line.trim();
                if (lineWithoutBlank.startsWith("<dubbo:service")) {
                    serviceInfo = getServiceInfoLine(lineWithoutBlank);
                    serviceInfoLines.add(serviceInfo);
                }
            }
        }
        if (serviceInfoLines.size() == 0) {
            showMsg("service size is zero,please check file content first");
            return null;
        }
        File resolveFile = new File(RESOLVE_FILE);

        if (!resolveFile.exists()) {
            showMsg("resolveFile is not exists");
        }
        FileWriter writer = new FileWriter(resolveFile, false);
        for (String serviceInfoLine : serviceInfoLines) {
            writer.write(serviceInfoLine + "=dubbo://127.0.0.1:20880" + "\r\n");
        }
        writer.flush();
        writer.close();
        return resolveFile;
    }


    private static String getServiceInfoLine(String lineWithoutBlank) {
        String[] strs = lineWithoutBlank.split("\"");
        String serviceInfo = strs[1];
        return serviceInfo;
    }

    private static void showMsg(String msg) {

        Messages.showMessageDialog(
                msg,
                "Dubbo Config Transformer",
                Messages.getInformationIcon()
        );
    }

}
