define(["Settings"], function(s) {
	var url = s.get('imgService'),
		settingsUrl = s.get('settingsUrl');
	
	function getImgUrl(w, h, txt) {
		return url + w + "x" + h + "?text=" + txt;
	}
	
	function loadSettings() {
		var settings = {};
		
		// do real loading
		
		return settings;
	}
	
	return {
		getImg: getImg,
		loadSettings: loadSettings
	};
});