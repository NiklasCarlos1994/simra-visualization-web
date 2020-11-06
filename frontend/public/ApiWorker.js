var progress = 0;
var URL_BACKEND = "";

self.onmessage = function (event) {
    switch (event.data[0]) {
        case "backendUrl":
            URL_BACKEND = event.data[1];
            break;
        case "incidents":
            loadIncidents(event.data[1]);
            break;
        case "route":
            break;
        case "polygon":
            loadPolygon(event.data[1], event.data[2]);
            break;
    }
};

const updateLoadingProgress = (progress, expectedTotal) => self.postMessage(["progress", progress, expectedTotal]);
const startLoading = () => updateLoadingProgress(0, 1);
const finishLoading = () => updateLoadingProgress(1, 1);

var incidents = {};
function loadIncidents(polygon) {
    if (!incidents.hasOwnProperty(polygon)) {
        startLoading();

        let url = `http://207.180.205.80:8000/api/incidents?exclude_type=0&contains=POLYGON%28%28${polygon.map(coord => (coord[0] / 100.0) + '+' + (coord[1] / 100.0)).join('%2C')}%29%29&format=json`;
        console.log(url);
        fetch(url).then(r => r.json()).then(result => {
                // for (let i = 0; i < result.length; i++) {
                //     let seed = parseInt(result[i].properties.key + result[i].properties.ts);
                //     result[i].geometry.coordinates[0] += getCoordinateOffset(seed);
                //     result[i].geometry.coordinates[1] += getCoordinateOffset(seed + 1);
                // }

                incidents[polygon] = result;
                self.postMessage(["incidents", result]);
                finishLoading();
            });
    } else {
        console.log("cache hit!");
        self.postMessage(["incidents", incidents[polygon]]);
    }
}

function loadPolygon(polygon, mode) {
    startLoading();
    let url = `http://207.180.205.80:8000/api/rides?${mode}=POLYGON%28%28${polygon.join('%2C')}%29%29&format=json`;
    console.log(url);
    fetch(url).then(r => r.json()).then(result => {
        self.postMessage(["polygon", result]);
        finishLoading();
    });
}

/**
 * This will provide a fixed-per-seed noise offset for one coordinate axis.
 * The value distribution is not uniform (which isn't really needed) but the function performs fast.
 */
function getCoordinateOffset(seed) {
    var x = Math.sin(seed) * 10000;
    x -= Math.floor(x);
    x = (x - 0.5) * 2; // range [-1, 1]

    return x / 5000;
}
