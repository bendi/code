require.config({
    urlArgs: 'cb=' + Math.random(),
    paths: {
        'jasmine': 'lib/jasmine-1.3.0/jasmine',
        'jasmine-html': 'lib/jasmine-1.3.0/jasmine-html'
    },
    shim: {
        jasmine: {
            exports: 'jasmine'
        },
        'jasmine-html': {
            deps: ['jasmine'],
            exports: 'jasmine'
        }
    }
});


require(['jasmine-html'], function (jasmine) {

    var jasmineEnv = jasmine.getEnv();
    jasmineEnv.updateInterval = 1000;

    var htmlReporter = new jasmine.HtmlReporter();

    jasmineEnv.addReporter(htmlReporter);

    jasmineEnv.specFilter = function (spec) {
        return htmlReporter.specFilter(spec);
    };

    var specs = [];

    specs.push('spec/MessageBusSpec');

	require(specs, function (spec) {
        jasmineEnv.execute();
    });

});