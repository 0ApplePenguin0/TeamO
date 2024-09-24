document.getElementById('approvalMenu').addEventListener('click', function () {
    let submenu = document.getElementById('approvalSubmenu');
    if (submenu.style.display === 'none' || submenu.style.display === '') {
        submenu.style.display = 'block';
    } else {
        submenu.style.display = 'none';
    }
});