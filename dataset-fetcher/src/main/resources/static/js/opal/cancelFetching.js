function cancelFetching(id) {

    alert("cancel fetching ");

    $(document).ready(function(){
        $('' +
            '<form style="display:none" action="/cancel">' +
                '<input name="id" value="' + id + '">' +
            '</form>').appendTo('body').submit();
    });

}