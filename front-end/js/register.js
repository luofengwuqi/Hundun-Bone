<script src="https://cdn.bootcdn.net/ajax/libs/jquery/3.7.1/jquery.min.js"></script>
$(function() {
  // 绑定表单提交事件
  $('#registerForm').on('submit', function(e) {
    // 阻止默认提交
    e.preventDefault();

    // 收集表单数据（jQuery序列化，自动匹配name属性）
    const formData = $(this).serialize();

    // Ajax请求
    $.ajax({
      url: '/api/register', // 后端接口地址
      type: 'POST',
      data: formData, // 序列化后的表单数据（表单格式）
      // 若要传递JSON格式，改为：data: JSON.stringify($(this).serializeArray()),
      // contentType: 'application/json;charset=utf-8',
      dataType: 'json', // 预期后端返回JSON格式
      success: function(res) {
        // 处理后端响应
        if (res.code === 200) {
          alert(res.msg || '注册成功！');
          window.location.href = 'login.html';
        } else {
          alert(res.msg || '注册失败！');
        }
      },
      error: function(xhr, status, error) {
        // 网络错误或接口异常提示
        alert('请求失败，请检查网络或接口状态！');
        console.log(error); // 控制台打印错误信息，便于调试
      }
    });
  });
});