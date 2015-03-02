if(process.env.COVERAGE) {
    require('coffee-coverage').register({
        path: 'abbr',
        basePath: __dirname + "/modules",
        exclude: ['/test', '/node_modules', '/.git'],
        initAll: true,
    });
}
