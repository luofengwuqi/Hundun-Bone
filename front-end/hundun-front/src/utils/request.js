import router from "@/router";
import axios from "axios";
import { useUserStore } from "@/stores/user.js";
import { ElMessage } from "element-plus";
import { show500Error } from '@/utils/500.js'

// 1. 创建实例
const request = axios.create({
  baseURL: "http://localhost:8080/",
  timeout: 5000,
  headers: {
    "Content-Type": "application/json;charset=UTF-8",
  },
});

// 安全获取 store（防止拦截器中获取失败）
let userStore = null;
const getToken = () => {
  if (!userStore) userStore = useUserStore();
  return userStore.token;
};

// 请求拦截器
request.interceptors.request.use((config) => {
  const token = getToken();
  if (token) {
    config.headers.Authorization = `Bearer ${token}`; // 标准格式
  }
  return config;
});

// 响应拦截器
request.interceptors.response.use(
  (response) => {
    // 文件下载特殊处理
    if (response.request.responseType === "blob") return response;

    // 修改判断条件：code 为 0 或 200 都表示成功
    if (response.data.code === 0 || response.data.code === 200) {
      return response.data;
    }

    ElMessage.error(response.data.message || "服务异常");
    return Promise.reject(response.data);
  },

  (error) => {
    // 401 登录过期
    if (error.response?.status === 401) {
      userStore?.setToken("");
      ElMessage.warning("登录已过期，请重新登录");

      if (router.currentRoute.value.path !== "/login") {
        router.push("/login");
      }
    }
    // 其他错误
    else {
      ElMessage.error(error.response?.data?.message || "网络异常");
      show500Error()
    }

    return Promise.reject(error);
  }
);

export default request;
