import request from '../utils/request'

// 1. 分页获取学生列表
export function getStudentPage(params) {
    return request({
        url: '/student/page',
        method: 'get',
        params // { current, size, name }
    })
}

// 2. 获取所有班级 (用于下拉框联动)
export function getAllClasses() {
    return request({
        url: '/class/list',
        method: 'get'
    })
}

// 3. 新增学生
export function addStudent(data) {
    return request({
        url: '/student',
        method: 'post',
        data
    })
}

// 4. 修改学生
export function updateStudent(data) {
    return request({
        url: '/student',
        method: 'put',
        data
    })
}

// 5. 删除学生
export function deleteStudent(sno) {
    return request({
        url: `/student/${sno}`,
        method: 'delete'
    })
}