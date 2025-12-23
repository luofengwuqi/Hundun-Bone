/**
 * 用户相关接口封装
 * 作用：所有用户接口集中管理，便于修改接口路径/参数
 */
; (function (window) {
  // 初始化axios配置
  axios.defaults.baseURL = Config.BASE_URL;
  axios.defaults.timeout = Config.TIMEOUT;
  axios.defaults.headers["Content-Type"] = "application/json";

  // 请求拦截器（添加token、处理请求前逻辑）
  axios.interceptors.request.use(
    (config) => {
      // 给需要认证的接口添加token
      const token = Utils.getToken();
      if (token) {
        config.headers["Authorization"] = `Bearer ${token}`;
      }
      return config;
    },
    (error) => Promise.reject(error)
  );

  // 响应拦截器（统一处理错误）
  axios.interceptors.response.use(
    (response) => {
      // 只返回响应数据，简化业务层处理
      return response.data;
    },
    (error) => {
      // 统一错误处理
      let errorMsg = "请求失败，请重试";
      if (error.response) {
        // 后端返回的错误
        const status = error.response.status;
        const data = error.response.data;

        switch (status) {
          case 401:
            errorMsg = "登录已过期，请重新登录";
            Utils.clearToken();
            window.location.href = "index.html"; // 跳回登录页
            break;
          case 403:
            errorMsg = "无权限访问";
            break;
          case 404:
            errorMsg = "接口不存在";
            break;
          case 500:
            errorMsg = "服务器内部错误";
            break;
          default:
            errorMsg = data.message || errorMsg;
        }
      } else if (error.request) {
        // 网络错误
        errorMsg = "网络异常，请检查网络";
      }
      // 抛出错误，让业务层捕获
      return Promise.reject(new Error(errorMsg));
    }
  );

  // 用户接口集合
  const UserApi = {
    /**
     * 用户登录
     * @param {object} params - 登录参数 {username, password}
     * @returns {Promise} Promise对象
     */
    login: function (params) {
      return axios.post("/user/login", params);
    },

    /**
     * 用户注册
     * @param {object} params - 注册参数 {username, password, checkPassword}
     * @returns {Promise} Promise对象
     */
    register: function (params) {
      return axios.post("/user/register", params);
    }
  };

  // 暴露接口
  window.UserApi = UserApi;
})(window);