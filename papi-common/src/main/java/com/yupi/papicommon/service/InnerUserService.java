package com.yupi.papicommon.service;

import com.yupi.papicommon.model.entity.User;

/**
 * 内部用户服务
 */
public interface InnerUserService {

    /**
     * 查询是否给用户分配密钥
     * @param accessKey
     * @return
     */
    User getInvokerUser(String accessKey);
}
