define(["Settings", "Dao"], function(s, d) {
	function run(){
		var img = document.createElement('img');
		img.src = d.getImgUrl(300, 300, "I'm alive!");
		document.body.appendChild(img);
	}
	
	return {
		run: run
	};
});