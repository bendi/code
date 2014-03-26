var Cylon = require('cylon');

Cylon.robot({
  connection: { name: 'ardrone', adaptor: 'ardrone', port: '192.168.1.1' },
  device: {name: 'drone', driver: 'ardrone'},

  work: function(my) {
	after((2).seconds(), function () {
		my.drone.hover();
		my.drone.land();
	});
		
	after((10).seconds(), function () {
		my.drone.stop();
	});
	
  }
}).start();