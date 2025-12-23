/**
 * 工具函数库
 * 作用：封装提示、存储、参数校验等通用功能，避免重复代码
 */
; (function (window) {
  const Utils = {
    /**
     * 显示错误提示
     * @param {string} msg - 提示信息
     * @param {string} selector - 提示元素选择器（默认#errorTip）
     */
    showErrorTip: function (msg, selector = "#errorTip") {
      const tipEl = document.querySelector(selector);
      if (!tipEl) return;

      tipEl.textContent = msg;
      tipEl.style.display = "block";

      // 3秒后自动隐藏
      setTimeout(() => {
        tipEl.style.display = "none";
      }, 3000);
    },

    /**
     * 显示成功提示（可扩展）
     * @param {string} msg - 提示信息
     */
    showSuccessTip: function (msg) {
      alert(msg); // 可替换为更美观的弹窗
    },

    /**
     * 存储token到localStorage
     * @param {string} token - 登录令牌
     */
    setToken: function (token) {
      localStorage.setItem(Config.TOKEN_KEY, token);
    },

    /**
     * 获取localStorage中的token
     * @returns {string|null} token值
     */
    getToken: function () {
      return localStorage.getItem(Config.TOKEN_KEY);
    },

    /**
     * 清除token
     */
    clearToken: function () {
      localStorage.removeItem(Config.TOKEN_KEY);
    },

    /**
     * 简单的参数非空校验
     * @param {object} data - 待校验的参数对象
     * @param {array} requiredFields - 必传字段数组
     * @returns {string|null} 校验失败返回提示语，成功返回null
     */
    validateParams: function (data, requiredFields) {
      for (const field of requiredFields) {
        if (!data[field] || data[field].trim() === "") {
          return `请输入${this.getFieldName(field)}`;
        }
      }
      return null;
    },

    /**
     * 字段名转中文（适配提示）
     * @param {string} field - 字段名
     * @returns {string} 中文名称
     */
    getFieldName: function (field) {
      const nameMap = {
        username: "用户名",
        password: "密码",
        checkPassword: "确认密码"
      };
      return nameMap[field] || field;
    }
  };

  // 暴露工具函数
  window.Utils = Utils;
})(window);