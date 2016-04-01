package com.cbchot.plugin.dubbo;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/3/29.
 */
public abstract class AbstarctDubboService extends DubboServiceInfo {

    private String parentFilePath;

    private static final String LINE_SEPERATOR = "\n";

    private static final String JAVA_FILE_PATH = "\\src\\main\\java\\com\\cbchot\\";


    private static final String SLAVE_MAPPER_PATH = "dao\\slave";
    private static final String MASTER_MAPPER_PATH = "dao\\master";

    protected static final String MASTER_IDENTIFY = "@m ";
    protected static final String SLAVE_IDENTIFY = "@s ";

    protected abstract String getPackageName();


    protected abstract String getImportInfo();


    protected abstract String getClassInfo();

    protected abstract String getClassBodyContent();

    protected abstract String getFilePath();


    public AbstarctDubboService(DubboServiceInfo dubboServiceInfo, String parentFilePath) {
        super(dubboServiceInfo.getServiceName(), dubboServiceInfo.getPojoName(), dubboServiceInfo.getMethodNames());
        this.parentFilePath = parentFilePath;
    }

    public List<String> getFileContent() {
        List<String> lines = new ArrayList<String>();
        lines.add(getPackageName() + LINE_SEPERATOR);
        lines.add(getImportInfo() + LINE_SEPERATOR);
        lines.add(getClassInfo() + " {" + LINE_SEPERATOR);
        lines.add(getClassBodyContent() + "\n}" + LINE_SEPERATOR);
        return lines;
    }


    public String getParentFilePath() {
        return parentFilePath;
    }

    public void setParentFilePath(String parentFilePath) {
        this.parentFilePath = parentFilePath;
    }

    public void createFile() throws IOException {
        FileUtils.writeLines(getFullFilePath(), "UTF-8", getFileContent());
    }

    protected String firstToLowerCase(String str) {
        return str.replaceFirst(str.substring(0, 1), str.substring(0, 1).toLowerCase());
    }

    public boolean deleteFile() {
        return getFullFilePath().delete();
    }

    public File getFullFilePath() {
        File file = new File(this.getParentFilePath() + JAVA_FILE_PATH + this.getFilePath() + ".java");
        return file;
    }

}






