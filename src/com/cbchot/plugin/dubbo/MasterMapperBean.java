package com.cbchot.plugin.dubbo;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by Administrator on 2016/3/29.
 */
public class MasterMapperBean extends AbstarctDubboService {

    private static final String MASTER_PATH = "dao\\master\\";

    public MasterMapperBean(DubboServiceInfo dubboServiceInfo, String parentFilePath) {
        super(dubboServiceInfo, parentFilePath);
    }

    @Override
    protected String getPackageName() {
        return "package com.cbchot.dao.master;";
    }

    @Override
    protected String getImportInfo() {
        return "import com.cbchot.pojo." + super.getPojoName() + ";\n" +
                "import java.util.*;\n";
    }

    @Override
    protected String getClassInfo() {
        return "public interface " + super.getMasterMapperName();
    }

    @Override
    protected String getClassBodyContent() {
        String masterMethods = "";
        for (String line : super.getMethodNames().split("\n")) {
            if (StringUtils.isEmpty(line))
                continue;
            if (line.startsWith(MASTER_IDENTIFY)) {
                line = StringUtils.remove(line, MASTER_IDENTIFY);
                String params[] = StringUtils.substringBetween(line, "(", ")").split(",");
                if (params.length > 1) {
                    line = StringUtils.substringBefore(line, "(") + "(Map<String,Object> params);";
                }
                masterMethods += line + "\n";
            }
        }
        return masterMethods;
    }

    @Override
    protected String getFilePath() {
        return MASTER_PATH + super.getMasterMapperName();
    }
}
