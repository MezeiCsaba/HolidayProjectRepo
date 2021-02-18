/**
 * 
 */
console.log("text ");

$('document').ready(function () {

    $('.table .eBtn').on('click', function (event) {
        event.preventDefault();
        var href = $(this).attr('href');
        var text = $(this).text(); //return New or Edit
  		console.log(text + 'text ');
  		console.log('text ');
        if (text === 'Edit') { 
            $.get(href, function (anevent, status) {
                $('.myForm #id').val(anevent.id);
                $('.myForm #startDate').val(anevent.startDate);
                $('.myForm #endDate').val(anevent.endDate);
               
            });
            $('.myForm #exampleModal').modal(show);
        } else {
            $('.myForm #id').val('');
            $('.myForm #startDate').val('');
            $('.myForm #endDate').val('');
            $('.myForm #exampleModal').modal(show);
        }
    });

    $('.table .delBtn').on('click', function (event) {
        event.preventDefault();
        var href = $(this).attr('href');
        $('#myModal #delRef').attr('href', href);
        $('#myModal').modal(show);
    });
});