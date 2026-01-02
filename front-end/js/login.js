/**
 * 登录页面业务逻辑
 * 作用：仅处理登录页面的交互逻辑
 */
;(function (window) {
  // 页面加载完成后执行
  window.onload = function () {
    // 绑定登录按钮点击事件
    const loginBtn = document.querySelector(".container button");
    if (loginBtn) {
      loginBtn.addEventListener("click", handleLogin);
    }

    // 绑定注册按钮跳转
    const registerLink = document.querySelector("span a[href='register.html']");
    if (registerLink) {
      registerLink.addEventListener("click", function (e) {
        e.preventDefault();
        window.location.href = "register.html";
      });
    }

    // 绑定回车登录（增强体验）
    document.addEventListener("keydown", function (e) {
      if (e.key === "Enter") {
        handleLogin();
      }
    });

    // 页面加载时检查记住密码
    checkRememberPassword();
  };

  /**
   * 检查记住密码功能
   */
  function checkRememberPassword() {
    const rememberCheckbox = document.querySelector(".radio");
    const usernameInput = document.querySelector("input[type='text']");
    const passwordInput = document.querySelector("input[type='password']");
    
    if (!rememberCheckbox || !usernameInput || !passwordInput) return;
    
    // 从localStorage读取保存的凭据
    const savedUsername = localStorage.getItem("savedUsername");
    const savedPassword = localStorage.getItem("savedPassword");
    
    if (savedUsername && savedPassword) {
      usernameInput.value = savedUsername;
      passwordInput.value = savedPassword;
      rememberCheckbox.checked = true;
    }
  }

  /**
   * 处理记住密码功能
   */
  function handleRememberPassword(username, password) {
    const rememberCheckbox = document.querySelector(".radio");
    
    if (rememberCheckbox && rememberCheckbox.checked) {
      // 保存到localStorage
      localStorage.setItem("savedUsername", username);
      localStorage.setItem("savedPassword", password);
    } else {
      // 清除保存的凭据
      localStorage.removeItem("savedUsername");
      localStorage.removeItem("savedPassword");
    }
  }

  /**
   * 显示消息提示
   */
  function showMessage(message, isSuccess = false) {
    // 移除已存在的提示
    const existingMsg = document.querySelector(".message-tip");
    if (existingMsg) {
      existingMsg.remove();
    }
    
    // 创建提示元素
    const messageDiv = document.createElement("div");
    messageDiv.className = `message-tip ${isSuccess ? 'success' : 'error'}`;
    messageDiv.textContent = message;
    messageDiv.style.cssText = `
      position: fixed;
      top: 20px;
      left: 50%;
      transform: translateX(-50%);
      padding: 12px 24px;
      background: ${isSuccess ? '#4CAF50' : '#f44336'};
      color: white;
      border-radius: 4px;
      z-index: 1000;
      font-size: 14px;
      box-shadow: 0 2px 8px rgba(0,0,0,0.2);
    `;
    
    document.body.appendChild(messageDiv);
    
    // 3秒后自动消失
    setTimeout(() => {
      if (messageDiv.parentNode) {
        messageDiv.remove();
      }
    }, 3000);
  }

  /**
   * 验证表单输入
   */
  function validateForm(username, password) {
    if (!username.trim()) {
      showMessage("请输入用户名");
      return false;
    }
    
    if (!password.trim()) {
      showMessage("请输入密码");
      return false;
    }
    
    if (username.trim().length < 2) {
      showMessage("用户名至少需要2个字符");
      return false;
    }
    
    if (password.trim().length < 6) {
      showMessage("密码至少需要6个字符");
      return false;
    }
    
    return true;
  }

  /**
   * 处理登录逻辑
   */
  function handleLogin() {
    // 1. 获取输入值
    const usernameInput = document.querySelector("input[type='text']");
    const passwordInput = document.querySelector("input[type='password']");
    
    if (!usernameInput || !passwordInput) {
      showMessage("无法找到输入框");
      return;
    }
    
    const username = usernameInput.value.trim();
    const password = passwordInput.value.trim();
    
    // 2. 验证输入
    if (!validateForm(username, password)) {
      return;
    }
    
    // 3. 禁用按钮，防止重复提交
    const loginBtn = document.querySelector(".container button");
    const originalText = loginBtn.textContent;
    loginBtn.textContent = "登录中...";
    loginBtn.disabled = true;
    
    // 4. 发送登录请求
    axios({
      url: 'http://localhost:8080/user/login',
      method: 'post',
      headers: {
        'Content-Type': 'application/json'
      },
      data: {
        username: username,
        password: password
      }
    })
    .then((response) => {
      // 登录成功
      showMessage(response.data.message || "登录成功！", true);
      
      // 处理记住密码
      handleRememberPassword(username, password);
      
      // 保存token（如果有）
      if (response.data.token) {
        localStorage.setItem("authToken", response.data.token);
      }
      
      // 保存用户信息（如果有）
      if (response.data.data) {
        localStorage.setItem("userInfo", JSON.stringify(response.data.data));
      }
      
      // 延迟跳转到首页
      setTimeout(() => {
        window.location.href = 'index-admin.html';
      }, 1500);
    })
    .catch((error) => {
      // 登录失败
      let errorMessage = "登录失败，请稍后重试";
      
      if (error.response) {
        // 服务器返回错误
        errorMessage = error.response.data.message || 
                      `服务器错误：${error.response.status}`;
      } else if (error.request) {
        // 请求已发出但没有响应
        errorMessage = "网络连接失败，请检查网络设置";
      } else {
        // 其他错误
        errorMessage = error.message;
      }
      
      showMessage(errorMessage);
    })
    .finally(() => {
      // 恢复按钮状态
      if (loginBtn) {
        loginBtn.textContent = originalText;
        loginBtn.disabled = false;
      }
    });
  }

  /**
   * 处理忘记密码（如果需要）
   */
  function handleForgotPassword() {
    const forgotLink = document.querySelector(".pw a[href='#']");
    if (forgotLink) {
      forgotLink.addEventListener("click", function (e) {
        e.preventDefault();
        showMessage("请联系管理员重置密码");
      });
    }
  }

  // 初始化忘记密码事件
  handleForgotPassword();

  // 暴露全局函数（便于调试）
  window.handleLogin = handleLogin;
})(window);