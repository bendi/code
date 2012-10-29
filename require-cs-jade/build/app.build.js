({
    appDir: "../",
    baseUrl: "scripts/",
    dir: "../../dist",
    //Comment out the optimize line if you want
    //the code minified by UglifyJS
    optimize: "none",

    paths: {
        "jquery": "empty:",
        "cs": "../lib/cs",
        "coffee-script": "../lib/coffee-script",
        "underscore": "../lib/underscore",
        "jade": "../lib/jade",
        "views": "../views"
    },

    shim: {
    	underscore: {
    		init: function() {
    			return _.noConflict();
    		},
    		exports: '_'
    	}
    },


    //Stub out the cs module after a build since
    //it will not be needed.
    stubModules: ['cs'],

    pragmas: {
    	excludeJadeOnSave:true
	},

    modules: [
        //Optimize the application files. jQuery is not
        //included since it is already in require-jquery.js
        {
        	name: "main",

        	exclude: ['cs', 'coffee-script', 'jade']
        }
    ]
})
