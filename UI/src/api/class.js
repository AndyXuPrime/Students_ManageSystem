import request from '../utils/request'

// 获取班级列表
export function getAllClasses() {
    return request({
        url: '/class/list',
        method: 'get'
    })
}

// 新增班级
export function addClass(data) {
    return request({
        url: '/class/add',
        method: 'post',
        data
    })
}

// 修改班级
export function updateClass(data) {
    return request({
        url: '/class/update',
        method: 'put',
        data
    })
}

// 删除班级
export function deleteClass(classno) {
    return request({
        url: `/class/delete/${classno}`,
        method: 'delete'
    })
}