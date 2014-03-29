var Cylon = require('cylon'),
	_ = require("lodash");

var speedAdjuster = 4.5, // higher number decreases action speed.  DO NOT set to less than 1
	maxSpeed = .12;

var upBorder = .4,
	downBorder = .3,
	upInitialPosition = 10;
	
var moveSafely;

/*
function moveSafely(direction, speed) {
	speed = Math.min(upMaxSpeed, speed);
	Logger.info("Moving", direction, "at speed", speed.toPrecision(3));
}
*/
	
function upDownHandler(hand) {
	speedAdjuster = Math.max(1, speedAdjuster);
	
      var yPos = hand.palmY; // position of hand on y axis
      var adjY = (yPos - upInitialPosition) / 500; // 0 to .8
	  
      if (adjY > upBorder) {
		moveSafely("up", Math.abs(upBorder - adjY));
      } else if (adjY < downBorder) {
		moveSafely("down", Math.abs(downBorder - adjY));
      }

}

var leftRightBorder = .1;

function leftRightHandler(hand) {

	var xPos = hand.palmX; // position of hand on x axis

    var adjX = xPos / 250; // -1.5 to 1.5

    if (adjX < -leftRightBorder) {
		moveSafely("left", Math.abs(adjX + leftRightBorder) / speedAdjuster);
    } else if (adjX > leftRightBorder) {
		moveSafely("right", Math.abs(adjX - leftRightBorder) / speedAdjuster);
    }
}

var frontBackBorder = .1;
	
function frontBackHandler(hand) {
      var zPos = hand.palmZ; // position of hand on z axis
      var adjZ = zPos / 250; // -2 to 2
		var adjZspeed = Math.abs(adjZ) / speedAdjuster; // front/back speed

      if (adjZ < -frontBackBorder) {
		moveSafely("front", Math.abs(adjZ + frontBackBorder) / speedAdjuster);
      } else if (adjZ > frontBackBorder) {
		moveSafely("back", Math.abs(adjZ - frontBackBorder) / speedAdjuster);
      }
}

var flightDuration = 20;

Cylon.robot({
	connections: [
		{name: 'ardrone', adaptor: 'ardrone', port: '192.168.1.1'},
		{name: 'leapmotion', adaptor: 'leapmotion', port: '127.0.0.1:6437'}
	],
	devices: [
		{name: 'drone', driver: 'ardrone', connection: "ardrone"},
		{name: 'leapmotion', driver: 'leapmotion', connection: "leapmotion"}
	],

  work: function(my) {
    my.drone.takeoff();

	moveSafely = function (direction, speed) {
		speed = Math.min(maxSpeed, speed);
		Logger.info("Moving", direction, "at speed", speed.toPrecision(3));
		try {
			my.drone[direction](speed);
		} catch(e) {
			console.log(e);
		}
	}
	
	after((5).seconds(), function () {
		my.drone.hover();
	});

	after((8).seconds(), function () {
		//my.leapmotion.on("hand", upDownHandler);
		my.leapmotion.on("hand", leftRightHandler);
		//my.leapmotion.on("hand", frontBackHandler);
	});
	
	var backToNormalAfter = flightDuration + 8;

	after((backToNormalAfter).seconds(), function () {
		Logger.info("Time's up!");
		my.leapmotion.removeAllListeners("hand");
		my.drone.hover();
	});
	
	after((backToNormalAfter + 1).seconds(), function () {
		my.drone.land();
	});
	
	after((backToNormalAfter + 7).seconds(), function () {
		Logger.info("Can be disconnected");
		my.drone.stop();
	});
	
	every((2).seconds(), function () {
		my.drone.hover();
	});
  }
}).start();