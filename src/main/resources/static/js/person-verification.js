export default class Verification{
    
    static typeOfVerification = Object.freeze({
        none: -1,
        failed : 0,
        success : 1,
        email : 2,
    });

    uid;
    code;
    state = Verification.typeOfVerification.none;

    constructor(uid){
        this.uid = uid;
    }

    attemptToVerifyWithOnlyUid(){
        var body = {
            uid: this.uid,
        }
        fetch("/mvc/person/verification", {
                method: "POST",
                body: JSON.stringify(body),
                cache: "no-cache",
                headers: new Headers({
                    "content-type": "application/json"
                })
        }).then((response) => response.json()).then((data) => {
            if(!data.state){
                this.state = Verification.typeOfVerification.failed;
            } else {
                this.state = data.state;
            }
        })
    }

    attemptToVerifyEmailWithCode(){
        var body = {
            uid: this.uid,
            code: this.code,
        }
        fetch("/mvc/person/verification/code", {
                method: "POST",
                body: JSON.stringify(body),
                cache: "no-cache",
                headers: new Headers({
                    "content-type": "application/json"
                })
        }).then((response) => response.json()).then((data) => {
            if(!data.state){
                this.state = Verification.typeOfVerification.failed;
            } else {
                this.state = data.state;
            }
        })
    }

    setCode(code){
        this.code = code;
    }

    setUid(uid){
        this.uid = uid;
    }

    getCode(){
        return this.code;
    }

    getUid(){
        return this.uid;
    }
}