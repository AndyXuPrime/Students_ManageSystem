import request from '../utils/request'

// 1. 获取所有课程
export function getCoursePage(params) {
    // 这里为了配合 MainConsole.vue 的逻辑，虽然是 list，但我们可以传参做过滤
    // 如果后端没做分页，这里 params 暂时可能只用到 keyword
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