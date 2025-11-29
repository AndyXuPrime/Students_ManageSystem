package main.java.com.javaee_hw.service.implement;

import com.sims.entity.Student;
import com.sims.repository.StudentRepository;
import com.sims.service.IStudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class StudentServiceImpl implements IStudentService {

    @Autowired
    private StudentRepository studentRepository;

    @Override
    public Page<Student> getStudentPage(int page, int size, String name) {
        // JPA 的页码是从 0 开始的，所以要 -1
        Pageable pageable = PageRequest.of(page - 1, size);
        
        if (name != null && !name.isEmpty()) {
            // 调用我们定义的“魔法方法”
            return studentRepository.findBySnameContaining(name, pageable);
        } else {
            // 调用默认的全查方法
            return studentRepository.findAll(pageable);
        }
    }

    @Override
    public Student saveStudent(Student student) {
        // JPA 的 save 方法：如果主键存在则更新，不存在则插入
        return studentRepository.save(student);
    }

    @Override
    public void deleteStudent(String sno) {
        studentRepository.deleteById(sno);
    }

    @Override
    public Student getStudentById(String sno) {
        return studentRepository.findById(sno).orElse(null);
    }
}