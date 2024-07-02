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
/* nút update profile */
$(document).ready(function() {
    $("#updateButton").click(function() {
        $("#profileInfo").hide();
        $("#updateFormContainer").show();
    });

    $("#cancelButton").click(function() {
        $("#updateFormContainer").hide();
        $("#profileInfo").show();
    });
});
/* nút kết bạn */
$(document).ready(function() {
    $('.friend-request-btn').click(function() {
        var friendId = $(this).data('friend-id');
        var button = $(this);

        $.ajax({
            url: '/friend/send',
            type: 'POST',
            data: { friendId: friendId },
            success: function(response) {
                console.log(response.message);
                button.text('Đã gửi yêu cầu');
                button.prop('disabled', true);
            },
            error: function(xhr, status, error) {
                console.error('Error:', error);
                var response = xhr.responseJSON;
                if (response && response.error) {
                    alert(response.error);
                } else {
                    alert('Có lỗi xảy ra khi gửi yêu cầu kết bạn.');
                }
            }
        });
    });
});

/* nút đồng ý kết bạn */
$(document).ready(function() {
    $(".accept-friend-request").click(function(event) {
        event.preventDefault();
        let friendRequestId = $(this).data("id");
        $.post("/friend/accept", { friendRequestId: friendRequestId }, function(response) {
            alert(response.message);
            location.reload();
        }).fail(function(response) {
            alert(response.responseJSON.error);
        });
    });
    /* nút từ chối  kết bạn */
    $(".reject-friend-request").click(function(event) {
        event.preventDefault();
        let friendRequestId = $(this).data("id");
        $.post("/friend/reject", { friendRequestId: friendRequestId }, function(response) {
            alert(response.message);
            location.reload();
        }).fail(function(response) {
            alert(response.responseJSON.error);
        });
    });
});
/* nút hủy kết bạn */
$(document).ready(function() {
    $(".unfriend-btn").click(function(event) {
        event.preventDefault();
        let friendId = $(this).data("id");
        $.post("/friend/unfriend", { friendId: friendId }, function(response) {
            alert(response.message);
            location.reload();
        }).fail(function(response) {
            alert(response.responseJSON.error);
        });
    });
});
/* ẩn nút nếu không đúng profile */
$(document).ready(function() {
    // Check if the profile belongs to the logged-in user
    var isOwnProfile = $('#profileInfo').data('is-own-profile');

    // Hide buttons if the profile does not belong to the logged-in user
    if (!isOwnProfile) {
        $('#updateButton').hide();
        $('.unfriend-btn').hide();
        $('.edit-btn').hide();
        $('.delete-btn').hide();
        $('.unfollow-btn').hide();
    }
});