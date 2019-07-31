function cancelFetching(portalName) {

    alert("cancel fetching portal " + portalName);

    $(document).ready(function(){
        $('' +
            '<form style="display: none" action="/cancel">' +
                '<input name="portalName" value="' + portalName + '">' +
            '</form>').appendTo('body').submit();
    });

}