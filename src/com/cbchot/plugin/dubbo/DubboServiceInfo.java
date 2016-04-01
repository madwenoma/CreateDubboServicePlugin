package com.cbchot.plugin.dubbo;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * Created by Administrator on 2016/3/29.
 */
public class DubboServiceInfo {
    private String serviceName;
    private String pojoName;
    private String methodNames;
    private String dbServiceName;
    private String masterMapperName;
    private String slaveMapperName;

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getPojoName() {
        return pojoName;
    }

    public void setPojoName(String pojoName) {
        this.pojoName = pojoName;
    }

    public String getMethodNames() {
        return methodNames;
    }

    public void setMethodNames(String methodNames) {
        this.methodNames = methodNames;
    }

    public DubboServiceInfo(String serviceName, String pojoName, String methodNames) {
        this.serviceName = serviceName;
        this.pojoName = pojoName;
        this.methodNames = methodNames;
        this.dbServiceName = StringUtils.replace(this.serviceName, "Facade", "Service");
        if (methodNames.contains("@m")) {
            this.masterMapperName = StringUtils.replace(this.serviceName, "Facade", "MasterMapper");
        }
        if (methodNames.contains("@s")) {
            this.slaveMapperName = StringUtils.replace(this.serviceName, "Facade", "SlaveMapper");
        }
    }

    public DubboServiceInfo() {
    }

    public String getDbServiceName() {
        return dbServiceName;
    }

    public void setDbServiceName(String dbServiceName) {
        this.dbServiceName = dbServiceName;
    }

    public String getMasterMapperName() {
        return masterMapperName;
    }

    public void setMasterMapperName(String masterMapperName) {
        this.masterMapperName = masterMapperName;
    }

    public String getSlaveMapperName() {
        return slaveMapperName;
    }

    public void setSlaveMapperName(String slaveMapperName) {
        this.slaveMapperName = slaveMapperName;
    }
}
