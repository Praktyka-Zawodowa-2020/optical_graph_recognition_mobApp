# Optical Graph Recognition
Mobile application for optical graph recognition. **A Google account is required to use the app.**

## Features
- Optical graph recognition from image, which can be taken from camera or file managers,
- Visualization of the recognized graph,
- Share recognized graph to other app,
- Opens files via the app to visualize a graph.
- Supports extensions such as GraphML (.graphml, .gml) and Graph6 (.g6).

## Used API

For optical recognition of graphs, the application uses the API from the [server](https://github.com/Praktyka-Zawodowa-2020/optical_graph_recognition_server). For authorization on the server, the data of the google user account is used, based on this, the Google Sign-In mechanism is integrated into the application.

## Used technologies
* [GraphView](https://github.com/Team-Blox/GraphView) - is used to display data in graph structures.
* [Fuel](https://github.com/kittinunf/Fuel) - the easiest HTTP networking library for Kotlin/Android.
* [Glide](https://github.com/bumptech/glide) - is a fast and efficient open source media management and image loading framework for Android that wraps media decoding, memory and disk caching, and resource pooling into a simple and easy to use interface.

## Documentation
Documentation of the project you can finde here [Click](docs/index.md)


#### Władysław Jakołcewicz 2020
