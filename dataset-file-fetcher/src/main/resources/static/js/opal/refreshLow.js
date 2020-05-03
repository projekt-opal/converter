$(document).ready(function() {
    var low = $('#low').val();
    setInterval(function() {
        $.get("/refresh?low="+low, (function (response) {
                if (response !== -1)
                    $('#low').val(response)
            }))
    }, 3000)
});