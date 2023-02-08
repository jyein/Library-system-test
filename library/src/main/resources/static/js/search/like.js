class LikeApi {
    static #instance = null;
    static getInstance() {
        if (this.#instance == null) {
            this.#instance = new likeApi();
        }
        return this.#instance;
    }
    
    
}

class LikeService {
    static #instance = null;
    static getInstance() {
        if (this.#instance == null) {
            this.#instance = new likeApi();
        }
        return this.#instance;
    }
    
    
}