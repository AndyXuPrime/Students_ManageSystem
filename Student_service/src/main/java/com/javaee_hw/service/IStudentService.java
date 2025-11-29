package main.java.com.javaee_hw.service;
import com.javaee_hw.entity.Student;
import org.springframework.data.domain.Page;

public interface IStudentService {
    /**
     * 分页查询学生
     * @param page 页码 (0开始)
     * @param size 每页数量
     * @param name 姓名模糊查询
     * @return 分页对象
     */
    Page<Student> getStudentPage(int page, int size, String name);

    /**
     * 保存或更新学生
     * @param student 学生实体
     * @return 保存后的实体
     */
    Student saveStudent(Student student); 

    /**
     * 根据学号删除
     * @param sno 学号
     */
    void deleteStudent(String sno);

    /**
     * 根据学号查询详情
     * @param sno 学号
     * @return 学生实体
     */
    Student getStudentById(String sno);
}