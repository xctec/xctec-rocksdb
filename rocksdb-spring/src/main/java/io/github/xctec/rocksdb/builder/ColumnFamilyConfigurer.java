package io.github.xctec.rocksdb.builder;

public class ColumnFamilyConfigurer {
    private String cfName = "default";

    private ColumnFamilyOptionsConfigurer configurer = x -> {
    };

    private ColumnFamilyOperationsConfigurer operationsConfigurer = x -> {
    };

    public ColumnFamilyConfigurer(String cfName) {
        this.cfName = cfName;
    }

    public ColumnFamilyConfigurer(String cfName, ColumnFamilyOptionsConfigurer configurer, ColumnFamilyOperationsConfigurer operationsConfigurer) {
        this.cfName = cfName;
        if (configurer != null) {
            this.configurer = configurer;
        }
        if (operationsConfigurer != null) {
            this.operationsConfigurer = operationsConfigurer;
        }
    }

    public String getCfName() {
        return cfName;
    }

    public void setCfName(String cfName) {
        this.cfName = cfName;
    }

    public ColumnFamilyOptionsConfigurer getConfigurer() {
        return configurer;
    }

    public void setConfigurer(ColumnFamilyOptionsConfigurer configurer) {
        this.configurer = configurer;
    }

    public ColumnFamilyOperationsConfigurer getOperationsConfigurer() {
        return operationsConfigurer;
    }

    public void setOperationsConfigurer(ColumnFamilyOperationsConfigurer operationsConfigurer) {
        this.operationsConfigurer = operationsConfigurer;
    }
}
