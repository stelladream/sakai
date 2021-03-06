// SAM-1817: This was originally in RichTextEditor.java
function show_editor(client_id, frame_id) {
	var status =  document.getElementById(client_id + '_textinput_current_status');
	status.value = "expanded";
	chef_setupformattedtextarea(client_id, true, frame_id);
	if (typeof setBlockDivs == "function" && typeof retainHideUnhideStatus == "function") {
		setBlockDivs();
		retainHideUnhideStatus('none');
	}
	var toggle=document.getElementById(client_id + "_toggle");
	if(toggle!==null){
		toggle.style.display = "none";
	}
}

function encodeHTML(text) {
	text = text.replace(
		/&/g, '&amp;').replace(
		/"/g, '&quot;').replace(
		/</g, '&lt;').replace(
		/>/g, '&gt;');
	return text;
}

function chef_setupformattedtextarea(client_id, shouldToggle, frame_id) {
	$("body").height($("body").outerHeight() + 600);

	var textarea_id = client_id + "_textinput";

	if (shouldToggle == true) {
		var input_text = document.getElementById(textarea_id);
		var input_text_value = input_text.value;
		var input_text_encoded = encodeHTML(input_text_value);
		input_text.value = input_text_encoded;
	}

	sakai.editor.launch(textarea_id,'','450','240');
	setMainFrameHeight(frame_id);
}

$( document ).ready(function() {
  $("#selectIndexForm\\:selectTable").tablesorter({ 
    theme: 'default', 
    sortList: [[2,0]],
    textExtraction: {
      0: function(node, table, cellIndex) { return $(node).find("a").text(); }
    }
  });
  $("#editform\\:questionpool-questions").tablesorter({
    theme: 'default',
    headers: {
      0: {
        sorter: false
      }
    }
  });
});
