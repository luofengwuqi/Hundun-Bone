$(function () {
  // 检测用户类型状态的监听
  hideAdmin();
  //添加监听
  const userTypeSelect = document.getElementById('userType');
  userTypeSelect.addEventListener('change', hideAdmin);

  // 绑定表单提交事件
  $('#registerForm').on('submit', function (e) {
    // 阻止默认提交
    e.preventDefault();

    // 收集表单数据并转换为JSON对象
    const formData = {};
    $(this).serializeArray().forEach(function (item) {
      formData[item.name] = item.value;
    });

    // 前端验证
    if (!validateForm(formData)) {
      return;
    }

    // Ajax请求
    $.ajax({
      url: 'http://localhost:8080/user/register', // 后端接口地址
      type: 'POST',
      data: JSON.stringify(formData), // 发送JSON格式数据
      contentType: 'application/json;charset=utf-8', // 指定内容类型
      dataType: 'json', // 预期后端返回JSON格式
      success: function (res) {
        // 处理后端响应
        if (res.code === 200) {
          alert(res.msg || '注册成功！');
          window.location.href = 'login.html';
        } else {
          alert(res.msg || '注册失败！');
        }
      },
      error: function (xhr, status, error) {
        // 网络错误或接口异常提示
        if (xhr.responseJSON && xhr.responseJSON.message) {
          alert('注册失败：' + xhr.responseJSON.message);
        } else {
          alert('请求失败，请检查网络或接口状态！');
        }
        console.log(error); // 控制台打印错误信息，便于调试
      }
    });
  });

  // 表单验证函数
  function validateForm(data) {
    // 验证手机号格式
    const phoneRegex = /^1[3-9]\d{9}$/;
    if (!data.phone || !phoneRegex.test(data.phone)) {
      alert('请输入正确的手机号格式（11位以1开头的手机号）');
      return false;
    }

    // 验证密码和确认密码
    if (!data.password || data.password.length < 6) {
      alert('密码长度不能少于6位');
      return false;
    }

    // if (!data.checkPassword && data.password !== data.checkPassword) {
    //   alert('两次输入的密码不一致');
    //   return false;
    // }

    return true;
  }
});







//添加监听
//进行隐藏或显示
function hideAdmin() {
  const userTypeSelect = document.getElementById('userType');
  const importantPasswordInput = document.getElementById('importantpassword');

  if (userTypeSelect) {
    const selectedValue = userTypeSelect.value;

    if (selectedValue === 'admin') {
      // 管理员显示管理专属字段
      let objs = document.querySelectorAll('.admin-only');
      for (let i = 0; i < objs.length; i++) {
        objs[i].style.display = 'block';
      }
      // 管理员启用重要密码字段
      importantPasswordInput.required = true;
      importantPasswordInput.disabled = false;
    } else {
      // 普通用户隐藏管理专属字段
      let objs = document.querySelectorAll('.admin-only');
      for (let i = 0; i < objs.length; i++) {
        objs[i].style.display = 'none';
      }
      // 普通用户禁用重要密码字段并移除验证要求
      importantPasswordInput.required = false;
      importantPasswordInput.disabled = true;
      // 清空隐藏字段的值，避免提交时包含不需要的数据
      importantPasswordInput.value = '';
    }
  }
}