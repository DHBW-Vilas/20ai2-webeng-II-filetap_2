module.exports = {
    globDirectory: '.',
    globPatterns: [
        '**/*.{svg,css,html,sass,png,webmanifest}'
    ],
    swDest: 'sw.js',
    ignoreURLParametersMatching: [
        /^utm_/,
        /^fbclid$/
    ]
};