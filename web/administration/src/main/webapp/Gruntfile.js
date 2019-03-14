/*jslint node: true */
'use strict';

var pkg = require('./package.json');
var proxySnippet = require('grunt-connect-proxy/lib/utils').proxyRequest;

//Using exclusion patterns slows down Grunt significantly
//instead of creating a set of patterns like '**/*.js' and '!**/node_modules/**'
//this method is used to create a set of inclusive patterns for all subdirectories
//skipping node_modules, bower_components, dist, and any .dirs
//This enables users to create any directory structure they desire.
var createFolderGlobs = function (fileTypePatterns) {
    fileTypePatterns = Array.isArray(fileTypePatterns) ? fileTypePatterns : [fileTypePatterns];
    var ignore = ['node_modules', 'bower_components', 'dist', 'temp', 'node'];
    var fs = require('fs');
    return fs.readdirSync(process.cwd())
        .map(function (file) {
            if (ignore.indexOf(file) !== -1 ||
                file.indexOf('.') === 0 || !fs.lstatSync(file).isDirectory()) {
                return null;
            } else {
                return fileTypePatterns.map(function (pattern) {
                    return file + '/**/' + pattern;
                });
            }
        })
        .filter(function (patterns) {
            return patterns;
        })
        .concat(fileTypePatterns);
};

module.exports = function (grunt) {

    // load all grunt tasks
    require('load-grunt-tasks')(grunt);
    // load ng-constant task
    grunt.loadNpmTasks('grunt-ng-constant');
    grunt.loadNpmTasks('grunt-ngdocs');

    // Project configuration.
    grunt.initConfig({
        ngconstant: {
            options: {
                name: 'config',
                constants: {
                    CFG :{
                        package: grunt.file.readJSON('package.json'),
                        USM : grunt.file.exists('USMconstants.yaml')?grunt.file.readYAML('USMconstants.yaml'):grunt.file.readYAML('USMconstants-default.yaml'),
                        maven: {
                            version: grunt.option('maven.version'),
                            timestamp:grunt.option('maven.buildts')
                        },
                        gruntts:new Date()
                    }
                },
                values: {
                    debug: true
                }
            },
            build: {
            },
            // Environment targets
            development: {
                options: {
                    dest: 'app/config.js'
                },
                constants: {
                    ENV: {
                        name: 'development'

                    }
                }
            },
            // Environment targets
            production: {
                options: {
                    dest: 'app/config.js'
                },
                constants: {
                    ENV: {
                        name: 'production'
                    }
                }
            }
        },
        connect: {
            server: {
                options: {
                    port : 9002,
                    hostname: 'localhost',

                    middleware: function (connect, options) {
                        var proxy = require('grunt-connect-proxy/lib/utils').proxyRequest;
                        return [
                            // Include the proxy first
                            proxy,
                            // Serve static files.
                            connect.static(String(options.base)),
                            // Make empty directories browsable.
                            connect.directory(String(options.base))
                        ];
                    }
                },
                proxies: grunt.file.exists('proxies.yaml')?grunt.file.readYAML('proxies.yaml'):{
                    context: ['/usm-authentication/rest', '/usm-authorisation/rest', '/usm-administration/rest','/config/rest'],
                   // host: 'localhost',
                   host: 'cygnus-dev.athens.intrasoft-intl.private',
                   // port: 8080,
                    port: 28443,
                    https: true,
                    secure: false,
                    xforward: false,
                    headers: {
                        "x-custom-added-header": ""
                    },
                    hideHeaders: ['x-removed-header']
                }
            }

        },
        watch: {
            main: {
                options: {
                    livereload: 35730,
                    livereloadOnError: false,
                    spawn: false
                },
                files: [createFolderGlobs(['*.js', '*.less', '*.html']), '!_SpecRunner.html', '!.grunt'],
                tasks: [] //all the tasks are run dynamically during the watch event handler
            }
        },
        jshint: {
            main: {
                options: {
                    jshintrc: '.jshintrc',
                    ignores: ['*.js', 'docs/**/*.js','tests/**', 'app/**/*-spec.js', 'app/**/e2e/*.js','app/**/e2e/**/*.js']
                },
                src: createFolderGlobs('*.js', '!/bower_components/**/*.js')

            }
        },
        clean: {
            before: {
                src: ['dist', 'temp']
            },
            after: {
            //    src: ['temp']
            },
            config:["app/config.js"]
        },
        less: {
            production: {
                files: {
                    'temp/app.css': 'app/app.less'
                }
            }
        },
        ngtemplates: {
            main: {
                options: {
                    module: 'app',
                    htmlmin: '<%= htmlmin.main.options %>',
                    url: function (url) {
                        return url.replace('app/', '');
                    }
                },
                src: [createFolderGlobs('*.html'), '!index.html', '!_SpecRunner.html','!app/bower_components/**/*.html'],
                dest: 'temp/templates.js'
            }
        },
        copy: {
            main: {
                files: [
                    {src: ['img/**'], dest: 'dist/'},
                    {src: ['bower_components/font-awesome/fonts/**'],dest: 'dist/',filter: 'isFile',expand: true},
                    {src: ['bower_components/bootstrap/fonts/**'],dest: 'dist/fonts',filter: 'isFile',flatten: true,expand: true},
                    {src: ['WEB-INF/**'], dest: 'dist/', filter: 'isFile', expand: true},
                    {src: ['**/*.properties'], dest: 'dist/', filter: 'isFile', expand: true},
                    {cwd: 'app/usm', src: ['ec-template/ec-html-bundle-spa/img/**'], dest: 'dist/usm/', filter: 'isFile', expand: true},
                    {cwd: 'app/usm', src: ['assets/global.json'], dest: 'dist/usm/', expand: true},
                    {cwd: 'app/usm', src: ['assets/EnvSettings*.json'], dest: 'dist/usm/', expand: true},
                    {cwd: 'app/usm', src: ['assets/translate/**/*'], dest: 'dist/usm/', expand: true}
                    //{src: ['bower_components/angular-ui-utils/ui-utils-ieshiv.min.js'], dest: 'dist/'},
                    //{src: ['bower_components/select2/*.png','bower_components/select2/*.gif'], dest:'dist/css/',flatten:true,expand:true},
                    //{src: ['bower_components/angular-mocks/angular-mocks.js'], dest: 'dist/'}
                ]
            }
        },
        dom_munger: {
            read: {
                options: {
                    read: [
                        {selector: 'script[data-concat!="false"]', attribute: 'src', writeto: 'appjs'},
                        {selector: 'link[rel="stylesheet/less"][data-concat!="false"]', attribute: 'href', writeto: 'appcss'}
                    ]

                },
                    src: 'app/index.html'
            },
            update: {
                options: {
                    remove: ['script[data-remove!="false"]', 'link[data-remove!="false"]'],
                    append: [
                        {selector: 'body', html: '<script src="app.full.min.js"></script>'},
                        {selector: 'head', html: '<link rel="stylesheet" href="app.full.min.css">'}
                    ]
                },
                src: 'app/index.html',
                dest: 'dist/index.html'
            }
        },
        cssmin: {
            main: {
                src: ['temp/app.css', '<%= dom_munger.data.appcss %>'],
                dest: 'dist/app.full.min.css'
            }
        },
        // Used to fix the relative path of fonts in minified css
        replace: {
            main: {
                src: ['dist/app.full.min.css'],
                overwrite: true,        // overwrite matched source files
                replacements: [
                    {
                        from: '../fonts/glyphicons-halflings',
                        to: 'fonts/glyphicons-halflings'
                    },
                    {
                        from: '../bower_components/font-awesome/fonts',
                        to: 'bower_components/font-awesome/fonts'
                    }
                ]
            }
        },
        concat: {
            main: {
                src: ['<%= dom_munger.data.appjs %>', '<%= ngtemplates.main.dest %>'],
                dest: 'temp/app.full.js',
                //cwd: 'app/',
                nonull: true
            }
        },
        ngAnnotate: {
            main: {
                src: 'temp/app.full.js',
                dest: 'temp/app.full.js'
            }
        },
        uglify: {
            main: {
              options: {
              mangle: false,
              beautify: true,
              banner: '/*Version: <%= grunt.template.today("yyyy-mm-dd HH:MM") %> */',
              sourceMap: true,
              compress: { unused: false}
              },
              src: 'temp/app.full.js',
              dest: 'dist/app.full.min.js'
            }
        },
        htmlmin: {
            main: {
                options: {
                    collapseBooleanAttributes: true,
                    collapseWhitespace: true,
                    removeAttributeQuotes: true,
                    removeComments: true,
                    removeEmptyAttributes: true,
                    removeScriptTypeAttributes: true,
                    removeStyleLinkTypeAttributes: true
                },
                files: {
                    'dist/index.html': 'dist/index.html'
                }
            }
        },
        //Imagemin has issues on Windows.
        //To enable imagemin:
        // - "npm install grunt-contrib-imagemin"
        // - Comment in this section
        // - Add the "imagemin" task after the "htmlmin" task in the build task alias
        // imagemin: {
        //   main:{
        //     files: [{
        //       expand: true, cwd:'dist/',
        //       src:['**/{*.png,*.jpg}'],
        //       dest: 'dist/'
        //     }]
        //   }
        // },
        karma: {
            options: {
                frameworks: ['jasmine'],
                files: [  //this files data is also updated in the watch handler, if updated change there too
                    '<%= dom_munger.data.appjs %>',
                    'bower_components/angular-mocks/angular-mocks.js',
                    createFolderGlobs('*-spec.js')
                ],
                logLevel: 'ERROR',
                reporters: ['mocha'],
                autoWatch: false, //watching is handled by grunt-contrib-watch
                singleRun: true
            },
            all_tests: {
                browsers: ['PhantomJS', 'Chrome', 'Firefox']
            },
            during_watch: {
                browsers: ['PhantomJS']
            }
        },
        // e2e testing
        protractor: {
            options: {
                keepAlive: false, // If false, the grunt process stops when the test fails.
                noColor: true, // If true, protractor will not use colors in its output
                configFile: "protractor.conf.js",
                debug: false
            },
            run: {},
            firefox:{
                options: {
                    args: {
                        browser: 'firefox'
                    }
                }
            },
            parallel:{
                options: {
                    configFile: "protractor-parallel.conf.js"
                }
            }
        },
        protractor_webdriver: {
            options: {
                // Task-specific options go here.
                // command (default): webdriver-manager start
                // path (default): ''
            }

        },
        war: {
            target: {
                options: {
                    war_dist_folder: 'dist/', /* Folder where to generate the WAR. */
                    war_name: 'administration', /* The name fo the WAR file (.war will be the extension) */
                    webxml_welcome: 'index.html',
                    webxml_display_name: 'administration'
                },
                files: [
                    {
                        expand: true,
                        cwd: 'dist/',
                        src: ['**/*'],
                        dest: '/'
                    }
                ]
            }
        },
        ngdocs: {
            usmauth: {
                title: 'USM Auth API Documentation',
                src: ['app/common/services/auth/**/*.js']
            }
        }
    });

    grunt.registerTask('logmun', 'dom_munger logging', function(arg1, arg2) {

        //grunt.log.writeln("dom_munger config");
        var appfiles = grunt.config.get('dom_munger.data.appjs');
        //console.log(appfiles);
        appfiles.forEach(function(part, index) {
          appfiles[index] = "app/"+appfiles[index];
        });
        //console.log(appfiles);
        grunt.config('dom_munger.data.appjs',appfiles);
        //console.log(grunt.config('dom_munger'));

    });
    grunt.task.registerTask('usmconstants', 'usm constants task', function(arg1, arg2) {
        if (arguments.length === 0) {
            grunt.log.writeln(this.name + ", no args");
            grunt.log.writeln("CLI options "+ grunt.log.wordlist(grunt.option.flags()));
            var maven = grunt.option('mvnarg');
            grunt.log.writeln("maven arg: "+ maven);

            grunt.task.run('ngconstant:production');
        } else {
            grunt.log.writeln(this.name + ", " + arg1 + " " + arg2);

            grunt.log.writeln("CLI options"+ grunt.log.wordlist(grunt.option.flags()));
            grunt.task.run('ngconstant:'+arg1);
        }
    });
    grunt.registerTask('buildm',['dom_munger', 'logmun']);
    grunt.registerTask('build', ['clean:config','jshint', 'clean:before', 'usmconstants' , 'less', 'dom_munger', 'logmun', 'ngtemplates', 'cssmin', 'replace', 'concat', 'ngAnnotate', 'uglify', 'copy', 'htmlmin', 'clean:after']);
    grunt.registerTask('serve', ['usmconstants:development','dom_munger:read', 'jshint', 'configureProxies:server', 'connect', 'watch']);
    //grunt.registerTask('test',['dom_munger:read','karma:all_tests']);
    grunt.registerTask('test', ['protractor:run']);

    grunt.event.on('watch', function (action, filepath) {
        //https://github.com/gruntjs/grunt-contrib-watch/issues/156

        var tasksToRun = [];

        if (filepath.lastIndexOf('.js') !== -1 && filepath.lastIndexOf('.js') === filepath.length - 3) {

            //lint the changed js file
            grunt.config('jshint.main.src', filepath);
            tasksToRun.push('jshint');

            //find the appropriate unit test for the changed file
            var spec = filepath;
            if (filepath.lastIndexOf('-spec.js') === -1 || filepath.lastIndexOf('-spec.js') !== filepath.length - 8) {
                spec = filepath.substring(0, filepath.length - 3) + '-spec.js';
            }

            //if the spec exists then lets run it
            if (grunt.file.exists(spec)) {
                var files = [].concat(grunt.config('dom_munger.data.appjs'));
                files.push('bower_components/angular-mocks/angular-mocks.js');
                files.push(spec);
                grunt.config('karma.options.files', files);
                tasksToRun.push('karma:during_watch');
            }
        }

        //if index.html changed, we need to reread the <script> tags so our next run of karma
        //will have the correct environment
        if (filepath === 'app/index.html') {
            tasksToRun.push('dom_munger:read');
        }

        grunt.config('watch.main.tasks', tasksToRun);

    });

};
