define(function() {

  var listeners = {};

  function addEventListener(event, fn){
    listeners[event] = listeners[event] || [];
    listeners[event].push(fn);
  }

  function removeEventListener(fn) {
  
    if (listeners === undefined) {
      // do nothing
    } else {
	  for(var i in listeners) {
	    var l = listeners[i];
        var id = l.indexOf(fn);
		
        if (id !== -1) {
          l.splice(id, 1);
        }
	  }
    }
  }

  function removeAllListeners(event) {
    if (!event) return;
      listeners[event] = [];
  }

  function notify(event, data) {
    if (listeners[event] === undefined) {
      // skip
    } else if (listeners[event].length) {
      listeners[event].forEach(function(fn) {
        fn(data);
      });
    }
  }

  function length(event) {
    return listeners[event] && listeners[event].length || 0;
  }

  function clear() {
    listeners = {};
  }

  return {
    addEventListener: addEventListener,
    removeEventListener: removeEventListener,
    removeAllListeners: removeAllListeners,
    notify: notify,
    clear: clear,
    length: length
  }

});
