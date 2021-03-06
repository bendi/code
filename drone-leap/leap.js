var Cylon = require('cylon'),
	_ = require("lodash");

var speedAdjuster = 3.5; // higher number decreases action speed.  DO NOT set to less than 1

var flying = true;

var upBorder = .4,
	downBorder = .3,
	upInitialPosition = 10,
	upMaxSpeed = .15;

function moveSafely(direction, speed) {
	speed = Math.min(upMaxSpeed, speed);
	Logger.info("Moving", direction, "at speed", speed.toPrecision(3));
}
	
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

var leapRobot = Cylon.robot({
  connection: {
    name: 'leapmotion',
    adaptor: 'leapmotion',
    port: '127.0.0.1:6437'
  },

  device: {
    name: 'leapmotion',
    driver: 'leapmotion'
  },

  work: function(my) {
	my.leapmotion.on('connect', function() {
      Logger.info("Connected");
    });

    my.leapmotion.on('start', function() {
      Logger.info("Started");
    });

    my.leapmotion.on('frame', function(frame) {
      //Logger.info(frame.toString());
    });

    my.leapmotion.on('hand', upDownHandler);
    my.leapmotion.on('hand', leftRightHandler);
	my.leapmotion.on('hand', frontBackHandler);

    my.leapmotion.on('pointable', function(pointable) {
      //Logger.info(pointable.toString());
    });

    my.leapmotion.on('gesture', function(gesture) {
      //Logger.info(gesture.toString());
    });
	
	every((0.1).seconds(), function () {
		//Logger.info(new Date());
	});
  }
});

leapRobot.start();