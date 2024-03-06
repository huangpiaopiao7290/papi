package com.yupi.papicommon.service;

/**
 * 内部用户接口信息服务
 */
public interface InnerUserInterfaceInfoService {

    /**
     * 调用接口统计
     * @param interfaceInfoId 接口id
     * @param userId 用户id
     * @return
     */
    boolean invokeCount(long interfaceInfoId, long userId);
}
