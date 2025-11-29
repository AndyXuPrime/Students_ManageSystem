package main.java.com.javaee_hw.service.implement;

import com.javaee_hw.entity.ClassInfo;
import com.javaee_hw.repository.ClassInfoRepository;
import com.javaee_hw.service.IClassInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ClassInfoServiceImpl implements IClassInfoService {

    @Autowired
    private ClassInfoRepository classInfoRepository;

    @Override
    public List<ClassInfo> getAllClasses() {
        return classInfoRepository.findAll();
    }
}