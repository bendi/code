define(['Dao'], function(dao) {

	var s1 = {
		imgService: "http://fpoimg.com/"
	};
	
	function get(key) {
		return s1[key];
	}
	
	function init() {
		s1.__proto__ = dao.loadSettings();
	};
	
	return {
		get: get,
		init: init
	};
});