package com.cbchot.plugin.dubbo;

/**
 * Created by Administrator on 2016/3/29.
 */
public class PojoBean  extends AbstarctDubboService{

    private static final String POJO_PATH = "pojo\\";

    public PojoBean(DubboServiceInfo dubboServiceInfo, String parentFilePath) {
        super(dubboServiceInfo, parentFilePath);
    }

    @Override
    protected String getPackageName() {
        return "package com.cbchot.pojo;";
    }

    @Override
    protected String getImportInfo() {
        return "";
    }

    @Override
    protected String getClassInfo() {
        return "public class " + super.getPojoName();
    }

    @Override
    protected String getClassBodyContent() {
        return "";
    }

    @Override
    protected String getFilePath() {
        return POJO_PATH + super.getPojoName();
    }
}
