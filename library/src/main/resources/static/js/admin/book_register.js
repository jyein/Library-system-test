window.onload = () => {
    // window.onload라는 함수는 딱 한번만 호출된다
    // (js파일이 여러개인경우엔 window.onload메소드는 하나만 존재해야함)
    BookRegisterService.getInstance().loadCategories();

    ComponentEvent.getInstance().addClickEventRegisterButton();
    // 우선순위가 높으므로 위로간다
    ComponentEvent.getInstance().addClickEventImgAddButton();
    ComponentEvent.getInstance().addChangeEventImgFile();
    ComponentEvent.getInstance().addClickEventImgRegisterButton();
    ComponentEvent.getInstance().addClickEventImgCancelButton();
}

const bookObj = {
    bookCode: "",
    bookName: "",
    author: "",
    publisher: "",
    publicationDate: "",
    category: ""
}

const fileObj = {
    files: new Array(),
    formData: new FormData()
};

class BookRegisterApi {
    static #instance = null;
    static getInstance() {
        if (this.#instance == null) {
            this.#instance = new BookRegisterApi();
        }
        return this.#instance;
    }

    registerBook() {
        let successFlag = false;

        $.ajax({
            async: false,
            type: "post",
            url: "http://localhost:8000/api/admin/book",
            contentType: "application/json",
            data: JSON.stringify(bookObj),
            dataType: "json",
            success: response => {
                successFlag = true;
            },
            error: error => {
                console.log(error);
                BookRegisterService.getInstance().setErrors(error.responseJSON.data);
            }
        });

        return successFlag;
    }

    /*
            encType: "multipart/form-data", // encType = encodingType 
            contentType: false,
            processData: false,
            multipart를 사용할때엔 위의 세개가 세트로 온다
    */
    registerImg() {
        // 멀티파트 데이터는 무조건 post요청으로 해야한다
        $.ajax({
            async: false,
            type: "post",
            url: `http://localhost:8000/api/admin/book/${bookObj.bookCode}/images`,
            encType: "multipart/form-data", // encType = encodingType 
            // JSON이랑 비슷한 형식으로 날라간다
            contentType: false,
            processData: false,
            data: fileObj.formData,
            dataType: "json",
            success: response => {
                alert("도서 이미지 등록 완료.");
                location.reload();
            },
            error: error => {
                console.log(error);
            }

        });
    }

    getCategories() {
        let responseData = null;

        $.ajax({
            async: false,
            type: "get",
            url: "http://localhost:8000/api/admin/categories",
            dataType: "json",
            success: response => {
                responseData = response.data;
            },
            error: error => {
                console.log(error);
            }
        });

        return responseData;
    }

}

class BookRegisterService {
    static #instance = null;
    static getInstance() {
        if (this.#instance == null) {
            this.#instance = new BookRegisterService();
        }
        return this.#instance;
    }

    setBookObjValues() {
        const registerInputs = document.querySelectorAll(".register-input");

        bookObj.bookCode = registerInputs[0].value;
        bookObj.bookName = registerInputs[1].value;
        bookObj.author = registerInputs[2].value;
        bookObj.publisher = registerInputs[3].value;
        bookObj.publicationDate = registerInputs[4].value;
        bookObj.category = registerInputs[5].value;
    }

    loadCategories() {
        const responseData = BookRegisterApi.getInstance().getCategories();

        const categorySelect = document.querySelector(".category-select");
        categorySelect.innerHTML = `<option value="">전체조회</option>`;

        responseData.forEach(data => {
            categorySelect.innerHTML += `
                <option value="${data.category}">${data.category}</option>
            `;
        });
    }

    setErrors(errors) {
        const errorMessages = document.querySelectorAll(".error-message");
        this.clearErrors();

        Object.keys(errors).forEach(key => {
            if (key == "bookCode") {
                errorMessages[0].innerHTML = errors[key];
            } else if (key == "bookName") {
                errorMessages[1].innerHTML = errors[key];
            } else if (key == "category") {
                errorMessages[5].innerHTML = errors[key];
            }
        });        
    }

    clearErrors() {
        const errorMessages = document.querySelectorAll(".error-message");

        errorMessages.forEach(error => {
            error.innerHTML = "";
        });
    }
}

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
            BookRegisterService.getInstance().setBookObjValues();
            const successFlag = BookRegisterApi.getInstance().registerBook();
            // 이게 true를 가져온다

            if (!successFlag) { // successFlag가 true가아니면 onclick메소드를 빠져나가라
                return;
            }

            if (confirm("도서 이미지를 등록하시겟습니까?")) {
                const imgAddButton = document.querySelector(".img-add-button");
                const imgCancelButton = document.querySelector(".img-cancel-button");

                imgAddButton.disabled = false;
                imgCancelButton.disabled = false;
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
                const imgRegisterButton = document.querySelector(".img-register-button");
                imgRegisterButton.disabled = false;
                ImgFileService.getInstance().getImgPreview();
                imgFile.value = null;
            }
        }
    }

    addClickEventImgRegisterButton() {
        const imgRegisterButton = document.querySelector(".img-register-button");

        imgRegisterButton.onclick = () => {
            // formData는 Map이랑 구조가 비슷하다
            fileObj.formData.append("files", fileObj.files[0]);
            // fileObj에 0번쨰 인덱스에있는 것을 formData에 append를 해준다
            BookRegisterApi.getInstance().registerImg();
        }
    }

    addClickEventImgCancelButton() {
        const imgCancelButton = document.querySelector(".img-cancel-button");

        imgCancelButton.onclick = () => {
            if(confirm("정말로 이미지 등록을 취소하시겠습니까?")) {
                location.reload();
            }
        }
    }
    
}