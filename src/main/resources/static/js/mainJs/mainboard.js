document.getElementById('approvalMenu').addEventListener('click', function () {
    let submenu = document.getElementById('approvalSubmenu');
    if (submenu.style.display === 'none'|| submenu.style.display === '') {
        submenu.style.display = 'block';
    } else {
        submenu.style.display = 'none';
    }
});


document.getElementById('adminpage').addEventListener('click', function () {
    let submenu2 = document.getElementById('adminpageSubmenu');
    if (submenu2.style.display === 'none'|| submenu2.style.display === '') {
        submenu2.style.display = 'block';
    } else {
        submenu2.style.display = 'none';
    }
});