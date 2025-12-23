/**
 * 登录页面业务逻辑
 * 作用：仅处理登录页面的交互逻辑，依赖工具函数和接口封装
 */
; (function (window) {
  // 页面加载完成后执行
  window.onload = function () {
    // 绑定登录按钮点击事件（替代HTML中的onclick）
    const loginBtn = document.querySelector(".container button");
    if (loginBtn) {
      loginBtn.addEventListener("click", handleLogin);
    }

    // 绑定回车登录（增强体验）
    document.addEventListener("keydown", function (e) {
      if (e.key === "Enter") {
        handleLogin();
      }
    });
  };

  /**
   * 处理登录逻辑
   */
  function handleLogin() {
    // 1. 获取输入参数
    const loginForm = document.querySelector("#loginForm");
    const formData = new FormData(loginForm);
    const params = {
      username: formData.get("username")?.trim() || "",
      password: formData.get("password")?.trim() || ""
    };

    // 2. 前端参数校验
    const validateMsg = Utils.validateParams(params, ["username", "password"]);
    if (validateMsg) {
      Utils.showErrorTip(validateMsg);
      return;
    }

    // 3. 调用登录接口
    UserApi.login(params)
      .then((res) => {
        // 4. 登录成功处理
        Utils.setToken(res.token); // 存储token
        Utils.showSuccessTip("登录成功！");
        // 跳转到首页（根据实际需求修改）
        window.location.href = "home.html";
      })
      .catch((error) => {
        // 5. 登录失败处理
        Utils.showErrorTip(error.message);
        console.error("登录失败详情：", error); // 控制台打印详情，便于调试
      });
  }

  // 暴露全局函数（可选，便于调试）
  window.handleLogin = handleLogin;
})(window);