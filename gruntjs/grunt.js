/*global module:false*/
module.exports = function(grunt) {

  grunt.loadNpmTasks('grunt-contrib-coffee');
  grunt.loadNpmTasks('grunt-contrib-requirejs');
  grunt.loadNpmTasks('grunt-rm');
  grunt.loadNpmTasks('grunt-preprocess');
  grunt.loadNpmTasks('grunt-contrib-clean');
  grunt.loadNpmTasks('grunt-env');
  grunt.loadNpmTasks('grunt-jasmine-task');
  grunt.loadTasks('tasks');

  // remove when 0.4.0
  grunt.util = grunt.util || grunt.utils;

  var _ = grunt.util._;

  grunt.registerMultiTask('inline', 'My inline task', function() {
    if (!this.data) {
      return;
    }

    var files = this.data.files;
    switch(this.target) {
      case 'app':
        inline(files);
        break;
    }

    function inline(files) {
      for(var dest in files) {
        if (!files.hasOwnProperty(dest)) continue;
        var txt = grunt.file.read(dest), inline = '';
        var baseDir = dest.split('/').slice(0,-1).join('/');
        txt = txt.replace(/<script.*?src="(.*?)".*?><\/script>/g, function(m, src) {
          inline += '// file: ' + src + '\n';
          inline += grunt.file.read(baseDir + '/' + src) + "\n";
          return '';
        });
        var inlines = files[dest];
        if (typeof inlines === 'string') {
          inlines = [inlines];
        }
        for(var i = 0; i<inlines.length;i++) {
          inline += '// file: ' + inlines[i] + '\n';
          inline += grunt.file.read(inlines[i]);
        }
        inline = "<script>\n(function(){\n" + inline + "\n})()\n</script>";
        var s = txt.split('<!-- @AWESOME_INSERT -->');
        txt = s[0] + inline + s[1];
        grunt.file.write(dest, txt);
      }
    }

  });

  // Project configuration.
  grunt.initConfig({
    env: {
      dev: {
        ENV_VAR: 'dev'
      },
      build: {
        ENV_VAR: 'build'
      }
    },
    clean: {
      dist: ['dist']
    },
    coffee: {
      compile: {
        files: {
          'webapp-tmp/scripts/**.js' : 'webapp/scripts/**/*.coffee'
        }
      }
    },
    jasmine: {
      index: ['test/htmlRunner.html']
    },
    instrument: {
      options : {
        basePath : 'webapp-tmp/scripts2',
        baseDir : 'webapp-tmp/scripts'
      },
      files: ['webapp-tmp/scripts/**']
    },
    requirejs: {
      compile: {
        options: {
          appDir: "webapp-tmp",
          baseUrl: "scripts",
          dir: "dist/",
          // Comment out the optimize line if you want
          // the code minified by UglifyJS
          //optimize: "none",

          paths: {
              "jquery": "empty:",
              "cs": "../../webapp/lib/cs",
              "coffee-script": "../../webapp/lib/coffee-script",
              "underscore": "../../webapp/lib/underscore",
              "jade": "../../webapp/lib/jade",
              "text": "../../webapp/lib/text",
              "views": "../../webapp/views"
          },

          shim: {
            underscore: {
              init: function() {
                return _.noConflict();
              },
              exports: '_'
            }
          },


          // Stub out the cs module after a build since
          // it will not be needed.
          stubModules: ['cs'],

          pragmas: {
            excludeJadeOnSave:true
          },

          inlineText: true,

          onBuildRead: function(moduleName, path, contents) {
            return contents.replace(/cs!/g, '');
          },

          onBuildWrite: function(moduleName, path, contents) {
            return contents.replace(/(jade|text)!/g, '');
          },

          modules: [
            // Optimize the application files. jQuery is not
            // included since it is already in require-jquery.js
            {
              name: "main",

              exclude: ['cs', 'coffee-script', 'jade']
            }
          ]
        }
      }
    },
    preprocess : {
      conf : {
        src : 'webapp/conf.js',
        dest : 'dist/conf.js'
      },
      app : {
        src : 'webapp/app.html',
        dest : 'dist/app.html'
      }
    },
    min: {
      requirejs: {
        src: "webapp/lib/require-jquery.js",
        dest: "dist/lib/require-jquery.js"
      },
      conf: {
        src: 'dist/conf.js',
        dest: 'dist/conf.js'
      }
    },
    inline: {
      app: {
        files: {
          'dist/app.html': 'dist/scripts/main.js'
        }
      }
    },
    rm: {
      build: ['dist/build.txt', 'dist/conf.js'],
      tmp: {
        dir: 'webapp-tmp/',
      },
      scripts: {
        dir: 'dist/scripts/'
      },
      lib: {
        dir: 'dist/lib/'
      }
    }
  });

  // Default task.
  grunt.registerTask('default', 'clean coffee requirejs min preprocess inline:app rm');
  grunt.registerTask('build', 'env:build clean coffee requirejs min preprocess rm');
  grunt.registerTask('coverage', 'env:dev clean coffee instrument jasmine');
};
