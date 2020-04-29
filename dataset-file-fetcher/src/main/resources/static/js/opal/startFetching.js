function startFetching() {

    alert('Fetching started ');
    document.getElementById("startProcess").style.display = "none";
    document.getElementById("stopProcess").style.display = "inline-block";
    var low = $('#low').val();
    var folderPath = $('#folderPath').val();
    $(document).ready(function(){
        $.get("/convert?low=" + low + "&folderPath=" + folderPath, (function (response) {}))
    });

}