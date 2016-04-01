package com.cbchot.plugin.dubbo;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by Administrator on 2016/3/29.
 */
public class IFacadeBean extends AbstarctDubboService {
    private static final String IFACADE_PATH = "ifacade\\";

    public IFacadeBean(DubboServiceInfo dubboServiceInfo, String parentFilePath) {
        super(dubboServiceInfo, parentFilePath);
    }

    @Override
    protected String getPackageName() {
        return "package com.cbchot.ifacade;";
    }

    @Override
    protected String getImportInfo() {
        return "import com.cbchot.pojo." + super.getPojoName() + ";" +
                "import java.util.*;";

    }

    @Override
    protected String getClassInfo() {
        return "public interface I" + super.getServiceName();
    }

    @Override
    protected String getClassBodyContent() {
        return StringUtils.remove(super.getMethodNames(), MASTER_IDENTIFY).replace(SLAVE_IDENTIFY, "");
    }

    @Override
    protected String getFilePath() {
        return IFACADE_PATH + "I" + super.getServiceName();
    }
}
