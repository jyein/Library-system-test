window.onload = () => {
    HeaderService.getInstance().loadHeader();

    SearchService.getInstance().clearBookList();
    SearchService.getInstance().loadSearchBook();
    SearchService.getInstance().loadCategories();
    SearchService.getInstance().setMaxPage();

    ComponentEvent.getInstance().addClickEventCategoryCheckboxs();
    ComponentEvent.getInstance().addScrollEventpaging();
    ComponentEvent.getInstance().addClickEventSearchButton();

    SearchService.getInstance().onLoadSearch();
}

let maxPage = 0;

const searchObj = {
    page: 1,
    searchValue: null,
    categories: new Array(), // 카테고리를 하나만 고를게 아니기때문에 리스트로 담아준다
    count: 10
}

class SearchApi {
    static #instance = null;
    static getInstance() {
        if (this.#instance == null) {
            this.#instance = new SearchApi();
        }
        return this.#instance;
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

    getTotalCount() {
        let responseData = null

        $.ajax({
            async: false,
            type: "get",
            url: "http://127.0.0.1:8000/api/search/totalcount",
            data: searchObj,
            dataType: "json",
            success: response => {
                responseData = response.data;
            },
            error: error => {
                console.log(error);
            }
        })

        return responseData;
    }

    searchBook() {
        let responseData = null

        $.ajax({
            async: false,
            type: "get",
            url: "http://127.0.0.1:8000/api/search",
            data: searchObj,
            dataType: "json",
            success: response => {
                responseData = response.data;
            },
            error: error => {
                console.log(error);
            }
        })

        return responseData;
    }
}

class SearchService {
    static #instance = null;
    static getInstance() {
        if (this.#instance == null) {
            this.#instance = new SearchService();
        }
        return this.#instance;
    }

    onLoadSearch() {
        const URLSearch = new URLSearchParams(location.search); // URL객체안에 파라미터가 들어간다
        if (URLSearch.has("searchValue")) {
            const searchValue = URLSearch.get("searchValue");
            if (searchValue == "") {
                return;
            }
            const searchInput = document.querySelector(".search-input");
            searchInput.value = searchValue;
            const searchButton = document.querySelector(".search-button");
            searchButton.click();
        }
    }

    setMaxPage() {
        const totalCount = SearchApi.getInstance().getTotalCount();
        maxPage = totalCount % 10 == 0
            ? totalCount / 10
            : Math.floor(totalCount / 10) + 1;
    }

    loadCategories() {
        const categoryList = document.querySelector(".category-list");
        categoryList.innerHTML = ``;

        const responseData = SearchApi.getInstance().getcategories();
        responseData.forEach(categoryObj => {
            categoryList.innerHTML += `
            <div class="category-item">
              <input type="checkbox" class="category-checkbox" 
              id="${categoryObj.category}" value="${categoryObj.category}">
              <label for="${categoryObj.category}">${categoryObj.category}</label>
            </div>
            `;
        })
    }

    clearBookList() {
        const contentFlex = document.querySelector(".content-flex");
        contentFlex.innerHTML = "";
    }

    loadSearchBook() {
        const responseData = SearchApi.getInstance().searchBook();
        const contentFlex = document.querySelector(".content-flex");
        const principal = PrincipalApi.getInstance().getPrincipal();

        const _bookButtons = document.querySelectorAll(".book-buttons");
        const bookButtonsLength = _bookButtons == null ? 0 : _bookButtons.length;

        responseData.forEach((data, index) => {
            contentFlex.innerHTML += `
            <div class="info-container">
            <div class="book-desc">
              <div class="img-container">
                <img src="http://127.0.0.1:8000/image/book/${data.saveName != null ? data.saveName : "no-img.png"}" class="book-img">
              </div>
              <div class="like-info"><i class="fa-regular fa-thumbs-up"></i><span class="like-count">${data.likeCount != null ? data.likeCount : 0}</span></div>
            </div>
            
            <div class="book-info">
              <div class="book-code">${data.bookCode}</div>
              <h3 class="book-name">${data.bookName}</h3>
              <div class="info-text book-author"><b>저자: </b>${data.author}</div>
              <div class="info-text book-publisher"><b>출판사: </b>${data.publisher}</div>
              <div class="info-text book-publicationdate"><b>출판일: </b>${data.publicationDate}</div>
              <div class="info-text book-category"><b>카테고리: </b>${data.category}</div>
              <div class="book-buttons">
                
              </div>
            </div>
          </div>
      </div>

            `;
            const bookButtons = document.querySelectorAll(".book-buttons");
            if (principal == null) {
                if (data.rentalDtlId != 0 && data.returnDate == null) {
                    bookButtons[bookButtonsLength + index].innerHTML = `
                      <button type="button" class="rental-button" disabled}>대여중</button>
                    `;
                } else {
                    bookButtons[bookButtonsLength + index].innerHTML = `
                      <button type="button" class="rental-button" disabled}>대여가능</button>
                    `;
                }

                bookButtons[bookButtonsLength + index].innerHTML += `
                  <button type="button" class="like-button" disabled}>추천</button>
                  `;
            } else { // 로그인이 되어있을때
                if (data.rentalDtlId != 0 && data.returnDate == null && data.userId != principal.user.userId) {
                    bookButtons[bookButtonsLength + index].innerHTML = `
                      <button type="button" class="rental-button" disabled}>대여중</button>
                    `;
                } else if (data.rentalDtlId != 0 && data.returnDate == null && data.userId == principal.user.userId) {
                    bookButtons[bookButtonsLength + index].innerHTML = `
                      <button type="button" class="return-button"}>반납하기</button>
                    `;
                } else {
                    bookButtons[bookButtonsLength + index].innerHTML = `
                      <button type="button" class="rental-button"}>대여하기</button>
                  `;
                }
                if (data.likeId != 0) {
                    bookButtons[bookButtonsLength + index].innerHTML += `
                        <button type="button" class="dislike-button">추천취소</button>
                    `;
                } else {
                    bookButtons[bookButtonsLength + index].innerHTML += `
                        <button type="button" class="like-button">추천</button>
                        `;
                }
            }


        })
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

    addClickEventCategoryCheckboxs() {
        const checkboxs = document.querySelectorAll(".category-checkbox");
        checkboxs.forEach(checkbox => {
            checkbox.onclick = () => {
                if (checkbox.checked) {
                    searchObj.categories.push(checkbox.value);
                } else {
                    const index = searchObj.categories.indexOf(checkbox.value);
                    searchObj.categories.splice(index, 1);
                }
                document.querySelector(".search-button").click();
            }
        });
    }

    addScrollEventpaging() {
        const html = document.querySelector("html");
        const body = document.querySelector("body");

        body.onscroll = () => {
            // console.log("html client: " + html.clientHeight);
            // 눈에보이는 화면의 높이
            // console.log("body offset: " + body.offsetHeight);
            // console.log("lient - offset - scrollTop = " + (body.offsetHeight - html.clientHeight - html.scrollTop));
            // console.log("html scrollTop: " + html.scrollTop);

            const scrollPosition = body.offsetHeight - html.clientHeight - html.scrollTop;

            if (scrollPosition < 250 && searchObj.page < maxPage) {
                searchObj.page++;
                SearchService.getInstance().loadSearchBook();
            }

        }
    }

    addClickEventSearchButton() {
        const searchButton = document.querySelector(".search-button");
        const searchInput = document.querySelector(".search-input");
        searchButton.onclick = () => {
            searchObj.searchValue = searchInput.value;
            searchObj.page = 1; // 1page부터 정렬
            window.scrollTo(0, 0); //최상단으로 이동
            SearchService.getInstance().clearBookList(); // 기존의 데이터 삭제
            SearchService.getInstance().setMaxPage(); // 검색결과에 대한 maxpage를 가져옴
            SearchService.getInstance().loadSearchBook(); // 카테고리 검색결과 가져오기
        }

        searchInput.onkeyup = () => {
            if (window.event.keyCode == 13) {
                searchButton.click();
            }
        }
    }
}