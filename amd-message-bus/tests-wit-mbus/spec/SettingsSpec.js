describe("Settings", function() {

  // prepare mock dependencies
  var mBusMock = jasmine.createSpyObj("mBus", ["addEventListener"]),
	daoMock = jasmine.createSpyObj("daoMock", ["read"]);
	
  beforeEach(function() {
	spyOn(daoMock, 'read').andCallFake(function() {
		return {};
	});
  });

  define("mBusMock", function() {
    return mBusMock;
  });
  
  define("daoMock", function() {
    return daoMock;
  });
  
  var r = require.config({
	map: {
		'Settings' : {
			'MessageBus': 'mBusMock',
			'Dao': 'daoMock'
		}
	}
  });
  
  define(["Settings"], function(s) {
	describe("Settings", function() {
      it("should register 1 event listeners", function() {
        expect(mBusMock.addEventListener.calls.length).toEqual(1);
      });
	  it("should register for dao:init event", function() {
		expect(mBusMock.addEventListener).toHaveBeenCalledWith("dao:init", jasmine.any(Function));
	  });
	  it("should read dao settings", function() {
		mBusMock.addEventListener.calls[0].args[1].call();
		expect(daoMock.read).toHaveBeenCalledWith("settings");
	  });
	});
  });

});