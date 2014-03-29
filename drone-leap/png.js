var Cylon = require('cylon'),
	http = require('http');

	
function createStream(png) {
	var server = http.createServer(function(req, res) {
	
		png.on('error', function (err) {
			console.error('png stream ERROR: ' + err);
		});

		res.writeHead(200, { 'Content-Type': 'multipart/x-mixed-replace; boundary=--daboundary' });
	  
		function sendPng(buffer) {
			res.write('--daboundary\nContent-Type: image/png\nContent-length: ' + buffer.length + '\n\n');
			res.write(buffer);
		}
	  
		png.on('data', sendPng);	  
	});
	
	server.listen(8000);  
 }
Cylon.robot({
  connection: { name: 'ardrone', adaptor: 'ardrone', port: '192.168.1.1' },
  device: {name: 'drone', driver: 'ardrone'},

  work: function(my) {
	var png = my.drone.getPngStream();
	
	createStream(png);
  }
}).start();