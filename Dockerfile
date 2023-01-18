FROM redis

COPY /config/redis-sentinel.conf /usr/local/etc/redis/redis-sentinel.conf

CMD [ "redis-sentinel", "/usr/local/etc/redis/redis-sentinel.conf" ]