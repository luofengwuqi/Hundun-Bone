// 主函数
(function () {
  // 检测用户类型状态的监听
  hideAdmin();
  //添加监听
  const userTypeSelect = document.getElementById('userType');
  userTypeSelect.addEventListener('change', hideAdmin);

})();



//添加监听
//进行隐藏或显示
function hideAdmin() {
  const userTypeSelect = document.getElementById('userType');
  // console.log(userTypeSelect);
  const importantPasswordInput = document.getElementById('importantpassword');

  if (userTypeSelect) {
    const selectedValue = userTypeSelect.value;
    console.log(selectedValue);

    if (selectedValue === 'normal') {
      let objs = document.querySelectorAll('.admin-only')//.style.display = 'none';
      for (let i = 0; i < objs.length; i++) {
        objs[i].style.display = 'none';
      }
      importantPasswordInput.disabled = true;
      importantPasswordInput.required = false;
    } else {
      let objs = document.querySelectorAll('.admin-only')//.style.display = 'none';
      for (let i = 0; i < objs.length; i++) {
        objs[i].style.display = 'block';
      }
      // document.querySelectorAll('.admin-only').style.display = 'block';
      // 启用字段验证
      importantPasswordInput.disabled = false;
      importantPasswordInput.required = true;
    }
    // // 防止恶意跳转的安全检查
    // if (selectedValue === 'normal') {
    //   window.location.href = 'register.html';
    // } else if (selectedValue === 'admin') {
    //   window.location.href = 'adminregister.html';
    // }
    // });
  }
}