import request from '../utils/request'

// 1. 获取所有课程
export function getCoursePage(params) {
    return request({
        url: '/course/list',
        method: 'get',
        params
    })
}

// 2. 新增课程
export function addCourse(data) {
    return request({
        url: '/course/add',
        method: 'post',
        data
    })
}

// 3. 修改课程
export function updateCourse(data) {
    return request({
        url: '/course/update',
        method: 'put',
        data
    })
}

// 4. 删除课程
export function deleteCourse(cno) {
    return request({
        url: `/course/delete/${cno}`,
        method: 'delete'
    })
}