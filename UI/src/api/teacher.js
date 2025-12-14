import request from '../utils/request'

// 1. 获取所有教师 (用于下拉框联动 & 列表展示)
export function getAllTeachers() {
    return request({
        url: '/teacher/list',
        method: 'get'
    })
}

// 2. 新增教师
export function addTeacher(data) {
    return request({
        url: '/teacher/add',
        method: 'post',
        data
    })
}

// 3. 修改教师
export function updateTeacher(data) {
    return request({
        url: '/teacher/update',
        method: 'put',
        data
    })
}

// 4. 删除教师
export function deleteTeacher(tno) {
    return request({
        url: `/teacher/delete/${tno}`,
        method: 'delete'
    })
}