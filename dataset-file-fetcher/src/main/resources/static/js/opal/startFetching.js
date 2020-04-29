function startFetching() {

    alert('Fetching started ');
    document.getElementById("startProcess").style.display = "none";
    document.getElementById("stopProcess").style.display = "inline-block";
    var low = $('#low').val();
    $(document).ready(function(){
        $.get("/convert?low=" + low, (function (response) {}))
    });

}