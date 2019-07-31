function startFetching(portalName) {

    var lnf = $('#' + portalName + '_lnf').val();
    var high = $('#' + portalName + '_high').val();

    alert('Starting fetching ' + portalName);

    $(document).ready(function(){
        $('' +
            '<form style="display: none" action="/convert">' +
                '<input name="portalName" value="' + portalName + '">' +
                '<input name="lnf" value="' + lnf + '">' +
                '<input name="high" value="' + high + '">' +
            '</form>').appendTo('body').submit();
    });

}