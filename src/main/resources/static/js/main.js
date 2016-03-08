function sendMessage(){
    var message = $("#message").val();
    $.post( "/message", { message: message } );
    $("#message").val("");
}
