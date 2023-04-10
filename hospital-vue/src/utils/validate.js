/**
 * 验证邮箱
 * @param {*} s
 */
export function isEmail(s) {
	return /^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+((.[a-zA-Z0-9_-]{2,3}){1,2})$/.test(s)
}

/**
 * 验证手机号码
 * @param {*} s
 */
export function isMobile(s) {
	return /^1[0-9]{10}$/.test(s)
}

/**
 * 验证电话号码
 * @param {*} s
 */
export function isPhone(s) {
	return /^([0-9]{3,4}-)?[0-9]{7,8}$/.test(s)
}

/**
 * 验证URL地址
 * @param {*} s
 */
export function isURL(s) {
	return /^http[s]?:\/\/.*/.test(s)
}

/**
 * 验证用户名
 * @param {Object} s
 */
export function isUsername(s) {
	return /^[a-zA-Z0-9]{5,50}$/.test(s)
}


/**
 * 验证密码
 * @param {Object} s
 */
export function isPassword(s) {
	return /^[a-zA-Z0-9]{6,20}$/.test(s)
}
