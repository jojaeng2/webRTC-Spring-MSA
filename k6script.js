
import http from 'k6/http';
import { sleep } from 'k6';

export default function () {
    const params = {
        headers: { 'Authorization': 'jwt eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJrc3cxMTEiLCJleHAiOjE2NjA1NzYxNjUsImlhdCI6MTY2MDU1ODE2NX0.bRlDqtMHbFxDbDvlkFxuv61DaFL0-IYgdI1Vl5uOixgLut9v_6mwZ_w3rXuvf6GPYMaDn24CvjW7spRfRK6hFQ' },
        };
  http.get('https://43.200.119.131:9443/api/v1/webrtc/chat/channels/partiDESC/0', params);

    // const params = {
    //     headers: { 
    //         'Authorization': 'jwt eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJrc3cxMTEiLCJleHAiOjE2NjA1NzYxNjUsImlhdCI6MTY2MDU1ODE2NX0.bRlDqtMHbFxDbDvlkFxuv61DaFL0-IYgdI1Vl5uOixgLut9v_6mwZ_w3rXuvf6GPYMaDn24CvjW7spRfRK6hFQ' ,
    //         "Content-Type" : "application/json"
        
    //     },
    //     };

    // for(var i = 0; i<20; i++) {
    //     const body = {
    //         "channelName" : "123" + i,
    //         "hashTags" : [
    //             "tag1", "tag2"
    //         ],
    //         "channelType" : "TEXT"
    //     }

    //     http.post('https://43.200.119.131:9443/api/v1/webrtc/chat/channel', JSON.stringify(body), params);
    // }
    // sleep(1);
}
