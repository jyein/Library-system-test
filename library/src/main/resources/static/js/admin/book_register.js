window.onload = () => {
    ComponentEvent.getInstance().addClickEventRegisterButton(); 
    // 우선순위가 높으므로 위로간다

    ComponentEvent.getInstance().addClickEventImgAddButton();
    ComponentEvent.getInstance().addChangeEventImgFile();
}

const fileObj = {
    files: new Array()
};

class ImgFileService {
    static #instance = null;
    static getInstance() {
        if (this.#instance == null) {
            this.#instance = new ImgFileService();
        }
        return this.#instance;
    }

    // 강의 2시간 20분쯤 설명
    getImgPreview() {
        const bookImg = document.querySelector(".book-img");

        const reader = new FileReader();

        reader.onload = (e) => {
            bookImg.src = e.target.result;
        }

        reader.readAsDataURL(fileObj.files[0]);
    }

}

class ComponentEvent {
    static #instance = null;
    static getInstance() {
        if (this.#instance == null) {
            this.#instance = new ComponentEvent();
        }
        return this.#instance;
    }

    // disabled 푸는것 연습
    addClickEventRegisterButton() {
        const registerButton = document.querySelector(".register-button");

        registerButton.onclick = () => {
            if (confirm("도서 이미지를 등록하시겟습니까?")) {
                const imgAddButton = document.querySelector(".img-add-button");
                const imgRegisterButton = document.querySelector(".img-register-button");

                imgAddButton.disabled = false;
                imgRegisterButton.disabled = false;
            } else {
                location.reload();
                // reload() = 새로고침(f5같은)
            }
        }

    }

    addClickEventImgAddButton() {
        const imgFile = document.querySelector(".img-file");
        const addButton = document.querySelector(".img-add-button");

        addButton.onclick = () => {
            imgFile.click();
        }
    }

    addChangeEventImgFile() {
        const imgFile = document.querySelector(".img-file");

        imgFile.onchange = () => {
            const formData = new FormData(document.querySelector(".img-form"));
            // 여기서 form을 잡아준이유는 form에서 정보를 가져오기위함
            let changeFlag = false;

            fileObj.files.pop();
            // change될때마다 비워버린다
            // pop 메서드 : 배열의 마지막 요소를 제거한 후, 제거한 요소를 반환

            formData.forEach(value => {
                console.log(value); // 여기서 value가 가져오는건 이미지 객체

                if (value.size != 0) {
                    fileObj.files.push(value);
                    changeFlag = true;
                }
            });

            if (changeFlag) {
                ImgFileService.getInstance().getImgPreview();
                imgFile.value = null;
            }
        }
    }
}