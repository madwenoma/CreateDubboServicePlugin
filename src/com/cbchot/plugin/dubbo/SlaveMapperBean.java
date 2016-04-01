package com.cbchot.plugin.dubbo;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by Administrator on 2016/3/29.
 */
public class SlaveMapperBean extends AbstarctDubboService {

    private static final String SLAVE_PATH = "dao\\slave\\";

    public SlaveMapperBean(DubboServiceInfo dubboServiceInfo, String parentFilePath) {
        super(dubboServiceInfo, parentFilePath);
    }

    @Override
    protected String getPackageName() {
        return "package com.cbchot.dao.slave;";
    }

    @Override
    protected String getImportInfo() {
        return "import com.cbchot.pojo." + super.getPojoName() + ";\n"  +
                "import java.util.*;\n";
    }

    @Override
    protected String getClassInfo() {
        return "public interface " + super.getSlaveMapperName();
    }

    @Override
    protected String getClassBodyContent() {
        String slaveMethods = "";
        for (String line : super.getMethodNames().split("\n")) {
            if (StringUtils.isEmpty(line))
                continue;
            if (line.startsWith(SLAVE_IDENTIFY)) {
                line = StringUtils.remove(line, SLAVE_IDENTIFY);
                String params[] = StringUtils.substringBetween(line, "(", ")").split(",");
                if (params.length > 1) {
                    line = StringUtils.substringBefore(line, "(") + "(Map<String,Object> params);";
                }
                slaveMethods += line + "\n";
            }
        }
        return slaveMethods;
    }

    @Override
    protected String getFilePath() {
        return SLAVE_PATH + super.getSlaveMapperName();
    }
}
