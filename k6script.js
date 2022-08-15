import http from 'k6/http';
import { sleep } from 'k6';

export default function () {
    const params = {
        cookies: { my_cookie: 'value' },
        headers: { 'X-MyHeader': 'k6test' },
        redirects: 5,
        tags: { k6test: 'yes' },
      };
  http.get('https://43.200.119.131:9443/api/v1/webrtc/chat/channels/partiDESC/0');
  sleep(1);
}
