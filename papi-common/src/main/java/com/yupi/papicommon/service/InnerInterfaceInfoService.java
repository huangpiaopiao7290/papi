package com.yupi.papicommon.service;

import com.yupi.papicommon.model.entity.InterfaceInfo;

/**
 * 内部接口信息服务
 */
public interface InnerInterfaceInfoService {

    /**
     * 查询接口是否存在
     * @param path 请求路径
     * @param method 请求方法
     * @return
     */
    InterfaceInfo getInterfaceInfo(String path, String method);
}
