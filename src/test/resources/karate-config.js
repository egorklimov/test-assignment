function fn() {
  let config;

  let port = karate.properties['karate.port'] || '8080'
  config = {
    baseUrl: 'http://localhost:' + port
  }

  karate.configure('connectTimeout', 1000);
  karate.configure('readTimeout', 1000);

  karate.log('baseUrl set to ' + config.baseUrl);
  return config;
}