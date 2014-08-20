$(document).ready(function() {

    $("#btn_annotate").click(function(e) {
        $("#input_section").hide();
        $("#output_text").empty();

        // GET annotations
        var input = $("#input_text").val();
        $.get("annotate", {
            text: input
        }).done(function(res) {
            $("#output_section").show();

            // build highlighted text, in reverse order
            var text = res.text;
            var curr_end = text.length;

            var annots_rev = res.annotations.reverse();
            var lastStart = 100000000000000;
            $.each(annots_rev, function() {

                var start = this.start;
                if (start < lastStart){
                    lastStart = start;
                    if (typeof start === "undefined") start = 0;
                    var end = this.end;
                    var type = this.type;

                    //add text before annot
                    var pre_text = text.substring(end, curr_end);
                    curr_end = start;
                    $("#output_text").prepend(pre_text);

                    // add annot
                    var annot_text = text.substring(start, end);
                    //var properties = "";
                    //$.each(this.params, function() {
                    //    if (typeof this.value !== "undefined")
                    //        properties += this.name + ":" + this.value + "  ";
                    //});
                    $("#output_text").prepend('<span class="annot ' + type + '" title="'+type+'">' + annot_text + '<span>');
                }
            });
            var pre_text = text.substring(0, curr_end);
            $("#output_text").prepend(pre_text);
        });
    });

    $("#btn_edit").click(function(e) {
        $("#output_section").hide();
        $("#input_section").show();
    });
});
