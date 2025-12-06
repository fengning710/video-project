// 导入封装好的 request 工具
import request from "../request";

/**
 * 分页查询视频（首页/搜索页用）
 * @param {Object} params - 查询参数（和后端接口对齐）
 * @param {number} params.pageNum - 页码（默认1）
 * @param {number} params.pageSize - 每页条数（默认10）
 * @param {string} params.keyword - 搜索关键词（默认空）
 * @returns {Promise} - 后端返回的分页结果（code、message、data）
 */

export const getVideoPage = (params = {}) => {
    // 默认参数（后端接口的默认值，防止用户没传）
    const defaultParams = {
        pageNum: 1,
        pageSize: 10,
        keyword: ''
    }
    // 合并用户传的参数和默认参数（用户传了就覆盖默认值）
    const finalParams = { ...defaultParams, ...params }
    return request({
        url: '/videos/list', // 后端分页接口 URL（和后端完全对齐）
        method: 'get',
        params: finalParams // 传递分页+关键词参数
    })
}

/**
 * 获取单个视频详情（播放页用）
 * @param {string} videoId - 视频VID（和后端接口参数对齐，比如 VMFbSHjXbY）
 * @returns {Promise} - 后端返回的视频详情（code、message、data）
 */
export const getVideoInfo = (videoId) => {
    return request({
        url: `/video/info/${videoId}`, // 后端详情接口 URL（和后端完全对齐）
        method: 'get'
    })
}

// 后续可补充整合视频列表查询