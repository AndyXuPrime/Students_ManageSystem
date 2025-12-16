import request from '../utils/request'

// 1. 上传文件 (对接 Resource_service)
export function uploadFile(formData) {
    return request({
        url: '/file/upload',
        method: 'post',
        data: formData,
        headers: { 'Content-Type': 'multipart/form-data' }
    })
}

// 2. 搜索文件 (根据课程名查询)
export function searchFiles(keyword) {
    return request({
        url: '/file/search',
        method: 'get',
        params: { keyword }
    })
}

// 3. 删除文件
export function deleteFile(id) {
    return request({
        url: `/file/delete/${id}`,
        method: 'delete'
    })
}