var Cylon = require('cylon');

Cylon.robot({
  connection: { name: 'ardrone', adaptor: 'ardrone', port: '192.168.1.1' },
  device: {name: 'drone', driver: 'ardrone'},

  work: function(my) {
    my.drone.takeoff();
	
	after((8).seconds(), function () {
		my.drone.hover();
		my.drone.left(0.8);
	});
		
	after((10).seconds(), function () {
		my.drone.hover();
		my.drone.right(0.8);
	});
		
	after((12).seconds(), function() { 
	  my.drone.hover();
	  my.drone.land();
	});
		
	after((18).seconds(), function() { 
	  my.drone.stop();
	});    
  }
}).start();