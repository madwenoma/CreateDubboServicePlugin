package com.cbchot.plugin.dubbo;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Administrator on 2016/3/29.
 */
public class ServiceBean extends AbstarctDubboService {
    private static final String SERVICE_PATH = "service\\";

    public ServiceBean(DubboServiceInfo dubboServiceInfo, String parentFilePath) {
        super(dubboServiceInfo, parentFilePath);
    }

    @Override
    protected String getPackageName() {
        return "package com.cbchot.service;";
    }

    @Override
    protected String getImportInfo() {
        String daoImport = "";
        if (super.getMethodNames().contains(SLAVE_IDENTIFY)) {
            daoImport = "import com.cbchot.dao.slave." + super.getSlaveMapperName() + ";\n";
        }
        if (super.getMethodNames().contains(MASTER_IDENTIFY)) {
            daoImport += "import com.cbchot.dao.master." + super.getMasterMapperName() + ";\n";
        }
        return "import org.springframework.beans.factory.annotation.Autowired;\n" +
                "import org.springframework.stereotype.Service;\n" +
                "import java.util.*;\n" +
                "import com.cbchot.pojo." + super.getPojoName() + ";\n" +
                daoImport;
    }

    @Override
    protected String getClassInfo() {
        return "@Service\n public class " + super.getDbServiceName();
    }

    @Override
    protected String getClassBodyContent() {
        String masterMapper = super.getMasterMapperName();
        String members = "";
        if (StringUtils.isNotEmpty(masterMapper)) {
            String lowerMasterMapper = super.firstToLowerCase(masterMapper);
            members += "@Autowired\n" +
                    "private " + masterMapper + " " + lowerMasterMapper + ";\n";
        }

        String slaveMapper = super.getSlaveMapperName();
        if (StringUtils.isNotEmpty(slaveMapper)) {
            String lowerSlaveMapper = super.firstToLowerCase(slaveMapper);
            members += "@Autowired\n" +
                    "private " + slaveMapper + " " + lowerSlaveMapper + ";\n";
        }


        String methods[] = super.getMethodNames().split("\n");
        List<String> lines = Arrays.asList(methods);
//        List<String> lineList = new ArrayList<String>();
        String lineStr = "";
        for (String line : lines) {
            if (StringUtils.isEmpty(line)) {
                continue;
            }
            line = StringUtils.remove(line, ";");
            String[] params = StringUtils.substringBetween(line, "(", ")").split(",");//int a, boolean b,String c
            String invokeParamStr = "";
            for (String param : params) {
                invokeParamStr += param.split(" ")[1] + ",";
            }
            invokeParamStr = StringUtils.substringBeforeLast(invokeParamStr, ",");//É¾³ý×îºóµÄ¶ººÅ(a,b,c)
            if (!line.contains("void")) {

                if (line.contains(MASTER_IDENTIFY)) {
                    line = getMethodBody(masterMapper, line, invokeParamStr, MASTER_IDENTIFY);
                }
                if (line.contains(SLAVE_IDENTIFY)) {
                    line = getMethodBody(slaveMapper, line, invokeParamStr, SLAVE_IDENTIFY);
                }

            } else {

                if (line.contains(MASTER_IDENTIFY)) {
                    line = getVoidMethodBody(masterMapper, line, invokeParamStr, MASTER_IDENTIFY);
                }
                if (line.contains(SLAVE_IDENTIFY)) {
                    line = getVoidMethodBody(slaveMapper, line, invokeParamStr, SLAVE_IDENTIFY);
                }
            }
            lineStr += "public " + line;

        }
        return members + lineStr;
    }

    @NotNull
    private String getMethodBody(String slaveMapper, String line, String invokeParamStr, String slaveIdentify) {

        line = StringUtils.remove(line, slaveIdentify);
        String methodName = StringUtils.substringBetween(line, " ", "(");
        if (invokeParamStr.contains(",")) {
            String mybatisMap = " {\nMap<String, Object> params = new HashMap<String, Object>();\n";
            for (String param : invokeParamStr.split(",")) {
                mybatisMap += "params.put(\"" + param + "\", " + param + ");\n";
            }
            line += mybatisMap;
            line += " return " + super.firstToLowerCase(slaveMapper) + "." + methodName + "(params);\n}\n";
        } else {
            line += " {\n return " + super.firstToLowerCase(slaveMapper) + "." + methodName + "(" + invokeParamStr + ");\n}\n";
        }

        return line;
    }

    @NotNull
    private String getVoidMethodBody(String slaveMapper, String line, String invokeParamStr, String slaveIdentify) {

        line = StringUtils.remove(line, slaveIdentify);
        String methodName = StringUtils.substringBetween(line, " ", "(");
        if (invokeParamStr.contains(",")) {
            String mybatisMap = " {\nMap<String, Object> params = new HashMap<String, Object>();\n";
            for (String param : invokeParamStr.split(",")) {
                mybatisMap += "params.put(\"" + param + "\", " + param + ");\n";
            }
            line += mybatisMap;
            line += super.firstToLowerCase(slaveMapper) + "." + methodName + "(params);\n}\n";
        } else {
            line += " {\n" + super.firstToLowerCase(slaveMapper) + "." + methodName + "(" + invokeParamStr + ");\n}\n";
        }
        return line;
    }

    @Override
    protected String getFilePath() {
        return SERVICE_PATH + super.getDbServiceName();
    }
}
