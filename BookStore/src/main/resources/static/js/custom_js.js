/* nút like */
$(document).ready(function() {
    $('.like-btn').click(function(event) {
        event.preventDefault();
        var button = $(this);
        var postId = button.data('post-id');
        var likeCountElement = button.closest('.card-body').find('.like-count'); // Tìm đến phần tử span trong .card-body của bài đăng được like

        $.ajax({
            url: '/user-posts/' + postId + '/like',
            type: 'POST',
            success: function(data) {
                // Cập nhật UI với số lượt thích mới của bài đăng cụ thể
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
    $('.unfriend-btn').click(function() {
        var friendId = $(this).data('id');
        if (confirm('Are you sure you want to unfriend this person?')) {
            $.ajax({
                url: '/friend/unfriend',
                type: 'POST',
                data: { friendId: friendId },
                success: function(response) {
                    alert('Friend removed successfully');
                    location.reload(); // Reload the page to reflect changes
                },
                error: function(xhr, status, error) {
                    alert('An error occurred while unfriending: ' + error);
                }
            });
        }
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
/* xét trong add  */
$(document).ready(function() {
    $('#createPostForm').submit(function(event) {
        event.preventDefault();

        // Clear previous messages
        $('#successMessage').hide().text('');
        $('#message').hide().text('');
        $('#contentError').text('');
        $('#classEntityError').text('');
        $('#subjectEntityError').text('');
        $('#imagesError').text('');

        var form = $('#createPostForm')[0];
        var formData = new FormData(form);

        $.ajax({
            type: 'POST',
            enctype: 'multipart/form-data',
            url: '/user-posts/new',
            data: formData,
            processData: false,
            contentType: false,
            cache: false,
            timeout: 600000,

            success: function(data) {
                if (data.status === 'success') {
                    $('#successMessage').text(data.message).fadeIn().delay(3000).fadeOut();
                    $('#createPostForm')[0].reset();
                } else {
                    // Handle errors if any (you can add this part if needed)
                }
            },
            error: function(xhr) {
                if (xhr.status === 400) {
                    var errors = xhr.responseJSON.errors;
                    $('#contentError').text(errors.content || "");
                    $('#classEntityError').text(errors.classEntity || "");
                    $('#subjectEntityError').text(errors.subjectEntity || "");
                    $('#imagesError').text(errors.images || "");
                } else {
                    $('#message').text('Đã xảy ra lỗi trong quá trình tạo bài đăng.').addClass('alert-danger').fadeIn().delay(3000).fadeOut();
                }
            }
        });
    });
});

/* đóng mở form đăng bài */
$(document).ready(function() {
    $('#textInput').on('click', function() {
        $('#postCreationForm').toggle();
    });
});
