## Glaze

Use Glaze to access your [Alpas](https://alpas.dev) [named routes](https://alpas.dev/docs/routing#route-name) from inside your JavaScript.

### Usage

You can use Glaze in one of two ways - using `glaze` template function that will make a global `Glaze` JavaScript object as well as a `route()` helper function, you can then call these to access named routes in your JavaScript.

You can also integrate Glaze with Mix by importing it in your asset pipeline. You can then use it from a frontend library like VueJS easily.

#### Examples:

```javascript

// Returns '/posts'
route('posts.index')

// Returns /posts/1
route('posts.show', 1)
route('posts.show', [1])
route('posts.show', {id: 1})

```

### Getting Started
1. Add Glaze as a Gradle dependency:

```gradle

implementation 'com.github.alpas:glaze:master'

```

2. Register `GlazeServiceProvider` in both `HttpKernel` and `ConsoleKernel`:

```kotlin

override fun serviceProviders(app: Application): Iterable<KClass<out ServiceProvider>> {
    return listOf(
        //...
        //...
        GlazeServiceProvider::class
    )
}

```

#### Using in a template

1. Add `{{ glaze() }}` in one of your layout template's `<head>` section. Any scripts added after this will have access to a global `Glaze` object and a `route()` helper function.

### Using it from a VueJS component

1. Publish all the required scripts by using the `alpas glaze:publish` console command. This will add two scripts — `route.js` and `glaze.js` — in `resources/js` folder.
2. Open `webpack.mix.js` file and add the following:

```javascript
const path = require('path')

mix.webpackConfig({
  resolve: {
    alias: {
      glaze: path.resolve('src/main/resources/js/route.js'),
    },
  },
})

```

3. Open `app.js` and make the `route()` method available in every Vue components:

```javascript

import Vue from 'vue'
import route from 'glaze'
import { Glaze } from './glaze'

// ...

Vue.mixin({
  methods: {
    route: (name, params, absolute) =>  route(name, params, absolute, Glaze)
  }
})

// ...

```

You can now use the `route()` method in your Vue components like so: `<a :href="route('login')">Login</a>`
Or you can call it from your VueJS component's script like so: `this.route('login')`


### Glaze Config

You can set the base URL to be used for each routes by extending the `GlazeConfig` file.

Glaze is hugely inspired and based on [Ziggy](https://github.com/tightenco/ziggy) and [js-routes](https://github.com/railsware/js-routes).
