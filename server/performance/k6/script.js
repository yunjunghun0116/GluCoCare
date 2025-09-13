import http from "k6/http";
import {check, sleep} from "k6";


const BASE_URL = "https://assured-mastodon-basically.ngrok-free.app";

export const options = {
    vus: 50,
    duration: "1m"
}

export function setup() {
    const res = http.post(`${BASE_URL}/api/members/login`, JSON.stringify({
        "email": "hello@naver.com",
        "password": "aaaaaaaa"
    }), {
        headers: {"Content-Type": "application/json"}
    });
    if (res.status === 200) {
        const {accessToken, refreshToken} = res.json();
        return {accessToken, refreshToken};
    }
}

export default function (data) {
    let accessToken = data.accessToken;
    let refreshToken = data.refreshToken;

    const headers = () => ({
        headers: {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${accessToken}`
        }
    })

    const apiEndpoint = `${BASE_URL}/api/patients/1/glucose-histories`

    let res = http.get(apiEndpoint, headers());

    // accessToken 없거나 401/403일 때 refreshToken 과정 추가
    if (res.status === 401 || res.status === 403) {
        const ref = http.post(`${BASE_URL}/api/members/refresh-token`, JSON.stringify({
            "token": refreshToken
        }), {
            headers: {"Content-Type": "application/json"}
        });

        if (ref.status === 200) {
            const json = ref.json();
            accessToken = json.accessToken;
            refreshToken = json.refreshToken;
            res = http.get(apiEndpoint, headers);
        }
    }

    check(res, {"성공 코드": (r) => r.status === 200});
    sleep(1);
}
