window.onload = () => {
    BookService.getInstance().loadBookList();
    BookService.getInstance().loadCategories();
    BookService.getInstance().loadSearchNumberList();
    ComponentEvent.getInstance().addClickEventSearchButton();
    ComponentEvent.getInstance().addClickEventDeleteButton();
    ComponentEvent.getInstance().addClickEventDeleteCheckAll();
}

let searchObj = {
    page: 1, // 현재페이지
    category: "",
    searchValue: "",
    order: "bookId",
    limit: "Y",
    count: 20
}

class BookSearchApi {
    static #instance = null;
    static getInstance() {
        if (this.#instance == null) {
            this.#instance = new BookSearchApi();
        }
        return this.#instance;
    }

    getBookList(searchObj) {
        let returnData = null;

        $.ajax({
            async: false,
            type: "get",
            url: "http://127.0.0.1:8000/api/admin/books",
            data: searchObj,
            dataType: "json",
            success: response => {
                console.log(response);
                returnData = response.data;
            },
            error: error => {
                console.log(error);
            }
        });

        return returnData;
    }

    getBookTotalCount(searchObj) {
        let returnData = null;

        $.ajax({
            async: false,
            type: "get",
            url: "http://127.0.0.1:8000/api/admin/books/totalcount",
            data: {
                "category": searchObj.category,
                "searchValue": searchObj.searchValue
            },
            dataType: "json",
            success: response => {
                console.log(response);
                returnData = response.data;
            },
            error: error => {
                console.log(error);
            }
        });

        return returnData;
    }

    getcategories() {
        let returnData = null;

        $.ajax({
            async: false,
            type: "get",
            url: "http://127.0.0.1:8000/api/admin/categories",
            dataType: "json",
            success: response => {
                console.log(response);
                returnData = response.data;
            },
            error: error => {
                console.log(error);
            }
        });

        return returnData;
    }

    deleteBooks(deleteArray) {
        let returnFlag = false;

        $.ajax({
            async: false,
            type: "delete",
            url: "http://127.0.0.1:8000/api/admin/books",
            contentType: "application/json",
            data: JSON.stringify(
                {bookIds: deleteArray}
            ),
            dataType: "json",
            success: response => {
                returnFlag = true;
            },
            error: error => {
                console.log(error);
            }
        });

        return returnFlag;
    }
}

class BookService {
    static #instance = null;
    static getInstance() {
        if (this.#instance == null) {
            this.#instance = new BookService();
        }
        return this.#instance;
    }

    loadBookList() {
        const responseData = BookSearchApi.getInstance().getBookList(searchObj);
        const checkAll = document.querySelector(".delete-checkall");
        checkAll.checked = false;
        // 페이지를 넘겻을때 전체선택 체크박스 초기화
        const bookListBody = document.querySelector(".content-table tbody");
        bookListBody.innerHTML = "";

        responseData.forEach((data, index) => {
            bookListBody.innerHTML += `
            <tr>
                <td><input type="checkbox" class="delete-checkbox"/></td>
                <td class="book-id">${data.bookId}</td>
                <td>${data.bookCode}</td>
                <td>${data.bookName}</td>
                <td>${data.author}</td>
                <td>${data.publisher}</td>
                <td>${data.publicationDate}</td>
                <td>${data.category}</td>
                <td>${data.rentalStatus == 'Y' ? "대여중" : "대여가능"}</td>
                <td><a href="/templates/admin/book_modification.html?bookCode=${data.bookCode}"><i class="fa-solid fa-square-pen"></i></a></td>
            </tr>
            `;
        });

        this.loadSearchNumberList();
        ComponentEvent.getInstance().addClickEventDeleteCheckbox();
    }

    loadSearchNumberList() {

        const pageController = document.querySelector(".page-controller")
        pageController.innerHTML = "";

        const totalCount = BookSearchApi.getInstance().getBookTotalCount(searchObj);
        const maxPageNumber = totalCount % searchObj.count == 0
            ? Math.floor(totalCount / searchObj.count)
            : Math.floor(totalCount / searchObj.count) + 1;
        // Math.floor 로 소숫점 단위를 버리겟다

        pageController.innerHTML = `
        <a href="javascript:void(0)" class="pre-button disabled">이전</a>
        <ul class="page-numbers"></ul>
        <a href="javascript:void(0)" class="next-button disabled">다음</a>
        `;
        // javascript:void(0) 를 적는이유는 하이퍼링크를 막아버리기 위해서이다
        // (이 하이퍼링크는 아무런 작용을 하지 않는다.)

        if (searchObj.page != 1) {
            const preButton = pageController.querySelector(".pre-button");
            preButton.classList.remove("disabled");

            preButton.onclick = () => {
                searchObj.page--;
                // searchObj.page -= 1;

                this.loadBookList();

            }
        }

        if (searchObj.page != maxPageNumber) {
            const nextButton = pageController.querySelector(".next-button");
            nextButton.classList.remove("disabled");

            nextButton.onclick = () => {
                searchObj.page++;
                // searchObj.page -= 1;

                this.loadBookList();
            }
        }

        const startIndex = searchObj.page % 5 == 0 ?
            searchObj.page - 4
            : searchObj.page - (searchObj.page % 5) + 1;
        const endIndex = startIndex + 4 <= maxPageNumber ?
            startIndex + 4
            : maxPageNumber;
        const pageNumbers = document.querySelector(".page-numbers")

        for (let i = startIndex; i <= endIndex; i++) {
            pageNumbers.innerHTML += `
                <a href="javascript:void(0)" class="page-button ${i == searchObj.page ? "disabled" : ""}"><li>${i}</li></a>
            `;
        }

        const pageButtons = document.querySelectorAll(".page-button");
        pageButtons.forEach(button => {
            const pageNumber = button.textContent;
            if (pageNumber != searchObj.page) {
                button.onclick = () => {
                    searchObj.page = pageNumber;
                    this.loadBookList();
                }
            }
        });
    }

    loadCategories() {
        const responseData = BookSearchApi.getInstance().getcategories();

        const categorySelect = document.querySelector(".category-select");
        categorySelect.innerHTML = `<option value="">전체조회</option>`;

        responseData.forEach(data => {
            categorySelect.innerHTML += `
                <option value="${data.category}">${data.category}</option>
            `;
        });
    }

    removeBooks(deleteArray) {
        let successFlag = BookSearchApi.getInstance().deleteBooks(deleteArray);

        if(successFlag) {
            searchObj.page = 1;
            this.loadBookList();
        }
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

    addClickEventSearchButton() {
        const categorySelect = document.querySelector(".category-select");
        const searchInput = document.querySelector(".search-input");
        const searchButton = document.querySelector(".search-button");

        searchButton.onclick = () => {
            searchObj.category = categorySelect.value;
            searchObj.searchValue = searchInput.value;
            searchObj.page = 1;
            BookService.getInstance().loadBookList();
        }

        searchInput.onkeyup = () => {
            // 엔터키 들어오면
            if (window.event.keyCode == 13) {
                searchButton.click();
            }
        }
    }

    addClickEventDeleteButton() {
        const deleteButton = document.querySelector(".delete-button");
        deleteButton.onclick = () => {
            if(confirm("정말로 삭제하시겠습니까?")) {
                // confirm = 위에 상태창에 예 아니오가 출력되는데 예를 누르면 true를 반환
                const deleteArray = new Array();
    
                const deleteCheckbox = document.querySelectorAll(".delete-checkbox");
                deleteCheckbox.forEach((deleteCheckbox, index) => {
                    if (deleteCheckbox.checked) {
                        const bookIds = document.querySelectorAll(".book-id")
                        // console.log("bookId : " + bookIds[index].textContent);
                        deleteArray.push(bookIds[index].textContent);
                        // 우리가 지우고자하는 것을 리스트에 담는다.
                    }
                });
    
                BookService.getInstance().removeBooks(deleteArray);
            }
        }
    }

    addClickEventDeleteCheckAll() {
        const checkAll = document.querySelector(".delete-checkall");

        checkAll.onclick = () => {
            const deleteCheckbox = document.querySelectorAll(".delete-checkbox");
            deleteCheckbox.forEach(deleteCheckbox => {
                deleteCheckbox.checked = checkAll.checked;
            })
        }
    }

    addClickEventDeleteCheckbox() {
        const deleteCheckboxs = document.querySelectorAll(".delete-checkbox");
        const checkAll = document.querySelector(".delete-checkall");
        
        deleteCheckboxs.forEach(deleteCheckbox => {
            deleteCheckbox.onclick = () => {
                const deleteCheckedCheckboxs = document.querySelectorAll(".delete-checkbox:checked");

                console.log("선택" + deleteCheckedCheckboxs.length);
                console.log("전체" + deleteCheckboxs.length);

                if(deleteCheckedCheckboxs.length == deleteCheckboxs.length) {
                    // 체크한 녀석들과 전체 채크의 갯수가 같으면
                    checkAll.checked = true;
                } else {
                    checkAll.checked = false;
                }
            }
        })

    }
}