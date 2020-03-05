class UrlBuilder {
  constructor(name, absolute, glazeObject) {

    this.name = name
    this.glaze = glazeObject
    this.route = this.glaze.namedRoutes[this.name]

    if (typeof this.name === 'undefined') {
      throw new Error('Glaze Error: You must provide a route name')
    } else if (typeof this.route === 'undefined') {
      throw new Error(`Glaze Error: route '${this.name}' is not found in the route list`)
    }

    this.absolute = typeof absolute === 'undefined' ? true : absolute
    this.domain = this.setDomain()
    this.path = this.route.uri.replace(/^\//, '')
  }

  setDomain() {
    if (!this.absolute) {
      return '/'
    }

    if (!this.route.domain) {
      return this.glaze.baseUrl.replace(/\/?$/, '/')
    }

    let host = (this.route.domain || this.glaze.baseDomain).replace(/\/+$/, '')

    if (this.glaze.basePort && (host.replace(/\/+$/, '') === this.glaze.baseDomain.replace(/\/+$/, ''))) {
      host = this.glaze.baseDomain + ':' + this.glaze.basePort
    }

    return this.glaze.baseProtocol + '://' + host + '/'
  }

  construct() {
    return this.domain + this.path
  }
}

export default UrlBuilder;
