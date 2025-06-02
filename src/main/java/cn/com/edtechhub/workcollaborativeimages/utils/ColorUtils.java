package cn.com.edtechhub.workcollaborativeimages.utils;

import lombok.extern.slf4j.Slf4j;

import java.awt.*;

/**
 * 工具类：计算颜色相似度
 */
@Slf4j
public class ColorUtils {

    /**
     * 计算两个颜色的相似度(0~1, 1 为完全相同)
     */
    public static double calculateSimilarity(Color color1, Color color2) {
        // 获取三色值
        int r1 = color1.getRed();
        int g1 = color1.getGreen();
        int b1 = color1.getBlue();

        int r2 = color2.getRed();
        int g2 = color2.getGreen();
        int b2 = color2.getBlue();

        // 计算欧氏距离
        double distance = Math.sqrt(Math.pow(r1 - r2, 2) + Math.pow(g1 - g2, 2) + Math.pow(b1 - b2, 2));

        // 计算相似度
        double res = 1 - distance / Math.sqrt(3 * Math.pow(255, 2));
        log.debug("比较 ({}, {}, {}) 和 ({}, {}, {}), 得到相似度为 {} (0~1, 1 为完全相同)", r1, g1, b1, r2, g2, b2, res);
        return res;
    }

}

