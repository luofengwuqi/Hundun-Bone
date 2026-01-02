function handleLogin() {
  // 1. 获取输入参数
  const loginForm = document.querySelector("#loginForm");
  const formData = new FormData(loginForm);
  const username = formData.get("username")?.trim() || "";
  const password = formData.get("password")?.trim() || "";
  
  const params = {
    username: username,
    password: password
  };

  // 2. 前端参数校验
  const validateMsg = Utils.validateParams(params, ["username", "password"]);
  if (validateMsg) {
    Utils.showErrorTip(validateMsg);
    return;
  }

  // 3. 调用登录接口（使用axios）
  axios({
    url: 'http://localhost:8080/user/login',
    method: 'post',  // 修正：应该用method而不是type
    data: params
  })
  .then((result) => {
    // 4. 登录成功处理
    Utils.setToken(result.data.token); // 注意：axios返回的数据在data属性中
    Utils.showSuccessTip(result.data.message || "登录成功！");
    window.location.href = "home.html";
  })
  .catch((error) => {
    // 5. 登录失败处理
    const errorMsg = error.response?.data?.message || error.message || "登录失败";
    Utils.showErrorTip(errorMsg);
    console.error("登录失败详情：", error);
  });
}