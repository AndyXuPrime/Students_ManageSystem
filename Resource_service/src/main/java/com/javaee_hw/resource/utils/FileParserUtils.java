package com.javaee_hw.resource.utils;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import java.util.UUID;

/**
 * 文件元数据处理工具类
 * (替代了原计划的 Tika 内容解析，改为轻量级的文件名处理)
 */
@Component
public class FileParserUtils {

    /**
     * 获取文件后缀名 (不带点)
     * 例如: "test.docx" -> "docx"
     */
    public String getFileExtension(String filename) {
        if (!StringUtils.hasText(filename)) {
            return "unknown";
        }
        return StringUtils.getFilenameExtension(filename);
    }

    /**
     * 生成唯一文件名 (防止 MinIO 覆盖同名文件)
     * 格式: UUID.后缀
     * 例如: "550e8400-e29b-41d4-a716-446655440000.pdf"
     */
    public String generateUniqueFilename(String originalFilename) {
        String suffix = getFileExtension(originalFilename);
        if (StringUtils.hasText(suffix)) {
            suffix = "." + suffix;
        } else {
            suffix = "";
        }
        return UUID.randomUUID().toString() + suffix;
    }

    /**
     * 根据后缀名判断是否为常见文档
     * (辅助方法，可用于前端展示图标或后端分类)
     */
    public boolean isDocument(String extension) {
        if (extension == null) return false;
        String ext = extension.toLowerCase();
        return ext.equals("doc") || ext.equals("docx") ||
                ext.equals("pdf") || ext.equals("ppt") ||
                ext.equals("pptx") || ext.equals("txt");
    }
}