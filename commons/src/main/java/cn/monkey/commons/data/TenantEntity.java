package cn.monkey.commons.data;

import java.io.Serializable;

public interface TenantEntity extends Serializable {
    String getTenantId();

    void setTenantId(String tenantId);

    String getPartitionId();

    void setPartitionId(String partitionId);
}
