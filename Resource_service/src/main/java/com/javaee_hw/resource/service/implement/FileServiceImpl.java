package com.javaee_hw.resource.service.implement;

import com.javaee_hw.resource.entity.CourseResource;
import com.javaee_hw.resource.repository.ResourceRepository;
import com.javaee_hw.resource.service.IFileService;
import com.javaee_hw.resource.utils.FileParserUtils;
import com.javaee_hw.resource.utils.MinioUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;

@Service
public class FileServiceImpl implements IFileService {

    @Autowired
    private MinioUtils minioUtils;

    @Autowired
    private FileParserUtils fileParserUtils;

    @Autowired
    private ResourceRepository resourceRepository; // 只保留 MySQL Repository

    // 删除了 ResourceEsRepository

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String upload(MultipartFile file, String courseName) {
        // 1. 获取文件信息
        String originalFilename = file.getOriginalFilename();
        String fileType = fileParserUtils.getFileExtension(originalFilename);
        long fileSize = file.getSize();

        // 2. 上传 MinIO
        String fileUrl = minioUtils.upload(file);

        // 3. 存入 MySQL
        CourseResource mysqlEntity = new CourseResource();
        mysqlEntity.setFileName(originalFilename);
        mysqlEntity.setFileType(fileType);
        mysqlEntity.setFileUrl(fileUrl);
        mysqlEntity.setCourseName(courseName);
        mysqlEntity.setFileSize(fileSize);
        mysqlEntity.setUploadTime(new Date());

        resourceRepository.save(mysqlEntity);

        // 4. ES 同步逻辑已删除

        return fileUrl;
    }

    @Override
    public List<CourseResource> search(String keyword) {
        // 改用 MySQL 的模糊查询
        return resourceRepository.findByFileNameContainingOrCourseNameContaining(keyword, keyword);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        resourceRepository.deleteById(id);
        // ES 删除逻辑已删除
    }
}