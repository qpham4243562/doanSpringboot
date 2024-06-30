/* nút like */
$(document).ready(function() {
    $('.like-btn').click(function(event) {
        event.preventDefault();
        var button = $(this);
        var postId = button.data('post-id');
        var row = button.closest('tr');
        var likeCountElement = row.find('.like-count');

        $.ajax({
            url: '/user-posts/' + postId + '/like',
            type: 'POST',
            success: function(data) {
                // Cập nhật UI với số lượt thích mới
                likeCountElement.text(data);
            },
            error: function(xhr, status, error) {
                console.error('Error:', error);
                alert('Đã xảy ra lỗi khi thực hiện yêu cầu.');
            }
        });
    });
});
