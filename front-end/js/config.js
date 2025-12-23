/**
 * 全局配置文件
 * 作用：管理接口地址、常量、环境配置，便于切换开发/生产环境
 */
; (function (window) {
  // 环境配置（开发/生产）
  const ENV = "development"; // 切换为"production"即可切换生产环境

  // 接口基础地址（按环境区分）
  const BASE_URL_MAP = {
    development: "http://localhost:8080", // 开发环境
    production: "https://api.15hundun.com" // 生产环境
  };

  // 暴露全局配置
  window.Config = {
    BASE_URL: BASE_URL_MAP[ENV],
    TIMEOUT: 10000, // 请求超时时间（10秒）
    TOKEN_KEY: "user_token" // 存储token的localStorage键名
  };
})(window);