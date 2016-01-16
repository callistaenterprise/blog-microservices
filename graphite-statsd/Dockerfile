#
# Build commands:
#
# $ cd graphite-statsd
# $ docker build -t magnuslarsson/ms-blog-graphite-statsd .
# $ docker push magnuslarsson/ms-blog-graphite-statsd
#
# $ docker tag magnuslarsson/ms-blog-graphite-statsd magnuslarsson/ms-blog-graphite-statsd:3
# $ docker push magnuslarsson/ms-blog-graphite-statsd:3
#
FROM hopsoft/graphite-statsd:latest
MAINTAINER Magnus Larsson <magnus.larsson.ml@gmail.com>
ADD conf/graphite/graphTemplates.conf /opt/graphite/conf/graphTemplates.conf
ADD conf/graphite/composer_widgets.js /opt/graphite/webapp/content/js/composer_widgets.js
ADD conf/statsd/config.js /opt/statsd/config.js