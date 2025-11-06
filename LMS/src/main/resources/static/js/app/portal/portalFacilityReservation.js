/** * <pre>
 * << 개정이력(Modification Information) >>
 * * 수정일      			수정자           수정내용
 * -----------   	-------------    ---------------------------
 * 2025. 10. 22.     정태일            최초 생성
 * 2025. 10. 30.     정태일            스크립트 수정
 * </pre>
 */

window.addEventListener('load', function() {
    // console.log("window.onload event fired."); // Debug Log: window.onload fired

    // 뷰 컨테이너
    const mainContent = document.querySelector('main.main-content'); // 메인 컨텐츠 컨테이너 가져오기
    const mapView = document.getElementById('campus-map-view');
    const facilityListView = document.getElementById('facility-list-container');

    // UI 요소
    // SVG가 인라인되었으므로, <svg> 요소를 직접 가져옵니다.
    const svgElement = document.querySelector('#campus-map-container svg'); // <svg> 태그 자체를 선택
    // console.log("svgElement retrieved:", svgElement); // Debug Log: svgElement state

    const facilityListTitle = document.getElementById('facility-list-title');
    const specificFacilityList = document.getElementById('specific-facility-list');
    const backToMapBtn = document.getElementById('back-to-map-btn');
    const loadingSpinnerOverlay = document.getElementById('loading-spinner-overlay'); // 로딩 스피너 요소 가져오기
    const paginationArea = document.createElement('div'); // 페이징 영역 추가
    paginationArea.className = 'pagination-area';

    const LECTURE_HALL_USAGE_CD = "CLASSROOM";
    const ADMIN_OFFICE_USAGE_CD = "ADMIN_OFFICE"; // ADMIN_OFFICE 추가

    // placeUsageCd를 한글명으로 매핑
    const placeUsageCdMap = {
        'STUDYROOM': '스터디룸',
        'CLASSROOM': '강의실',
        'SEMINAR': '세미나실',
        'ADMIN_OFFICE': '행정실', // 필터에서는 제외되지만, 혹시 모를 경우를 대비해 매핑 유지
        'ALL': '전체'
    };

    // let allFacilitiesInBuilding = []; // 모든 시설 데이터를 저장할 변수 (필터링 전 원본 데이터)
    // let currentFilteredFacilities = []; // 현재 필터링된 시설 데이터를 저장할 변수
    let currentBuildingCd = null; // 현재 선택된 건물 코드
    let currentBuildingName = null; // 현재 선택된 건물 이름
    let currentFilterType = 'ALL'; // 현재 선택된 필터 타입
    let currentPage = 1; // 현재 페이지
    let currentSize = 5; // 한 페이지당 보여줄 항목 수

    const filterButtonsContainer = document.querySelector('.filter-buttons-container');

    /**
     * 뷰를 전환하는 함수
     * @param {string} viewToShow - 'map' 또는 'list'
     */
    function switchView(viewToShow) {
        if (viewToShow === 'list') {
            mainContent.classList.add('split-view'); // split-view 클래스 추가
            mapView.style.display = 'block'; // 맵 뷰 보이도록
            facilityListView.style.display = 'block'; // 시설 목록 뷰 보이도록
        } else {
            mainContent.classList.remove('split-view'); // split-view 클래스 제거
            mapView.style.display = 'block'; // 맵 뷰 보이도록
            facilityListView.style.display = 'none'; // 시설 목록 뷰 숨김
        }
    }

    // Helper function to get icon based on placeUsageCd
    function getIconForUsageCd(placeUsageCd) {
        switch (placeUsageCd) {
            case 'STUDYROOM': return '📚'; // Book icon
            case 'CLASSROOM': return '🏫'; // School icon
            case 'SEMINAR': return '🗣️'; // Speaking head icon
            case 'ADMIN_OFFICE': return '🏢'; // Office building icon
            default: return '📍'; // Pin icon
        }
    }

    /**
     * 시설 데이터를 테이블로 렌더링하는 함수
     * @param {Array} facilitiesToRender - 렌더링할 시설 배열
     */
    function renderFacilitiesTable(facilitiesToRender) {
        const tableBody = specificFacilityList.querySelector('tbody');
        tableBody.innerHTML = ''; // 기존 목록 초기화

        if (facilitiesToRender.length > 0) {
            facilitiesToRender.forEach(facility => {
                // console.log("Facility being processed:", facility.placeName, "Usage Code:", facility.placeUsageCd, "User Role:", USER_ROLE); // 디버깅 추가

                const row = document.createElement('tr');
                row.innerHTML = `
                    <td>${facility.placeName}</td>
                    <td>${getIconForUsageCd(facility.placeUsageCd)} ${placeUsageCdMap[facility.placeUsageCd] || facility.placeUsageCd}</td>
                    <td>${facility.capacity}명</td>
                    <td>
                        <a href="${C_PATH}/portal/facility/calendar/${facility.placeCd}" class="btn btn-primary btn-sm">예약하기</a>
                    </td>
                `;
                tableBody.appendChild(row);
            });
        } else {
            tableBody.innerHTML = `<tr><td colspan="4" style="text-align: center; padding: 40px;">이 건물에는 예약 가능한 시설이 없습니다.</td></tr>`;
        }
    }

    /**
     * 페이징 버튼을 렌더링하는 함수
     * @param {object} paginationInfo - 페이징 정보 객체
     * @param {string} buildingCd - 건물 코드
     * @param {string} buildingName - 건물 이름
     * @param {string} filterType - 현재 필터 타입
     */
    function renderPagination(paginationInfo, buildingCd, buildingName, filterType) {
        paginationArea.innerHTML = ''; // 기존 페이징 초기화

        if (paginationInfo.totalPage > 1) {
            // 이전 페이지 버튼
            if (paginationInfo.currentPage > 1) {
                const prevBtn = document.createElement('button');
                prevBtn.className = 'page-btn';
                prevBtn.textContent = '이전';
                prevBtn.addEventListener('click', () => fetchAndDisplayFacilities(buildingCd, buildingName, filterType, paginationInfo.currentPage - 1, currentSize));
                paginationArea.appendChild(prevBtn);
            }

            // 페이지 번호 버튼
            for (let i = paginationInfo.startPage; i <= paginationInfo.endPage; i++) {
                const pageBtn = document.createElement('button');
                pageBtn.className = `page-btn ${i === paginationInfo.currentPage ? 'active' : ''}`;
                pageBtn.textContent = i;
                pageBtn.addEventListener('click', () => fetchAndDisplayFacilities(buildingCd, buildingName, filterType, i, currentSize));
                paginationArea.appendChild(pageBtn);
            }

            // 다음 페이지 버튼
            if (paginationInfo.currentPage < paginationInfo.totalPage) {
                const nextBtn = document.createElement('button');
                nextBtn.className = 'page-btn';
                nextBtn.textContent = '다음';
                nextBtn.addEventListener('click', () => fetchAndDisplayFacilities(buildingCd, buildingName, filterType, paginationInfo.currentPage + 1, currentSize));
                paginationArea.appendChild(nextBtn);
            }
        }
        // 페이징 영역을 facility-list-container의 적절한 위치에 추가
        // 기존 button-container 아래에 추가하거나 새로운 컨테이너를 만들 수 있습니다.
        const listFooterContainer = document.querySelector('.list-footer-container');
        if (listFooterContainer) {
            listFooterContainer.appendChild(paginationArea);
        } else {
            facilityListView.appendChild(paginationArea); // fallback
        }
    }

    /**
     * 필터링된 시설을 표시하는 함수
     * @param {string} filterType - 필터링할 시설 유형 코드 (예: 'CLASSROOM', 'ALL')
     */
    function filterFacilities(filterType) {
        currentFilterType = filterType; // 현재 필터 타입 업데이트
        currentPage = 1; // 필터 변경 시 1페이지로 초기화
        fetchAndDisplayFacilities(currentBuildingCd, currentBuildingName, currentFilterType, currentPage, currentSize);

        // 활성 버튼 상태 업데이트는 fetchAndDisplayFacilities 내부에서 처리될 것임
    }

    /**
     * 특정 건물의 시설 목록을 가져와 표시하는 함수
     * @param {string} buildingCd - 건물 코드
     * @param {string} buildingName - 건물 이름
     * @param {string} filterType - 필터링할 시설 유형 코드 (예: 'CLASSROOM', 'ALL')
     * @param {number} page - 요청할 페이지 번호
     * @param {number} size - 한 페이지당 항목 수
     */
    async function fetchAndDisplayFacilities(buildingCd, buildingName, filterType = 'ALL', page = 1, size = 5) {
        // console.log(`[DEBUG] fetchAndDisplayFacilities called with: buildingCd=${buildingCd}, filterType=${filterType}, page=${page}`);

        const isNewBuilding = buildingCd !== currentBuildingCd;
        // console.log(`[DEBUG] Is it a new building? ${isNewBuilding}`);

        currentBuildingCd = buildingCd; // 현재 건물 코드 저장
        currentBuildingName = buildingName; // 현재 건물 이름 저장
        currentFilterType = filterType; // 현재 필터 타입 저장
        currentPage = page; // 현재 페이지 저장
        currentSize = size; // 현재 사이즈 저장

        facilityListTitle.textContent = `${buildingName} 시설 목록`;
        loadingSpinnerOverlay.style.display = 'flex'; // 스피너 표시

        try {
            let url = `${C_PATH}/portal/facility/getFacilities?parentCd=${buildingCd}&page=${page}&size=${size}`;
            if (filterType !== 'ALL') {
                url += `&placeUsageCd=${filterType}`;
            }
            // console.log(`[DEBUG] Fetching URL: ${url}`);

            const response = await fetch(url);
            if (!response.ok) {
                throw new Error(`Network response was not ok: ${response.statusText}`);
            }
            const data = await response.json(); // 응답이 Map 형태로 변경됨
            // console.log("[DEBUG] Server Response Data:", JSON.stringify(data, null, 2));

            const facilities = data.facilities; // 시설 목록
            const paginationInfo = data.paginationInfo; // 페이징 정보
            const allUniqueUsageCds = data.allUniqueUsageCds; // 모든 시설 유형 코드

            // --- 필터 버튼 생성 디버깅 ---
            // console.log("[DEBUG] --- Starting Filter Button Creation ---");
            // console.log("[DEBUG] Condition for button recreation (isNewBuilding):", isNewBuilding);
            // console.log("[DEBUG] filterButtonsContainer element:", filterButtonsContainer);

            if (isNewBuilding) { // 새 건물일 때만 버튼을 다시 생성
                // console.log("[DEBUG] Recreating filter buttons.");
                filterButtonsContainer.innerHTML = ''; // 기존 버튼 초기화
                
                const allBtn = document.createElement('button');
                allBtn.type = 'button';
                allBtn.className = 'btn btn-outline-primary btn-sm filter-btn'; // active 클래스는 아래에서 추가
                allBtn.dataset.filter = 'ALL';
                allBtn.textContent = '전체';
                allBtn.addEventListener('click', () => filterFacilities('ALL'));
                filterButtonsContainer.appendChild(allBtn);
                // console.log("[DEBUG] 'All' button created and appended.");

                // console.log("[DEBUG] allUniqueUsageCds from server:", allUniqueUsageCds);
                const filteredUsageCdsForButtons = allUniqueUsageCds; // Backend now handles ADMIN_OFFICE filtering
                // console.log("[DEBUG] Filtered usage codes for buttons:", filteredUsageCdsForButtons);

                if (filteredUsageCdsForButtons.length > 0) {
                    filteredUsageCdsForButtons.forEach(usageCd => {
                        // console.log(`[DEBUG] Creating button for: ${usageCd}`);
                        const btn = document.createElement('button');
                        btn.type = 'button';
                        btn.className = 'btn btn-outline-primary btn-sm filter-btn';
                        btn.dataset.filter = usageCd;
                        btn.textContent = placeUsageCdMap[usageCd] || usageCd;
                        btn.addEventListener('click', () => filterFacilities(usageCd));
                        filterButtonsContainer.appendChild(btn);
                        // console.log(`[DEBUG] Appended button for: ${usageCd}`);
                    });
                } else {
                    // console.log("[DEBUG] No other filter buttons to create.");
                }
            } else {
                // console.log("[DEBUG] Skipping filter button recreation.");
            }
            // console.log("[DEBUG] --- Finished Filter Button Creation ---");

            // --- 활성 버튼 상태 업데이트 디버깅 ---
            // console.log(`[DEBUG] Updating active button state. Current filter: ${currentFilterType}`);
            document.querySelectorAll('.filter-btn').forEach(btn => {
                btn.classList.remove('active');
                if (btn.dataset.filter === currentFilterType) {
                    btn.classList.add('active');
                }
            });

            // --- 테이블 및 페이징 렌더링 ---
            // console.log("[DEBUG] Rendering facilities table.");
            renderFacilitiesTable(facilities);
            
            // console.log("[DEBUG] Rendering pagination with paginationInfo:", JSON.stringify(paginationInfo, null, 2));
            renderPagination(paginationInfo, buildingCd, buildingName, filterType);
            
            switchView('list');
        } catch (error) {
            console.error('Error fetching facilities:', error);
            Swal.fire('오류', '시설 목록을 불러오는 중 문제가 발생했습니다.', 'error');
        } finally {
            loadingSpinnerOverlay.style.display = 'none';
        }
    }

    // SVG가 인라인되었으므로, <object>의 load 이벤트 대신 바로 SVG 내부 요소에 접근합니다.
    if (svgElement) {
        const buildingGroups = svgElement.querySelectorAll('.building-group');
        // console.log("Found building groups:", buildingGroups); // Debug Log 3
        if (buildingGroups.length === 0) {
            console.warn("No elements with class 'building-group' found inside SVG. Check SVG structure and class names.");
        }
        buildingGroups.forEach(group => {
            group.addEventListener('click', function() {
                const buildingCd = this.dataset.buildingCd;
                const buildingName = this.dataset.buildingName;
                // console.log("Building clicked:", buildingName, "(", buildingCd, ")"); // Debug Log 4
                if (buildingCd && buildingName) {
                    // 건물 클릭 시 항상 1페이지부터 시작
                    fetchAndDisplayFacilities(buildingCd, buildingName, 'ALL', 1, currentSize);
                } else {
                    console.warn("Clicked building group is missing data-building-cd or data-building-name.", this);
                }
            });
            group.addEventListener('mouseenter', function() {
                this.style.cursor = 'pointer';
            });
            group.addEventListener('mouseleave', function() {
                this.style.cursor = 'default';
            });
        });
    } else {
        console.error("SVG element not found. Check if the SVG is correctly inlined and accessible.");
    }

    // '지도에서 다시 선택' 버튼 클릭 이벤트
    backToMapBtn.addEventListener('click', function() {
        switchView('map');
    });
});