define(["Settings"], function(s) {
	var url = s.get('imgService');
	
	function getImgUrl(w, h, txt) {
		return url + w + "x" + h + "?text=" + txt;
	}
	
	return {
		getImgUrl: getImgUrl
	};
});