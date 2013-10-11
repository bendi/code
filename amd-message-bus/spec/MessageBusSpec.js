define(["MessageBus"], function(mBus) {
describe("message bus", function() {

  beforeEach(function() {
    mBus.clear();
  })
  
  it("should allow adding listener for event", function() {

    mBus.addEventListener("myEvent", function(){});

    expect(mBus.length("myEvent")).toEqual(1);

  });

  it("should allow removing listener for event", function() {

    var fn = function(){};

    mBus.addEventListener("myEvent", fn);

    expect(mBus.length("myEvent")).toEqual(1);

    mBus.removeEventListener(fn);

    expect(mBus.length("myEvent")).toEqual(0);

  });

  it("should allow broadcast", function() {

    var fn = jasmine.createSpy('fn'),

    o =  {prop:11};

    mBus.addEventListener("myEvent", fn);

    expect(mBus.length("myEvent")).toEqual(1);

    mBus.notify("myEvent", o);

    expect(fn.calls.length).toEqual(1);

  });

  it("should allow additional data within broadcast", function() {

    var fn = jasmine.createSpy('fn'),

    o =  {prop:11};

    mBus.addEventListener("myEvent", fn);

    expect(mBus.length("myEvent")).toEqual(1);

    mBus.notify("myEvent", o);

    expect(fn.calls.length).toEqual(1);

    expect(fn).toHaveBeenCalledWith(o);

  });
});
});