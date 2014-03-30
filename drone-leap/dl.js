var Cylon = require("cylon"),
    _ = require("lodash"),
	http = require("http");

var speedAdjuster = 4.5, // higher number decreases action speed.  DO NOT set to less than 1
    maxSpeed = 0.12,
    flightDuration = 20;

var upBorder = 0.4,
    downBorder = 0.3,
    upInitialPosition = 10;

var leftRightBorder = 0.1;
var frontBackBorder = 0.1;

var moveSafely;

/*
function moveSafely(direction, speed) {
    speed = Math.min(upMaxSpeed, speed);
    Logger.info("Moving", direction, "at speed", speed.toPrecision(3));
}
*/

function upDownHandler(hand) {
    speedAdjuster = Math.max(1, speedAdjuster);

    var yPos = Math.min(500, hand.palmY); // position of hand on y axis
    var adjY = (yPos - upInitialPosition) / 500; // 0 to .8

    if (adjY > upBorder) {
        moveSafely("up", Math.abs(upBorder - adjY));
    } else if (adjY < downBorder) {
        moveSafely("down", Math.abs(downBorder - adjY));
    }
}

function leftRightHandler(hand) {
    speedAdjuster = Math.max(1, speedAdjuster);

    var xPos = Math.min(250, hand.palmX); // position of hand on x axis

    var adjX = xPos / 250; // -1.5 to 1.5

    if (adjX < -leftRightBorder) {
        moveSafely("left", Math.abs(adjX + leftRightBorder) / speedAdjuster);
    } else if (adjX > leftRightBorder) {
        moveSafely("right", Math.abs(adjX - leftRightBorder) / speedAdjuster);
    }
}

function frontBackHandler(hand) {
    speedAdjuster = Math.max(1, speedAdjuster);

    var zPos = Math.min(250, hand.palmZ); // position of hand on z axis
    var adjZ = zPos / 250; // -2 to 2
    var adjZspeed = Math.abs(adjZ) / speedAdjuster; // front/back speed

    if (adjZ < -frontBackBorder) {
        moveSafely("front", Math.abs(adjZ + frontBackBorder) / speedAdjuster);
    } else if (adjZ > frontBackBorder) {
        moveSafely("back", Math.abs(adjZ - frontBackBorder) / speedAdjuster);
    }
}

function createPngStream(png) {
    var server = http.createServer(function (req, res) {

        png.on("error", function (err) {
            console.error("png stream ERROR: " + err);
        });

        res.writeHead(200, { "Content-Type": "multipart/x-mixed-replace; boundary=--daboundary" });

        function sendPng(buffer) {
            res.write("--daboundary\nContent-Type: image/png\nContent-length: " + buffer.length + "\n\n");
            res.write(buffer);
        }

        png.on("data", sendPng);
    });

    server.listen(8000);
}

var safetyTimeouts = {};

function clearSafetyTimeout(direction) {
    if (safetyTimeouts[direction]) {
        clearTimeout(safetyTimeouts[direction]);
        safetyTimeouts[direction] = undefined;
    }
}

function initSafetyTimeout(direction, drone) {
    safetyTimeouts[direction] = _.delay(function () {
        drone[direction](0);
    }, 250);
}

var robot = Cylon.robot({
    connections: [
        {name: "ardrone", adaptor: "ardrone", port: "192.168.1.1"},
        {name: "leapmotion", adaptor: "leapmotion", port: "127.0.0.1:6437"}
    ],
    devices: [
        {name: "drone", driver: "ardrone", connection: "ardrone"},
        {name: "leapmotion", driver: "leapmotion", connection: "leapmotion"}
    ],

    work: function (my) {
        var drone = my.drone,
            leapmotion = my.leapmotion;

        createPngStream(drone.getPngStream());

        drone.takeoff();

        function move(direction, speed) {
            try {
                clearSafetyTimeout(direction);
                drone[direction](speed);
                initSafetyTimeout(direction, drone);
            } catch (e) {
                console.log(e);
            }
        }

        moveSafely = function (direction, speed) {
            speed = Math.min(maxSpeed, speed);
            Logger.info("Moving", direction, "at speed", speed.toPrecision(3));
            move(direction, speed);
        };

        after((8).seconds(), function () {
            //leapmotion.on("hand", upDownHandler);
            leapmotion.on("hand", leftRightHandler);
            //leapmotion.on("hand", frontBackHandler);
        });
		
		if (flightDuration <= 0) {
			leapmotion.on("gesture", function (gesture) {
				var type = gesture.type;
				switch (gesture.type) {
				case "swipe":
				case "circle":
					Logger.info("Land!");
					leapmotion.removeAllListeners("hand");
					leapmotion.removeAllListeners("gesture");
					drone.hover();
					after((1).seconds(), function () {
						drone.land();
					});
					break;
				}
			});
		} else {
			var backToNormalAfter = flightDuration + 8;

			after((backToNormalAfter).seconds(), function () {
				Logger.info("Time's up!");
				leapmotion.removeAllListeners("hand");
				drone.hover();
			});

			after((backToNormalAfter + 1).seconds(), function () {
				drone.land();
			});

			after((backToNormalAfter + 7).seconds(), function () {
				Logger.info("Can be disconnected");
				drone.stop();
			});
		}
    }
});

robot.start();