import request from "../request";


/**
 * 用户登录
 * @param {Object} params - 登录参数
 * @param {string} params.userName - 用户名
 * @param {string} params.userPassword - 密码
 * @returns {Promise} - 后端返回的登录数据（比如token、用户id）
 */

export const userLogin = (params) =>{
    return request.post("/login",params);
}

export const userRegister = (params) =>{
    return request.post("/register",params);
}