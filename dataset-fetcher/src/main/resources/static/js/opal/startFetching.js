function startFetching(id) {

    var lnf = $('#' + 'lnf_' + id).val();
    var high = $('#' + 'high_' + id).val();
    var step = $('#' + 'step_' + id).val();

    alert('Fetching started ');

    $(document).ready(function(){
        $('' +
            '<form style="display:none" action="/convert">' +
                '<input name="id" value="' + id + '">' +
                '<input name="lnf" value="' + lnf + '">' +
                '<input name="high" value="' + high + '">' +
                '<input name="step" value="' + step + '">' +
            '</form>').appendTo('body').submit();
    });

}