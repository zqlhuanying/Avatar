package com.personal.java8.cache.request;

/**
 * @author huanying
 */
public class GroupKeys {
    private GroupKeys() { }

    /**
     * 售后明细
     */
    public static GroupKey generatePlatformFlows(String orderSn) {
        return GroupKey.simpleGenerate(CacheTimeUnitEnum.FIVE_SECONDS, orderSn, "platformFlows");
    }

    /**
     * 获取正向物流标签
     */
    public static GroupKey generateLogisticStatus(String orderSn) {
        return GroupKey.simpleGenerate(CacheTimeUnitEnum.FIVE_SECONDS, orderSn, "logisticStatus");
    }

    /**
     * 正向物流
     */
    public static GroupKey generateLogistic(String orderSn) {
        return GroupKey.simpleGenerate(CacheTimeUnitEnum.FIVE_SECONDS, orderSn, "logistic");
    }

    /**
     * 商品信息
     */
    public static GroupKey generateOrderGoods(String orderSn) {
        return GroupKey.simpleGenerate(CacheTimeUnitEnum.FIVE_SECONDS, orderSn, "orderGoods");
    }

    /**
     * 缓存整个Judge模型的请求
     */
    public static GroupKey generateJudgeRequest(String orderSn) {
        return GroupKey.simpleGenerate(CacheTimeUnitEnum.FIVE_SECONDS, orderSn, "judgeRequest");
    }
}
