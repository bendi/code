define(function() {

	var settings = {
		imgService: "http://fpoimg.com/"
	};
	
	function get(key) {
		return settings[key];
	}
	
	return {
		get: get
	};
});