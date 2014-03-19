var Cylon = require('cylon'),
	_ = require("lodash");

var speedAdjuster = 2.5; // higher number decreases action speed.  DO NOT set to less than 1

var flying = true;

function move(hand) {
	var xPos = hand.palmX; // position of hand on x axis
      var yPos = hand.palmY; // position of hand on y axis
      var zPos = hand.palmZ; // position of hand on z axis

      var adjX = xPos / 250; // -1.5 to 1.5
      var adjXspeed = Math.abs(adjX)/ speedAdjuster; // left/right speed
      var adjY = (yPos - 60) / 500; // 0 to .8
      var adjYspeed = Math.abs(.4-adjY) // up/down speed
      var adjZ = zPos / 250; // -2 to 2
      var adjZspeed = Math.abs(adjZ) / speedAdjuster; // front/back speed

      if (adjX < 0 && flying) { // flying set in takeoff() and land() to prevent actions while drone landed
        stopped = false;
		Logger.info("Move left", "speed: ", adjXspeed);
		/*
        $(".left").attr({id: 'highlight'})
        $(".right").attr({id: ''})
        setTimeout(function (){
          return faye.publish("/drone/move", {
    	      action: 'left',
    	      speed: adjXspeed
 			    })
        }, timeout);
		*/
      } else if (adjX > 0 && flying) {
        stopped = false;
		Logger.info("Move right", "speed: ", adjXspeed);
/*
        $(".right").attr({id: 'highlight'})
        $(".left").attr({id: ''})
        setTimeout(function (){
          return faye.publish("/drone/move", {
      	    action: 'right',
      	    speed: adjXspeed
   			  })
        }, timeout);
*/
      }

      if (adjY > 0.4 && flying) {
        stopped = false;
		Logger.info("Move up", "speed: ", adjYspeed);
		/*
        $(".up").attr({id: 'highlight'})
        $(".down").attr({id: ''})
        setTimeout(function (){
          return faye.publish("/drone/move", {
      	    action: 'up',
      	    speed: adjYspeed
          })
        }, timeout/2);
		*/
      } else if (adjY < 0.4 && flying) {
        stopped = false;
		Logger.info("Move down", "speed: ", adjYspeed);
		/*
        $(".down").attr({id: 'highlight'})
        $(".up").attr({id: ''})
        setTimeout(function (){
          return faye.publish("/drone/move", {
      	    action: 'down',
      	    speed: adjYspeed
   			  })
        }, timeout/2);
		*/
      }

      if (adjZ < 0 && flying) {
        stopped = false;
		Logger.info("Move front", "speed: ", adjZspeed);
/*
        $(".front").attr({id: 'highlight'})
        $(".back").attr({id: ''})
        setTimeout(function (){
          return faye.publish("/drone/move", {
      	    action: 'front',
      	    speed: adjZspeed
   			  })
        }, timeout/3);
*/
      } else if (adjZ > 0 && flying) {
		Logger.info("Move back", "speed: ", adjYspeed);
/*
        stopped = false;
        $(".back").attr({id: 'highlight'})
        $(".front").attr({id: ''})
        setTimeout(function (){
          return faye.publish("/drone/move", {
      	    action: 'back',
      	    speed: adjZspeed
   			  })
        }, timeout/3);
*/
      }
}
/*
Cylon.robot({
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

    my.leapmotion.on('hand', _.throttle(move, 100));

    my.leapmotion.on('pointable', function(pointable) {
      //Logger.info(pointable.toString());
    });

    my.leapmotion.on('gesture', function(gesture) {
      //Logger.info(gesture.toString());
    });  }
}).start();

*/