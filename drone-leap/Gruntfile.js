/*global module:false*/
module.exports = function (grunt) {
    "use strict";
    
    var _ = require("lodash");
    
    grunt.loadNpmTasks("grunt-contrib-jshint");
    
    // Project configuration.
    grunt.initConfig({
        // js linting options
        jshint : {
            all : [
                "Gruntfile.js",
                "dl.js",
            ],
            options : {
                jshintrc: "jshintrc.json"
            },
        }
	});
	
    // Default task.
    grunt.registerTask("default", "jshint");
};
