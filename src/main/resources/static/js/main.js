// Feather icons + NProgress on every page
feather.replace();
NProgress.done();
document.querySelectorAll('.sidebar-link').forEach(function (link) {
    link.addEventListener('click', function () { NProgress.start(); });
});


// SSE – real-time notifications
const evtSource = new EventSource('/notifications/stream');
evtSource.addEventListener('notification', function (e) {
    const data = JSON.parse(e.data);
    document.getElementById('notifHeader').textContent = data.header;
    document.getElementById('notifContent').textContent = data.content;
    new bootstrap.Toast(document.getElementById('notifToast'), { delay: 6000 }).show();
});
evtSource.onerror = function () { evtSource.close(); };
window.addEventListener('beforeunload', function () { evtSource.close(); });
