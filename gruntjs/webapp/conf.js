require.config({
  // @exclude
  baseUrl: 'scripts',
  paths: {
    "cs": "../lib/cs",
    "coffee-script": "../lib/coffee-script",
    "underscore": "../lib/underscore",
    "jade": "../lib/jade",
    "text": "../lib/text",
    "views": "../views"
  },
  // @endexclude
  shim: {
    underscore: {
      init: function() {
        return _.noConflict();
      },
      exports: '_'
    }
  }
});

// @exclude
var main = 'main';
main = 'cs!' + main;
require([main]);
// @endexclude
