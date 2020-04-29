function cancelFetching() {

    alert("cancel fetching ");
    document.getElementById("startProcess").style.display = "inline-block";
    document.getElementById("stopProcess").style.display = "none";

    $(document).ready(function(){
        $.get("/cancel", (function (response) {}))
    });

}