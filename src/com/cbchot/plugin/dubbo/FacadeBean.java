package com.cbchot.plugin.dubbo;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Administrator on 2016/3/29.
 */
public class FacadeBean extends AbstarctDubboService {
    private static final String FACADE_PATH = "facade\\";

    public FacadeBean(DubboServiceInfo dubboServiceInfo, String parentFilePath) {
        super(dubboServiceInfo, parentFilePath);
    }


    @Override
    protected String getPackageName() {
        return "package com.cbchot.facade;";
    }

    @Override
    protected String getImportInfo() {
        return "import org.springframework.beans.factory.annotation.Autowired;\n" +
                "import org.springframework.stereotype.Component;\n" +
                "import com.cbchot.ifacade.I" + super.getServiceName() + ";\n" +
                "import com.cbchot.pojo." + super.getPojoName() + ";\n" +
                "import com.cbchot.service." + getDbServiceName() + ";\n" +
                "import java.util.*;\n";
    }

    @Override
    protected String getClassInfo() {
        return "@Component\n public class " + super.getServiceName() + " implements I" + super.getServiceName();
    }

    @Override
    protected String getClassBodyContent() {
        String dbServiceName = getDbServiceName();
        String lowerDbServiceName = dbServiceName.replaceFirst(dbServiceName.substring(0, 1), dbServiceName.substring(0, 1).toLowerCase());
        String members = "@Autowired\n" +
                "private " + dbServiceName + " " + lowerDbServiceName + ";\n";

        String methodNames = StringUtils.remove(super.getMethodNames(), MASTER_IDENTIFY);
        methodNames = StringUtils.remove(methodNames, SLAVE_IDENTIFY);
        String methods[] = methodNames.split("\n");
        List<String> lines = Arrays.asList(methods);
//        List<String> lineList = new ArrayList<String>();
        String lineStr = "";
        for (String line : lines) {
            if (StringUtils.isEmpty(line)) {
                continue;
            }
            line = StringUtils.remove(line, ";");
            String methodName = StringUtils.substringBetween(line, " ", "(");
            String[] params = StringUtils.substringBetween(line, "(", ")").split(",");//int a, boolean b,String c
            String invokeParamStr = "";
            for (String param : params) {
                invokeParamStr += param.split(" ")[1] + ",";
            }
            invokeParamStr = StringUtils.substringBeforeLast(invokeParamStr, ",");//É¾³ý×îºóµÄ¶ººÅ


            if (!line.contains("void")) {
                line += " {\n return " + lowerDbServiceName + "." + methodName + "(" + invokeParamStr + ");\n}\n";
            } else {
//                line += "{\n}\n";
                line += " {\n" + lowerDbServiceName + "." + methodName + "(" + invokeParamStr + ");\n}\n";
            }
//            lineList.add(line);
            lineStr += "public " + line;

        }
        return members + lineStr;
    }


    @Override
    protected String getFilePath() {
        return FACADE_PATH + super.getServiceName();
    }

}
